package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class RotateRightHandler extends RequestHandler {
	
	public RotateRightHandler() {
		super("/robot/right");
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		Robot.getInstance().turnRight();
		
		Response response = new Response("no information yet");
		return response;
	}

}
