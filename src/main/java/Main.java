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
        FileManager fileManager = new FileManager();
        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(5);
        AtomicBoolean isFireAlarmTriggered = new AtomicBoolean(false);

        int[] quantityOfTables = {3, 2, 3, 2};
        fileManager.createTablesToFile(quantityOfTables);

        AtomicReference<List<Table>> tablesSortedByCapacity = new AtomicReference<>(fileManager.readTablesFromFile()
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
                GUI.printMessage("Guests are not comming");
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
                                fileManager.writeTablesToFile(tablesSortedByCapacity.get());
                                queue.remove(queueGroup);
                                tablesSortedByCapacity.get().forEach(System.out::println);
                                tablesSortedByCapacity.set(fileManager.readTablesFromFile());
                                queue.forEach(System.out::println);
                                GUI.printMessage("----------------------------------------------------------------------");
                                Thread.sleep(8 * 1000);     // pizzerman serve quest each 8 seconds
                                assignedToTable = true;
                                break;
                            }
                        }
                    } while (assignedToTable && !isFireAlarmTriggered.get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    GUI.printMessage("Pizzerman is not serving quests");

                }
            }
        });
        Thread firemanFighter = new Thread(() -> {
            try {
                Thread.sleep(30 * 1000);    // time to fire alarm
                isFireAlarmTriggered.set(true);
                GUI.printMessage("Firefighter thread: Fire! Immediate evacuation!");
                queue.clear();
                GUI.printMessage("Queue state :" + queue.stream().count());
                for (Table table : tablesSortedByCapacity.get()) {
                    table.clearAllGroupsFromTable();
                    table.setOccupied(false);
                    table.setCapacity(table.getInitialCapacity());
                }
                GUI.printMessage("Firefighter: Pizzeria has been closer,everyone left.");
                tablesSortedByCapacity.get().forEach(System.out::println);
                fileManager.writeTablesToFile(tablesSortedByCapacity.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GUI.printMessage("Firefighter został przerwany.");
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

