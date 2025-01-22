package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class FireFighter {
    public static void main(String[] args) {

        Thread firemanFighter = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9876);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                int secondsToAlarm = 4;                                                             // second to alarm
                for (int i = secondsToAlarm; i > 0; i--) {
                    System.out.println("Seconds to alarm: " + i);
                    Thread.sleep(1000);
                }

                out.println("fire");
                System.out.println("Firefighter sent fire signal!");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("FireFighter process exiting with code 0.");
        });
        firemanFighter.start();
    }
}
