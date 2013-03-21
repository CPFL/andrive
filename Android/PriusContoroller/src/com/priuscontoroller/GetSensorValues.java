package com.priuscontoroller;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class GetSensorValues extends Activity implements OnTouchListener, SensorEventListener {
	public String address;
	public String port;
	public int port_number;

	private Spinner gear_boxes;
	private Button button1, button2;

	private final static int WC=LinearLayout.LayoutParams.WRAP_CONTENT;
	private final static float FILTERING=0.1f;

	//センサー用変数
	private SensorManager sensorManager;
	private Sensor        accelerometer;
	private Sensor        orientation;
	private float[] values=new float[6];

	//加速度表示バー
	private ProgressBar progressBar1, progressBar2;
	//ピッチ、ロー表示用textview
	TextView pitchText;

	//バーの割合
	public float rate;
	/*
	 * to Server
	 *	pitch, accel
	 * */
	private float accelerator, brake;
	private float pitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);


		//パラメータの取得
		address = getIntent().getExtras().getString("address");
		port = getIntent().getExtras().getString("port");
		if(address.length() == 0){
			address = "192.168.2.226";
		}
		if(port.length() == 0){
			port = "12345";
		}
		port_number = Integer.parseInt(port);


		button1 = (Button)this.findViewById(R.id.button1);
		button1.setOnTouchListener(this);

		button2 = (Button)this.findViewById(R.id.button2);
		button2.setOnTouchListener(this);

		sensorManager=(SensorManager)getSystemService(
				Context.SENSOR_SERVICE);

		List<Sensor> list;
		list=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (list.size()>0) accelerometer=list.get(0);
		list=sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (list.size()>0) orientation=list.get(0);

		/*
		 * ギアボックス
		 * B:演じブレーキ強
		 * R:後退
		 * N:ニュートラル
		 * D:ドライブ
		 */
		Spinner spinner = (Spinner)findViewById(R.id.gear_boxes);
		String[] gear = {"B", "R", "N", "D"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.gear, gear);
		spinner.setAdapter(adapter);
		spinner.setSelection(2);


		//プログレスバー
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setMax(100);
		progressBar1.setProgress(0);

		progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
		progressBar2.setMax(100);
		progressBar2.setProgress(0);

		//ピッチ、ロー表示
		pitchText = (TextView) findViewById(R.id.textView3);

		//サーバへ接続
		GetSensorNative.connectServer(address, port_number);

	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO 自動生成されたメソッド・スタブ

		rate = 100 - event.getY() / 1079 * 100;

		//ブレーキ側
		if(v == button1){
			brake = rate;
			accelerator = 0;
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				// 指がタッチした時の処理を記述
				progressBar1.setProgress(0);
				progressBar2.setProgress((int)brake);
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				// 指がスライドした時の処理を記述
				progressBar1.setProgress(0);
				progressBar2.setProgress((int)brake);
			}
			else {
				// タッチした指が離れた時の処理を記述
				brake = 0;
				accelerator = 0;
				progressBar1.setProgress(0);
				progressBar2.setProgress(0);
			}
		}

		//アクセル側
		else if(v == button2){
			accelerator = rate;
			brake = 0;
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				// 指がタッチした時の処理を記述
				progressBar1.setProgress((int)accelerator);
				progressBar2.setProgress(0);
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				// 指がスライドした時の処理を記述
				progressBar2.setProgress(0);
				progressBar1.setProgress((int)accelerator);
			}
			else {
				// タッチした指が離れた時の処理を記述
				accelerator = 0;
				brake = 0;
				progressBar1.setProgress(0);
				progressBar2.setProgress(0);
			}
		}
		Log.d("accelerator+brake", "a:" + accelerator + "b:" + brake);
		return true;
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onStop();
			}
		};
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor==accelerometer) {
			values[0]=(event.values[0]*FILTERING)+(values[0]*(1.0f-FILTERING));
			values[1]=(event.values[1]*FILTERING)+(values[1]*(1.0f-FILTERING));
			values[2]=(event.values[2]*FILTERING)+(values[2]*(1.0f-FILTERING));
		}
		if (event.sensor==orientation) {
			values[3]=(event.values[0]*FILTERING)+(values[3]*(1.0f-FILTERING));
			values[4]=(event.values[1]*FILTERING)+(values[4]*(1.0f-FILTERING));
			values[5]=(event.values[2]*FILTERING)+(values[5]*(1.0f-FILTERING));
		}
		String text="PriusController"+
				"\nServer: "+ address + ", " + port +
				"\nX軸方向加速度:"+values[0]+
				"\nY軸方向加速度:"+values[1]+
				"\nZ軸方向加速度:"+values[2]+
				"\n方位:"    +values[3]+
				"\nピッチ:"  +values[4]+
				"\nロール:"  +values[5];
		pitchText.setText("pitch" + values[4]);
		pitch = values[4];
		GetSensorNative.sendSensorValue((int)pitch , (int)accelerator, (int)brake);
		Log.v("XXXXXXXXXXXXXXXXXXXX", "pitch: " + (int)values[4] + "roll: " + (int)values[3]);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (accelerometer!=null) {
			sensorManager.registerListener(this,accelerometer,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (orientation!=null) {
			sensorManager.registerListener(this,orientation,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		sensorManager.unregisterListener(this);
	}

}



