package com.jenkov.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>Socket.java</strong><br>
 * Created: <strong>16 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class Socket {

	public long socketId;

	public SocketChannel	socketChannel;
	public IMessageReader	messageReader;
	public MessageWriter	messageWriter;

	public boolean endOfStreamReached;

	public Socket(SocketChannel socketChannel) { this.socketChannel = socketChannel; }

	public int read(ByteBuffer byteBuffer) throws IOException {
		int	bytesRead		= socketChannel.read(byteBuffer);
		int	totalBytesRead	= bytesRead;

		while (bytesRead > 0) {
			bytesRead		= socketChannel.read(byteBuffer);
			totalBytesRead	+= bytesRead;
		}
		if (bytesRead == -1) endOfStreamReached = true;

		return totalBytesRead;
	}

	public int write(ByteBuffer byteBuffer) throws IOException {
		int	bytesWritten		= socketChannel.write(byteBuffer);
		int	totalBytesWritten	= bytesWritten;

		while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
			bytesWritten		= socketChannel.write(byteBuffer);
			totalBytesWritten	+= bytesWritten;
		}

		return totalBytesWritten;
	}
}
