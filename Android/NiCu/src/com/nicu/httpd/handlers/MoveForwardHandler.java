package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class MoveForwardHandler extends RequestHandler {
	
	public MoveForwardHandler() {
		super("/robot/forward");
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		Robot.getInstance().moveForward();
		
		Response response = new Response("no information yet");
		return response;
	}

}
