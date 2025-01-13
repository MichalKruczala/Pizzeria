package model;

import model.Group;

import java.util.List;

public class Table {
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
                "}";
    }
}