package com.minicontact;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



public class AlphaNavView extends View {
	
	private AlphaNavListener alphaNavListener;
	private final String[] sections = {"#","A","B","C","D","E","F","G","H","I","J","K","L"
			,"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","@"};
	
	boolean showBkg = false;
	int curSelectedSection = -1;
	private final Paint paint = new Paint();

	public AlphaNavView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AlphaNavView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphaNavView(Context context) {
		super(context);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(showBkg){
		    canvas.drawColor(Color.parseColor("#40000000"));
		}
		
	    final int height = getHeight();
	    final int width = getWidth();
	    final int singleHeight = height / sections.length;
	    float xPos, yPos;
	    
	    paint.setTextSize(singleHeight);
	    paint.setAntiAlias(true);
	    for(int i=0;i<sections.length;i++){
	       if(i != curSelectedSection){
	    	   paint.setColor(Color.parseColor("#696969"));
	       }else {
	    	   paint.setColor(Color.parseColor("#3399FF"));
	       }
	       
	       xPos = width/2  - paint.measureText(sections[i])/2;
	       yPos = singleHeight * i + singleHeight;
	       canvas.drawText(sections[i], xPos, yPos, paint);
	       //paint.reset();
	    }
	   
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
	    final float yCoordinate = event.getY();
	    final int oldChoose = curSelectedSection;
	    final AlphaNavListener listener = alphaNavListener;
	    final int selectedSection = (int) (yCoordinate/getHeight()*sections.length);
	    
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				showBkg = true;
				if(oldChoose != selectedSection && listener != null){
					if(selectedSection > 0 && selectedSection < sections.length){
						listener.navToSection(sections[selectedSection]);
						curSelectedSection = selectedSection;
						invalidate();
					}
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				if(oldChoose != selectedSection && listener != null){
					if(selectedSection > 0 && selectedSection < sections.length){
						listener.navToSection(sections[selectedSection]);
						curSelectedSection = selectedSection;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				showBkg = false;
				curSelectedSection = -1;
				invalidate();
				break;
			default:
					break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setAlphaNavListener(
			final AlphaNavListener alphaNavListener) {
		this.alphaNavListener = alphaNavListener;
	}

	public interface AlphaNavListener{
		void navToSection(String selectedSection);
	}
	
}
