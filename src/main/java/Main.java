import model.Group;
import model.Pizzeria;
import model.Table;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(5);
        AtomicBoolean isFireAlarmTriggered = new AtomicBoolean(false);

        FileManager manager = new FileManager();
        int[] quantityOfTables = {3, 2, 3, 2};
        manager.createTablesToFile(quantityOfTables);

        AtomicReference<List<Table>> tablesSortedByCapacity = new AtomicReference<>(manager.readTablesFromFile()
                .stream()
                .sorted((t1, t2) -> t1.getInitialCapacity() - t2.getInitialCapacity())
                .toList());

        Thread guestsThread = new Thread(() -> {
            try {
                while (!isFireAlarmTriggered.get()) {
                    queue.put(new Group(Group.getRandomGroupSize(3)));
                    Thread.sleep(6 * 1000);              // czas co ile goście przychodzą
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Guests are not comming");
            }
        });


        Thread pizzerman = new Thread(() -> {
            while (!isFireAlarmTriggered.get()) {
                try {
                    boolean assignedToTable;
                    do {
                        assignedToTable = false;
                        for (Group queueGroup : queue) {
                            Pizzeria.removeGroupsAfterCertainTime(tablesSortedByCapacity.get(), 60);
                            if (tryAssignGroupToTable(queueGroup, tablesSortedByCapacity.get())) {
                                manager.writeTablesToFile(tablesSortedByCapacity.get());
                                queue.remove(queueGroup);
                                tablesSortedByCapacity.get().forEach(System.out::println);
                                tablesSortedByCapacity.set(manager.readTablesFromFile());
                                queue.forEach(System.out::println);
                                System.out.println("----------------------------------------------------------------------");
                                Thread.sleep(8 * 1000);     // pizzerman serve quest each 8 seconds
                                assignedToTable = true;
                                break;
                            }
                        }
                    } while (assignedToTable && !isFireAlarmTriggered.get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Pizzerman is not serving quests");

                }
            }
        });
        Thread firemanFighter = new Thread(() -> {
            try {
                Thread.sleep(30 * 1000);    // time to fire alarm
                isFireAlarmTriggered.set(true);
                System.out.println("Firefighter thread: Fire! Immediate evacuation!");
                queue.clear();
                System.out.println("Queue state :"+ queue.stream().count());
                for (Table table : tablesSortedByCapacity.get()) {
                    table.clearAllGroupsFromTable();
                    table.setOccupied(false);
                    table.setCapacity(table.getInitialCapacity());
                }
                System.out.println("Firefighter: Pizzeria została zamknięta,wszyscy wyszli.");
                tablesSortedByCapacity.get().forEach(System.out::println);
                manager.writeTablesToFile(tablesSortedByCapacity.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Firefighter został przerwany.");
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
            } else if (table.isOccupied() && Group.compareGroupSizes(table.getGroups(), group.getSize()) && group.getSize() <= table.getCapacity()) {
                table.addGroupToTable(group);
                table.setOccupied(true);
                table.setCapacity(table.getCapacity() - group.getSize());
                return true;
            }
        }
        return false;
    }
}

