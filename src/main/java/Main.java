import dobreee.Group;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {

        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(50);
        Pizzeria pizzeria = new Pizzeria(1, 2, 3, 4);
        System.out.println(pizzeria);

        Thread guestsThread = new Thread(() -> {
            try {
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
        });


        Thread pizzerman = new Thread(() -> {
            try {
                Optional<Group> optional = Optional.ofNullable(queue.peek());   // podglądanie pierwszego elementu kolejki
                for (Group queueGroup : queue) {
                    for (Pizzeria.Table table : pizzeria.getTables()) {
                        if (!table.isOccupied() && table.getCapacity() == queueGroup.getSize()) {
                            //pizzerman siadaj ich
                            System.out.println("usiedli");
                        }

                        List<Group> tableGroups = table.getGroups();
                        if (table.isOccupied() && compareGroupSizesByTable(tableGroups, queueGroup.getSize())) {
                            //pizzerman siadaj ich
                            System.out.println("usiedli");
                        }
                    }
                }

                queue.take();
                Thread.sleep(2000); // Obsługiwanie grupy
                queue.take();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Konsument: Przerwano");
            }
        });

        guestsThread.start();
        pizzerman.start();

        System.out.println(pizzeria);

    }

    public static boolean compareGroupSizesByTable(List<Group> tableGroups, int size) {
        if (tableGroups.isEmpty()) {
            return true;
        }
        Group firstGroup = tableGroups.get(0);
        for (Group element : tableGroups) {
            if (!element.equals(firstGroup)) {
                return false;
            }
        }
        return true;
    }

}

