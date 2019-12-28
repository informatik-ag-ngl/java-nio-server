package com.jenkov.nioserver;

import java.util.Queue;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>WriteProxy.java</strong><br>
 * Created: <strong>22 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class WriteProxy {

	private MessageBuffer	messageBuffer;
	private Queue<Message>	writeQueue;

	public WriteProxy(MessageBuffer messageBuffer, Queue<Message> writeQueue) {
		this.messageBuffer	= messageBuffer;
		this.writeQueue		= writeQueue;
	}

	public Message getMessage() { return messageBuffer.getMessage(); }

	public boolean enqueue(Message message) { return writeQueue.offer(message); }
}