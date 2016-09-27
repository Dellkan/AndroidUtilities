package com.dellkan.net.parsers;

import com.dellkan.net.Request;

import java.io.InputStream;

public interface InboundParser {
	public int getResponseCode();
	public String getResponse();

	/**
	 * onStart get called when the request is about to start
	 */
	public void onStart();

	/**
	 * Use to determine whether the request was a success, before retrieving body.
	 * @param statusCode ResponseCode from server.
	 * @return true if should continue to read response body. False to drop the body like it's hot
	 */
	public boolean onStatusCode(int statusCode);

	/**
	 * Receives the response as parsed by {@link #onResponse(InputStream)}.
	 * Make sense of the server output here, and delegate to onSuccess or onFailure as appropriate
	 */
	public void onFinish();
	public void onSuccess();
	public void onFailure();

	/**
	 * Your custom parser goes here.
	 * Please note that this is run in the worker thread
	 * @param inputStream the inputStream representing the response. Remember to close it
	 */
	public String onResponse(InputStream inputStream);
	public Throwable getException();
	public void setException(Throwable e);
	public Request getRequest();
	public void setRequest(Request request);
}
