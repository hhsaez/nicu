package com.nicu.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nicu.R;
import com.nicu.scan.ScanActivity;
import com.nicu.test.TestActivity;

public class MenuActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		Button btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, ScanActivity.class);
				startActivity(intent);
			}
		});
		
		Button btnTest = (Button) findViewById(R.id.btnTest);
		btnTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, TestActivity.class);
				startActivity(intent);
			}
		});
	}

}
