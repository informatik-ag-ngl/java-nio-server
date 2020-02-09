package com.jenkov.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>SocketProcessor.java</strong><br>
 * Created: <strong>16 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class SocketProcessor implements Runnable {

	private Queue<Socket> inboundSocketQueue;

	private MessageBuffer	readMessageBuffer;	// TODO: Not used now - but perhaps will be later - to check for space in the
												// buffer before reading from sockets
	@SuppressWarnings("unused")
	private MessageBuffer	writeMessageBuffer;	// TODO: Not used now - but perhaps will be later - to check for space in the
												// buffer before reading from sockets (space for more to write?)

	private IMessageReaderFactory messageReaderFactory;

	private Queue<Message> outboundMessageQueue = new LinkedList<>(); // TODO: use a better / faster queue.

	private Map<Long, Socket> socketMap = new HashMap<>();

	private ByteBuffer	readByteBuffer	= ByteBuffer.allocate(1024 * 1024);
	private ByteBuffer	writeByteBuffer	= ByteBuffer.allocate(1024 * 1024);
	private Selector	readSelector;
	private Selector	writeSelector;

	private IMessageProcessor	messageProcessor;
	private WriteProxy			writeProxy;

	private long nextSocketId = 16 * 1024; // start incoming socket ids from 16K - reserve bottom ids for pre-defined
											// sockets (servers).

	private Set<Socket>	emptyToNonEmptySockets	= new HashSet<>();
	private Set<Socket>	nonEmptyToEmptySockets	= new HashSet<>();

	private Set<ISocketIdListener> socketIdListeners = new HashSet<>();

	public SocketProcessor(Queue<Socket> inboundSocketQueue, MessageBuffer readMessageBuffer, MessageBuffer writeMessageBuffer,
			IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) throws IOException {
		this.inboundSocketQueue = inboundSocketQueue;

		this.readMessageBuffer	= readMessageBuffer;
		this.writeMessageBuffer	= writeMessageBuffer;
		writeProxy				= new WriteProxy(writeMessageBuffer, outboundMessageQueue);

		this.messageReaderFactory = messageReaderFactory;

		this.messageProcessor = messageProcessor;

		readSelector	= Selector.open();
		writeSelector	= Selector.open();
	}

	@Override
	public void run() {
		while (true) {
			try {
				executeCycle();
				Thread.sleep(100);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void executeCycle() throws IOException {
		takeNewSockets();
		readFromSockets();
		writeToSockets();
	}

	public void takeNewSockets() throws IOException {
		Socket newSocket = inboundSocketQueue.poll();

		while (newSocket != null) {
			newSocket.socketId = nextSocketId++;
			newSocket.socketChannel.configureBlocking(false);

			newSocket.messageReader = messageReaderFactory.createMessageReader();
			newSocket.messageReader.init(readMessageBuffer);

			newSocket.messageWriter = new MessageWriter();

			socketMap.put(newSocket.socketId, newSocket);
			socketIdListeners.forEach(l -> l.socketRegistered(nextSocketId - 1));

			SelectionKey key = newSocket.socketChannel.register(readSelector, SelectionKey.OP_READ);
			key.attach(newSocket);

			newSocket = inboundSocketQueue.poll();
		}
	}

	public void readFromSockets() throws IOException {
		int readReady = readSelector.selectNow();

		if (readReady > 0) {
			Set<SelectionKey>		selectedKeys	= readSelector.selectedKeys();
			Iterator<SelectionKey>	keyIterator		= selectedKeys.iterator();

			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				readFromSocket(key);
				keyIterator.remove();
			}
			selectedKeys.clear();
		}
	}

	public void registerSocketIdListener(ISocketIdListener listener) {
		socketIdListeners.add(listener);
	}

	private void readFromSocket(SelectionKey key) throws IOException {
		Socket socket = (Socket) key.attachment();
		boolean	cancelled	= false;

		try {
			socket.messageReader.read(socket, readByteBuffer);

			List<Message> fullMessages = socket.messageReader.getMessages();
			if (fullMessages.size() > 0) {
				for (Message message : fullMessages) {
					message.socketId = socket.socketId;
					// the message processor will eventually push outgoing messages into an
					// IMessageWriter for this socket.
					messageProcessor.process(message, writeProxy);
				}
				fullMessages.clear();
			}
		} catch (IOException e) {
			cancelled = true;
		} finally {
			if (cancelled || socket.endOfStreamReached) {
				System.out.println("Socket closed: " + socket.socketId);
				socketMap.remove(socket.socketId);
				socketIdListeners.forEach(l -> l.socketCancelled(socket.socketId));
				key.attach(null);
				key.cancel();
				key.channel().close();
			}
		}
	}

	public void writeToSockets() throws IOException {

		// Take all new messages from outboundMessageQueue
		takeNewOutboundMessages();

		// Cancel all sockets which have no more data to write.
		cancelEmptySockets();

		// Register all sockets that *have* data and which are not yet registered.
		registerNonEmptySockets();

		// Select from the Selector.
		int writeReady = writeSelector.selectNow();

		if (writeReady > 0) {
			Set<SelectionKey>		selectionKeys	= writeSelector.selectedKeys();
			Iterator<SelectionKey>	keyIterator		= selectionKeys.iterator();

			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();

				Socket socket = (Socket) key.attachment();

				socket.messageWriter.write(socket, writeByteBuffer);

				if (socket.messageWriter.isEmpty()) { nonEmptyToEmptySockets.add(socket); }

				keyIterator.remove();
			}
			selectionKeys.clear();
		}
	}

	private void registerNonEmptySockets() throws ClosedChannelException {
		for (Socket socket : emptyToNonEmptySockets)
			socket.socketChannel.register(writeSelector, SelectionKey.OP_WRITE, socket);
		emptyToNonEmptySockets.clear();
	}

	private void cancelEmptySockets() {
		for (Socket socket : nonEmptyToEmptySockets) {
			SelectionKey key = socket.socketChannel.keyFor(writeSelector);
			key.cancel();
		}
		nonEmptyToEmptySockets.clear();
	}

	private void takeNewOutboundMessages() {
		Message outMessage = outboundMessageQueue.poll();
		while (outMessage != null) {
			Socket socket = socketMap.get(outMessage.socketId);

			if (socket != null) {
				MessageWriter messageWriter = socket.messageWriter;
				if (messageWriter.isEmpty()) {
					messageWriter.enqueue(outMessage);
					nonEmptyToEmptySockets.remove(socket);
					emptyToNonEmptySockets.add(socket); // not necessary if removed from nonEmptyToEmptySockets in prev. statement.
				} else messageWriter.enqueue(outMessage);
			}

			outMessage = outboundMessageQueue.poll();
		}
	}
}