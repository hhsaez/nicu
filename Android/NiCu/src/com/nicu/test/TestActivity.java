package com.nicu.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.nicu.R;

public class TestActivity extends Activity {
	
	private TestViewModel viewModel;
	
	private SeekBar sbLeftMotor;
	private SeekBar sbRightMotor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		this.viewModel = new TestViewModel(this);
		
		this.sbLeftMotor = (SeekBar) findViewById(R.id.sbLeftMotor);
		this.sbRightMotor = (SeekBar) findViewById(R.id.sbRightMotor);
		
		getActionBar().setTitle(this.viewModel.getAddress());
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.viewModel.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.viewModel.onResume();
	}
	
	public void onMotorsSpeedChanged(int left, int right) {
		this.sbLeftMotor.setProgress(100 + left);
		this.sbRightMotor.setProgress(100 + right);
	}
}

