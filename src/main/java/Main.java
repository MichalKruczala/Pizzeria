import dobreee.Group;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {

        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(50);
        Pizzeria pizzeria = new Pizzeria(1, 2, 3, 1);
        System.out.println(pizzeria);

        //  Thread guestsThread = new Thread(() -> {
        try {
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


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Goście nie przychodzą");
        }
        //  });


        // Thread pizzerman = new Thread(() -> {
        System.out.println(queue);
        try {
            List<Pizzeria.Table> tablesSortedByCapacity = pizzeria.getTables()
                    .stream()
                    .sorted(((o1, o2) -> o1.getCapacity() - o2.getCapacity()))
                    .toList();
            //Optional<Group> optional = Optional.ofNullable(queue.peek());   // podglądanie pierwszego elementu kolejki
            for (Group queueGroup : queue) {

                for (Pizzeria.Table table : tablesSortedByCapacity) {
                    if (!table.isOccupied() && table.getCapacity() <= queueGroup.getSize()) {
                        table.addGroupToTable(queueGroup);
                        table.setOccupied(true);
                        table.setCapacity(table.getCapacity() - queueGroup.getSize());
                        System.out.println("pierwszy if");
                    }

                    List<Group> tableGroups = table.getGroups();
                    if (table.isOccupied() && compareGroupSizesByTable(tableGroups, queueGroup.getSize()) && queueGroup.getSize() <= table.getCapacity()) {
                        table.addGroupToTable(queueGroup);
                        table.setOccupied(true);
                        table.setCapacity(table.getCapacity() - queueGroup.getSize());
                        System.out.println("drugi if");
                    }
                }
            }

          //  while (true) {
               //zmaleziono stolik
                //grupa usiadła - while na false
          //  }
            queue.take();
          //  Thread.sleep(2000); // Obsługiwanie grupy
         //   queue.take();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Konsument: Przerwano");
        }
        //  });
        // guestsThread.start();
        // pizzerman.start();
        System.out.println(pizzeria);

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

