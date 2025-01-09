import dobreee.Group;

import java.util.*;

public class Pizzeria {
    private List<Table> tables;

    public Pizzeria(List<Table> tables) {
        this.tables = tables;
    }

    public Pizzeria() {
    }

    public Pizzeria(int x1, int x2, int x3, int x4) {
        tables = new ArrayList<>();
        for (int i = 0; i < x1; i++) tables.add(new Table(1));
        for (int i = 0; i < x2; i++) tables.add(new Table(2));
        for (int i = 0; i < x3; i++) tables.add(new Table(3));
        for (int i = 0; i < x4; i++) tables.add(new Table(4));
    }

    public synchronized Table findTableForGroup(Group group) {
        for (Table table : tables) {
            if (table.tryToSeat(group)) {
                return table;
            }
        }
        return null; // Brak dostępnego stolika
    }
}
