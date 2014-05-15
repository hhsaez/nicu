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

	public TestViewModel(TestActivity activity) {
		this.activity = activity;
		
		this.sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
		
		Robot.getInstance().addObserver(this);
		Robot.getInstance().setEnsureSpeeds(true);
		
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
		
		try {
			this.httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onPause()
	{
		sensorManager.unregisterListener(this);
		Robot.getInstance().shutdown();
		this.httpServer.stop();
	}

	public String getAddress()
	{
		return this.httpServer.getAddress();
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
		this.activity.onMotorsSpeedChanged(robot.getLeftMotorSpeed(), robot.getRightMotorSpeed());		
	}
}
