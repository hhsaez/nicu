package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class MoveBackwardHandler extends RequestHandler {
	
	public MoveBackwardHandler() {
		super("/robot/backward");
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		Robot.getInstance().turnBack();
		
		Response response = new Response("no information yet");
		return response;
	}

}
