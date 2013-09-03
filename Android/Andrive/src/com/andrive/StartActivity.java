package com.andrive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StartActivity extends Activity {
	private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private final static int FP = LinearLayout.LayoutParams.FILL_PARENT;
	private EditText addressText;
	private EditText portText;
	private TextView textView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.rgb(255, 255, 255));
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);

		textView = new TextView(this);
		textView.setText("IP-address");
		textView.setTextColor(Color.rgb(0, 0, 0));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(textView);

		String address = "";
		addressText = new EditText(this);
		addressText.setText(address);
		addressText.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout.addView(addressText);

		textView = new TextView(this);
		textView.setText("Port number");
		textView.setTextColor(Color.rgb(0, 0, 0));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(textView);

		String port = "";
		portText = new EditText(this);
		portText.setText(port);
		portText.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout.addView(portText);

		Button btn = new Button(this);
		btn.setText("GetControl");
		btn.setOnClickListener(listener);
		btn.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(btn);
		setContentView(layout);
	}

	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v("StartActivity","clicked \"get connect\" button");
			Intent intent = new Intent(getApplication(), GetSensorValues.class);
			intent.putExtra("address", addressText.getText().toString());
			intent.putExtra("port", portText.getText().toString());
			startActivityForResult(intent, 0);
		}
	};
}
