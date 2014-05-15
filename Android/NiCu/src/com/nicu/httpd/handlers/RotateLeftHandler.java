package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class RotateLeftHandler extends RequestHandler {
	
	public RotateLeftHandler() {
		super("/robot/left");
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		Robot.getInstance().turnLeft();
		
		Response response = new Response("no information yet");
		return response;
	}

}
