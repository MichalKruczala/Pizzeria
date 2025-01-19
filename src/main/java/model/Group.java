package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import app.Main;

public class Group {
    private int size;
    private String serviceTime;
    private List<Long> userThreadIds;

    public Group(int size) {
        this.size = size;
        this.serviceTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.userThreadIds = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Thread t = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            t.start();
            Main.registerThread(t);
            this.userThreadIds.add(t.getId());
        }
    }
    public Group(int size, String serviceTime, List<Long> userThreadIds) {
        this.size = size;
        this.serviceTime = serviceTime;
        this.userThreadIds = userThreadIds;
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

    public List<Long> getUserThreadIds() {
        return userThreadIds;
    }

    public void setUserThreadIds(List<Long> userThreadIds) {
        this.userThreadIds = userThreadIds;
    }

    @Override
    public String toString() {
        return "Group{" +
                "size=" + size +
                ", serviceTime=" + serviceTime +
                ", userThreadIds=" + userThreadIds +
                '}';
    }
}
