import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

    // Store client names and handlers
    private static ConcurrentHashMap<String, ClientHandler> clients =
            new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started...");

            // Thread for server console commands
            new Thread(() -> {
                try {
                    BufferedReader serverInput =
                            new BufferedReader(new InputStreamReader(System.in));
                    String command;

                    while ((command = serverInput.readLine()) != null) {
                        if (command.equalsIgnoreCase("LIST")) {
                            listClients();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // List all connected client names
    public static void listClients() {
        System.out.println("Connected Clients:");
        for (String name : clients.keySet()) {
            System.out.println("- " + name);
        }
    }

    // Client Handler
    static class ClientHandler extends Thread {

        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(
                        socket.getOutputStream(), true);

                // Ask for unique name
                while (true) {
                    output.println("Enter your name:");
                    clientName = input.readLine();

                    if (clientName == null) return;

                    if (!clients.containsKey(clientName)) {
                        clients.put(clientName, this);
                        break;
                    } else {
                        output.println("Name already taken. Try another name.");
                    }
                }

                output.println("Welcome " + clientName);
                System.out.println(clientName + " connected.");

                String message;

                // Server only displays messages
                while ((message = input.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                }

            } catch (IOException e) {
                System.out.println(clientName + " disconnected.");
            } finally {
                try {
                    clients.remove(clientName);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}