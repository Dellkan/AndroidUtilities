package com.dellkan.net;

import android.net.Uri;
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

public class JSONOutboundParser implements OutboundCallbackParser {
	public static final String utf = "UTF-8";
	@Override
	public URL alterURL(Request request, URL url) {
		if (request.getMethod().equals(Request.Method.GET) && request.getParams() != null) {
			Uri.Builder builder = Uri.parse(url.toString()).buildUpon();
			for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
				if (entry.getValue() == null) {
					builder.appendQueryParameter(entry.getKey(), "null");
				} else {
					builder.appendQueryParameter(entry.getKey(), entry.getValue().toString());
				}
			}
			try {
				url = new URL(builder.build().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}


	@Override
	public void preConnect(Request request, HttpURLConnection connection) throws IOException {
		if (request.getParams() != null && request.getMethod().equals(Request.Method.POST)) {
			// Node.js servers crashes to high hell if we set content-type without sending content.
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
		}
	}

	@Override
	public void postConnect(Request request, HttpURLConnection connection) throws IOException {
		if (request.getParams() != null) {
			if (request.getMethod().equals(Request.Method.POST)) {
				writeJSON(connection.getOutputStream(), request.getParams());
			}
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
}
