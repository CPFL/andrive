package com.andrive;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import com.andrive.ui.OverlayedPreviewView;
import com.andrive.ui.PitchHandle;

public class GetSensorValues extends Activity implements OnTouchListener, SensorEventListener {
	public String address;
	public String port;
	public int port_number;

	private Button brakeButton, accelButton;

	private final static float FILTERING=0.1F;

	//センサー用変数
	private SensorManager sensorManager;
	private Sensor        accelerometer;
	private Sensor        orientation;
	private float[] values=new float[6];

	//加速度表示バー
	private ProgressBar progressBar1, progressBar2;
	
	// ピッチ表示用
	private PitchHandle pitchRing = null;

	//バーの割合
	public float rate;
	/*
	 * to Server
	 *	pitch, accel
	 * */
	private float accelerator, brake;
	private float pitch;
	
	// camera preview と overlay
	private OverlayedPreviewView overrayedPreviewView;
	
	// remember display size (in Pixel)
	private int displayHeight;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);


		//パラメータの取得
		address = getIntent().getExtras().getString("address");
		port = getIntent().getExtras().getString("port");
		if(address.length() == 0){
			//address = "133.6.204.151";
			//address = "192.168.2.227";
//			address = "172.24.15.51";
//			address = "192.168.11.3";
//			address = "192.168.5.48";
			address = "192.168.1.157";
		}
		if(port.length() == 0){ 
			port = "12345";
		}
		port_number = Integer.parseInt(port);
		Log.v("get sensor","IP Addr = " + address + ", Port = " + port);

		brakeButton = (Button)this.findViewById(R.id.main_brake_btn);
		brakeButton.setOnTouchListener(this);
		
		accelButton = (Button)this.findViewById(R.id.main_accelerate_btn);
		accelButton.setOnTouchListener(this);
		
		sensorManager=(SensorManager)getSystemService(
				Context.SENSOR_SERVICE);

		List<Sensor> list;
		list=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (list.size()>0) accelerometer=list.get(0);
		list=sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (list.size()>0) orientation=list.get(0);


		//プログレスバー
		progressBar1 = (ProgressBar) findViewById(R.id.main_accelerate_progressbar);
		progressBar1.setMax(100);
		progressBar1.setProgress(0);

		progressBar2 = (ProgressBar) findViewById(R.id.main_brake_progressbar);
		progressBar2.setMax(100);
		progressBar2.setProgress(0);

		//ピッチ、ロー表示
		pitchRing = (PitchHandle)findViewById(R.id.main_pitch_ring);
		
		overrayedPreviewView = (OverlayedPreviewView)findViewById(R.id.main_overrayed_preview);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		displayHeight = displaymetrics.heightPixels - getStatusBarHeight();
		
		
		//サーバへ接続
		GetSensorNative.connectServer(address, port_number);
	}

	public int getStatusBarHeight() {
	   int result = 0;
	   int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	   if (resourceId > 0) {
	      result = getResources().getDimensionPixelSize(resourceId);
	   }
	   return result;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
//		rate = 100 - event.getY() / 1079 * 100;
		rate = 100 - event.getY() / displayHeight * 100;
		
		//ブレーキ側
		if(v == brakeButton){
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
		else if(v == accelButton){
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
		
		String text="Andrive"+
				"\nServer: "+ address + ", " + port +
				"\nX軸方向加速度:"+values[0]+
				"\nY軸方向加速度:"+values[1]+
				"\nZ軸方向加速度:"+values[2]+
				"\n方位:"    +values[3]+
				"\nピッチ:"  +values[4]+
				"\nロール:"  +values[5];
		
		pitch = values[4];
		
		// 120度以上のpitchは無視する
		if(pitch > 120) {
			pitch = 120;
		} else if (pitch < -120) {
			pitch = -120;
		}
		
		pitchRing.rotate(pitch);

//		Log.v("Signal", "before sendSensorValue");
//		Log.v("onSensorChanged", text);

		GetSensorNative.sendSensorValue(pitch , accelerator, brake, overrayedPreviewView.getGearInNumber());

		
		GetSensorNative.getSignal();
		//Log.v("Signal", "after getSignal");

	}
	
	@Override
	public void onResume() {
		super.onResume();

		if (accelerometer!=null) {
			sensorManager.registerListener(this,accelerometer,
					SensorManager.SENSOR_DELAY_GAME);
		}
		if (orientation!=null) {
			sensorManager.registerListener(this,orientation,
					SensorManager.SENSOR_DELAY_GAME);
		}
		overrayedPreviewView.resume();
	}

	@Override
	protected void onStop() {
		super.onStop();

		sensorManager.unregisterListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		overrayedPreviewView.pause();
	}
}



