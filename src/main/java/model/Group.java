package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Group {
    private int size;
    private String serviceTime;

    public Group(int size) {
        this.size = size;
        this.serviceTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    }

    public static int getRandomGroupSize(int maxGroupSize) {
        return (int) (Math.random() * maxGroupSize) + 1;
    }

    public static boolean compareGroupSizes(List<Group> tableGroups, int comingGroupSize) {
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
            if (group.getSize() != comingGroupSize) {
                return false;
            }
        }
        return true;
    }

    public int getSize() {
        return size;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }


    @Override
    public String toString() {
        return "Group{" +
                "size=" + size +
                ", serviceTime=" + serviceTime +
                '}';
    }
}
