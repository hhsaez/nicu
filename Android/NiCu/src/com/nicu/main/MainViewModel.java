package com.nicu.main;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;

import com.nicu.bluetooth.BLManagerObserver;
import com.nicu.bluetooth.BTManager;
import com.nicu.httpd.NiCuHTTPD;
import com.nicu.httpd.handlers.CameraHandler;
import com.nicu.httpd.handlers.MoveBackwardHandler;
import com.nicu.httpd.handlers.MoveForwardHandler;
import com.nicu.httpd.handlers.RotateLeftHandler;
import com.nicu.httpd.handlers.RotateRightHandler;
import com.nicu.httpd.handlers.StatusHandler;
import com.nicu.httpd.handlers.StopMovementHandler;
import com.nicu.model.Robot;
import com.nicu.utils.Log;

public class MainViewModel implements BLManagerObserver, Robot.Observer {
	
	private MainActivity activity;
	
	private boolean connected;
	private boolean ledOn;
	private NiCuHTTPD httpServer;

	private Robot robot = new Robot();
	
	public MainViewModel(MainActivity activity)
	{
		this.robot = new Robot();
		this.robot.addObserver(this);
		
		this.activity = activity;
		
		this.httpServer = new NiCuHTTPD(this.activity);
		this.httpServer.registerHandler(new StatusHandler());
		this.httpServer.registerHandler(new CameraHandler());
		this.httpServer.registerHandler(new MoveForwardHandler(this.robot));
		this.httpServer.registerHandler(new MoveBackwardHandler(this.robot));
		this.httpServer.registerHandler(new RotateLeftHandler(this.robot));
		this.httpServer.registerHandler(new RotateRightHandler(this.robot));
		this.httpServer.registerHandler(new StopMovementHandler(this.robot));
	}
	
	public void onResume()
	{
		BTManager.getInstance().setObserver(this);
		
		try {
			this.httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onPause()
	{
		BTManager.getInstance().setObserver(null);
		BTManager.getInstance().disconnect();
		
		this.httpServer.stop();
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public boolean isLEDOn()
	{
		return ledOn;
	}
	
	public String getAddress()
	{
		return this.httpServer.getAddress();
	}
	
	public void connect(BluetoothDevice device)
	{
		BTManager.getInstance().connect(device);
	}
	
	public void disconnect()
	{
		if (isConnected()) {
			BTManager.getInstance().disconnect();
		}
		else {
			this.activity.onDisconnectSuccess();
		}
	}
	
	public void turnLEDOn()
	{
		BTManager.getInstance().sendData("W");
	}
	
	public void turnLEDOff()
	{
		BTManager.getInstance().sendData("E");
	}
	
	public void changeMotorsSpeed(int leftValue, int rightValue, int minValue, int maxValue)
	{
		int leftSpeed = leftValue - ( maxValue - minValue ) / 2;
		int rightSpeed = rightValue - ( maxValue - minValue ) / 2;
		
		changeMotorsSpeed(leftSpeed, rightSpeed);
	}
	
	public void changeMotorsSpeed(int left, int right) {
		String command = "L " + left + " R " + right;
		Log.debug(command);
		BTManager.getInstance().sendData(command);
	}

	@Override
	public void onConnectSuccess() {
		this.activity.onConnectSuccess();
	}

	@Override
	public void onConnectError(String message) {
		this.activity.onConnectError(message);
	}

	@Override
	public void onDisconnectSuccess() {
		this.activity.onDisconnectSuccess();
	}

	@Override
	public void onDisconnectError(String message) {
		this.activity.onDisconnectError(message);
	}

	@Override
	public void onSendSuccess(String data) {
		if (data.equals("W")) {
			this.ledOn = true;
			this.activity.onLEDOn();
		}
		else if (data.equals("E")) {
			this.ledOn = false;
			this.activity.onLEDOff();
		}
	}

	@Override
	public void onSendError(String message) {
		this.activity.onSendError(message);
	}
	
	@Override
	public void onDataReceived(String data) {
		this.activity.onData(data);
	}
	
	@Override
	public void onSpeedChanged(Robot robot) {
		changeMotorsSpeed(robot.getLeftMotorSpeed(), robot.getRightMotorSpeed());
	}
	
}
