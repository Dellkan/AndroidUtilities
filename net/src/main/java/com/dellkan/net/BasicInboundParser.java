package com.dellkan.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicInboundParser implements InboundCallbackParser {
	private Throwable exception;
	private static List<InboundCallbackParser> globalHandlers = new ArrayList<>();
	private List<InboundCallbackParser> localHandlers = new ArrayList<>();
	private boolean isHandler = false;
	private Request request;

	private int responseCode;
	private String rawResponse;

	public BasicInboundParser() {
		this.localHandlers.addAll(globalHandlers);
	}

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

	public static void addGlobalHandler(InboundCallbackParser callback) {
		globalHandlers.add(callback);
	}

	public static void removeGlobalHandler(InboundCallbackParser callback) {
		globalHandlers.remove(callback);
	}

	public void clearGlobalHandlers() {
		globalHandlers.clear();
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
	public void onStart() {
		if (!isHandler) {
			for (InboundCallbackParser callback : localHandlers) {
				callback.setRequest(getRequest());
				callback.onStart();
			}
		}
	}

	@Override
	public boolean onStatusCode(int statusCode) {
		this.responseCode = statusCode;

		if (statusCode < 200 || statusCode >= 300) {
			if (BuildConfig.DEBUG) {
				new Exception(String.format("Server response error. %s \nStatusCode: %d", getRequest().getURL().toString(), getResponseCode())).printStackTrace();
			}
		}

		if (!isHandler) {
			for (InboundCallbackParser callback : localHandlers) {
				callback.setRequest(getRequest());
				callback.onStatusCode(statusCode);
			}
		}

		return true;
	}

	@Override
	public void onFinish() {
		if (!isHandler) {
			for (InboundCallbackParser callback : localHandlers) {
				callback.setRequest(getRequest());
				callback.onFinish();
			}
		}
	}

	@Override
	public void onSuccess() {
		if (!isHandler) {
			for (InboundCallbackParser callback : localHandlers) {
				callback.setRequest(getRequest());
				callback.onSuccess();
			}
		}
	}

	@Override
	public void onFailure() {
		if (!isHandler) {
			for (InboundCallbackParser callback : localHandlers) {
				callback.setRequest(getRequest());
				callback.onFailure();
			}
		}
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
