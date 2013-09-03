package com.andrive.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.andrive.R;

public class PitchHandle extends FrameLayout {
	private final Context _context;
	private ImageView pitchRing;
	private Bitmap pitchBitmap;
	private Bitmap warningBitmap;
	private boolean inWarn;
	private float threshold;
	private float pitch;
	private float previousPitch;
	// Timer
	private Timer timer;
	
	public PitchHandle(Context context) {
		this(context,null);
	}
	
	public PitchHandle(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}	
 	
    public PitchHandle(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	_context = context;
		init();
    }
    
    private void init() {
    	inWarn = false;
    	threshold = 60F; // TODO : 変更可能に

		timer = new Timer();
		timer.schedule(new MonitorSensorValue(), 100, 200);
    	
		prepareImages();
		this.addView(pitchRing);
		
		this.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, Gravity.CENTER));
				
		this.setWillNotDraw(false);
    }
    
    private void prepareImages() {
    	pitchRing = new ImageView(_context);
		pitchBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.pitch_ring);
		
		pitchRing.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, Gravity.CENTER));
		pitchRing.setImageBitmap(pitchBitmap);
		
		
		warningBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.warning);
    }
    
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.save();
		if(inWarn) {
			canvas.drawBitmap(warningBitmap, pitchRing.getLeft()-warningBitmap.getWidth()/2+pitchBitmap.getWidth()/2,
					pitchRing.getTop()-warningBitmap.getHeight()/2+pitchBitmap.getHeight()/2, null);
		}
		canvas.restore();
	}
    
	public void rotate(float pitch) {
		if (!detectIntenseSensorChanging(pitch, previousPitch)) {
			inWarn = false;
			this.pitch = pitch;
			Matrix matrix = new Matrix();
			pitchRing.setScaleType(ScaleType.MATRIX);
			matrix.preRotate( -pitch, pitchRing.getDrawable().getBounds().width()/2,
					pitchRing.getDrawable().getBounds().height()/2);
			pitchRing.setImageMatrix(matrix);
			invalidate();
		} else {
			inWarn = true;
		}
	}

//	@Override
//    public boolean onTouch(View view, MotionEvent event) {
//		switch(event.getAction()) {
//    	case MotionEvent.ACTION_UP:
//    	case MotionEvent.ACTION_POINTER_UP:
//    		return true;
//		default:
//			// pass
//    	}
//    	return false;	
//    }
	
	public void setWarning(boolean b) {
		inWarn = b;
	}
	
	public void stop() {
		pitchRing = null;
	}
	
	public void resume() {
		prepareImages();
	}
	
	private boolean detectIntenseSensorChanging(float current, float previous) {
		if(Math.abs(current - previous) > threshold ) {
			return true;
		} else {
			return false;
		}
	}
	
//	private void changeMonitorInterval(int milsec) {
//		timer.schedule(new MonitorSensorValue(), 100, milsec);
//	}
	
	class MonitorSensorValue extends TimerTask {
		   public void run() {
			   if(!inWarn) {
				   previousPitch = pitch;
			   }
		   }
	}
}
