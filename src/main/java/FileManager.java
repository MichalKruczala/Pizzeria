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

    public void createTablesFile(int[] quantityOfTables) {
        try {
            semaphore.acquire();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (int i = 0; i < quantityOfTables.length; i++) {
                    int tableCapacity = i + 1; // Pojemność stołów domyślnie od 1 wzwyż
                    for (int j = 0; j < quantityOfTables[i]; j++) {
                        if (quantityOfTables[i] > 0) {
                            writer.write(String.format("Table{initialCapacity=%d, capacity=%d, isOccupied=false, groups=[]}\n", tableCapacity, tableCapacity));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            } finally {
                semaphore.release();
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
                    tables.add(parseTable(line));
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
    public void writeTablesToFile(List<Table> tables) {
        try {
            semaphore.acquire();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Table table : tables) {
                    writer.write(formatTableForFile(table));
                }
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted: " + e.getMessage());
        }
    }
    private String formatTableForFile(Table table) {
        return String.format("Table{initialCapacity=%d, capacity=%d, isOccupied=%b, groups=%s}\n",
                table.getInitialCapacity(), table.getCapacity(), table.isOccupied(), formatGroups(table.getGroups()));
    }

    private String formatGroups(List<Group> groups) {
        return "[" + groups.stream()
                .map(group -> String.format("model.Group{size=%d}", group.getSize()))
                .collect(Collectors.joining(", ")) + "]";
    }

    private Table parseTable(String line) {
        line = line.substring(line.indexOf('{') + 1, line.lastIndexOf('}'));
        String[] props = line.split(", ");
        int initialCapacity = Integer.parseInt(props[0].split("=")[1]);
        int capacity = Integer.parseInt(props[1].split("=")[1]);
        boolean isOccupied = Boolean.parseBoolean(props[2].split("=")[1]);
        String groupsStr = props[3].split("=")[1];
        groupsStr = groupsStr.substring(1, groupsStr.length() - 1);
        List<Group> groups = Arrays.stream(groupsStr.split(", "))
                .filter(s -> !s.isEmpty())
                .map(s -> new Group(Integer.parseInt(s.replaceAll("\\D+", ""))))
                .collect(Collectors.toList());

        return new Table(initialCapacity, capacity, isOccupied, groups);
    }
}
