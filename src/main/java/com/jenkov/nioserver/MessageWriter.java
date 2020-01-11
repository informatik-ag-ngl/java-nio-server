package com.jenkov.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>MessageWriter.java</strong><br>
 * Created: <strong>21 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class MessageWriter {

	private List<Message>	writeQueue	= new ArrayList<>();
	private Message			messageInProgress;
	private int				bytesWritten;

	public void enqueue(Message message) {
		if (messageInProgress == null) {
			messageInProgress	= message;
			bytesWritten		= 0;
		} else writeQueue.add(message);
	}

	public void write(Socket socket, ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put(messageInProgress.sharedArray, messageInProgress.offset + bytesWritten, messageInProgress.length - bytesWritten);
		byteBuffer.flip();

		bytesWritten += socket.write(byteBuffer);
		byteBuffer.clear();

		if (bytesWritten >= messageInProgress.length) {
			if (writeQueue.size() > 0) {
				messageInProgress	= writeQueue.remove(0);
				bytesWritten		= 0;
			} else messageInProgress = null;
			// TODO: unregister from selector
		}
	}

	public boolean isEmpty() { return writeQueue.isEmpty() && messageInProgress == null; }
}