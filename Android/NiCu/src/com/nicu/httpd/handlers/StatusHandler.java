package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;
import com.nicu.utils.Log;

public class StatusHandler extends RequestHandler {
	
	public StatusHandler()
	{
		super("/robot/status");
	}
	
	public Response handleRequest(IHTTPSession session)
	{
		String json = Robot.getInstance().toString();
		Log.debug(json);
		Response response = new Response(json);
		return response;
	}

}
