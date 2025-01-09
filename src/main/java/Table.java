import dobreee.Group;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private int capacity;
    private boolean isOccupied;
    private List<Group> groups;

    public Table(int capacity) {
        this.capacity = capacity;
        this.isOccupied = false;
        this.groups = new ArrayList<>();
    }

    public synchronized boolean tryToSeat(Group group) {

        if (!isOccupied && group.getSize() <= capacity) {
            groups.add(group);
            if (group.getSize() == capacity) {
                isOccupied = true; // Stolik pełny
            }
            return true;
        }
        return false;
    }

    public synchronized void leaveTable() {
        // Zwalnia stolik
        groups.clear();
        isOccupied = false;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isOccupied() {
        return isOccupied;
    }
}
