package com.nicu.main;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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

public class MainViewModel implements BLManagerObserver, Robot.Observer, SensorEventListener {
	
	private MainActivity activity;
	
	private boolean connected;
	private boolean ledOn;
	private NiCuHTTPD httpServer;
	private SensorManager sensorManager;

	public MainViewModel(MainActivity activity)
	{
		Robot.getInstance().addObserver(this);
		Robot.getInstance().setEnsureSpeeds(true);
		
		this.activity = activity;
		
		this.sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
		
		this.httpServer = new NiCuHTTPD(this.activity);
		this.httpServer.registerHandler(new StatusHandler());
		this.httpServer.registerHandler(new CameraHandler());
		this.httpServer.registerHandler(new MoveForwardHandler());
		this.httpServer.registerHandler(new MoveBackwardHandler());
		this.httpServer.registerHandler(new RotateLeftHandler());
		this.httpServer.registerHandler(new RotateRightHandler());
		this.httpServer.registerHandler(new StopMovementHandler());
	}
	
	public void onResume()
	{
		if (!Robot.getInstance().isRunning()) {
			Robot.getInstance().run();
		}
		
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
		
		BTManager.getInstance().setObserver(this);
		
		try {
			this.httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onPause()
	{
		Robot.getInstance().shutdown();
		
		sensorManager.unregisterListener(this);
		
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
	
	public void onSensorChanged(SensorEvent event) {
		// get the angle around the z-axis rotated
        float azimuth = (float)(event.values[0] * Math.PI / 180.0f);
        Robot.getInstance().setCurrentHeading(azimuth);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// unused
	}
	
	@Override
	public void onSpeedChanged(Robot robot) {
		changeMotorsSpeed(robot.getLeftMotorSpeed(), robot.getRightMotorSpeed());
		this.activity.onMotorsSpeedChanged(robot.getLeftMotorSpeed(), robot.getRightMotorSpeed());
	}
	
}
