package com.jenkov.nioserver;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>ISocketIdListener.java</strong><br>
 * Created: <strong>03.01.2020</strong><br>
 * 
 * @author Kai S. K. Engelbart
 */
public interface ISocketIdListener {

	/**
	 * Is invoked when a new {@link Socket} is registered by the server
	 * 
	 * @param socketId the ID of the newly registered socket
	 */
	void socketRegistered(long socketId);

	/**
	 * Is invoked when a {@link Socket} is cancelled by the server
	 * 
	 * @param socketId the ID of the cancelled socket
	 */
	void socketCancelled(long socketId);
}
