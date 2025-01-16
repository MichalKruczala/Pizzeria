import model.Group;
import model.Table;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {

        FileManager manager = new FileManager();
        int[] quantityOfTables = {3, 2, 3, 2};
        manager.createTablesFile(quantityOfTables);
        AtomicReference<List<Table>> tablesSortedByCapacity = new AtomicReference<>(manager.readTablesFromFile()
                .stream()
                .sorted((t1, t2) -> t1.getInitialCapacity() - t2.getInitialCapacity())
                .toList());
        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(5);

        Thread guestsThread = new Thread(() -> {
            try {
                while (true) {
                    queue.put(new Group(Group.getRandomGroupSize()));
                    Thread.sleep(2 * 1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Goście nie przychodzą");
            }
        });


        Thread pizzerman = new Thread(() -> {
            while (true) {
                try {
                    boolean assignedToTable;
                    do {
                        assignedToTable = false;
                        for (Group queueGroup : queue) {
                            if (tryAssignGroupToTable(queueGroup, tablesSortedByCapacity.get())) {
                                manager.writeTablesToFile(tablesSortedByCapacity.get());
                                queue.remove(queueGroup);
                                queue.forEach(System.out::println);
                                checkAndRemoveGroups(tablesSortedByCapacity.get());
                                tablesSortedByCapacity.get().stream().forEach(System.out::println);
                                tablesSortedByCapacity.set(manager.readTablesFromFile());
                                queue.forEach(System.out::println);
                                Thread.sleep(4 * 1000);          ////   czas obsługi kelnera
                                assignedToTable = true;
                                break;
                            }
                        }

                    } while (assignedToTable);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Goście nie przychodzą");

                }
            }
        });
        guestsThread.start();
        pizzerman.start();
    }


    public static void checkAndRemoveGroups(List<Table> tables) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (Table table : tables) {
            Iterator<Group> iterator = table.getGroups().iterator();
            int totalRemovedCapacity = 0;

            while (iterator.hasNext()) {
                Group group = iterator.next();
                LocalTime groupTime = LocalTime.parse(group.getServiceTime(), timeFormatter);
                if (Duration.between(groupTime, LocalTime.now()).toMillis() > 60_000) {
                    totalRemovedCapacity += group.getSize();
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


    private static boolean tryAssignGroupToTable(Group group, List<Table> tablesSortedByCapacity) {
        for (Table table : tablesSortedByCapacity) {
            if (!table.isOccupied() && group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                System.out.println("pierwszy if");
                return true;
            } else if (table.isOccupied() && compareGroupSizes(table.getGroups(), group.getSize()) && group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                System.out.println("drugi if");
                return true;
            }
        }
        return false;
    }

    public static boolean compareGroupSizes(List<Group> tableGroups, int comingGroupSize) {
        if (tableGroups.isEmpty()) {
            return true;
        }
        Group firstGroup = tableGroups.get(0);
        for (Group group : tableGroups) {
            if (group.getSize() != firstGroup.getSize()) {
                return false;
            }
        }
        for (Group group : tableGroups) {
            if (group.getSize() != comingGroupSize) {
                return false;
            }
        }
        return true;
    }
}

