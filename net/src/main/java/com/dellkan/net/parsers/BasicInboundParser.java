package com.dellkan.net.parsers;

import com.dellkan.net.BuildConfig;
import com.dellkan.net.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicInboundParser implements InboundParser {
	private Throwable exception;
	private boolean isHandler = false;
	private Request request;

	private int responseCode;
	private String rawResponse;

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponse() {
		return rawResponse;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable e) {
		this.exception = e;
	}

	/*
		Callbacks
	 */

	@Override
	public boolean onStatusCode(int statusCode) {
		this.responseCode = statusCode;

		if (statusCode < 200 || statusCode >= 300) {
			if (BuildConfig.DEBUG) {
				new Exception(String.format("Server response error. %s \nStatusCode: %d", getRequest().getURL().toString(), getResponseCode())).printStackTrace();
			}
		}

		return true;
	}

	@Override
	public String onResponse(InputStream inputStream) {
		try {
			// Start reading response
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String inputLine;
			StringBuilder responseBuild = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				responseBuild.append(inputLine);
			}
			in.close();

			rawResponse = responseBuild.toString();

			// Get rid of bom
			rawResponse = rawResponse.trim().replace("\uFEFF", "");

			return rawResponse;
		} catch (UnsupportedEncodingException e) {
			exception = e;
			e.printStackTrace();
		} catch (IOException e) {
			exception = e;
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				exception = e;
				e.printStackTrace();
			}
		}
		return "";
	}
}
