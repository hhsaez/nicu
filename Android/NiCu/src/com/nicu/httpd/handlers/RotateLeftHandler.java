package com.nicu.httpd.handlers;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.model.Robot;

public class RotateLeftHandler extends RequestHandler {
	
	private Robot robot;
	
	public RotateLeftHandler(Robot robot) {
		super("/robot/left");
		
		this.robot = robot;
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		this.robot.setMotorsSpeed(Robot.MOTOR_MIN_SPEED, Robot.MOTOR_MAX_SPEED);
		Response response = new Response("no information yet");
		return response;
	}

}
