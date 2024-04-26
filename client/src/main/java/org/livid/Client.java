package org.livid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // client
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            LOGGER.info("Connected to server at " + HOST + ":" + PORT);
            String userInput;
            while (true) {
                System.out.print("Enter message: ");
                userInput = scanner.nextLine();
                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }
                out.println(userInput);
                String response = in.readLine();
                LOGGER.log(Level.INFO, "Server response: {0}", response);
            }
        } catch (UnknownHostException e) {
            LOGGER.severe("Server not found: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.severe("I/O error: " + e.getMessage());
        }
    }
}

