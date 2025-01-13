import model.Group;
import model.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class FileManager {
    private static final String FILE_PATH = "pizzeria_tables.txt";
    private final Semaphore semaphore = new Semaphore(1);

    public void createTablesFile(int[] capacities) {
        try {
            semaphore.acquire();  // Acquire the semaphore before writing
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (int capacity = 1; capacity <= capacities.length; capacity++) {
                    for (int i = 0; i < capacities[capacity - 1]; i++) {
                        writer.write("model.Table{capacity=" + capacity + ", isOccupied=false, groups=[]}\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            } finally {
                semaphore.release();  // Release the semaphore after writing
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted: " + e.getMessage());
        }
    }

    public List<Table> readTablesFromFile() {
        List<Table> tables = new ArrayList<>();
        try {
            semaphore.acquire();
            try {
                List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
                for (String line : lines) {
                    Table table = parseTable(line);
                    tables.add(table);
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted: " + e.getMessage());
        }
        return tables;
    }

    private Table parseTable(String line) {
        line = line.substring(line.indexOf('{') + 1, line.lastIndexOf('}'));
        String[] props = line.split(", ");
        int capacity = Integer.parseInt(props[0].split("=")[1]);
        boolean isOccupied = Boolean.parseBoolean(props[1].split("=")[1]);
        String groupsStr = props[2].split("=")[1];
        groupsStr = groupsStr.substring(1, groupsStr.length() - 1);
        List<Group> groups = Arrays.asList(groupsStr.isEmpty() ? new String[0] : groupsStr.split(", "))
                .stream()
                .map(s -> new Group(Integer.parseInt(s.trim())))
                .collect(Collectors.toList());

        return new Table(capacity, isOccupied, groups);
    }
}

