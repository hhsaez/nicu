package com.nicu.model.controllers;

import com.nicu.model.Robot;

public class GoToAngleController implements RobotController {
	
	public GoToAngleController() {

	}

	@Override
	public void execute(Robot robot) {
		float e_k = robot.getCurrentHeading() - robot.getDesiredHeading();
		e_k = (float) Math.atan2(Math.sin(e_k), Math.cos(e_k));
		
		float w = robot.getkP() * e_k;
		
		float vR = robot.getRightMotorTrim() + (2.0f * robot.getVelocity() + w * robot.getLength() ) / (2.0f * robot.getRadius());
		float vL = robot.getLeftMotorTrim() + (2.0f * robot.getVelocity() - w * robot.getLength() ) / (2.0f * robot.getRadius());
		
		boolean movementAllowed = true;
		for (int i = 0; i < robot.getSensors().length; i++) {
			movementAllowed = movementAllowed && (robot.getSensors()[i] >= 30);
		}
		
		robot.setMotorsSpeed((int) vL, (int) vR);
	}

}
