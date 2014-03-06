package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;

public class StatusHandler extends RequestHandler {
	
	public StatusHandler()
	{
		super("/robot/status");
	}
	
	public Response handleRequest(IHTTPSession session)
	{
		Response response = new Response("no information yet");
		return response;
	}

}
