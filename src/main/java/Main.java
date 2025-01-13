import model.Group;
import model.Pizzeria;
import model.Table;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {


        FileManager manager = new FileManager();
        int[] capacities = {9, 2, 6, 9};
        manager.createTablesFile(capacities);
        List<Table> tables = manager.readTablesFromFile();
        tables.forEach(System.out::println);

        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(50);
        Pizzeria pizzeria = new Pizzeria(1, 2, 3, 4);
      //  System.out.println(pizzeria);

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

        List<Table> tablesSortedByCapacity = pizzeria.getTables()
                .stream()
                .sorted((o1, o2) -> o1.getCapacity() - o2.getCapacity())
                .toList();

        Group queueGroup;
        while ((queueGroup = queue.peek()) != null) {
            boolean assignedToTable = false;
            for (Table table : tablesSortedByCapacity) {
                if (!table.isOccupied() && queueGroup.getSize() <= table.getCapacity()) {
                    table.addGroupToTable(queueGroup);
                    table.setOccupied(true);
                    table.setCapacity(table.getCapacity() - queueGroup.getSize());
                    System.out.println("pierwszy if");
                    assignedToTable = true;
                    break;
                } else if (table.isOccupied() && compareGroupSizesByTable(table.getGroups(), queueGroup.getSize()) && queueGroup.getSize() <= table.getCapacity()) {
                    table.addGroupToTable(queueGroup);
                    table.setOccupied(true);
                    table.setCapacity(table.getCapacity() - queueGroup.getSize());
                    System.out.println("drugi if");
                    assignedToTable = true;
                    break;
                }
            }
            if (assignedToTable) {
                queue.poll();
            } else {
                break;
            }
        }
      //  System.out.println(pizzeria);
       // System.out.println(queue);
    }


    public static boolean compareGroupSizesByTable(List<Group> tableGroups, int size) {
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
            if (group.getSize() != size) {
                return false;
            }
        }
        return true;
    }

}

