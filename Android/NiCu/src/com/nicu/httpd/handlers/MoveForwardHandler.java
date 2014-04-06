package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class MoveForwardHandler extends RequestHandler {
	
	private Robot robot;
	
	public MoveForwardHandler(Robot robot) {
		super("/robot/forward");
		
		this.robot = robot;
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		this.robot.setMotorsSpeed(Robot.MOTOR_MAX_SPEED, Robot.MOTOR_MAX_SPEED);
		
		Response response = new Response("no information yet");
		return response;
	}

}
