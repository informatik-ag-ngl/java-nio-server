package com.jenkov.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.Test;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>SelectorTest.java</strong><br>
 * Created: <strong>21 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class SelectorTest {

    @Test
    public void test() throws IOException {
        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.bind(new InetSocketAddress("localhost", 9999));

        socketChannel.configureBlocking(false);

        SelectionKey key1 = socketChannel.register(selector, SelectionKey.OP_WRITE);
        key1.cancel();

        SelectionKey key2 = socketChannel.register(selector, SelectionKey.OP_WRITE);
        key2.cancel();
    }
}