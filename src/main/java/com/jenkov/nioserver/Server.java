package com.jenkov.nioserver;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>Server.java</strong><br>
 * Created: <strong>24 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class Server {

	private SocketAcceptor	socketAccepter;
	private SocketProcessor	socketProcessor;

	private int						tcpPort;
	private IMessageReaderFactory	messageReaderFactory;
	private IMessageProcessor		messageProcessor;

	public Server(int tcpPort, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) {
		this.tcpPort				= tcpPort;
		this.messageReaderFactory	= messageReaderFactory;
		this.messageProcessor		= messageProcessor;
	}

	public void start() throws IOException {

		Queue<Socket> socketQueue = new ArrayBlockingQueue<>(1024); // TODO: move 1024 to ServerConfig

		socketAccepter = new SocketAcceptor(tcpPort, socketQueue);

		MessageBuffer	readBuffer	= new MessageBuffer();
		MessageBuffer	writeBuffer	= new MessageBuffer();

		socketProcessor = new SocketProcessor(socketQueue, readBuffer, writeBuffer, this.messageReaderFactory, this.messageProcessor);

		Thread	accepterThread	= new Thread(socketAccepter);
		Thread	processorThread	= new Thread(socketProcessor);

		accepterThread.start();
		processorThread.start();
	}
}