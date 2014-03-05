package com.nicu.bluetooth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import com.nicu.utils.Log;

public class BTManager {
	
	public static final UUID SPP_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private static BTManager instance = null;
	
	public static BTManager getInstance() {
		if (instance == null) {
			instance = new BTManager();
		}
		
		return instance;
	}
	
	private BLManagerObserver observer;
	
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private DeviceConnectedThread deviceThread;
	
	private BTManager() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		setObserver(null);
	}
	
	public void setObserver(BLManagerObserver observer)
	{
		this.observer = observer;
		if (this.observer == null) {
			this.observer = new DefaultBLManagerObserver();
		}
	}
	
	public BLManagerObserver getObserver()
	{
		return this.observer;
	}
	
	public boolean isBluetoothSupported() {
		return adapter != null;
	}
	
	public boolean isBluetoothEnabled() {
		return isBluetoothSupported() && adapter.isEnabled();
	}
	
	public boolean startDiscovery()
	{
		return this.adapter.startDiscovery();
	}
	
	public boolean cancelDiscovery()
	{
		return this.adapter.cancelDiscovery();
	}
	
	public boolean connect(BluetoothDevice device) {
		this.device = device;
		if (this.device == null) {
			this.getObserver().onConnectError("Invalid bluetooth device");
			return false;
		}
		
		BluetoothSocket socket = null;
		
		try {
			Log.debug("Creating comm socket");
			if (Build.VERSION.SDK_INT >= 10) {
				final Method m = this.device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
				socket = (BluetoothSocket) m.invoke(device, SPP_SERVICE_UUID);
			}
			else {
				socket = this.device.createRfcommSocketToServiceRecord(SPP_SERVICE_UUID);
			}
		}
		catch (Exception e) {
			return false;
		}
		
		if (socket != null) {
			try {
				this.deviceThread = new DeviceConnectedThread(socket, this);
			}
			catch (IOException e) {
				return false;
			}
		}
		
		return (socket != null);
	}
	
	public void disconnect() {
		if (this.deviceThread != null) {
			try {
				this.deviceThread.cancel();
				this.getObserver().onDisconnectSuccess();
			}
			catch (IOException e) {
				e.printStackTrace();
				this.getObserver().onDisconnectError(e.getMessage());
			}
		}
	}

	public void sendData(String data) {
		if (this.deviceThread != null) {
			try {
				this.deviceThread.write(data.getBytes());
				this.getObserver().onSendSuccess(data);
			}
			catch (Exception e) {
				e.printStackTrace();
				this.getObserver().onSendError("Cannot send data '" + data + "' because of the following error: " + e.getMessage());
			}
		}
		else {
			this.getObserver().onSendError("No output stream instance to send data '" + data + "'");
		}
	}
	
	public void onDataReceived(int byteCount, byte[] bytes)
	{
		String str;
		try {
			str = new String(bytes, "UTF-8");
			this.getObserver().onDataReceived(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}

