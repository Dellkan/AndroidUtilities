package com.dellkan.net.parsers.json;

import com.dellkan.net.parsers.BasicInboundParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class JSONInboundParser extends BasicInboundParser {
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

    @Override
    public String onResponse(InputStream inputStream) {
        String response = super.onResponse(inputStream);

        if (getException() == null) {
            if (response != null && !response.isEmpty()) {
                String token = response.substring(0, 1) + response.substring(response.length() - 1);
                try {
                    Object type = new JSONTokener(token).nextValue();
                    dataObj = type instanceof JSONObject ? new JSONObject(response) : new JSONObject();
                    dataArray = type instanceof JSONArray ? new JSONArray(response) : new JSONArray();
                } catch (JSONException e) {
                    setException(e);
                    e.printStackTrace();
                }
            }
        }

        return response;
    }
}
