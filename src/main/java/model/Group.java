package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Group {
    private int size;
    private String serviceTime;

    public Group(int size) {
        this.size = size;
        this.serviceTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

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




    public static int getRandomGroupSize() {
        return (int) (Math.random() * 3) + 1;

    }
    @Override
    public String toString() {
        return "Group{" +
                "size=" + size +
                ", serviceTime=" + serviceTime +
                '}';
    }
}
