package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;

public abstract class RequestHandler {
	
	public static final String RESPONSE_MIME_PLAINTEXT = "text/plain";
    public static final String RESPONSE_MIME_HTML = "text/html";
	public static final String RESPONSE_MIME_JS = "application/javascript";
	public static final String RESPONSE_MIME_CSS = "text/css";
	public static final String RESPONSE_MIME_PNG = "image/png";
	public static final String RESPONSE_MIME_JPG = "image/jpeg";
	public static final String RESPONSE_MIME_DEFAULT_BINARY = "application/octet-stream";
    public static final String RESPONSE_MIME_XML = "text/xml";

	private String name;

	protected RequestHandler(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void onServerStart() {

	}

	public abstract Response handleRequest(IHTTPSession session);

	public void onServerStop() {

	}

}
