package com.dellkan.net;

public interface RequestCallback {
	public void onStart();
	public boolean onStatusCode(int statusCode);
	public void onFinish();
	public void onSuccess();
	public void onFailure();
}
