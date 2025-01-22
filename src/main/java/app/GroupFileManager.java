package app;

import model.Group;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GroupFileManager {

    private static final String FILE_PATH = "shared_memory.txt";

    public static void writeQueueToFile(BlockingQueue<Group> queue) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "rw");
             FileChannel channel = file.getChannel()) {
            FileLock lock = channel.lock();
            try {
                file.setLength(0);
                for (Group group : queue) {
                    file.writeBytes(group.getSize() + "," + group.getServiceTime() + System.lineSeparator());
                }

            } finally {
                lock.release();
            }
        } catch (IOException e) {
            System.err.println("Error while saving queue status to file: " + e.getMessage());
        }
    }


    public static BlockingQueue<Group> readGroupsFromFile() {
        BlockingQueue<Group> queue = new ArrayBlockingQueue<>(6);

        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r");
             FileChannel channel = file.getChannel()) {

            FileLock lock = channel.lock(0, Long.MAX_VALUE, true);
            try {
                String line;
                while ((line = file.readLine()) != null) {
                    Group group = parseGroupFromString(line);
                    if (group != null) {
                        queue.offer(group);
                    }
                }
            } finally {
                lock.release();
            }
        } catch (IOException e) {
            System.err.println("Error reading groups from file: " + e.getMessage());
        }

        return queue;
    }

    private static Group parseGroupFromString(String line) {
        try {
            String[] parts = line.split(",");
            int size = Integer.parseInt(parts[0].trim());
            String serviceTime = parts[1].trim();

            return new Group(size, serviceTime);
        } catch (Exception e) {
            System.err.println("Error parsing group: " + e.getMessage());
            return null;
        }
    }
    public static void clearFile() {
        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "rw");
             FileChannel channel = file.getChannel()) {
            FileLock lock = channel.lock();
            try {
                file.setLength(0);
                System.out.println("File " + FILE_PATH + " has been cleared.");
            } finally {
                lock.release();
            }
        } catch (IOException e) {
            System.err.println("Error while clearing the file: " + e.getMessage());
        }
    }

}
