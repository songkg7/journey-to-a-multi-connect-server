package org.livid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Server is running on port " + PORT);

            // Accept multiple clients sequentially
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            LOGGER.severe("Could not listen on port " + PORT + ": " + e.getMessage());
        }
    }
}
