package com.dellkan.net.parsers;

import android.support.annotation.WorkerThread;

import com.dellkan.net.Request;

import java.io.InputStream;

public interface InboundParser {
	public int getResponseCode();
	public String getResponse();

	/**
	 * Use to determine whether the request was a success, before retrieving body.
	 * @param statusCode ResponseCode from server.
	 * @return true if should continue to read response body. False to drop the body like it's hot
	 */
	public boolean onStatusCode(int statusCode);

	/**
	 * Your custom parser goes here.
	 * Please note that this is run in the worker thread
	 * @param inputStream the inputStream representing the response. Remember to close it
	 */
	@WorkerThread
	public String onResponse(InputStream inputStream);
	public Throwable getException();
	public void setException(Throwable e);
	public Request getRequest();
	public void setRequest(Request request);
}
