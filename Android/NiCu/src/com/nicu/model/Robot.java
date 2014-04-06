package com.nicu.model;

import java.util.ArrayList;
import java.util.List;

public class Robot {
	
	public static final int MOTOR_MAX_SPEED = 100;
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
	
	public void setMotorsSpeed(int left, int right) {
		this.leftMotorSpeed = left;
		this.rightMotorSpeed = right;
		
		for (Observer o : this.observers) {
			o.onSpeedChanged(this);
		}
	}

}
