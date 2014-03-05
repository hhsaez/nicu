package com.nicu.bluetooth;

import com.nicu.utils.Log;

public class DefaultBLManagerObserver implements BLManagerObserver {

	@Override
	public void onConnectSuccess() {
		Log.info("Connect succeeded");
	}

	@Override
	public void onConnectError(String message) {
		Log.error("Connection error: " + message);
	}

	@Override
	public void onDisconnectSuccess() {
		Log.info("Disconnect succeeded");
	}

	@Override
	public void onDisconnectError(String message) {
		Log.error("Disconnection error: " + message);
	}

	@Override
	public void onSendSuccess(String data) {
		Log.info("Data sent: '" + data + "'");
	}

	@Override
	public void onSendError(String message) {
		Log.error("Send error: " + message);
	}
	
	@Override
	public void onDataReceived(String data) {
		Log.info("Received: " + data);		
	}

}
