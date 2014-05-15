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
		try {
			Response response = new Response(Robot.getInstance().toJSON().toString());
			return response;
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.debug("Error handling status request: " + e.getMessage());
			return null;
		}
	}

}
