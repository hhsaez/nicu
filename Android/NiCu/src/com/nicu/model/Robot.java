package com.nicu.model;

import java.util.ArrayList;
import java.util.List;

import com.nicu.utils.Log;

public class Robot {
	
	public static final int MOTOR_MAX_SPEED = 100;
	public static final int MOTOR_CRUISE_SPEED = 90;
	public static final int MOTOR_MIN_SPEED = -100;
	
	private static Robot instance = null;
	
	public static Robot getInstance() {
		if (instance == null) {
			instance = new Robot();
		}
		
		return instance;
	}
	
	public interface Observer {
		void onSpeedChanged(Robot robot);
	}
	
	private List<Observer> observers = new ArrayList<Observer>();
	
	private int leftMotorSpeed = 0;
	private int rightMotorSpeed = 0;
	private boolean ensureSpeed = false;
	
	private float currentHeading = 0;
	private float desiredHeading = 0;
	
	private float L = 10.0f;	// distance between wheels
	private float R = 2.5f;		// wheel radius
	private float V = 0.0f;		// current velocity [-100, 100]
	
	private float kP = 10.0f;	// proportional gain
	
	private boolean running = false;
	
	public Robot() {
		
	}
	
	public void addObserver(Observer observer) {
		if (!this.observers.contains(observer)) {
			this.observers.add(observer);
		}
	}

	public void removeObserver(Observer observer) {
		if (this.observers.contains(observer)) {
			this.observers.remove(observer);
		}
	}
	
	public int getLeftMotorSpeed() {
		return this.leftMotorSpeed;
	}
	
	public int getRightMotorSpeed() {
		return this.rightMotorSpeed;
	}
	
	public boolean shouldEnsureSpeeds() {
		return this.ensureSpeed;
	}
	
	public void setEnsureSpeeds(boolean value) {
		this.ensureSpeed = value;
	}
	
	public void setCurrentHeading(float value) {
		this.currentHeading = value;
	}
	
	public float getCurrentHeading() {
		return this.currentHeading;
	}
	
	public void setDesiredHeading(float value) {
		this.desiredHeading = value;
	}
	
	public float getDesiredHeading() {
		return this.desiredHeading;
	}
	
	public void moveForward() {
		setDesiredHeading(getCurrentHeading());
		V = MOTOR_MAX_SPEED;
	}
	
	public void turnLeft() {
		setDesiredHeading(getCurrentHeading() + (float)Math.PI / 2.0f);
		V = 0.0f;
	}
	
	public void turnRight() {
		setDesiredHeading(getCurrentHeading() - (float)Math.PI / 2.0f);
		V = 0.0f;
	}
	
	public void turnBack() {
		setDesiredHeading(getCurrentHeading() + (float)Math.PI);
		V = 0.0f;
	}
	
	public void stop() {
		setDesiredHeading(getCurrentHeading());
		V = 0.0f;
	}
	
	private void setMotorsSpeed(int left, int right) {
		this.leftMotorSpeed = clampSpeed(left);
		this.rightMotorSpeed = clampSpeed(right);
		
		for (Observer o : this.observers) {
			o.onSpeedChanged(this);
		}
	}

	private int clampSpeed(int input) {
		if (this.ensureSpeed) {
			if (input > (MOTOR_MAX_SPEED / 3) && input < MOTOR_CRUISE_SPEED) {
				return MOTOR_CRUISE_SPEED;
			}
			else if (input > MOTOR_MAX_SPEED) {
				return MOTOR_MAX_SPEED;
			}
			else if (input < (-MOTOR_MAX_SPEED / 3) && input > -MOTOR_CRUISE_SPEED) {
				return -MOTOR_CRUISE_SPEED;
			}
			else if (input < -MOTOR_MAX_SPEED) {
				return -MOTOR_MAX_SPEED;
			}
			
			return input;
		}
		else {
			return Math.max(MOTOR_MIN_SPEED, Math.min(MOTOR_MAX_SPEED, input));
		}
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void run() {
		this.running = true;
		
		stop();
		
		new Thread(new Runnable() {
			
			private long lastTime = 0;
			
			@Override
			public void run() {
				while (running) {
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastTime > 1000) {
						lastTime = currentTime;
						
						float e_k = getCurrentHeading() - getDesiredHeading();
						e_k = (float) Math.atan2(Math.sin(e_k), Math.cos(e_k));
						
						float w = kP * e_k;
						
						float vR = (2 * V + w * L ) / (2.0f * R);
						float vL = (2 * V - w * L ) / (2.0f * R);
						
						setMotorsSpeed((int) vL, (int) vR);
						
						Log.debug("" + getCurrentHeading() + " " + getDesiredHeading());
					}
				}
			}
		}).start();
	}
	
	public void shutdown() {
		this.running = false;
	}

}
