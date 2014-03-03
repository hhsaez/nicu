package com.nicu.bluetooth;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import com.nicu.utils.Log;

public class BLManager {
	
	public static final UUID SPP_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private static BLManager instance = null;
	
	public static BLManager getInstance() {
		if (instance == null) {
			instance = new BLManager();
		}
		
		return instance;
	}
	
	private BLManagerObserver observer;
	
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private OutputStream outputStream;
	
	private BLManager() {
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

		return setupSocket();
	}
	
	public void disconnect() {
		if (closeStream() && closeSocket()) {
			this.getObserver().onDisconnectSuccess();
		}
	}

	public void sendData(String data) {
		if (this.outputStream != null) {
			try {
				this.outputStream.write(data.getBytes());
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
	
	private BluetoothDevice findDevice(String address) {
		Log.debug("Searching for devices...");
		Set<BluetoothDevice> pairedDevices = this.adapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			Log.debug("Found " + pairedDevices.size() + " bonded devices");
			for (BluetoothDevice device : pairedDevices) {
				Log.debug("Device: " + device.getName() + " (" + device.getAddress() + ")");
				if (device.getAddress().equals(address)) {
					return device;
				}
			}
		}
		else {
			Log.debug("No devices found");
		}
		
		return null;
	}
	
	private boolean setupSocket() {
		try {
			Log.debug("Creating comm socket");
			if (Build.VERSION.SDK_INT >= 10) {
				final Method m = this.device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
				this.socket = (BluetoothSocket) m.invoke(device, SPP_SERVICE_UUID);
			}
			else {
				this.socket = this.device.createRfcommSocketToServiceRecord(SPP_SERVICE_UUID);
			}
			
			Log.debug("Connecting to socket");
			this.socket.connect();
			
			Log.debug("Opening output stream");
			this.outputStream = this.socket.getOutputStream();
		}
		catch (Exception e) {
			e.printStackTrace();
			this.getObserver().onConnectError("Unable to setup socket: " + e.getMessage());
			closeSocket();
			return false;
		}
		
		return true;
	}
		
	private boolean closeStream() {
		try {
			if (this.outputStream != null) {
				this.outputStream.flush();
				this.outputStream.close();
				this.outputStream = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			this.getObserver().onDisconnectError("Failed to cleanup BL state: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean closeSocket() {
		try {
			if (this.socket != null) {
				this.socket.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			this.getObserver().onDisconnectError("Cannot close socket: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
}
