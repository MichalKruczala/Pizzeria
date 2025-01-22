package app;

import model.Group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Guests {
    public static void main(String[] args) throws IOException {

        Thread guestsThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9876);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                boolean[] isFireAlarmTriggered = {false};

                Thread groupAdderThread = new Thread(() -> {
                    try {
                        while (!isFireAlarmTriggered[0]) {
                            BlockingQueue<Group> queue = GroupFileManager.readGroupsFromFile();
                            if (queue.size() < 6) {
                                Group group = new Group(Group.getRandomGroupSize(3));
                                queue.put(group);
                                GroupFileManager.writeQueueToFile(queue);
                                System.out.println("   Added group: " + group);
                                queue.forEach(g -> System.out.println(Group.toStringWithoutThreads(g)));
                                Thread.sleep(1000);
                            } else {
                                System.out.println("   Queue is Full    ");
                                Thread.sleep(1000);
                            }

                        }
                    } catch (InterruptedException e) {
                        System.out.println("Group adder thread interrupted.");
                    }finally {
                        GroupFileManager.clearFile();
                    }
                });


                Thread serverListenerThread = new Thread(() -> {
                    try {
                        String response;
                        while ((response = in.readLine()) != null) {
                            System.out.println(response);
                            if (response.equals("evacuation")) {
                                isFireAlarmTriggered[0] = true;
                                break;
                            }
                        }
                        System.out.println("Server closed the connection. Guests are not coming...");
                        isFireAlarmTriggered[0] = true;
                    } catch (IOException e) {
                        System.out.println("Error in server listener thread: " + e.getMessage());
                    }
                });
                groupAdderThread.start();
                serverListenerThread.start();
                groupAdderThread.join();
                serverListenerThread.join();
                System.out.println("Guests process exiting with code 0.");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        guestsThread.start();
    }
}
