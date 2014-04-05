package com.nicu.scan;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.nicu.httpd.NiCuHTTPD;
import com.nicu.main.MainActivity;

public class ScanActivity extends ListActivity {
	
	private static final int REQUEST_ENABLE_BT = 1;
	
	private ScanViewModel viewModel;
	
	private NiCuHTTPD httpServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		this.viewModel = new ScanViewModel(this);
		this.httpServer = new NiCuHTTPD(this);
		
		getActionBar().setTitle(this.httpServer.getAddress());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		this.httpServer.stop();
		this.viewModel.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			this.httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.viewModel.resume();
	}

	public void onBLUnsupported() {
		showAlert("Bluetooth is not support", "Your phone does not support Bluetooth", "Close");
	}

	public void onBLDisabled() {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, REQUEST_ENABLE_BT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
			showAlert("Bluetooth is disable", "Bluetooth is required to use this application", "Close");
		}
	}
	
	public void onScanStarted()
	{
		Toast.makeText(this, "Scanning for devices", Toast.LENGTH_LONG).show();
	}
	
	public void onScanStopped()
	{
		Toast.makeText(this, "Scan stopped", Toast.LENGTH_SHORT).show();
	}
	
	public void onDevicesFound(List<BluetoothDevice> devices)
	{
		setListAdapter(new ScanAdapter(this, devices));
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		BluetoothDevice device = (BluetoothDevice) listView.getItemAtPosition(position);
		if (this.viewModel.connectToDevice(device)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		else {
			showAlert("Error connecting to " + device.getName(), "Cannot connect to device. Please make sure it's working correctly", "Terminate");
		}
	}
	
	private void showAlert(final String title, final String message, final String positiveButton)
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
				
				builder.setTitle(title);
				builder.setMessage(message);
				
				builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						ScanActivity.this.finish();
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

}
