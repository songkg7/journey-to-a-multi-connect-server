package org.livid;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            new Reactor(PORT).run();
        } catch (IOException e) {
            LOGGER.severe("Failed to start server: " + e.getMessage());
        }
    }
}
