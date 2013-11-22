package com.andrive.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.andrive.GetSensorNative;
import com.andrive.GetSensorValues;
import com.andrive.R;

public class GearBoxView extends FrameLayout implements OnTouchListener {
	private final Context _context;
	private ImageView thumbImageView, barImageView;
	private Bitmap thumbBitmap, barBitmap;
	private final List<Float> absoluteGearPositions;
	private Gear gear = Gear.NEUTRAL;

	private final static int GEAR_BRAKE = 0;
	private final static int GEAR_REVERSE = 1;
	private final static int GEAR_NEUTRAL = 2;
	private final static int GEAR_DRIVE = 3;
	
	
	public GearBoxView(Context context) {
		this(context,null);
	}
	
	public GearBoxView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}	
 	
    public GearBoxView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	_context = context;
		absoluteGearPositions = new ArrayList<Float>();
    	initView();
    }
    
	private void initView() {		
		prepareImages();

		float barBottom = barImageView.getBottom();
		float step = barBitmap.getHeight()/(Gear.values().length-1);
		int count = 0;
		for( Gear gear : Gear.values() ) {
			setGearPosition(gear, barBottom+count*step);
			count++;
		}
		
		this.setWillNotDraw(false);
		setOnTouchListener(this);
	}
	
	private void prepareImages() {
		thumbImageView = new ImageView(_context);
		thumbBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.gear_thumb);
		thumbImageView.setImageBitmap(thumbBitmap);

		barImageView = new ImageView(_context);
		barBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.gear_bar);
		barImageView.setImageBitmap(barBitmap);
	}
	
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.save();
		canvas.drawBitmap(barBitmap, barImageView.getLeft(), barImageView.getTop()+thumbBitmap.getHeight()/2, null);
		canvas.drawBitmap(thumbBitmap, thumbImageView.getLeft()-thumbBitmap.getWidth()/2+barBitmap.getWidth()/2,
				getGearPosition(gear), null);
		canvas.restore();
		Log.v("Gear", "" + barImageView.getLeft() + "," + barImageView.getTop()+thumbBitmap.getHeight()/2);
	}
	
	private void setGearPosition(Gear g, float pos) {
		if(absoluteGearPositions == null) {
			return;
		}
		switch(g) {
		case BRAKE:
			absoluteGearPositions.add(GEAR_BRAKE, pos);
			return;
		case REVERSE:
			absoluteGearPositions.add(GEAR_REVERSE, pos);
			return;			
		case NEUTRAL:
			absoluteGearPositions.add(GEAR_NEUTRAL, pos);
			return;			
		case DRIVE:
			absoluteGearPositions.add(GEAR_DRIVE, pos);
			return;
		default:
			assert(false);
		}
	}
	
	private float getGearPosition(Gear g) {
		switch(g) {
		case BRAKE:
			return absoluteGearPositions.get(GEAR_BRAKE);
		case REVERSE:
			return absoluteGearPositions.get(GEAR_REVERSE);
		case NEUTRAL:
			return absoluteGearPositions.get(GEAR_NEUTRAL);
		case DRIVE:
			return absoluteGearPositions.get(GEAR_DRIVE);
		default:
			assert(false);
			return -1;
		}
	}
	
	public int getGearInNumber() {
		switch(gear) {
		case BRAKE: 
			return GEAR_BRAKE;
		case REVERSE:
			return GEAR_REVERSE;
		case NEUTRAL:
			return GEAR_NEUTRAL;
		case DRIVE:
			return GEAR_DRIVE;
		default:
			assert(false);
			return -1;
		}
	}
	
	private Gear culcNearestGearForTouchedPoint(float point) {
		float minDist = -1F;
		Gear nearestGear = Gear.NEUTRAL;
		
		for( Gear g : Gear.values() ) {
			if (minDist == -1F) {
				minDist = Math.abs(getGearPosition(g)-point);
				nearestGear = g;
		   } else if(Math.abs(getGearPosition(g)-point) < minDist) {
				minDist = Math.abs(getGearPosition(g)-point);
				nearestGear = g;
			}
		}
		return nearestGear;
	}
	
    @Override
    public boolean onTouch(View view, MotionEvent event) {
    	switch(event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    	case MotionEvent.ACTION_POINTER_DOWN:
    	case MotionEvent.ACTION_MOVE:
    		gear = culcNearestGearForTouchedPoint(event.getY());
    		invalidate();
    		return true;
    	}
    	//gear change
		GetSensorNative.sendSensorValue(GetSensorValues.sterring, GetSensorValues.accelerator, 0, getGearInNumber(), GetSensorValues.pmFlag);
    	return false;	
    }
    
	private enum Gear {
		BRAKE,
		REVERSE,
		NEUTRAL,
		DRIVE		
	}	
}
