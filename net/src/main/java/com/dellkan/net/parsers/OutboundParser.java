package com.dellkan.net.parsers;

import com.dellkan.net.Request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public interface OutboundParser {
	/**
	 * Runs before the connection is initiated. Use this to modify the URL, for instance, to put GET parameters into the url
	 * @param request The running request
	 * @param url The base url
	 * @return modified url, i.e with GET parameters
	 */
	public URL alterURL(Request request, URL url);

	/**
	 * Runs right before the connection is established
	 * @param request
	 * @param connection
	 */
	public void preConnect(Request request, HttpURLConnection connection) throws IOException;

	/**
	 * Runs right after the connection is established
	 * @param request
	 * @param connection
	 */
	public void postConnect(Request request, HttpURLConnection connection) throws IOException;
}
