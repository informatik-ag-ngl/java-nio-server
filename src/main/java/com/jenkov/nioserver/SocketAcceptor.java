package com.jenkov.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>SocketAcceptor.java</strong><br>
 * Created: <strong>19 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class SocketAcceptor implements Runnable {

	private int					tcpPort;
	private ServerSocketChannel	serverSocket;

	private Queue<Socket> socketQueue;

	public SocketAcceptor(int tcpPort, Queue<Socket> socketQueue) {
		this.tcpPort		= tcpPort;
		this.socketQueue	= socketQueue;
	}

	public void run() {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(tcpPort));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while (true) {
			try {
				SocketChannel socketChannel = serverSocket.accept();

				System.out.println("Socket accepted: " + socketChannel);

				// TODO: check if the queue can even accept more sockets.
				this.socketQueue.add(new Socket(socketChannel));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}