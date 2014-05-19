package com.nicu.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothSocket;

import com.nicu.utils.Log;

public class DeviceConnectedThread {

	private final BluetoothSocket socket;
	private final InputStream inStream;
	private final OutputStream outStream;
	private final BTManager btManager;
	
	private boolean running = false; 

	public DeviceConnectedThread(BluetoothSocket socket, BTManager manager) throws IOException {
		this.socket = socket;
		this.socket.connect();
		
		this.inStream = socket.getInputStream();
		this.outStream = socket.getOutputStream();
		this.btManager = manager;
	}

	public void run() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] buffer = new byte[1024]; // buffer store for the stream
				int bytes; // bytes returned from read()
				
				running = true;
				
				List<Byte> line = new ArrayList<Byte>();
				while (running) {
					try {
						// Read from the InputStream
						bytes = inStream.read(buffer);
						for (int i = 0; i < bytes; i++) {
							if (buffer[i] == '\n') {
								// send data to manager only when we've got a full line
								byte[] result = new byte[line.size()];
								for (int j = 0; j < result.length; j++) {
									result[j] = line.get(j);
								}
								btManager.onDataReceived(result.length, result);
							}
							else {
								line.add(buffer[i]);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						Log.error("Cannot read data: " + e.getMessage());
						break;
					}
				}
				
				Log.info("Connection thread stopped");
			}
		}).start();
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) throws IOException {
		outStream.write(bytes);
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() throws IOException {
		socket.close();
		this.running = false;
	}

}
