import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);

            BufferedReader serverInput =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOutput =
                    new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInput =
                    new BufferedReader(new InputStreamReader(System.in));

            // Read server prompts (like Enter your name)
            new Thread(() -> {
                try {
                    String message;
                    while ((message = serverInput.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            // Send messages to server
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                serverOutput.println(userMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}