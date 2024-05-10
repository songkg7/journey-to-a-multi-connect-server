package org.livid.handler;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptHandler implements Handler {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public AcceptHandler(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void handle() {
        // 새로운 클라이언트 연결 수락
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                new EchoHandler(selector, socketChannel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
