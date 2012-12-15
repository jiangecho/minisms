package com.minicontact;

import com.minicontact.AlphaNavView.AlphaNavListener;
import com.minisms.ConversationActivity;
import com.minisms.MiniGestureListener;
import com.minisms.MiniService;
import com.minisms.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ContactListActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnTouchListener{

	private ListView listView;
	private ContactListAdapter adapter;
	private GestureDetector gestureDetector;
	private MiniService mMiniService;
	
	private TextView overLay;
	private AlphaNavView alphaNavView;
	private Handler handler;
	private OverLayThread overLayThread;
	private String text;
	
	private boolean alertUp = false;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mMiniService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mMiniService = ((MiniService.MiniBinder)service).getService();
			adapter = mMiniService.getContactListAdapter();
			listView.setAdapter(adapter);
			Log.i("jiang adapter  xxx", "setadapter xxx");
			adapter.notifyDataSetChanged();
		}
	};
	
	private void doBindService(){
		bindService(new Intent(this, MiniService.class), mConnection, 0);
	}
	
	private void unBindService(){
		unbindService(mConnection);
			
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		doBindService();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.contact_list);
		initOverlay();
		listView = (ListView)findViewById(R.id.lv_contact_list);
		alphaNavView = (AlphaNavView)findViewById(R.id.alphaNavView);
		alphaNavView.setAlphaNavListener(new MiniAlphaNavListner());
		listView.setOnItemClickListener(this);
		listView.setOnTouchListener(this);
		listView.setOnItemLongClickListener(this);
		gestureDetector = new GestureDetector(new MiniGestureListener(this));
		
		text = getIntent().getStringExtra("TEXT");
		handler = new Handler();
		overLayThread = new OverLayThread();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("jiang adapter  yyy", "setadapter yyy");
		final WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		super.onDestroy();
		unBindService();
		windowManager.removeView(overLay);
		adapter.unRegisterDataSetObserver();
		handler = null;
		overLay = null;
		listView = null;
		adapter = null;
		gestureDetector = null;
		mMiniService = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int thread_id = -1;
		final ContactEntity entity = (ContactEntity)adapter.getItem(position);
		final String number = entity.getNumber();
		final String[] projection = {"_id", "thread_id"};
		//String selection = "address='" + number + "'";
		final StringBuilder selectionBuilder = new StringBuilder();
		selectionBuilder.append("address='");
		selectionBuilder.append(number);
		selectionBuilder.append("'");
		
		if (number.startsWith("+86")) {
			//selection += " or address='" + number.substring(3) +"'";
			selectionBuilder.append(" or address='");
			selectionBuilder.append(number.substring(3));
			selectionBuilder.append("'");
		}else {
			//selection += " or address='" + "+86" +  number +"'";
			selectionBuilder.append(" or address='");
			selectionBuilder.append("+86");
			selectionBuilder.append(number);
			selectionBuilder.append("'");
		}
		
		final Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), projection, selectionBuilder.toString() , null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				thread_id = cursor.getInt(1);
			}
			cursor.close();
		}
		
		final Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra("NAME", entity.getName());
		intent.putExtra("PHONENUMBER", entity.getNumber());
		intent.putExtra("THREAD_ID", thread_id);
		intent.putExtra("TEXT", text);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		alertUp = true;
		final ContactEntity entity = (ContactEntity)adapter.getItem(position);
		final String number = entity.getNumber();
		final String[] options = getResources().getStringArray(R.array.contact_list_options);
		final AlertDialog dialog = new AlertDialog.Builder(ContactListActivity.this)
							.setItems(options, new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									call(number);
								}
							})
							.setTitle(entity.getName())
							.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(true);
		
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				alertUp = false;
				
			}
		});
		return true;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}


	public void swipeToRight(){
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	public void swipeToLeft(){
		finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if ((gestureDetector != null) && (!alertUp)) {
			gestureDetector.onTouchEvent(event);
		}
		return false;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
    private void initOverlay() {
    	final LayoutInflater inflater = LayoutInflater.from(this);
    	overLay = (TextView) inflater.inflate(R.layout.overlay, null);
    	overLay.setVisibility(View.INVISIBLE);
		final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		final WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overLay, lp);
    }
    
	private class OverLayThread implements Runnable{

		@Override
		public void run() {
			if (overLay != null) {
				overLay.setVisibility(View.GONE);
			}
		}
		
	}
	
	private class MiniAlphaNavListner implements AlphaNavListener{

		@Override
		public void navToSection(String selectedSection) {
			// TODO Auto-generated method stub
			final int position = adapter.getPositionForSection(selectedSection);
			overLay.setText(selectedSection);
			overLay.setVisibility(View.VISIBLE);
			handler.removeCallbacks(overLayThread);
			handler.postDelayed(overLayThread, 500);
			if (position != -1) {
				listView.setSelection(position);
			}
		}
		
	}
	
	private void call(String number){
		final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		startActivity(intent);
		
	}
	
	public void onHeadClick(View view){
		
	}
}
