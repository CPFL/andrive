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

	View v;
	
	protected Paint innerPaint;	
	
	//preview size
	static int preview_width = 0;
	static int preview_height = 0;

	// Camera PreviewÁî®„ÅÆSurfaceView
	private SurfaceView preview;
	private SurfaceHolder previewHolder;

	// Gear Box
	private GearBoxView gearBox;
	public static DrawView drawView;
	
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
		LayoutInflater inflator = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflator.inflate(R.layout.overrayed_preview_view, this);
		
		this.setLayoutParams(new LayoutParams
                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.innerPaint = new Paint(); 
        this.innerPaint.setARGB(225, 75, 75, 75); 
        this.innerPaint.setAntiAlias(true); 

		// gear box
		gearBox = (GearBoxView)v.findViewById(R.id.preview_gear_box);
		drawView = (DrawView)v.findViewById(R.id.preview_surface_view);
	}


	public int getGearInNumber() {
		return gearBox.getGearInNumber();
	}
	

	public void pause() {
	}

	public void resume() {
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		// ViewÇÃÉTÉCÉYÇéÊìæ
		//preview = (SurfaceView)v.findViewById(R.id.preview_surface_view);
		preview_width = drawView.getWidth();
		preview_height = drawView.getHeight();

		Log.v("onWindowFocusChanged", "preview width=" + preview_width + ", preview=" + preview_height);
	}
}