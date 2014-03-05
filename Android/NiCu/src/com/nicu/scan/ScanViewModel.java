package com.nicu.scan;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.nicu.bluetooth.BTManager;

public class ScanViewModel {
	
	private ScanActivity activity;
	
	private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	private boolean scanning = false;
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            devices.add(device);
	            activity.onDevicesFound(devices);
	        }
	    }
	};
	
	public ScanViewModel(ScanActivity activity)
	{
		this.activity = activity;
	}
	
	public void resume()
	{
		if (!BTManager.getInstance().isBluetoothSupported()) {
			this.activity.onBLUnsupported();
		}
		else if (!BTManager.getInstance().isBluetoothEnabled()) {
			this.activity.onBLDisabled();
		}		
		else {
			startScanning();
		}
	}
	
	public void pause()
	{
		stopScanning();
	}
	
	private void startScanning()
	{
		this.devices.clear();
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.activity.registerReceiver(receiver, filter);
		this.scanning = BTManager.getInstance().startDiscovery();
		if (scanning) {
			this.activity.onScanStarted();
		}
	}
	
	private void stopScanning()
	{
		if (this.scanning) {
			this.activity.unregisterReceiver(receiver);
			this.scanning = !BTManager.getInstance().cancelDiscovery();
		}
		
		if (!this.scanning) {
			this.activity.onScanStopped();
		}
	}
	
	public boolean connectToDevice(BluetoothDevice device)
	{
		return BTManager.getInstance().connect(device);
	}
	
}
