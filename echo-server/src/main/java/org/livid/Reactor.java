package org.livid;

import org.livid.handler.AcceptHandler;
import org.livid.handler.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.logging.Logger;

public class Reactor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Reactor.class.getName());
    private final Selector selector;

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        LOGGER.info("Server started on port " + port);
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(selector, serverSocketChannel.validOps());

        selectionKey.attach(new AcceptHandler(selector, serverSocketChannel));
    }

    @Override
    public void run() {
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey selectionKey : selected) {
                    dispatch(selectionKey);
                }
                selected.clear();
            }
        } catch (IOException ex) {
            LOGGER.severe("Failed to start server: " + ex.getMessage());
        }
    }

    void dispatch(SelectionKey selectionKey) {
        Handler handler = (Handler) selectionKey.attachment();
        handler.handle();
    }
}
