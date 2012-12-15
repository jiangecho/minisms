package com.minisms;

import com.minicontact.ContactListActivity;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class MiniGestureListener extends SimpleOnGestureListener{

    private static final int SWIPE_MIN_DISTANCE = 120;   
    //private static final int SWIPE_MAX_OFF_PATH = 250;   
    private static final int SWIPE_THRESHOLD_VELOCITX = 200;  	
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;  	
    
    private static final int SWIPE_TO_LEFT = 0;
    private static final int SWIPE_TO_RIGHT = 1;
    private static final int SWIPE_TO_TOP = 2;
    private static final int SWIPE_TO_BOTTOM = 3;
    
    
	Activity ownerActivity;
	public MiniGestureListener(Activity activity){
		super();
		this.ownerActivity = activity;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int direction = swipe(e1, e2, velocityX, velocityY);
		Log.i("jiang", "swipe xxxxx  " + direction);
		
		if (ownerActivity instanceof MiniSMSActivity) {
			switch (direction) {
			case SWIPE_TO_LEFT:
				((MiniSMSActivity)ownerActivity).swipeToLeft();
				break;
			case SWIPE_TO_RIGHT:
				((MiniSMSActivity)ownerActivity).swipeToRight();
				break;
			case SWIPE_TO_TOP:
				((MiniSMSActivity)ownerActivity).swipeToTop();
				break;
			case SWIPE_TO_BOTTOM:
				((MiniSMSActivity)ownerActivity).swipeToBottom();
				break;
			default:
				break;
			}
			
		}else if (ownerActivity instanceof ConversationListActivity) {
			switch (direction) {
			case SWIPE_TO_LEFT:
				((ConversationListActivity)ownerActivity).swipeToLeft();
				break;
			case SWIPE_TO_RIGHT:
				((ConversationListActivity)ownerActivity).swipeToRight();
				break;
			default:
				break;
			}
			
		}else if(ownerActivity instanceof ContactListActivity){
			switch (direction) {
			case SWIPE_TO_LEFT:
				((ContactListActivity)ownerActivity).swipeToLeft();
				break;
			case SWIPE_TO_RIGHT:
				((ContactListActivity)ownerActivity).swipeToRight();
				break;
			default:
				break;
			}
			
		}
		
		return true; 
	}


	private int swipe(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY){
		int ret = -1;
		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE 
				&& (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITX)) {
			ret = SWIPE_TO_LEFT;
		}else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITX)) {
			ret = SWIPE_TO_RIGHT;
		}else if((e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) 
				&& (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)){
			ret = SWIPE_TO_TOP;
		}else if((e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) 
				&& (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)){
			ret = SWIPE_TO_BOTTOM;
		}
		return ret;
	}
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.i("jiang", "onDown");
		return super.onDown(e);
	}
}
