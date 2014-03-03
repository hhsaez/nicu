package com.nicu.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.nicu.R;

public class MainActivity extends Activity {

	private MainViewModel viewModel;

	private Button btnOn;
	// private Button btnStop;
	private SeekBar sbLeftMotor;
	private SeekBar sbRightMotor;

	private SeekBar.OnSeekBarChangeListener onSeekBarChangeLister = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			int leftValue = sbLeftMotor.getProgress();
			int rightValue = sbRightMotor.getProgress();
			viewModel.changeMotorsSpeed(leftValue, rightValue, 0, 200);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.viewModel = new MainViewModel(this);
		
		Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		btnDisconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewModel.disconnect();
			}

		});

		this.btnOn = (Button) findViewById(R.id.btnOn);
		this.btnOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!viewModel.isLEDOn()) {
					viewModel.turnLEDOn();
				} else {
					viewModel.turnLEDOff();
				}
			}

		});

		this.sbLeftMotor = (SeekBar) findViewById(R.id.sbLeftMotor);
		sbLeftMotor.setOnSeekBarChangeListener(this.onSeekBarChangeLister);
		this.sbRightMotor = (SeekBar) findViewById(R.id.sbRightMotor);
		sbRightMotor.setOnSeekBarChangeListener(this.onSeekBarChangeLister);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			cleanup();
			finish();
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.viewModel.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.viewModel.onPause();
	}
	
	private void showMessage(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
						.show();
			}

		});
	}

	public void onConnectSuccess() {

	}

	public void onConnectError(String message) {
		showMessage("Connection error: " + message);
	}

	public void onDisconnectSuccess() {
		cleanup();
		finish();
	}

	public void onDisconnectError(String message) {
		showMessage("Disconnection error: " + message);
	}

	public void onLEDOn() {
		showMessage("LED on");
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				btnOn.setText(getString(R.string.btnOff));
			}
		});
	}

	public void onLEDOff() {
		showMessage("LED off");
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				btnOn.setText(getString(R.string.btnOn));
			}
		});
	}
	
	private void cleanup()
	{
		
	}

	public void onSendError(String message) {
		showMessage(message);
	}

}
