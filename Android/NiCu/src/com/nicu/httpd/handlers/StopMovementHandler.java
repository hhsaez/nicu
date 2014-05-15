package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class StopMovementHandler extends RequestHandler {
	
	public StopMovementHandler() {
		super("/robot/stop");
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		Robot.getInstance().stop();
		
		Response response = new Response("no information yet");
		return response;
	}

}
