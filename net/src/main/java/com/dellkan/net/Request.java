package com.dellkan.net;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
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
    public static final String utf = "UTF-8";

    private URL url;
    private @NonNull Method method = Method.GET;
    private @Nullable RequestCallback callback;

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

    public void setCallback(@Nullable RequestCallback callback) {
        this.callback = callback;
        if (callback != null) {
            callback.setRequest(this);
        }
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
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        long startTime = new Date().getTime();
        try {
            URL url = getURL();
            // If we're dealing with a GET with parameters, we need to deal with those parameters..
            // NOW, before connecting, or even setting up the connection
            if (this.method.equals(Method.GET) && this.params != null) {
                Uri.Builder builder = Uri.parse(url.toString()).buildUpon();
                for (Map.Entry<String, Object> entry : this.params.entrySet()) {
                    if (entry.getValue() == null) {
                        builder.appendQueryParameter(entry.getKey(), "null");
                    } else {
                        builder.appendQueryParameter(entry.getKey(), entry.getValue().toString());
                    }
                }
                url = new URL(builder.build().toString());
            }
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

            if (this.params != null && this.method.equals(Method.POST)) {
                // Node.js servers crashes to high hell if we set content-type without sending content.
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
            }

            // Connect
            connection.connect();

            // Start output (streaming, post-connect, for POST variables that won't affect the connect URL)
            if (this.params != null) {
                if (method.equals(Method.POST)) {
                    writeJSON(connection.getOutputStream(), this.params);
                }
            }

            // Start input
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                responseCode = connection.getResponseCode();
            }
            if (callback != null) {
                shouldReadResponse = callback.onStatusCode(responseCode);
                if (shouldReadResponse) {
                    if (responseCode >= 400) {
                        response = callback.onResponse(connection.getErrorStream());
                    } else {
                        response = callback.onResponse(connection.getInputStream());
                    }
                }
            }

            // Close connection
            connection.disconnect();
        } catch (IOException e) {
            if (callback != null) {
                callback.setException(e);
            }
            e.printStackTrace();
            // NewRelic.noticeNetworkFailure(url.toString(), method.name(), startTime, new DateTime().getMillis(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callback != null) {
            callback.onFinish();
        }
    }

    private static void writeJSON(OutputStream outputStream, Map<String, Object> data) {
        try {
            outputStream.write('{');
            Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, Object> value = iterator.next();
                outputStream.write(escape(value.getKey()));
                outputStream.write(": ".getBytes(utf));
                writeJSONValue(outputStream, value.getValue());
                if (iterator.hasNext()) {
                    outputStream.write(", ".getBytes(utf));
                }
            }
            outputStream.write('}');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeJSONValue(OutputStream outputStream, Object value) {
        try {
            if (value instanceof Map) {
                //noinspection unchecked
                writeJSON(outputStream, (Map<String, Object>) value);
            } else if (value instanceof List) {
                outputStream.write('[');
                //noinspection unchecked
                ListIterator<Object> list = ((List<Object>) value).listIterator();
                while(list.hasNext()) {
                    writeJSONValue(outputStream, list.next());
                    if (list.hasNext()) {
                        outputStream.write(", ".getBytes(utf));
                    }
                }
                outputStream.write(']');
            } else if (value instanceof Uri) {
                writeFileToJSON(outputStream, (Uri) value);
            } else if (value instanceof org.json.JSONObject) {
                outputStream.write(value.toString().getBytes(utf));
            } else if (value instanceof Boolean) {
                outputStream.write(((Boolean) value ? "true" : "false").getBytes(utf));
            } else if (value instanceof Long) {
                outputStream.write((((Number) value).longValue() + "").getBytes(utf));
            } else if (value instanceof Double) {
                outputStream.write((((Number) value).doubleValue() + "").getBytes(utf));
            } else if (value instanceof Float) {
                outputStream.write((((Number) value).floatValue() + "").getBytes(utf));
            } else if (value instanceof Integer) {
                outputStream.write((((Number) value).intValue() + "").getBytes(utf));
            } else {
                outputStream.write(escape(value.toString()));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            try {
                outputStream.write(escape("null"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void writeFileToJSON(OutputStream outputStream, Uri uri) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(uri.getPath()));
            byte[] buffer = new byte[8192];
            int bytesRead;
            int bytesInStream = 0;
            int bytesBeforeFlush = 1024 * 512;
            Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream, Base64.NO_WRAP);

            // Add Mime
            outputStream.write('"');
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                if (type != null && !type.isEmpty()) {
                    //outputStream.write(("data:" + type + ";base64,").getBytes(utf));
                }
            }

            // Start writing
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                base64OutputStream.write(buffer, 0, bytesRead);
                bytesInStream += buffer.length;
                if (bytesInStream > bytesBeforeFlush) {
                    outputStream.flush();
                    bytesInStream = 0;
                }
            }
            outputStream.write('"');
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Courtesy of Simple-JSON: https://goo.gl/XoW8RF
    // Changed a bit to suit our needs in this class.
    static byte[] escape(String string) {
        try {
            // If it's null, just return prematurely.
            if (string == null) {
                return "null".getBytes(utf);
            }

            // Create a string builder to generate the escaped string.
            StringBuilder sb = new StringBuilder(128);

            // Surround with quotations.
            sb.append('"');

            int length = string.length(), pos = -1;
            while (++pos < length) {
                char ch = string.charAt(pos);
                switch (ch) {
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '\\':
                        sb.append("\\\\");
                        break;
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    default:
                        // Reference: https://www.unicode.org/versions/Unicode5.1.0/
                        if ((ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                            String intString = Integer.toHexString(ch);
                            sb.append("\\u");
                            int intLength = 4 - intString.length();
                            for (int zero = 0; zero < intLength; zero++) {
                                sb.append('0');
                            }
                            sb.append(intString.toUpperCase(Locale.US));
                        } else {
                            sb.append(ch);
                        }
                        break;
                }
            }

            // Surround with quotations.
            sb.append('"');


            return sb.toString().getBytes(utf);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "null".getBytes();
    }

    public static Request newInstance(@NonNull URL url, @Nullable Map<String, Object> params, @NonNull RequestCallback callback) {
        return newInstance(url, Method.POST, params, callback);
    }

    public static Request newInstance(@NonNull String url, @Nullable Map<String, Object> params, @NonNull RequestCallback callback) {
        return newInstance(url, Method.POST, params, callback);
    }

    public static Request newInstance(@NonNull URL url, @NonNull Method method, @Nullable Map<String, Object> params, @NonNull RequestCallback callback) {
        Request request = new Request(url, method);

        request.setParameters(params);
        request.setCallback(callback);

        request.start();

        return request;
    }

    public static Request newInstance(@NonNull String url, @NonNull Method method, @Nullable Map<String, Object> params, @NonNull RequestCallback callback) {
        try {
            return newInstance(new URL(url), method, params, callback);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
