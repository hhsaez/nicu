package com.nicu.httpd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.nicu.httpd.handlers.CameraHandler;
import com.nicu.httpd.handlers.RequestHandler;
import com.nicu.httpd.handlers.StatusHandler;

public class NiCuHTTPD extends NanoHTTPD {
	
	private static final int DEFAULT_HTTP_PORT = 8080;
	
	private Map<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();

	public NiCuHTTPD() {
		super(DEFAULT_HTTP_PORT);
		
		registerHandler(new StatusHandler());
		registerHandler(new CameraHandler());
	}
	
	@Override
	public void start() throws IOException {
		super.start();
		for (String key : this.handlers.keySet()) {
			RequestHandler handler = this.handlers.get(key);
			if (handler != null) {
				handler.onServerStart();
			}
		}
	}
	
	@Override
	public void stop() {
		for (String key : this.handlers.keySet()) {
			RequestHandler handler = this.handlers.get(key);
			if (handler != null) {
				handler.onServerStop();
			}
		}
		
		super.stop();
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		RequestHandler handler = this.handlers.get(session.getUri());
		if (handler != null) {
			return handler.handleRequest(session);
		}
		
		return super.serve(session);
	}
	
	private void registerHandler(RequestHandler handler)
	{
		this.handlers.put(handler.getName(), handler);
	}

}
