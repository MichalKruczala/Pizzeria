import dobreee.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //  BlockingQueue<Group> queue = new ArrayBlockingQueue<>(50);
        Pizzeria pizzeria = new Pizzeria(1, 2, 3, 3);
        System.out.println(pizzeria);

        //  Thread guestsThread = new Thread(() -> {
        ///
        //try {
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));
//            queue.put(new Group(Group.getRandomGroupSize()));


        //  } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //    System.out.println("Goście nie przychodzą");
        //  }
        //  });
        List<Group> queue = new ArrayList<>();
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));
        queue.add(new Group(Group.getRandomGroupSize()));


        // Thread pizzerman = new Thread(() -> {
        System.out.println(queue);
        System.out.println("ilość grup" + queue.size());
        //  try {
        List<Pizzeria.Table> tablesSortedByCapacity = pizzeria.getTables()
                .stream()
                .sorted(((o1, o2) -> o1.getCapacity() - o2.getCapacity()))
                .toList();
        Iterator<Group> it = queue.iterator();
        while (it.hasNext()) {
            Group queueGroup = it.next();
            for (Pizzeria.Table table : tablesSortedByCapacity) {
                if (!table.isOccupied() && queueGroup.getSize() <= table.getCapacity()) {
                    table.addGroupToTable(queueGroup);
                    table.setOccupied(true);
                    table.setCapacity(table.getCapacity() - queueGroup.getSize());
                    System.out.println("pierwszy if");
                    it.remove();
                    break;
                } else if (table.isOccupied() && compareGroupSizesByTable(table.getGroups(), queueGroup.getSize()) && queueGroup.getSize() <= table.getCapacity()) {
                    table.addGroupToTable(queueGroup);
                    table.setOccupied(true);
                    table.setCapacity(table.getCapacity() - queueGroup.getSize());
                    System.out.println("drugi if");
                    it.remove();
                    break;
                }
            }
        }

        System.out.println(pizzeria);
        System.out.println(queue);
    }


    //  while (true) {
    //zmaleziono stolik
    //grupa usiadła - while na false
    //  }
    //  queue.take();
    //  Thread.sleep(2000); // Obsługiwanie grupy
    //   queue.take();

    //   } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // System.out.println("Konsument: Przerwano");
    // }
    //  });
    // guestsThread.start();
    // pizzerman.start();




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

