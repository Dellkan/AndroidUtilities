package com.dellkan.net;

import android.support.annotation.NonNull;

import com.dellkan.net.parsers.InboundParser;

import java.io.InputStream;

public class RequestCallback<T extends InboundParser> {
    private T mParser;

    public RequestCallback(@NonNull T parser) {
        this.mParser = parser;
    }

    public Request getRequest() {
        return mParser.getRequest();
    }

    public T getParser() {
        return mParser;
    }

    public int getResponseCode() {
        return mParser.getResponseCode();
    }

    public String getResponse() {
        return mParser.getResponse();
    }

    public Throwable getException() {
        return mParser.getException();
    }

    /**
     * onStart get called when the request is about to start
     */
    public void onStart() {

    }

    /**
     * Receives the response as parsed by {@link com.dellkan.net.parsers.InboundParser#onResponse(InputStream)}.
     * Make sense of the server output here, and delegate to onSuccess or onFailure as appropriate
     */
    public void onFinish() {
        // Trigger callbacks
        if (getResponseCode() >= 200 && getResponseCode() < 300 && getException() == null) {
            onSuccess();
        } else {
            onFailure();
        }
    }

    public void onSuccess() {

    }

    public void onFailure() {

    }
}
