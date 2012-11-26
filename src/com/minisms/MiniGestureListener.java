package com.minisms;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class MiniGestureListener extends SimpleOnGestureListener{

    private static final int SWIPE_MIN_DISTANCE = 120;   
    private static final int SWIPE_MAX_OFF_PATH = 250;   
    private static final int SWIPE_THRESHOLD_VELOCITX = 200;  	
    
	Activity ownerActivity;
	public MiniGestureListener(Activity activity){
		super();
		this.ownerActivity = activity;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.i("jiang", "onFling");
		if ((Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE) 
				&& (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITX)) {
			Log.i("jiang", "swipe left or right");
			
			if(ownerActivity instanceof MiniSMSActivity){
				((MiniSMSActivity)ownerActivity).swipe();
			}
			
		}else {
			super.onFling(e1, e2, velocityX, velocityY);
		}
		return true; 
	}


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.i("jiang", "onDown");
		return super.onDown(e);
	}
}
