package com.nicu.model.controllers;

import com.nicu.model.Robot;

public class StopController implements RobotController {

	@Override
	public void execute(Robot robot) {
		float vR = robot.getRightMotorTrim();
		float vL = robot.getLeftMotorTrim();
		
		robot.setMotorsSpeed((int) vL, (int) vR);
	}

}
