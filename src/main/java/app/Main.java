package app;

import Managers.FileManager;
import gui.GUI;
import model.Group;
import model.Pizzeria;
import model.Table;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static final Map<Long, Thread> threadMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(6);
        AtomicBoolean isFireAlarmTriggered = new AtomicBoolean(false);

        int[] quantityOfTables = {3, 2, 3, 2};
        fileManager.createTablesToFile(quantityOfTables);

        AtomicReference<List<Table>> tablesSortedByCapacity = new AtomicReference<>(
                fileManager.readTablesFromFile()
                        .stream()
                        .sorted((t1, t2) -> t1.getInitialCapacity() - t2.getInitialCapacity())
                        .toList()
        );

        Thread guestsThread = new Thread(() -> {
            try {
                while (!isFireAlarmTriggered.get()) {
                    queue.put(new Group(Group.getRandomGroupSize(40)));
                   // Thread.sleep(3 * 1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GUI.printMessage("Guests are not comming");
            }
        });

        Thread pizzerman = new Thread(() -> {
            while (!isFireAlarmTriggered.get()) {
                boolean assignedToTable;
                do {
                    assignedToTable = false;
                    for (Group queueGroup : queue) {
                        Pizzeria.removeGroupsAfterCertainTime(tablesSortedByCapacity.get(), 3);
                        if (tryAssignGroupToTable(queueGroup, tablesSortedByCapacity.get())) {
                            fileManager.writeTablesToFile(tablesSortedByCapacity.get());
                            queue.remove(queueGroup);
                            tablesSortedByCapacity.get().forEach(System.out::println);
                            tablesSortedByCapacity.set(fileManager.readTablesFromFile());
                            queue.forEach(System.out::println);
                            GUI.printMessage("----------------------------------------------------------------------");
                          //  Thread.sleep(5 * 1000);
                            assignedToTable = true;
                            break;
                        }
                    }
                } while (assignedToTable && !isFireAlarmTriggered.get());
            }
        });

        Thread firemanFighter = new Thread(() -> {
            try {
                Thread.sleep(40 * 1000);
                isFireAlarmTriggered.set(true);
                GUI.printMessage("Firefighter thread: Fire! Immediate evacuation!");
                for (Group group : queue) {
                    for (Long tid : group.getUserThreadIds()) {
                        Thread thread = Main.getThreadById(tid);
                        if (thread != null) {
                            System.out.println("Guest ->Thread TID " + tid + " finished by firefighter from queue");
                            thread.interrupt();
                            Main.removeThreadById(tid);
                        }
                    }
                }
                queue.clear();
                for(Map.Entry<Long, Thread> entry : threadMap.entrySet()){
                    System.out.println("Guest ->Thread ID " + entry.getKey() + " left restaurant cause firefighter");
                }
                    threadMap.clear();
                for (Table table : tablesSortedByCapacity.get()) {
                    table.clearAllGroupsFromTable();
                    table.setOccupied(false);
                    table.setCapacity(table.getInitialCapacity());
                }
                GUI.printMessage("Firefighter: Pizzeria has been closed, everyone left.\n");

                tablesSortedByCapacity.get().forEach(System.out::println);
                fileManager.writeTablesToFile(tablesSortedByCapacity.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GUI.printMessage("Firefighter has been interrupted.");
            }
        });
        guestsThread.start();
        pizzerman.start();
        firemanFighter.start();
    }

    private static boolean tryAssignGroupToTable(Group group, List<Table> tablesSortedByCapacity) {
        for (Table table : tablesSortedByCapacity) {
            if (!table.isOccupied() && group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                return true;
            } else if (table.isOccupied() &&
                    Group.compareGroupSizes(table.getGroups(), group.getSize()) &&
                    group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                return true;
            }
        }
        return false;
    }

    public static void registerThread(Thread t) {
        threadMap.put(t.getId(), t);
    }

    public static Thread getThreadById(long tid) {
        return threadMap.get(tid);
    }

    public static void removeThreadById(long tid) {
        threadMap.remove(tid);
    }
}
