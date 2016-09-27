package com.dellkan.net;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dellkan.net.parsers.InboundParser;
import com.dellkan.net.parsers.OutboundParser;
import com.dellkan.net.parsers.json.JSONOutboundParser;
import com.dellkan.net.parsers.json.JSONInboundParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic request class for sending/receiving data.
 * It is designed to work with {@link com.dellkan.dialogs.RequestDialog RequestDialog's} in order to show progress to the user,
 * however, it also works standalone.
 *
 * It assumes all outgoing and ingoing data is in json format, and should be read/written as such.
 */
public class Request extends AsyncTask<Void, String, Void> {
    public enum Method {GET, POST}

    private URL url;
    private @NonNull Method method = Method.GET;
    private @NonNull OutboundParser outboundParser = new JSONOutboundParser();
    private @Nullable InboundParser inboundParser;

    private @Nullable Map<String, String> headers;
    private @Nullable Map<String, Object> params;

    private boolean shouldReadResponse = true;
    private int responseCode = -1;
    private String response;

    public Request(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.method = Method.GET;
    }

    public Request(URL url) {
        this.url = url;
        this.method = Method.GET;
    }

    public Request(String url, @NonNull Method method) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.method = method;
    }

    public Request(URL url, @NonNull Method method) {
        this.url = url;
        this.method = method;
    }

    public void setInboundParser(@Nullable InboundParser inboundParser) {
        this.inboundParser = inboundParser;
        if (inboundParser != null) {
            inboundParser.setRequest(this);
        }
    }

    public void setOutboundParser(@NonNull OutboundParser outboundParser) {
        this.outboundParser = outboundParser;
    }

    public void addHeader(String param, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(param, value);
    }

    public void addParameter(String param, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(param, value);
    }

    public void setParameters(@Nullable Map<String, Object> params) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        if (params != null && params.size() > 0) {
            this.params.putAll(params);
        }
    }

    public URL getURL() {
        return this.url;
    }

    public void start() {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onPreExecute() {
        if (inboundParser != null) {
            inboundParser.onStart();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        long startTime = new Date().getTime();
        try {
            URL url = getURL();
            // If we're dealing with a GET with parameters, we need to deal with those parameters..
            // NOW, before connecting, or even setting up the connection
            url = outboundParser.alterURL(this, url);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(1000 * 30); // Wait max 15 seconds to connect before giving up
            connection.setReadTimeout(1000 * 30); // Wait max 15 seconds to download content before giving up

            // HTTP connection reuse which was buggy pre-froyo
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }

            // Set header stuff (content type)
            connection.setRequestProperty("Accept", "application/json");

            // Start output)
            if (this.headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            outboundParser.preConnect(this, connection);

            // Connect
            connection.connect();

            // Start output (streaming, post-connect, for POST variables that won't affect the connect URL)
            outboundParser.postConnect(this, connection);

            // Start input
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                responseCode = connection.getResponseCode();
            }
            if (inboundParser != null) {
                shouldReadResponse = inboundParser.onStatusCode(responseCode);
                if (shouldReadResponse) {
                    if (responseCode >= 400) {
                        response = inboundParser.onResponse(connection.getErrorStream());
                    } else {
                        response = inboundParser.onResponse(connection.getInputStream());
                    }
                }
            }

            // Close connection
            connection.disconnect();
        } catch (IOException e) {
            if (inboundParser != null) {
                inboundParser.setException(e);
            }
            e.printStackTrace();
            // NewRelic.noticeNetworkFailure(url.toString(), method.name(), startTime, new DateTime().getMillis(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (inboundParser != null) {
            inboundParser.onFinish();
        }
    }

    public Method getMethod() {
        return this.method;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public static Request newInstance(@NonNull URL url, @Nullable Map<String, Object> params, @NonNull JSONInboundParser callback) {
        return newInstance(url, Method.POST, params, callback);
    }

    public static Request newInstance(@NonNull String url, @Nullable Map<String, Object> params, @NonNull JSONInboundParser callback) {
        return newInstance(url, Method.POST, params, callback);
    }

    public static Request newInstance(@NonNull URL url, @NonNull Method method, @Nullable Map<String, Object> params, @NonNull JSONInboundParser callback) {
        Request request = new Request(url, method);

        request.setParameters(params);
        request.setInboundParser(callback);

        request.start();

        return request;
    }

    public static Request newInstance(@NonNull String url, @NonNull Method method, @Nullable Map<String, Object> params, @NonNull JSONInboundParser callback) {
        try {
            return newInstance(new URL(url), method, params, callback);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
