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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.andrive.ui.DrawView;
import com.andrive.ui.OverlayedPreviewView;
import com.andrive.ui.PitchHandle;

public class GetSensorValues extends Activity implements OnTouchListener, SensorEventListener, OnCheckedChangeListener {
	public String addressOfSignal, addressOfPicture;
	public String portOfSignalText, portOfPictureText;
	public int portOfSignal, portOfPicture;

	private Button speedButton, leftSteeringBotton, rightSteeringBotton;

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

	//トグルボタン(Program / Manual)
	ToggleButton pmBotton;
	// 1: Program, 0: Manual
	public static int pmFlag = 1;

	/*
	 * to Server
	 *	pitch, accel
	 * */
	public static float accelerator;
	public static float sterring;
	private float pitch;

	// camera preview と overlay
	private OverlayedPreviewView overrayedPreviewView;

	// 画像受信スレッド
	BackGroundThread backGroundThread;

	// Displayサイズ
	int displayWidth, displayHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_ui);

		// ウィンドウマネージャのインスタンス取得
		WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		// ディスプレイのインスタンス生成
		Display disp = wm.getDefaultDisplay();
		displayWidth = disp.getWidth();
		displayHeight = disp.getHeight() - getStatusBarHeight();
		Log.v("Display", "Width = " + disp.getWidth() + ", Height = " + (disp.getHeight() - getStatusBarHeight()) );

		//パラメータの取得
		addressOfSignal = getIntent().getExtras().getString("addressOfSignal");
		addressOfPicture = getIntent().getExtras().getString("addressOfPicture");

		portOfSignalText = getIntent().getExtras().getString("portOfSignal");
		portOfPictureText = getIntent().getExtras().getString("portOfPicture");

		if(addressOfSignal.length() == 0){
			//address = "133.6.204.151";
			addressOfSignal = "192.168.2.227";
			//address = "192.168.24.52";
			//			address = "172.24.15.51";
			//			address = "192.168.11.3";
			//			address = "192.168.5.48";
			//			address = "192.168.1.157";
		}
		if(addressOfPicture.length() == 0){
			//address = "133.6.204.151";
			addressOfPicture = "192.168.2.227";
			//address = "192.168.24.52";
			//			address = "172.24.15.51";
			//			address = "192.168.11.3";
			//			address = "192.168.5.48";
			//			address = "192.168.1.157";
		}

		if(portOfSignalText.length() == 0){ 
			portOfSignalText = "12335";
		}
		if(addressOfPicture.length() == 0){ 
			addressOfPicture = "12336";
		}

		portOfSignal = Integer.parseInt(portOfSignalText);
		Log.v("Andrive_signal","IP Addr = " + addressOfSignal + ", Port = " + portOfSignal);
		portOfPicture = Integer.parseInt(portOfPictureText);
		Log.v("Andrive_picture","IP Addr = " + addressOfPicture + ", Port = " + portOfPicture);

		speedButton = (Button)this.findViewById(R.id.speed_bar);
		speedButton.setOnTouchListener(this);

		leftSteeringBotton = (Button)this.findViewById(R.id.steering_left_bar);
		leftSteeringBotton.setOnTouchListener(this);

		rightSteeringBotton = (Button)this.findViewById(R.id.steering_right_bar);
		rightSteeringBotton.setOnTouchListener(this);

		//		sensorManager=(SensorManager)getSystemService(
		//				Context.SENSOR_SERVICE);
		//
		//		List<Sensor> list;
		//		list=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		//		if (list.size()>0) accelerometer=list.get(0);
		//		list=sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		//		if (list.size()>0) orientation=list.get(0);


		//プログレスバー
		progressBar1 = (ProgressBar) findViewById(R.id.acc_bar);
		progressBar1.setMax(100);
		progressBar1.setProgress(0);

		progressBar2 = (ProgressBar) findViewById(R.id.brake_bar);
		progressBar2.setMax(100);
		progressBar2.setProgress(0);

		//ピッチ、ロー表示
		pitchRing = (PitchHandle)findViewById(R.id.main_pitch_ring);
		overrayedPreviewView = (OverlayedPreviewView)findViewById(R.id.main_overrayed_preview);

		//トグルボタン
		pmBotton = (ToggleButton) findViewById(R.id.PM_botton);
		pmBotton.setOnCheckedChangeListener(this);


		//サーバへ接続
		GetSensorNative.connectServer(addressOfSignal, portOfSignal);
		GetSensorNative.connectServer(addressOfPicture, portOfPicture);

		//画像受信スレッド
		backGroundThread = new BackGroundThread();
		backGroundThread.setFlag(true);
		backGroundThread.start();

		pitch = 0; 

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

		//speed
		if(v == speedButton){
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				// 指がタッチした時の処理を記述
				accelerator = (50 - event.getY() / displayHeight * 100) * 2;
				makePacket(1);
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				// 指がスライドした時の処理を記述
				accelerator = (50 - event.getY() / displayHeight * 100) * 2;
				makePacket(1);
			}
			else {
				// タッチした指が離れた時の処理を記述
				accelerator = 0;
				progressBar1.setProgress(0);
				progressBar2.setProgress(0);
				makePacket(1);
			}
		}

		//ステアリング(left)
		else if(v == leftSteeringBotton){
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				// 指がタッチした時の処理を記述
				sterring = (float) (100 - event.getX() / (displayWidth * 0.302) * 100);
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				// 指がスライドした時の処理を記述
				sterring = (float) (100 - event.getX() / (displayWidth * 0.302) * 100);
			}
			else {
				// タッチした指が離れた時の処理を記述
				sterring = 0;
				return true;
			}

			//左ハンドルのためマイナス反転
			if(sterring > 100)
				sterring = 100;
			else if(sterring < 0)
				sterring = 0;
			if(sterring != 0)
				sterring = -sterring;


			//			if(sterring > 0)
			//				pitch += (sterring/10) * (sterring/10) / 10;
			//			else if(sterring < 0)
			//				pitch -= (sterring/10) * (sterring/10) / 10;

			//Log.v("pitch", "" + (sterring/10) * (sterring/10) / 10);

			//			if(pitch > 100)
			//				pitch = -100;


			//相対値
			//pitchRing.rotate(-(float)(pitch * 1.6));
			//絶対値
			pitchRing.rotate(-(float)(sterring * 1.6));
			//Log.v("pitch", "" + pitch);
			makePacket(2);				
		}

		else if(v == rightSteeringBotton){
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				// 指がタッチした時の処理を記述
				sterring = (float) ((event.getX() - displayWidth * 0.302 ) / (displayWidth * 0.302) * 100 + 100);
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				// 指がスライドした時の処理を記述
				sterring = (float) ((event.getX() - displayWidth * 0.302 ) / (displayWidth * 0.302) * 100 + 100);
			}
			else {
				// タッチした指が離れた時の処理を記述
				sterring = 0;
				return true;
			}

			if(sterring > 100)
				sterring = 100;
			else if(sterring < 0)
				sterring = 0;

			//相対値
			//pitchRing.rotate(-(float)(pitch * 1.6));
			//絶対値
			pitchRing.rotate(-(float)(sterring * 1.6));
			//Log.v("pitch", "" + pitch);
			makePacket(2);		
		}

		return true;
	}

	void makePacket(int type){
		switch(type){
		case 1:
			if(accelerator > 0){
				progressBar1.setProgress((int)accelerator);
				progressBar2.setProgress(0);
				if(accelerator > 100)
					accelerator = 100;
				//相対値
				//GetSensorNative.sendSensorValue(pitch, accelerator, 0, overrayedPreviewView.getGearInNumber(), pmFlag);
				//Log.v("pitch", "" + pitch);
				//絶対値
				GetSensorNative.sendSensorValue(sterring, accelerator, 0, overrayedPreviewView.getGearInNumber(), pmFlag);
				Log.v("sterring", "" + sterring);

			}
			else{
				progressBar1.setProgress(0);
				progressBar2.setProgress(-(int)accelerator);
				//相対値
				//GetSensorNative.sendSensorValue(pitch, 0, -accelerator, overrayedPreviewView.getGearInNumber(), pmFlag);
				//Log.v("pitch", "" + pitch);
				//絶対値
				GetSensorNative.sendSensorValue(sterring, 0, -accelerator, overrayedPreviewView.getGearInNumber(), pmFlag);
				Log.v("sterring", "" + sterring);

			}
			break;
		case 2:
			if(sterring > 0){
				progressBar1.setProgress((int)accelerator);
				progressBar2.setProgress(0);

				//相対値
				//GetSensorNative.sendSensorValue(pitch, accelerator, 0, overrayedPreviewView.getGearInNumber(), pmFlag);
				//Log.v("pitch", "" + pitch);
				//絶対値
				GetSensorNative.sendSensorValue(sterring, accelerator, 0, overrayedPreviewView.getGearInNumber(), pmFlag);
				Log.v("sterring", "" + sterring);
			}
			else{
				progressBar1.setProgress(0);
				progressBar2.setProgress(-(int)accelerator);
				//相対値
				//GetSensorNative.sendSensorValue(pitch, 0, -accelerator, overrayedPreviewView.getGearInNumber(), pmFlag);
				//Log.v("pitch", "" + pitch);
				//絶対値
				GetSensorNative.sendSensorValue(sterring, 0, -accelerator, overrayedPreviewView.getGearInNumber(), pmFlag);
				Log.v("sterring", "" + sterring);
			}
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//		OnClickListener listener = new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				onStop();
		//			}
		//		};
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//		if (event.sensor==accelerometer) {
		//			values[0]=(event.values[0]*FILTERING)+(values[0]*(1.0f-FILTERING));
		//			values[1]=(event.values[1]*FILTERING)+(values[1]*(1.0f-FILTERING));
		//			values[2]=(event.values[2]*FILTERING)+(values[2]*(1.0f-FILTERING));
		//		}
		//		if (event.sensor==orientation) {
		//			values[3]=(event.values[0]*FILTERING)+(values[3]*(1.0f-FILTERING));
		//			values[4]=(event.values[1]*FILTERING)+(values[4]*(1.0f-FILTERING));
		//			values[5]=(event.values[2]*FILTERING)+(values[5]*(1.0f-FILTERING));
		//		}
		//		
		//		String text="Andrive"+
		//				"\nServer: "+ address + ", " + port +
		//				"\nX軸方向加速度:"+values[0]+
		//				"\nY軸方向加速度:"+values[1]+
		//				"\nZ軸方向加速度:"+values[2]+
		//				"\n方位:"    +values[3]+
		//				"\nピッチ:"  +values[4]+
		//				"\nロール:"  +values[5];
		//		
		//		pitch = values[4];
		//		
		//		// 120度以上のpitchは無視する
		//		if(pitch > 120) {
		//			pitch = 120;
		//		} else if (pitch < -120) {
		//			pitch = -120;
		//		}
		//		
		//		pitchRing.rotate(pitch);

		//		Log.v("Signal", "before sendSensorValue");
		//		Log.v("onSensorChanged", text);

		//GetSensorNative.sendSensorValue(pitch , accelerator, brake, overrayedPreviewView.getGearInNumber(), pmFlag);


		//GetSensorNative.getSignal();
		//Log.v("Signal", "after getSignal");
	}

	//pushed toggle button
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			Log.v("Toggle", "On");
			pmFlag = 1;
			GetSensorNative.sendSensorValue(sterring, accelerator, 0, overrayedPreviewView.getGearInNumber(), pmFlag);
		}else{
			Log.v("Toggle", "Off");
			pmFlag = 0;
			GetSensorNative.sendSensorValue(sterring, accelerator, 0, overrayedPreviewView.getGearInNumber(), pmFlag);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		//		if (accelerometer!=null) {
		//			sensorManager.registerListener(this,accelerometer,
		//					SensorManager.SENSOR_DELAY_GAME);
		//		}
		//		if (orientation!=null) {
		//			sensorManager.registerListener(this,orientation,
		//					SensorManager.SENSOR_DELAY_GAME);
		//		}
		overrayedPreviewView.resume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		backGroundThread.setFlag(false);
		GetSensorNative.closeConnect();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		overrayedPreviewView.pause();
	}
}



