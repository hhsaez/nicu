package com.nicu.test;

import java.io.IOException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.nicu.httpd.NiCuHTTPD;
import com.nicu.httpd.handlers.CameraHandler;
import com.nicu.httpd.handlers.MoveBackwardHandler;
import com.nicu.httpd.handlers.MoveForwardHandler;
import com.nicu.httpd.handlers.RotateLeftHandler;
import com.nicu.httpd.handlers.RotateRightHandler;
import com.nicu.httpd.handlers.StatusHandler;
import com.nicu.httpd.handlers.StopMovementHandler;
import com.nicu.model.Robot;

public class TestViewModel implements Robot.Observer, SensorEventListener {
	
	private TestActivity activity;
	
	private NiCuHTTPD httpServer;
	private SensorManager sensorManager;

	private Robot robot = new Robot();
	
	public TestViewModel(TestActivity activity) {
		this.activity = activity;
		
		this.sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
		
		this.robot = new Robot();
		this.robot.addObserver(this);
		this.robot.setEnsureSpeeds(false);
		
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
		if (!this.robot.isRunning()) {
			this.robot.run();
		}
		
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
		
		try {
			this.httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onPause()
	{
		sensorManager.unregisterListener(this);
		this.robot.shutdown();
		this.httpServer.stop();
	}

	public String getAddress()
	{
		return this.httpServer.getAddress();
	}
	
	public void onSensorChanged(SensorEvent event) {
		// get the angle around the z-axis rotated
        float azimuth = (float)(event.values[0] * Math.PI / 180.0f);
        this.robot.setCurrentHeading(azimuth);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// unused
	}
	
	@Override
	public void onSpeedChanged(Robot robot) {
		this.activity.onMotorsSpeedChanged(robot.getLeftMotorSpeed(), robot.getRightMotorSpeed());		
	}
}
