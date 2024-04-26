package org.livid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            LOGGER.info("Client connected: " + clientSocket.getInetAddress().getHostAddress());
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                LOGGER.log(Level.INFO, "Received from client: {0}", inputLine);
                out.println("Echo: " + inputLine); // Echo back the received message
            }
            LOGGER.info("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            LOGGER.severe("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.severe("Failed to close client socket: " + e.getMessage());
            }
        }
    }
}
