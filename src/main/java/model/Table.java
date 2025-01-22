package model;

import model.Group;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private int initialCapacity;
    private int capacity;
    private boolean isOccupied;
    private List<Group> groups;

    public Table(int initialCapacity, int capacity, boolean isOccupied, List<Group> groups) {
        this.initialCapacity = initialCapacity;
        this.capacity = capacity;
        this.isOccupied = isOccupied;
        this.groups = groups;
    }
    public void clearAllGroupsFromTable() {
        this.setGroups(new ArrayList<>());
    }


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void addGroupToTable(Group group) {
        this.groups.add(group);
    }


    public int getInitialCapacity() {
        return initialCapacity;
    }

    public void setInitialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
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
                "initialCapacity=" + initialCapacity +
                ", capacity=" + capacity +
                ", isOccupied=" + isOccupied +
                ", groups=" + groups +
                '}';
    }
}