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
	private EditText addressTextOfSignal, addressTextOfPicture;
	private EditText portTextOfSignal, portTextOfPicture;
	private TextView textView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.rgb(255, 255, 255));
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);

		// get address and port(Signal)
		textView = new TextView(this);
		textView.setText("IP-address(Signal)");
		textView.setTextColor(Color.rgb(0, 0, 0));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(textView);

		//String address = "192.168.";
		String address = "";

		addressTextOfSignal = new EditText(this);
		addressTextOfSignal.setText(address);
		addressTextOfSignal.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout.addView(addressTextOfSignal);

		textView = new TextView(this);
		textView.setText("Port number(Signal)");
		textView.setTextColor(Color.rgb(0, 0, 0));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(textView);

		String port = "12335";
		portTextOfSignal = new EditText(this);
		portTextOfSignal.setText(port);
		portTextOfSignal.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout.addView(portTextOfSignal);
		
		textView = new TextView(this);
		textView.setText("IP-address(Picture)");
		textView.setTextColor(Color.rgb(0, 0, 0));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(textView);

		// get address and port(Picture)
		addressTextOfPicture = new EditText(this);
		addressTextOfPicture.setText(address);
		addressTextOfPicture.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout.addView(addressTextOfPicture);

		port = "12336";
		textView = new TextView(this);
		textView.setText("Port number(Picture)");
		textView.setTextColor(Color.rgb(0, 0, 0));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(textView);

		portTextOfPicture = new EditText(this);
		portTextOfPicture.setText(port);
		portTextOfPicture.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout.addView(portTextOfPicture);
		
		

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
			intent.putExtra("addressOfSignal", addressTextOfSignal.getText().toString());
			intent.putExtra("addressOfPicture", addressTextOfPicture.getText().toString());

			intent.putExtra("portOfSignal", portTextOfSignal.getText().toString());
			intent.putExtra("portOfPicture", portTextOfPicture.getText().toString());

			startActivityForResult(intent, 0);
		}
	};
}
