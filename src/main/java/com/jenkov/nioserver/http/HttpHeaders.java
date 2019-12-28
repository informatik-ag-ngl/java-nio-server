package com.jenkov.nioserver.http;

/**
 * Project: <strong>java-nio-server</strong><br>
 * File: <strong>HttpHeaders.java</strong><br>
 * Created: <strong>19 Oct 2015</strong><br>
 *
 * @author jjenkov
 */
public class HttpHeaders {

	public static int	HTTP_METHOD_GET		= 1;
	public static int	HTTP_METHOD_POST	= 2;
	public static int	HTTP_METHOD_PUT		= 3;
	public static int	HTTP_METHOD_HEAD	= 4;
	public static int	HTTP_METHOD_DELETE	= 5;

	public int httpMethod = 0;

	public int	hostStartIndex	= 0;
	public int	hostEndIndex	= 0;

	public int contentLength = 0;

	public int	bodyStartIndex	= 0;
	public int	bodyEndIndex	= 0;
}