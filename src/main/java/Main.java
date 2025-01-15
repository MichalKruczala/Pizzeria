import model.Group;
import model.Pizzeria;
import model.Table;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        FileManager manager = new FileManager();
        int[] quantityOfTables = {2, 0, 0, 2};
        manager.createTablesFile(quantityOfTables);
        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(50);

        //  Thread guestsThread = new Thread(() -> {
        //try {
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        queue.put(new Group(Group.getRandomGroupSize()));
        //  } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //    System.out.println("Goście nie przychodzą");
        //  }
        //  });
        //  List<model.Group> queue = new ArrayList<>();

        // Thread pizzerman = new Thread(() -> {
        //  System.out.println(queue);
        // System.out.println("ilość grup" + queue.size());
        //  try {
        List<Table> tablesSortedByCapacity = manager.readTablesFromFile()
                .stream()
                .sorted((t1, t2) -> t1.getCapacity() - t2.getCapacity())
                .toList();
        Pizzeria pizzeria = new Pizzeria(tablesSortedByCapacity);

        boolean assignedToTable;
        do {
            assignedToTable = false;
            for (Group queueGroup : queue) {
                if (tryAssignGroupToTable(queueGroup, tablesSortedByCapacity)) {
                    queue.remove(queueGroup);
                    assignedToTable = true;
                    break;
                }
            }
        } while (assignedToTable);

        manager.writeTablesToFile(pizzeria.getTables()); // do ifów jak uda sie usiąść gościa zeby zapisać do pliku
        System.out.println("printy");
        pizzeria.getTables().forEach(System.out::println);
        System.out.println(queue);
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

