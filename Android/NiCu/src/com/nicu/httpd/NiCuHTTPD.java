package com.nicu.httpd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.nicu.httpd.handlers.RequestHandler;

public class NiCuHTTPD extends NanoHTTPD {
	
	private static final int DEFAULT_HTTP_PORT = 8080;

	private Context context;
	private Map<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();

	public NiCuHTTPD(Context context) {
		super(DEFAULT_HTTP_PORT);
		this.context = context;
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
	
	public String getAddress() {
		WifiManager wifiMgr = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		@SuppressWarnings("deprecation")
		String ipAddress = Formatter.formatIpAddress(ip);
		return "http://" + ipAddress + ":" + DEFAULT_HTTP_PORT;
	}
	
	public void registerHandler(RequestHandler handler)
	{
		this.handlers.put(handler.getName(), handler);
	}

}
