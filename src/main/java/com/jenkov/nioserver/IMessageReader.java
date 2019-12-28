package com.jenkov.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>HttpUtilTest.java</strong><br>
 * Created: <strong>16 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public interface IMessageReader {

	public void init(MessageBuffer readMessageBuffer);

	public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

	public List<Message> getMessages();
}