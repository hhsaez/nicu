package com.nicu.scan;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nicu.R;

public class ScanAdapter extends ArrayAdapter<BluetoothDevice> {
	
	private Context context;
	private List<BluetoothDevice> devices;

	public ScanAdapter(Context context, List<BluetoothDevice> devices)
	{
		super(context, R.layout.item_device_list, devices);
		
		this.context = context;
		this.devices = devices;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.item_device_list, parent, false);
		
		BluetoothDevice device = this.devices.get(position);
		
		TextView txtDeviceName = (TextView) view.findViewById(R.id.txtDeviceName);
		txtDeviceName.setText(device.getName());
		
		TextView txtDeviceAddress = (TextView) view.findViewById(R.id.txtDeviceAddress);
		txtDeviceAddress.setText(device.getAddress());
		
		return view;
	}
}
