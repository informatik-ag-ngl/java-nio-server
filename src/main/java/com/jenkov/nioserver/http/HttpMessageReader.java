package com.jenkov.nioserver.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jenkov.nioserver.IMessageReader;
import com.jenkov.nioserver.Message;
import com.jenkov.nioserver.MessageBuffer;
import com.jenkov.nioserver.Socket;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>HttpMessageReader.java</strong><br>
 * Created: <strong>18 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class HttpMessageReader implements IMessageReader {

	private MessageBuffer messageBuffer;

	private List<Message>	completeMessages	= new ArrayList<>();
	private Message			nextMessage;

	@Override
	public void init(MessageBuffer readMessageBuffer) {
		messageBuffer			= readMessageBuffer;
		nextMessage				= messageBuffer.getMessage();
		nextMessage.metaData	= new HttpHeaders();
	}

	@Override
	public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
		socket.read(byteBuffer);
		byteBuffer.flip();

		if (byteBuffer.remaining() == 0) {
			byteBuffer.clear();
			return;
		}

		nextMessage.writeToMessage(byteBuffer);

		int endIndex = HttpUtil.parseHttpRequest(nextMessage.sharedArray,
				nextMessage.offset,
				nextMessage.offset + nextMessage.length,
				(HttpHeaders) nextMessage.metaData);
		if (endIndex != -1) {
			Message message = messageBuffer.getMessage();
			message.metaData = new HttpHeaders();

			message.writePartialMessageToMessage(nextMessage, endIndex);

			completeMessages.add(nextMessage);
			nextMessage = message;
		}
		byteBuffer.clear();
	}

	@Override
	public List<Message> getMessages() { return completeMessages; }
}