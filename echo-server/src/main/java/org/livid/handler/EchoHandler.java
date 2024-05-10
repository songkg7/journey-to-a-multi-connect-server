package org.livid.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class EchoHandler implements Handler {
    private static final Logger LOGGER = Logger.getLogger(EchoHandler.class.getName());
    private static final int READING = 0;
    private static final int SENDING = 1;

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private final ByteBuffer buffer = ByteBuffer.allocate(256);
    int state = READING;

    EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        // Attach a handler to handle when an event occurs in SocketChannel.
        selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        selector.wakeup();
    }

    @Override
    public void handle() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            LOGGER.severe("Failed to handle client: " + ex.getMessage());
        }
    }

    void read() throws IOException {
        int readCount = socketChannel.read(buffer);
        if (readCount > 0) {
            buffer.flip();
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        state = SENDING;
    }

    void send() throws IOException {
        socketChannel.write(buffer);
        LOGGER.info("Echoed: " + new String(buffer.array()));
        buffer.clear();
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READING;
    }}
