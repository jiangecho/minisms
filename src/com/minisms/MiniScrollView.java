package com.minisms;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MiniScrollView extends ScrollView{

	public MiniScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MiniScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MiniScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.i("jiang", "MyScrollView onTouchEvent");
		super.onTouchEvent(ev);
		//to support swipe left and right
		return false;
	}
	

}
