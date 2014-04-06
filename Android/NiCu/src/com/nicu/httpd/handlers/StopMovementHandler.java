package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class StopMovementHandler extends RequestHandler {
	
	private Robot robot;
	
	public StopMovementHandler(Robot robot) {
		super("/robot/stop");
		
		this.robot = robot;
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		this.robot.setMotorsSpeed(0, 0);
		Response response = new Response("no information yet");
		return response;
	}

}
