package com.nicu.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Robot {
	
	public static final int MOTOR_MAX_SPEED = 100;
	
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
	private int minSpeed = 90;
	private int speedThreshold = 10;
	private boolean clampSpeed = false;
	
	private float currentHeading = 0;
	private float desiredHeading = 0;
	
	private float L = 10.0f;	// distance between wheels
	private float R = 2.5f;		// wheel radius
	private float V = 0.0f;		// current velocity [-100, 100]
	
	private float kP = 30.0f;	// proportional gain
	
	private int orderTimeout = 200;
	
	private boolean running = false;
	
	private int[] sensors = new int[3];
	
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
	
	public boolean getClampSpeed() {
		return this.clampSpeed;
	}
	
	public void setClampSpeed(boolean value) {
		this.clampSpeed = value;
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
		V = 2.0f * MOTOR_MAX_SPEED;
	}
	
	public void turnLeft() {
		setDesiredHeading(getCurrentHeading() - (float)Math.PI / 2.0f);
		V = 0.0f;
	}
	
	public void turnRight() {
		setDesiredHeading(getCurrentHeading() + (float)Math.PI / 2.0f);
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
		this.leftMotorSpeed = clampSpeedIfNeeded(left);
		this.rightMotorSpeed = clampSpeedIfNeeded(right);
		
		for (Observer o : this.observers) {
			o.onSpeedChanged(this);
		}
	}

	private int clampSpeedIfNeeded(int input) {
		if (this.clampSpeed) {
			if (input < -getSpeedThreshold() && input > -getMinSpeed()) {
				return -getMinSpeed();
			}
			else if (input > getSpeedThreshold() && input < getMinSpeed()) {
				return getMinSpeed();
			}
			else {
				return input;
			}
		}
		else {
			return Math.max(-MOTOR_MAX_SPEED, Math.min(MOTOR_MAX_SPEED, input));
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
						
						float vR = (2.0f * V + w * L ) / (2.0f * R);
						float vL = (2.0f * V - w * L ) / (2.0f * R);
						
						setMotorsSpeed((int) vL, (int) vR);
					}
				}
			}
		}).start();
	}
	
	public void shutdown() {
		this.running = false;
	}
	
	public int getOrderTimeout() {
		return orderTimeout;
	}

	public void setOrderTimeout(int orderTimeout) {
		this.orderTimeout = orderTimeout;
	}

	public float getkP() {
		return kP;
	}

	public void setkP(float kP) {
		this.kP = kP;
	}

	public int getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(int minSpeed) {
		this.minSpeed = minSpeed;
	}

	public int getSpeedThreshold() {
		return speedThreshold;
	}

	public void setSpeedThreshold(int speedThreshold) {
		this.speedThreshold = speedThreshold;
	}

	public int[] getSensors() {
		return sensors;
	}

	public void setSensors(int[] sensors) {
		this.sensors = sensors;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("left", this.getLeftMotorSpeed());
		json.put("right", this.getRightMotorSpeed());
		json.put("minSpeed", this.getMinSpeed());
		json.put("speedThreshold", this.getSpeedThreshold());
		json.put("currentHeading", this.getCurrentHeading());
		json.put("desiredHeading", this.getDesiredHeading());
		json.put("orderTimeout", this.getOrderTimeout());
		json.put("clampSpeed", this.getClampSpeed());
		json.put("kP", this.getkP());
		json.put("sensors", this.getSensors());
		return json;
	}
	
	public void fromJSON(JSONObject json) throws JSONException
	{
		this.setMinSpeed(json.getInt("minSpeed"));
		this.setSpeedThreshold(json.getInt("speedThreshold"));
		this.setOrderTimeout(json.getInt("orderTimeout"));
		this.setClampSpeed(json.getBoolean("clampSpeed"));
		this.setkP((float) json.getDouble("kP"));
	}
	
	@Override
	public String toString() {
		try {
			return this.toJSON().toString();
		}
		catch (Exception e) {
			return super.toString();
		}
	}

}
