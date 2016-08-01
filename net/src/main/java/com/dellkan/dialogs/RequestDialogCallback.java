package com.dellkan.dialogs;

import com.dellkan.net.BuildConfig;
import com.dellkan.net.InboundCallbackParser;
import com.dellkan.net.JSONRequestCallback;
import com.dellkan.net.R;
import com.dellkan.net.Request;

import java.io.InputStream;

/**
 * Identical to {@link JSONRequestCallback} except that it reroutes all callbacks through so you get access to the {@link RequestDialog}
 */
public class RequestDialogCallback implements InboundCallbackParser {
    private RequestDialog dialog;
    private InboundCallbackParser callback = new JSONRequestCallback() {
	    /*
	        We have to make a couple of redirections in here, since the proxy won't automagically
	        route to this' onSuccess and onFailure
	     */
	    @Override
	    public void onSuccess() {
		    super.onSuccess();
		    RequestDialogCallback.this.onSuccess();
	    }

	    @Override
	    public void onFailure() {
		    super.onFailure();
		    RequestDialogCallback.this.onFailure();
	    }
    };

    public RequestDialogCallback() {
    }

    public RequestDialogCallback(RequestDialog dialog) {
        this.dialog = dialog;
    }

    // Setters
    public RequestDialogCallback setDialog(RequestDialog dialog) {
        this.dialog = dialog;
        return this;
    }

    @Override
    public int getResponseCode() {
        return callback.getResponseCode();
    }

	@Override
	public String getResponse() {
		return callback.getResponse();
	}

	// Reroute through original callbacks
    @Override
    public final void onStart() {
        callback.onStart();
        onStart(dialog);
    }

    @Override
    public boolean onStatusCode(int statusCode) {
        return callback.onStatusCode(statusCode);
    }

    @Override
    public final void onFinish() {
        callback.onFinish();
        onFinish(dialog);
    }

    @Override
    public final void onSuccess() {
        callback.onSuccess();
        onSuccess(dialog);
    }

    @Override
    public final void onFailure() {
        callback.onFailure();
        onFailure(dialog);
    }

    @Override
    public String onResponse(InputStream inputStream) {
        return callback.onResponse(inputStream);
    }

    @Override
    public Throwable getException() {
        return callback.getException();
    }

    @Override
    public void setException(Throwable e) {
        callback.setException(e);
    }

    @Override
    public Request getRequest() {
        return callback.getRequest();
    }

    @Override
    public void setRequest(Request request) {
        callback.setRequest(request);
    }

    // Routed callbacks

    /**
     * See {@link #onStart()}
     * @param dialog Requesting dialog. Use it to change content, dismiss, etc
     */
    public void onStart(RequestDialog dialog) {
        dialog.setMessage(R.string.sending);
        dialog.onStart();
    }

    /**
     * See {@link #onFinish()}
     */
    public void onFinish(RequestDialog dialog) {
        dialog.onFinish();
    }

    /**
     * See {@link #onSuccess()}
     */
    public void onSuccess(RequestDialog dialog) {
        dialog.onSuccess();
    }

    /**
     * Default handling is to set a generic error and stop loading
     * @param dialog A reference to the SinglePartRequestDialog
     */
    public void onFailure(RequestDialog dialog) {
        dialog.onFailure();
        if (BuildConfig.DEBUG) {
            if (callback.getException() != null) {
                dialog.setFinished(String.format("%s\n%s", getRequest().getURL().toString(), getException().getMessage()));
            } else {
                dialog.setFinished(String.format("%d\n%s", getResponseCode(), getResponse()));
            }
        } else {
            dialog.setFinished(R.string.error_network);
        }
    }
}
