package com.andrive.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.andrive.R;

public class OverlayedPreviewView extends FrameLayout {
	private final Context _context;
	Handler handler= new Handler();
	private	Bitmap	_bmpImageFile;		//image file

	/* test */ 
	static final long FPS = 20;
	static final long FRAME_TIME = 1000 / FPS;
	static final int BALL_R = 30;
	Thread thread;
	int cx = BALL_R, cy = BALL_R;
	int screen_width, screen_height;
	Canvas canvas;
	View v;
	
	protected Paint innerPaint;
	/* test */
	
	
	//preview size
	int preview_width = 0, preview_height = 0;

	// Is camera Previewing?
	private boolean inPreview;

	// Camera Preview逕ｨ縺ｮSurfaceView
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	private Camera camera;

	// Gear Box
	private GearBoxView gearBox;

	public OverlayedPreviewView(Context context) {
		this(context,null);
	}

	public OverlayedPreviewView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}	

	public OverlayedPreviewView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_context = context;
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		inPreview = false;
		LayoutInflater inflator = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflator.inflate(R.layout.overrayed_preview_view, this);

		// CameraPreview
		preview = (SurfaceView)v.findViewById(R.id.preview_surface_view);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		this.setLayoutParams(new LayoutParams
                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.innerPaint = new Paint(); 
        this.innerPaint.setARGB(225, 75, 75, 75); 
        this.innerPaint.setAntiAlias(true); 

		//		/* --------------------- test ------------------------------ */
		//		int re = 0;
		//		canvas = previewHolder.lockCanvas();
		//		if(LoadImageFile("/mnt/sdcard/recieve.jpg",true) == false){
		//			previewHolder.unlockCanvasAndPost(canvas);
		//			Log.v("test", "loadimage");
		//		}
		//		
		//		if(re == 0){
		//			//preview image
		//			preview_width = 1357;
		//			preview_height = 731;
		//			if(preview_width > 0 || preview_height > 0){
		//				Log.v("canvas draw", "xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		//				_bmpImageFile = Bitmap.createScaledBitmap(_bmpImageFile, preview_width, preview_height , true);
		//				canvas.drawBitmap(_bmpImageFile, 0, 0, null);
		//			}
		//		}
		//		
		//		/* --------------------- test ------------------------------ */


		// gear box
		gearBox = (GearBoxView)v.findViewById(R.id.preview_gear_box);

		//		new Thread(new Runnable() {
		//			@Override
		//			public void run() {
		//				// マルチスレッドにしたい処理 ここから
		//				while(true){
		//				handler.post(new Runnable() {
		//					@Override
		//					public void run() {
		//						Log.v("testset", "tsetsetst");
		//					}
		//				});
		//				}
		//
		//				// マルチスレッドにしたい処理 ここまで
		//			}
		//		}).start();
	}

	private boolean LoadImageFile(String strFile,final boolean bInvalidate) {
		//load image
		_bmpImageFile = BitmapFactory.decodeFile(strFile);

		if(_bmpImageFile == null)
			return false;

		return	(_bmpImageFile != null) ? true : false;
	}

	public int getGearInNumber() {
		return gearBox.getGearInNumber();
	}

	public void pause() {
		if (inPreview) {
//			camera.stopPreview();
			inPreview = false;
//			camera.release();
			camera = null;
		}
	}

	public void resume() {
		Log.d("camera","camera =" +Camera.getNumberOfCameras());
		//	    camera=Camera.open();
		//	    startPreview();
	}

	private void initPreview(int width, int height) {
		//    	Log.v("initPreview","######################");
		//		if(camera != null && previewHolder.getSurface() != null) {
		//			try {
		//		    	Log.v("initPreview","######################");
		//				camera.setPreviewDisplay(previewHolder);
		//			} catch(Throwable e) {
		//				e.printStackTrace();
		//			}
		//	
		//			Camera.Parameters parameters = camera.getParameters();
		//			Camera.Size bestSize = null;
		//			for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
		//				if(size.width <= width && size.height <= height) {
		//					if(bestSize == null) {
		//						bestSize = size;
		//					} else {
		//						int bestArea = bestSize.width * bestSize.height;
		//						int checkArea = size.width * size.height;
		//						if(checkArea > bestArea) { 
		//							bestSize = size;
		//						}
		//					}
		//				}
		//			}
		//	
		//			if(bestSize != null) {	
		//				parameters.setPreviewSize(bestSize.width, bestSize.height);
		//				camera.setParameters(parameters);
		//			}
		//		}
	}

	private void startPreview() {
		if(camera!=null) {
			//			camera.startPreview();
			inPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override public void surfaceDestroyed(SurfaceHolder holder) {
			if(camera != null) {
				//				camera.stopPreview();
				//				camera.release();
				camera = null;
			}
		}

		@Override public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			initPreview(width, height);
			Log.d("SurfaceChanged", "width="+width+", height="+height);
			//			startPreview();
		}

	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		// Viewのサイズを取得
		preview = (SurfaceView)v.findViewById(R.id.preview_surface_view);
		preview_width = preview.getWidth();
		preview_height = preview.getHeight();

		//Log.v("onWindowFocusChanged", "preview width=" + preview_width + ", preview=" + preview_height);
	}
	
	@Override
    protected void dispatchDraw(Canvas canvas) {
        RectF drawRect = new RectF(); 
        drawRect.set(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight()); 
        Log.v("xxxxxxxxxxxxxxxx", "getMeasuredWidth " + this.getMeasuredWidth() + "getMeasuredHeight() " + this.getMeasuredHeight());
        canvas.drawRoundRect(drawRect, 5, 5, this.innerPaint); 

        super.dispatchDraw(canvas);
    }

}