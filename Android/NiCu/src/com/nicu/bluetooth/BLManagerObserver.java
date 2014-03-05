package com.nicu.bluetooth;

public interface BLManagerObserver {
	void onConnectSuccess();
	void onConnectError(String message);
	
	void onDisconnectSuccess();
	void onDisconnectError(String message);
	
	void onSendSuccess(String data);
	void onSendError(String message);
	
	void onDataReceived(String data);
}