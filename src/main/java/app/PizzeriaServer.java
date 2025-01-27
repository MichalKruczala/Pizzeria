package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PizzeriaServer {
    private static final int PORT = 9876;
    private static volatile boolean isFire = false;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Pizzeria server started and listening on port " + PORT);
            while (!isFire) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(new ClientHandler(clientSocket));
                } catch (SocketException e) {
                    if ("Socket closed".equalsIgnoreCase(e.getMessage())) {
                        System.out.println("Server socket manually closed - stopping accept()");
                        break;
                    } else {
                        throw e;
                    }
                }
            }
            System.out.println("Shutting down due to fire alarm.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                executorService.shutdownNow();
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public static void triggerFire() {
        isFire = true;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFireTriggered() {
        return isFire;
    }



    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    if ("fire".equals(clientMessage)) {
                        System.out.println("Fire alarm received from client!");
                        PizzeriaServer.triggerFire();
                    }
                    if (PizzeriaServer.isFireTriggered()) {
                        out.println("evacuation");
                    } else {
                        out.println("order received");
                    }
                }
            } catch (SocketException e) {
                System.out.println("SocketException (Lost connection): " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
