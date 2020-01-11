package com.jenkov.nioserver;

import java.nio.ByteBuffer;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>HttpUtilTest.java</strong><br>
 * Created: <strong>16 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class Message {

	private MessageBuffer messageBuffer;

	public long socketId; // the id of source socket or destination socket, depending on whether is going
							// in or out.

	public byte[]	sharedArray;
	public int		offset;		// offset into sharedArray where this message data starts.
	public int		capacity;	// the size of the section in the sharedArray allocated to this message.
	public int		length;		// the number of bytes used of the allocated section.

	public Object metaData;

	public Message(MessageBuffer messageBuffer) { this.messageBuffer = messageBuffer; }

	/**
	 * Writes data from the ByteBuffer into this message - meaning into the buffer
	 * backing this message.
	 *
	 * @param byteBuffer The ByteBuffer containing the message data to write.
	 * @return
	 */
	public int writeToMessage(ByteBuffer byteBuffer) {
		int remaining = byteBuffer.remaining();

		while (this.length + remaining > capacity)
			if (!this.messageBuffer.expandMessage(this)) return -1;

		int bytesToCopy = Math.min(remaining, capacity - length);
		byteBuffer.get(sharedArray, offset + length, bytesToCopy);
		length += bytesToCopy;

		return bytesToCopy;
	}

	/**
	 * Writes data from the byte array into this message - meaning into the buffer
	 * backing this message.
	 *
	 * @param byteArray The byte array containing the message data to write.
	 * @return
	 */
	public int writeToMessage(byte[] byteArray) { return writeToMessage(byteArray, 0, byteArray.length); }

	/**
	 * Writes data from the byte array into this message - meaning into the buffer
	 * backing this message.
	 *
	 * @param byteArray The byte array containing the message data to write.
	 * @return
	 */
	public int writeToMessage(byte[] byteArray, int offset, int length) {
		int remaining = length;

		while (this.length + remaining > capacity)
			if (!this.messageBuffer.expandMessage(this)) return -1;

		int bytesToCopy = Math.min(remaining, capacity - length);
		System.arraycopy(byteArray, offset, sharedArray, this.offset + this.length, bytesToCopy);
		this.length += bytesToCopy;
		return bytesToCopy;
	}

	/**
	 * In case the buffer backing the nextMessage contains more than one HTTP
	 * message, move all data after the first
	 * message to a new Message object.
	 *
	 * @param message  The message containing the partial message (after the first
	 *                 message).
	 * @param endIndex The end index of the first message in the buffer of the
	 *                 message given as parameter.
	 */
	public void writePartialMessageToMessage(Message message, int endIndex) {
		int	startIndexOfPartialMessage	= message.offset + endIndex;
		int	lengthOfPartialMessage		= message.offset + message.length - endIndex;

		System.arraycopy(message.sharedArray, startIndexOfPartialMessage, sharedArray, offset, lengthOfPartialMessage);
	}

	public int writeToByteBuffer(ByteBuffer byteBuffer) { return 0; }
}