package com.jenkov.nioserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>MessageTest.java</strong><br>
 * Created: <strong>18 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class MessageTest {

	@Test
	public void testWriteToMessage() {
		MessageBuffer messageBuffer = new MessageBuffer();

		Message		message		= messageBuffer.getMessage();
		ByteBuffer	byteBuffer	= ByteBuffer.allocate(1024 * 1024);

		fill(byteBuffer, 4096);

		int written = message.writeToMessage(byteBuffer);
		assertEquals(4096, written);
		assertEquals(4096, message.length);
		assertSame(messageBuffer.smallMessageBuffer, message.sharedArray);

		fill(byteBuffer, 124 * 1024);
		written = message.writeToMessage(byteBuffer);
		assertEquals(124 * 1024, written);
		assertEquals(128 * 1024, message.length);
		assertSame(messageBuffer.mediumMessageBuffer, message.sharedArray);

		fill(byteBuffer, (1024 - 128) * 1024);
		written = message.writeToMessage(byteBuffer);
		assertEquals(896 * 1024, written);
		assertEquals(1024 * 1024, message.length);
		assertSame(messageBuffer.largeMessageBuffer, message.sharedArray);

		fill(byteBuffer, 1);
		written = message.writeToMessage(byteBuffer);
		assertEquals(-1, written);
	}

	private void fill(ByteBuffer byteBuffer, int length) {
		byteBuffer.clear();
		for (int i = 0; i < length; i++)
			byteBuffer.put((byte) (i % 128));
		byteBuffer.flip();
	}
}