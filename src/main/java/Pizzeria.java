import dobreee.Group;

import java.util.ArrayList;
import java.util.List;

public class Pizzeria {
    @Override
    public String toString() {
        return "Pizzeria{" +
                "tables=" + tables +
                "}\n";
    }

    public List<Table> getTables() {
        return tables;
    }

    private List<Table> tables= new ArrayList<>();

    public Pizzeria(int x1, int x2, int x3, int x4) {
        for (int i = 0; i < x1; i++) tables.add(new Table(1,false,new ArrayList<>()));
        for (int i = 0; i < x2; i++) tables.add(new Table(2,false,new ArrayList<>()));
        for (int i = 0; i < x3; i++) tables.add(new Table(3,false,new ArrayList<>()));
        for (int i = 0; i < x4; i++) tables.add(new Table(4,false,new ArrayList<>()));
    }


    public static class Table {
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        private  int capacity;
        private boolean isOccupied;
        private List<Group> groups;

        public Table(int capacity,boolean isOccupied, List<Group> groups) {
            this.capacity = capacity;
            this.isOccupied = isOccupied;
            this.groups = groups;
        }
        public int getCapacity() {
            return capacity;
        }
        public void addGroupToTable(Group group){
            this.groups.add(group);
        }
        public boolean isOccupied() {
            return isOccupied;
        }

        public void setOccupied(boolean occupied) {
            isOccupied = occupied;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }

        @Override
        public String toString() {
            return "Table{" +
                    "capacity=" + capacity +
                    ", isOccupied=" + isOccupied +
                    ", groups=" + groups +
                    "}\n";
        }
    }
}