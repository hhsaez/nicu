package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class MoveBackwardHandler extends RequestHandler {
	
	private Robot robot;
	
	public MoveBackwardHandler(Robot robot) {
		super("/robot/backward");
		
		this.robot = robot;
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		this.robot.turnBack();
		
		Response response = new Response("no information yet");
		return response;
	}

}
