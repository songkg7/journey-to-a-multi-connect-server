package org.livid;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // server
        try (
                ServerSocket serverSocket = new ServerSocket(PORT);
                ExecutorService executor = Executors.newFixedThreadPool(10)
        ) {
            LOGGER.info("Server is running on port " + PORT);

            // Accept multiple clients sequentially
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            LOGGER.severe("Could not listen on port " + PORT + ": " + e.getMessage());
        }
    }
}
