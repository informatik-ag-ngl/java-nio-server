package com.jenkov.nioserver.http;

import com.jenkov.nioserver.IMessageReader;
import com.jenkov.nioserver.IMessageReaderFactory;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>HttpMessageReaderFactory.java</strong><br>
 * Created: <strong>18 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class HttpMessageReaderFactory implements IMessageReaderFactory {

	@Override
	public IMessageReader createMessageReader() { return new HttpMessageReader(); }
}