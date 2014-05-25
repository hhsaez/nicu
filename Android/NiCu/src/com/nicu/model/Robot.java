package com.nicu.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nicu.model.controllers.GoToAngleController;
import com.nicu.model.controllers.RobotController;
import com.nicu.model.controllers.StopController;

public class Robot {
	
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
	
	private int leftMotorTrim = 0;
	private int rightMotorTrim = 0;
	
	private int leftMotorSpeed = 0;
	private int rightMotorSpeed = 0;
	private int minSpeed = 10;
	private int maxSpeed = 50;
	private int speedThreshold = 10;
	private boolean clampSpeed = false;
	
	private float currentHeading = 0;
	private float desiredHeading = 0;
	
	private float length = 10.0f;	// distance between wheels
	private float radius = 2.5f;		// wheel radius
	
	private float velocity = 0.0f;	// current velocity [-100, 100]
	
	private float kP = 5.0f;	// proportional gain
	
	private int orderTimeout = 0;
	
	private boolean running = false;
	
	private int[] sensors = new int[3];
	
	private RobotController controller = new StopController();
	
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

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public RobotController getController() {
		return controller;
	}

	public void setController(RobotController controller) {
		this.controller = controller;
	}
	
	public int getLeftMotorTrim() {
		return leftMotorTrim;
	}

	public void setLeftMotorTrim(int leftMotorTrim) {
		this.leftMotorTrim = leftMotorTrim;
	}

	public int getRightMotorTrim() {
		return rightMotorTrim;
	}

	public void setRightMotorTrim(int rightMotorTrim) {
		this.rightMotorTrim = rightMotorTrim;
	}

	public void moveForward() {
		setDesiredHeading(getCurrentHeading());
		setVelocity(getMaxSpeed());
		setController(new GoToAngleController());
	}
	
	public void turnLeft() {
		setDesiredHeading(getCurrentHeading() - (float)Math.PI / 2.0f);
		setVelocity(0.0f);
		setController(new GoToAngleController());
	}
	
	public void turnRight() {
		setDesiredHeading(getCurrentHeading() + (float)Math.PI / 2.0f);
		setVelocity(0.0f);
		setController(new GoToAngleController());
	}
	
	public void turnBack() {
		setDesiredHeading(getCurrentHeading() + (float)Math.PI);
		setVelocity(0.0f);
		setController(new GoToAngleController());
	}
	
	public void stop() {
		setDesiredHeading(getCurrentHeading());
		setVelocity(0.0f);
		setController(new StopController());
	}
	
	public void setMotorsSpeed(int left, int right) {
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
			return Math.max(-getMaxSpeed(), Math.min(getMaxSpeed(), input));
		}
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void run() {
		this.running = true;
		
		stop();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				long lastTime = 0;

				while (running) {
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastTime > 500) {
						lastTime = currentTime;
						getController().execute(Robot.this);
					}
				}
			}
		}).start();
	}
	
	public void shutdown() {
		this.running = false;
	}

	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("left", this.getLeftMotorSpeed());
		json.put("right", this.getRightMotorSpeed());
		json.put("leftTrim", this.getLeftMotorTrim());
		json.put("rightTrim", this.getRightMotorTrim());
		json.put("maxSpeed", this.getMaxSpeed());
		json.put("minSpeed", this.getMinSpeed());
		json.put("speedThreshold", this.getSpeedThreshold());
		json.put("currentHeading", this.getCurrentHeading());
		json.put("desiredHeading", this.getDesiredHeading());
		json.put("orderTimeout", this.getOrderTimeout());
		json.put("clampSpeed", this.getClampSpeed());
		json.put("kP", this.getkP());
		json.put("velocity", this.getVelocity());
		
		JSONArray sensorsJson = new JSONArray();
		for (int i = 0; i < this.sensors.length; i++) {
			sensorsJson.put(this.sensors[i]);
		}
		json.put("sensors", sensorsJson);
		
		return json;
	}
	
	public void fromJSON(JSONObject json) throws JSONException
	{
		this.setMinSpeed(json.getInt("minSpeed"));
		this.setMaxSpeed(json.getInt("maxSpeed"));
		this.setSpeedThreshold(json.getInt("speedThreshold"));
		this.setOrderTimeout(json.getInt("orderTimeout"));
		this.setClampSpeed(json.getBoolean("clampSpeed"));
		this.setkP((float) json.getDouble("kP"));
		this.setLeftMotorTrim(json.getInt("leftTrim"));
		this.setRightMotorTrim(json.getInt("rightTrim"));
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

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

}
