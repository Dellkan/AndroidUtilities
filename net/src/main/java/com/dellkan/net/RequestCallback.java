package com.dellkan.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RequestCallback {
    private Throwable exception;
    private static List<RequestCallback> globalHandlers = new ArrayList<>();
    private List<RequestCallback> localHandlers = new ArrayList<>();
    private boolean isHandler = false;
    private Request request;

    public RequestCallback() {
        this.localHandlers.addAll(globalHandlers);
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    private int responseCode;
    private String rawResponse;
    private JSONObject dataObj;
    private JSONArray dataArray;

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() {
        return rawResponse;
    }

    public JSONObject getObjectResponse() {
        return dataObj != null ? dataObj : new JSONObject();
    }

    public JSONArray getArrayResponse() {
        return dataArray != null ? dataArray : new JSONArray();
    }

    public static void addGlobalHandler(RequestCallback callback) {
        callback.isHandler = true;
        globalHandlers.add(callback);
    }

    public static void removeGlobalHandler(RequestCallback callback) {
        globalHandlers.remove(callback);
    }

    public void addLocalHandler(RequestCallback callback) {
        callback.isHandler = true;
        callback.setRequest(getRequest());
        localHandlers.add(callback);
    }

    public void removeLocalHandler(RequestCallback callback) {
        localHandlers.remove(callback);
    }

    public void clearGlobalHandlers() {
        globalHandlers.clear();
    }

    public void clearLocalHandlers() {
        localHandlers.clear();
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

    /**
     * onStart get called when the request is about to start
     */
    public void onStart() {
        if (!isHandler) {
            for (RequestCallback callback : localHandlers) {
                callback.setRequest(getRequest());
                callback.onStart();
            }
        }
    }

    /**
     * Use to determine whether the request was a success, before retrieving body.
     * @param statusCode ResponseCode from server.
     * @return true if should continue to read response body. False to drop the body like it's hot
     */
    public boolean onStatusCode(int statusCode) {
        this.responseCode = statusCode;

        if (statusCode < 200 || statusCode >= 300) {
            if (BuildConfig.DEBUG) {
                new Exception(String.format("Server response error. %s \nStatusCode: %d", getRequest().getURL().toString(), getResponseCode())).printStackTrace();
            }
        }

        if (!isHandler) {
            for (RequestCallback callback : localHandlers) {
                callback.setRequest(getRequest());
                callback.onStatusCode(statusCode);
            }
        }

        return true;
    }

    /**
     * Your custom parser goes here.
     * Please note that this is run in the worker thread
     * @param inputStream the inputStream representing the response. Remember to close it
     */
    public final String onResponse(InputStream inputStream) {
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

    /**
     * Receives the response as parsed by {@link #onResponse(InputStream)}.
     * Make sense of the server output here, and delegate to onSuccess or onFailure as appropriate
     */
    public void onFinish() {
        if (!isHandler) {
            for (RequestCallback callback : localHandlers) {
                callback.setRequest(getRequest());
                callback.onFinish();
            }
        }

        if (exception == null) {
            String response = getResponse();
            if (response != null && !response.isEmpty()) {
                String token = response.substring(0, 1) + response.substring(response.length() - 1);
                try {
                    Object type = new JSONTokener(token).nextValue();
                    dataObj = type instanceof JSONObject ? new JSONObject(response) : new JSONObject();
                    dataArray = type instanceof JSONArray ? new JSONArray(response) : new JSONArray();
                    if (responseCode >= 200 && responseCode < 300) {
                        onSuccess();
                    } else {
                        onFailure();
                    }
                } catch (JSONException e) {
                    exception = e;
                    e.printStackTrace();
                    onFailure();
                }
            }
        } else {
            onFailure();
        }
    }

    public void onSuccess() {
        if (!isHandler) {
            for (RequestCallback callback : localHandlers) {
                callback.setRequest(getRequest());
                callback.onSuccess();
            }
        }
    }

    public void onFailure() {
        if (!isHandler) {
            for (RequestCallback callback : localHandlers) {
                callback.setRequest(getRequest());
                callback.onFailure();
            }
        }
    }
}
