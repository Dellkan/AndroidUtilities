package com.dellkan.net;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Endpoint {
	private static List<Endpoint> register = new ArrayList<>();
	public enum MODE {
		DEV, STAGING, PROD, DEFAULT
	}

	private Endpoint base;
	private String prodURL;
	private String stagingURL;
	private String devURL;
	private MODE overwriteMode;

	private void addToRegister() {
		register.add(this);
	}

	public static List<Endpoint> getValues() {
		return new ArrayList<>(register);
	}

	public Endpoint(Endpoint base) {
		this.base = base;
	}

	public Endpoint(Endpoint base, String component) {
		this.base = base;
		this.prodURL = component;
	}

	public Endpoint(String prodURL) {
		this(prodURL, null);
	}

	public Endpoint(String prodURL, String stagingURL) {
		this(prodURL, stagingURL, null);
	}

	public Endpoint(String prodURL, String stagingURL, String devURL) {
		this.prodURL = prodURL;
		this.stagingURL = stagingURL;
		this.devURL = devURL;
	}

	public String getURL(Object... args) {
		return getURL(MODE.DEFAULT, args);
	}

	public String getURL(MODE mode, Object... args) {
		String url = "";
		if (base != null) {
			url += base.getURL(mode);
		}
		if (overwriteMode != null && mode.equals(MODE.DEFAULT)) {
			mode = overwriteMode;
		}
		switch (mode) {
			case DEV:
				if (!TextUtils.isEmpty(devURL)) {
					url += devURL;
					break;
				}
			case STAGING:
				if (!TextUtils.isEmpty(stagingURL)) {
					url += stagingURL;
					break;
				}
			case DEFAULT:
			case PROD:
				if (!TextUtils.isEmpty(prodURL)) {
					url += prodURL;
					break;
				}
		}
		if (args != null && args.length > 0) {
			return String.format(url, args);
		} else {
			return url;
		}
	}

	public void setURL(String prod) {
		setURL(prod, this.stagingURL, this.devURL);
	}

	public void setURL(String prod, String staging) {
		setURL(prod, staging, this.devURL);
	}

	public void setURL(String prod, String staging, String dev) {
		this.devURL = dev;
		this.stagingURL = staging;
		this.prodURL = prod;
	}

	public MODE getOverwriteMode() {
		return overwriteMode != null ? overwriteMode : MODE.DEFAULT;
	}

	public void setOverwriteMode(MODE mode) {
		this.overwriteMode = mode;
	}

	@Override
	public String toString() {
		return getURL();
	}
}
