package com.jenkov.nioserver.example;

import java.io.IOException;

import com.jenkov.nioserver.IMessageProcessor;
import com.jenkov.nioserver.Message;
import com.jenkov.nioserver.Server;
import com.jenkov.nioserver.http.HttpMessageReaderFactory;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>Main.java</strong><br>
 * Created: <strong>19 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class Main {

	public static void main(String[] args) throws IOException {

		String httpResponse = "HTTP/1.1 200 OK\r\n" + "Content-Length: 38\r\n" + "Content-Type: text/html\r\n" + "\r\n"
				+ "<html><body>Hello World!</body></html>";

		byte[] httpResponseBytes = httpResponse.getBytes("UTF-8");

		IMessageProcessor messageProcessor = (request, writeProxy) -> {
			System.out.println("Message Received from socket: " + request.socketId);

			Message response = writeProxy.getMessage();
			response.socketId = request.socketId;
			response.writeToMessage(httpResponseBytes);

			writeProxy.enqueue(response);
		};

		Server server = new Server(9999, new HttpMessageReaderFactory(), messageProcessor);

		server.start();
	}
}