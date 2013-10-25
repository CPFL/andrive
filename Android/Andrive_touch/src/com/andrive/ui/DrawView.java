package com.andrive.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.andrive.R;

public class DrawView extends SurfaceView implements Callback {
	private final Context _context;
	private ImageView preview;
	private Bitmap _bmpImageFile;
	private SurfaceHolder holder;
	Handler  mHandler   = new Handler();

	int screen_width, screen_height;

	Resources res = this.getContext().getResources();
	Bitmap grass = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);

	public DrawView(Context context) {
		this(context,null);
	}

	public DrawView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}	

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_context = context;
		holder = getHolder();
		holder.addCallback(this);	
		setWillNotDraw(false);
	}


	private boolean LoadImageFile(String strFile,final boolean bInvalidate) {
		_bmpImageFile = BitmapFactory.decodeFile(strFile);
		if(_bmpImageFile != null)
			_bmpImageFile = Bitmap.createScaledBitmap(_bmpImageFile, screen_width, screen_height, true);
		return	(_bmpImageFile != null) ? true : false;
	}

	private void prepareImages() {
		preview = new ImageView(_context);
		LoadImageFile("/mnt/sdcard/recieve.jpg",true);
		//_bmpImageFile = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_launcher);
		preview.setImageBitmap(_bmpImageFile);

	}


	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas = holder.lockCanvas();
		prepareImages();
		if(_bmpImageFile != null)
			canvas.drawBitmap(_bmpImageFile, 0, 0, null);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		screen_width = width;
		screen_height = height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setPreview() {
		// TODO 自動生成されたメソッド・スタブ
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				invalidate();		//to update the display
			}
		});
	}


}
