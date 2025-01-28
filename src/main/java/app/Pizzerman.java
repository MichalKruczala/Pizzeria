package app;

import model.Group;
import model.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Pizzerman {
    public static final Map<Long, Thread> threadMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        FileManager fileManager = new FileManager();
        int[] quantityOfTables = {3, 2, 3, 3};
        fileManager.createTablesToFile(quantityOfTables);

        AtomicReference<List<Table>> tablesSortedByCapacity = new AtomicReference<>(
                fileManager.readTablesFromFile()
                        .stream()
                        .sorted((t1, t2) -> t1.getInitialCapacity() - t2.getInitialCapacity())
                        .collect(Collectors.toList())
        );
        try (Socket socket = new Socket("localhost", 9876);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            AtomicBoolean isFireAlarmTriggered = new AtomicBoolean(false);

            Thread pizzermanThread = new Thread(() -> {
                BlockingQueue<Group> queue;
                while (!isFireAlarmTriggered.get()) {
                    boolean assignedToTable;
                    do {
                        assignedToTable = false;
                        queue = GroupFileManager.readGroupsFromFile();
                        for (Group queueGroup : queue) {
                            removeGroupsAndTheirThreadsAfterCertainTime(tablesSortedByCapacity.get(), 2000);  // average time group sit by table
                            if (tryAssignGroupToTable(queueGroup, tablesSortedByCapacity.get())) {
                                fileManager.writeTablesToFile(tablesSortedByCapacity.get());
                                queue.remove(queueGroup);
                                GroupFileManager.writeQueueToFile(queue);
                                GUI.printMessage("           Positioning guests in the pizzeria");
                                tablesSortedByCapacity.get().forEach(System.out::println);
                                tablesSortedByCapacity.set(fileManager.readTablesFromFile());
                                GUI.printMessage("            Quantity of groups in queue: " + queue.size());
                                queue.stream().map(Group::toStringWithoutThreads).forEach(System.out::println);
                                GUI.printMessage("----------------------------------------------------------------------");
                                try {
                                    Thread.sleep(2 * 1000);      // serving time
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                queue = GroupFileManager.readGroupsFromFile();
                                assignedToTable = true;
                                break;
                            }
                        }
                    } while (assignedToTable && !isFireAlarmTriggered.get());
                    removeGroupsAndTheirThreadsAfterCertainTime(tablesSortedByCapacity.get(), 0);
                    threadMap.clear();
                }
            });


            Thread serverListenerThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if ("evacuation".equals(response)) {
                            isFireAlarmTriggered.set(true);
                            break;
                        }
                    }
                    System.out.println("Server closed the connection. Pizzerman and Guests are leaving...");
                    isFireAlarmTriggered.set(true);
                } catch (IOException e) {
                    System.out.println("Error in server listener thread: " + e.getMessage());
                }
            });
            serverListenerThread.start();
            pizzermanThread.start();
            pizzermanThread.join();
            serverListenerThread.join();
            System.out.println("Pizzerman process exiting with code 0.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean tryAssignGroupToTable(Group group, List<Table> tablesSortedByCapacity) {
        for (Table table : tablesSortedByCapacity) {
            if (!table.isOccupied() && group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                group.setServiceTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                runNewThreadForEachGuest(group);
                return true;
            } else if (table.isOccupied() && Group.compareGroupSizes(table.getGroups(), group.getSize()) && group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                group.setServiceTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                runNewThreadForEachGuest(group);
                return true;
            }
        }
        return false;
    }

    public static void runNewThreadForEachGuest(Group group) {
        List<Long> threadIds = new ArrayList<>();
        for (int i = 0; i < group.size(); i++) {
            Thread t = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            t.setDaemon(true);
            t.start();
            threadIds.add(t.threadId());


            registerThread(t);
        }
        group.setUserThreadIds(threadIds);
    }


    public static void removeGroupsAndTheirThreadsAfterCertainTime(List<Table> tables, int timeLimitInSeconds) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (Table table : tables) {
            Iterator<Group> iterator = table.getGroups().iterator();
            int totalRemovedCapacity = 0;
            while (iterator.hasNext()) {
                Group group = iterator.next();
                LocalTime groupTime = LocalTime.parse(group.getServiceTime(), timeFormatter);
                if (Duration.between(groupTime, LocalTime.now()).getSeconds() > timeLimitInSeconds) {
                    totalRemovedCapacity += group.getSize();
                    for (Long tid : group.getUserThreadIds()) {
                        Thread t = getThreadById(tid);
                        if (t != null) {
                            t.interrupt();
                            System.out.println("Ending thread -> ID: " + tid);
                            removeThreadById(tid);
                        }
                    }
                    iterator.remove();
                }
            }
            table.setCapacity(table.getCapacity() + totalRemovedCapacity);
            if (table.getGroups().isEmpty()) {
                table.setOccupied(false);
                table.setCapacity(table.getInitialCapacity());
            } else {
                table.setOccupied(true);
            }
        }
    }

    public static void registerThread(Thread t) {
        threadMap.put(t.threadId(), t);

    }

    public static Thread getThreadById(long tid) {
        return threadMap.get(tid);
    }

    public static void removeThreadById(long tid) {
        threadMap.remove(tid);
    }

}
