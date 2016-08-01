package com.dellkan.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class JSONRequestCallback extends BasicInboundParser {
    private JSONObject dataObj;
    private JSONArray dataArray;

    public JSONObject getObjectResponse() {
        return dataObj != null ? dataObj : new JSONObject();
    }

    public JSONArray getArrayResponse() {
        return dataArray != null ? dataArray : new JSONArray();
    }
    /*
        Callbacks
     */
    public void onFinish() {
        super.onFinish();

        if (getException() == null) {
            String response = getResponse();
            if (response != null && !response.isEmpty()) {
                String token = response.substring(0, 1) + response.substring(response.length() - 1);
                try {
                    Object type = new JSONTokener(token).nextValue();
                    dataObj = type instanceof JSONObject ? new JSONObject(response) : new JSONObject();
                    dataArray = type instanceof JSONArray ? new JSONArray(response) : new JSONArray();
                    if (getResponseCode() >= 200 && getResponseCode() < 300) {
                        onSuccess();
                    } else {
                        onFailure();
                    }
                } catch (JSONException e) {
                    setException(e);
                    e.printStackTrace();
                    onFailure();
                }
            }
        } else {
            onFailure();
        }
    }

    public void onSuccess() {

    }

    public void onFailure() {

    }
}
