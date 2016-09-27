package com.dellkan.sample;

import com.dellkan.net.Endpoint;

public final class URLS {
	private URLS() {}

	public static final Endpoint GOOGLE_BASE = new Endpoint("https://www.googleapis.com/");
	public static final Endpoint YOUTUBE_API_BASE = new Endpoint(GOOGLE_BASE, "youtube/v3/");
	public static final Endpoint YOUTUBE_SEARCH = new Endpoint(YOUTUBE_API_BASE, "search");
}
