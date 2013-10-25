package com.andrive;

import android.util.Log;

import com.andrive.ui.OverlayedPreviewView;

public class BackGroundThread extends Thread{
	boolean threadFlag;
	String log;

	public void setFlag(boolean flag){
		threadFlag = flag;
	}

	public void run(){
		while(threadFlag){
			log = GetSensorNative.getPhoto();
			Log.v("BackGroundThread","run + " + log);
			OverlayedPreviewView.drawView.setPreview();
		}
	}
}