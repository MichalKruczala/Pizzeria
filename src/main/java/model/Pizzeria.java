package model;

import app.Main;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class Pizzeria {
    private List<Table> tables;

    public Pizzeria(List<Table> tables) {
        this.tables = tables;
    }

    public static void removeGroupsAfterCertainTime(List<Table> tables, int timeLimitInSeconds) {
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
                        Thread t = Main.getThreadById(tid);
                        if (t != null) {
                            t.interrupt();
                            System.out.println("kończę wątek o ID: " + tid);
                            Main.removeThreadById(tid);
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

    @Override
    public String toString() {
        return "model.Pizzeria{" +
                "tables=" + tables +
                "}\n";
    }

    public List<Table> getTables() {
        return tables;
    }
}
