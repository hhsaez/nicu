package com.nicu.main;

import android.bluetooth.BluetoothDevice;

import com.nicu.bluetooth.BTManager;
import com.nicu.bluetooth.BLManagerObserver;
import com.nicu.utils.Log;

public class MainViewModel implements BLManagerObserver {
	
	private MainActivity activity;
	private boolean connected;
	private boolean ledOn;
	
	public MainViewModel(MainActivity activity)
	{
		this.activity = activity;
	}
	
	public void onResume()
	{
		BTManager.getInstance().setObserver(this);
	}
	
	public void onPause()
	{
		BTManager.getInstance().setObserver(null);
		BTManager.getInstance().disconnect();
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public boolean isLEDOn()
	{
		return ledOn;
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
		
		String command = "L " + leftSpeed + " R " + rightSpeed;
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
	
}
