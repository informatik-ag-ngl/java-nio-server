package com.jenkov.nioserver;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>HttpUtilTest.java</strong><br>
 * Created: <strong>16 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public interface IMessageProcessor {

	public void process(Message message, WriteProxy writeProxy);
}