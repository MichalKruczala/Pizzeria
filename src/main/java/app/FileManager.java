package app;

import model.Group;
import model.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileManager {
    private static final String FILE_PATH = "pizzeria_tables.txt";
    private final Semaphore semaphore = new Semaphore(1);

    public void createTablesToFile(int[] quantityOfTables) {
        try {
            semaphore.acquire();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (int i = 0; i < quantityOfTables.length; i++) {
                    int tableCapacity = i + 1;
                    for (int j = 0; j < quantityOfTables[i]; j++) {
                        if (quantityOfTables[i] > 0) {
                            writer.write(String.format("Table{initialCapacity=%d, capacity=%d, isOccupied=false, groups=[]}\n",
                                    tableCapacity, tableCapacity));
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
            GUI.printMessage("Thread was interrupted: " + e.getMessage());
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
                GUI.printMessage("Error reading file: " + e.getMessage());
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            GUI.printMessage("Thread was interrupted: " + e.getMessage());
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
                GUI.printMessage("Error writing to file: " + e.getMessage());
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            GUI.printMessage("Thread was interrupted: " + e.getMessage());
        }
    }

    private String formatTableForFile(Table table) {
        return String.format(
                "Table{initialCapacity=%d, capacity=%d, isOccupied=%b, groups=%s}\n",
                table.getInitialCapacity(),
                table.getCapacity(),
                table.isOccupied(),
                formatGroups(table.getGroups())
        );
    }

    private String formatGroups(List<Group> groups) {
        return "[" + groups.stream()
                .map(group -> String.format(
                        "model.Group{size=%d, serviceTime=%s, userThreads=%s}",
                        group.getSize(),
                        group.getServiceTime(),
                        group.getUserThreadIds().toString()
                ))
                .collect(Collectors.joining(", ")) + "]";
    }
    public Table parseTable(String line) {
        line = line.trim();

        int initialCapacity = Integer.parseInt(
                line.replaceAll(".*initialCapacity=(\\d+).*", "$1")
        );
        int capacity = Integer.parseInt(
                line.replaceAll(".*capacity=(\\d+).*", "$1")
        );
        boolean isOccupied = Boolean.parseBoolean(
                line.replaceAll(".*isOccupied=(true|false).*", "$1")
        );

        List<Group> groups = new ArrayList<>();
        Pattern groupPattern = Pattern.compile(
                "model\\.Group\\{size=(\\d+), serviceTime=([\\d:]+), userThreads=\\[(.*?)\\]\\}"
        );
        Matcher matcher = groupPattern.matcher(line);

        while (matcher.find()) {
            int size = Integer.parseInt(matcher.group(1));
            String serviceTime = matcher.group(2);
            String userThreadsStr = matcher.group(3).trim();
            List<Long> tidList = new ArrayList<>();
            if (!userThreadsStr.isEmpty()) {
                String[] splitTids = userThreadsStr.split(",");
                for (String tid : splitTids) {
                    tidList.add(Long.parseLong(tid.trim()));
                }
            }
            Group group = new Group(size, serviceTime, tidList);
            groups.add(group);
        }
        return new Table(initialCapacity, capacity, isOccupied, groups);
    }
}
