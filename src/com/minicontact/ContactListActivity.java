package com.minicontact;

import com.minisms.ConversationActivity;
import com.minisms.MiniGestureListener;
import com.minisms.R;

import android.R.bool;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ContactListActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnTouchListener{

	private ListView listView;
	private ContactListAdapter adapter;
	private AsyncQueryHandler asyncQueryHandler;
	private boolean needRefresh = true;
	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.contact_list);
		listView = (ListView)findViewById(R.id.lv_contact_list);
		listView.setOnItemClickListener(this);
		listView.setOnTouchListener(this);
		adapter = ContactListAdapter.getInstance(this);
		adapter.clear();
		
		asyncQueryHandler = new MyAsynQueryHandler(getContentResolver());
		
		gestureDetector = new GestureDetector(new MiniGestureListener(this));
	}
	
	
	@Override
	protected void onResume() {
		String[] projection = {Phone.DISPLAY_NAME, Phone.DATA1, "sort_key"};
		super.onResume();
		
		if (needRefresh) {
			asyncQueryHandler.startQuery(0, null, Phone.CONTENT_URI, projection, 
					null, null, "sort_key COLLATE LOCALIZED asc");
			needRefresh = false;
		}
	}

	

	private class MyAsynQueryHandler extends AsyncQueryHandler{

		public MyAsynQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// TODO Auto-generated method stub
			super.onQueryComplete(token, cookie, cursor);
			ContactEntity entity;
			if (cursor != null) {
				while (cursor.moveToNext()) {
					entity = new ContactEntity(cursor.getString(0), cursor.getString(1), cursor.getString(2));
					adapter.addElement(entity);
				}
				cursor.close();
				
			}
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int thread_id = -1;
		ContactEntity entity = (ContactEntity)adapter.getItem(position);
		String number = entity.getNumber();
		String[] projection = {"_id", "thread_id"};
		String selection = "address='" + number + "'";
		
		if (number.startsWith("+86")) {
			selection += " or address='" + number.substring(3) +"'";
		}else {
			selection += " or address='" + "+86" +  number +"'";
		}
		
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), projection, selection , null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				thread_id = cursor.getInt(1);
			}
			cursor.close();
		}
		
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra("NAME", entity.getName());
		intent.putExtra("PHONENUMBER", entity.getNumber());
		intent.putExtra("THREAD_ID", thread_id);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
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
		gestureDetector.onTouchEvent(event);
		return false;
	}
	
}
