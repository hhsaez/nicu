package com.nicu.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

import com.nicu.utils.Log;

public class DeviceConnectedThread extends Thread {

	private final BluetoothSocket socket;
	private final InputStream inStream;
	private final OutputStream outStream;
	private final BTManager btManager;

	public DeviceConnectedThread(BluetoothSocket socket, BTManager manager) throws IOException {
		this.socket = socket;
		this.socket.connect();
		
		this.inStream = socket.getInputStream();
		this.outStream = socket.getOutputStream();
		this.btManager = manager;
	}

	public void run() {
		byte[] buffer = new byte[1024]; // buffer store for the stream
		int bytes; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream
				bytes = this.inStream.read(buffer);
				// Send the obtained bytes to the UI activity
				btManager.onDataReceived(bytes, buffer);
//				mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//						.sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
				Log.error("Cannot read data: " + e.getMessage());
				break;
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) throws IOException {
		outStream.write(bytes);
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() throws IOException {
		socket.close();
	}

}
