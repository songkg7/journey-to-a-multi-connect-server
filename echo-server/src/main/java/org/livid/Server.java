package org.livid;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (
                ServerSocketChannel channel = ServerSocketChannel.open();
                Selector selector = Selector.open()
        ) {
            channel.bind(new InetSocketAddress(PORT));
            channel.configureBlocking(false); // non-blocking mode
            LOGGER.info("Server started on port " + PORT);

            channel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(256);

            while (true) {
                selector.select(); // blocking

                // 선택된 키 셋 반복
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        // 새로운 클라이언트 연결 수락
                        accept(channel, selector);
                    } else if (key.isReadable()) {
                        // 클라이언트로부터 데이터 읽기
                        read(key, buffer);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to open server socket channel: " + e.getMessage());
        }
    }

    private static void read(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1) {
            return;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String message = new String(bytes);
        LOGGER.log(Level.INFO, "Received from client: {0}", message);
        if (triggered(message)) {
            LOGGER.info("Client disconnected: " + socketChannel.getRemoteAddress());
            buffer.clear();
            socketChannel.close();
            key.cancel();
        } else {
            LOGGER.log(Level.INFO, "Echo: {0}", message);
            socketChannel.write(ByteBuffer.wrap(bytes));
            buffer.clear();
        }
    }

    private static boolean triggered(String message) {
        return "quit\n".equalsIgnoreCase(message);
    }


    private static void accept(ServerSocketChannel channel, Selector selector) throws IOException {
        SocketChannel clientChannel = channel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        LOGGER.info("Client connected: " + clientChannel.getRemoteAddress());
    }
}
