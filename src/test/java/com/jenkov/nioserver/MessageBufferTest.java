package com.jenkov.nioserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>MessageBufferTest.java</strong><br>
 * Created: <strong>18 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class MessageBufferTest {

	@Test
	public void testGetMessage() {

		MessageBuffer messageBuffer = new MessageBuffer();

		Message message = messageBuffer.getMessage();

		assertNotNull(message);
		assertEquals(0, message.offset);
		assertEquals(0, message.length);
		assertEquals(4 * 1024, message.capacity);

		Message message2 = messageBuffer.getMessage();

		assertNotNull(message2);
		assertEquals(4096, message2.offset);
		assertEquals(0, message2.length);
		assertEquals(4 * 1024, message2.capacity);

		// TODO: test what happens if the small buffer space is depleted of messages.
	}

	@Test
	public void testExpandMessage() {
		MessageBuffer messageBuffer = new MessageBuffer();

		Message message = messageBuffer.getMessage();

		byte[] smallSharedArray = message.sharedArray;

		assertNotNull(message);
		assertEquals(0, message.offset);
		assertEquals(0, message.length);
		assertEquals(4 * 1024, message.capacity);

		messageBuffer.expandMessage(message);
		assertEquals(0, message.offset);
		assertEquals(0, message.length);
		assertEquals(128 * 1024, message.capacity);

		byte[] mediumSharedArray = message.sharedArray;
		assertNotSame(smallSharedArray, mediumSharedArray);

		messageBuffer.expandMessage(message);
		assertEquals(0, message.offset);
		assertEquals(0, message.length);
		assertEquals(1024 * 1024, message.capacity);

		byte[] largeSharedArray = message.sharedArray;
		assertNotSame(smallSharedArray, largeSharedArray);
		assertNotSame(mediumSharedArray, largeSharedArray);

		// next expansion should not be possible.
		assertFalse(messageBuffer.expandMessage(message));
		assertEquals(0, message.offset);
		assertEquals(0, message.length);
		assertEquals(1024 * 1024, message.capacity);
		assertSame(message.sharedArray, largeSharedArray);
	}
}