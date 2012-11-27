package com.minisms;

import java.util.ArrayList;
import java.util.List;


import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ConversationActivity extends Activity {
	
	private final String SMS_URI_ALL = "content://sms/";
	private final String SMS_URI_CONVERSATION = "content://threads/";
	private final String SMS_URI_INBOX = "content://sms/inbox";
	private final String SMS_URI_SEND = "content://sms/sent";
	private final String SMS_URI_DRAFT = "content://sms/draft";
	
	private final String ADDR = "address";
	private final String THREAD_ID = "thread_id";
	
	public static final Uri MMSSMS_FULL_CONVERSATION_URI = Uri.parse("content://mms-sms/conversations");
	public static final Uri CONVERSATION_URI = MMSSMS_FULL_CONVERSATION_URI.buildUpon()
							.appendQueryParameter("simple", "true").build();  
		
	private ListView mListView;
	private Button mSendBtn;
	private EditText mEditText;
	private int mThread_id;
	private String mPhoneNumber;
	private ConversationAdapter mAdapter;
	private ContentResolver mContentResolver;		
	private SharedPreferences mSharedPreferences; 
	private ContentObserver observer;
	
	private int selectedIndex = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.i("jiang", "onCreateeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);
		mContentResolver = getContentResolver();
		
		mPhoneNumber = getIntent().getStringExtra("PHONENUMBER");
		//mPhoneNumber = "15555215556";
		mSharedPreferences = getSharedPreferences("currentPhoneNumber", MODE_PRIVATE);
		mSharedPreferences.edit().putString("currentPhoneNumber", mPhoneNumber).apply();
		
		//mThread_id = getThreadId(mPhoneNumber);
		mThread_id = getIntent().getIntExtra("THREAD_ID", -1);
		Log.i("jiang", "thread_id" + mThread_id);
		
		mAdapter = new ConversationAdapter(mPhoneNumber, getChatEntities(), this);
		Log.i("jiang ", "madaterrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr " + mAdapter);
		mListView = (ListView)findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mAdapter.getCount() - 1);
		mListView.setSelector(android.R.color.transparent);
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("jiang", "ConversationActivity onItemLongClick");
				selectedIndex = position;
				String[] options = getResources().getStringArray(R.array.conversation_options);
				AlertDialog dialog = new AlertDialog.Builder(ConversationActivity.this)
									.setTitle(mPhoneNumber)
									.setItems(options, new DialogInterface.OnClickListener() {
										
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											switch (which) {
											case 0:
												deleteCurMsg();
												break;

											default:
												break;
											}
											
										}
										}).create();
				dialog.show();
				return false;
			}
		});
		//mListView.setStackFromBottom(true);
		mEditText = (EditText)findViewById(R.id.et_sendmessage);
		mSendBtn = (Button)findViewById(R.id.btn_send);
		mSendBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editable text = mEditText.getText();
				mEditText.setText("");
				if((text != null) && (text.length() > 0)){
					int _id = sendMSG(mPhoneNumber, text.toString());
					mAdapter.addConversationEntity(
							new ConversationEntity(System.currentTimeMillis(), text.toString(), 2, _id));
				}else {
					Toast.makeText(ConversationActivity.this, "please input", Toast.LENGTH_SHORT).show();
				}
			}
		});

		observer = new ConversationObserver(new Handler());
		mContentResolver.registerContentObserver(CONVERSATION_URI, true, observer);
		
	}

	public void back(View view){
		//overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		
		Log.i("jiang", "onBackPressed");
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mSharedPreferences.edit().putString("currentPhoneNumber", null).apply();
		Log.i("jiang", "onPauseeeeeeeeeeeeeeeeeeeee");
		super.onPause();
	}
//
//	@Override
//	protected void onRestart() {
//		// TODO Auto-generated method stub
//		Log.i("jiang", "onRestarttttttttttttttttttttttttttttttt");
//		super.onRestart();
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		Log.i("jiang", "onResumeeeeeeeeeeeeeeeeeeeeeeeeeee");
//		super.onResume();
//	}
//
//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		Log.i("jiang", "onStarttttttttttttttttttttttttttttt");
//		super.onStart();
//	}
//
//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		Log.i("jiang", "onStoppppppppppppppppppppppppppp");
//		super.onStop();
//	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContentResolver.unregisterContentObserver(observer);
		mListView = null;
		mAdapter = null;
		mContentResolver = null;
		mSharedPreferences = null;
	}


	public void onHeadClick(View view){
		//TODO implement onHeadClick method
		Log.i("jiang", "ConversationActivity onHeadClick");
	}
	private int getThreadId(String phoneNumber){
		int threadId = -1;
		int threadIdColumn = -1;
		Uri uri = Uri.parse(SMS_URI_ALL);
		String[] projection = {THREAD_ID, ADDR};
		
		Cursor cursor = mContentResolver.query(uri, projection, ADDR + "=?", new String[]{phoneNumber}, null);
		
		if (cursor != null) {
			threadIdColumn = cursor.getColumnIndex(THREAD_ID);
			while (cursor.moveToNext()) {
				threadId = cursor.getInt(threadIdColumn);
			}
		}
		cursor.close();
		
		return threadId;
	}
	
	private List<ConversationEntity> getChatEntities(){
		ConversationEntity chatEntity;
		List<ConversationEntity> list = new ArrayList<ConversationEntity>();
		Uri uri = Uri.parse(SMS_URI_ALL);
		//String[] projection = {"_id", ADDR, "person", "body", "date", "type"};
		String[] projection = {"_id", ADDR, "person", "body", "date", "type", "read"};
		Cursor cursor = mContentResolver.query(uri, projection, THREAD_ID + "=?",
				new String[]{Integer.toString(mThread_id)}, "date asc");
		
		ContentValues values = new ContentValues();
		values.put("read", 1);
		
		if (cursor != null) {
			int timeIndex = cursor.getColumnIndex("date");
			int bodyIndex = cursor.getColumnIndex("body");
			int typeIndex = cursor.getColumnIndex("type");
			int _idIndex = cursor.getColumnIndex("_id");
			int readIndex = cursor.getColumnIndex("read");
			
			int _id;
			int read;
			
			while(cursor.moveToNext()){
				_id = cursor.getInt(_idIndex);
				chatEntity = new ConversationEntity(cursor.getLong(timeIndex), cursor.getString(bodyIndex),
						cursor.getInt(typeIndex), _id);
				list.add(chatEntity);
				
				read = cursor.getInt(readIndex);
				if (read == 0) {
					mContentResolver.update(uri, values, "thread_id=" + mThread_id + " and _id=" + _id, null);
					
				}
			}
			
			cursor.close();
		}
		return list;
	}
	
	private class ConversationObserver extends ContentObserver{

		public ConversationObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean deliverSelfNotifications() {
			// TODO Auto-generated method stub
			//return super.deliverSelfNotifications();
			return false;
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			int _id;
			ConversationEntity chatEntity;
			ContentValues values = new ContentValues();
			values.put("read", 1);
			
			super.onChange(true);
			Log.i("jiang", "ConversationActivity onChange " + mThread_id + " " + selfChange);
			
			if(mThread_id == -1){
				mThread_id = getThreadId(mPhoneNumber);
			}
			
			if (mThread_id == -1) {
				return ;
			}
			
			String[] projection = {"date", "body", "read", "_id"};
			Cursor cursor = mContentResolver.query(Uri.parse(SMS_URI_INBOX), projection, "thread_id=" + mThread_id
					+ " and read=0", null, "date desc limit 1");
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					chatEntity = new ConversationEntity(cursor.getLong(0), cursor.getString(1), 1,cursor.getInt(3));
					Log.i("jiang", "addConversationEntity");
					mAdapter.addConversationEntity(chatEntity);
					
					_id = cursor.getInt(3);
					mContentResolver.update(Uri.parse(SMS_URI_INBOX), values, "thread_id=" + mThread_id + " and _id=" + _id, null);
				}
				mAdapter.notifyDataSetChanged();
				cursor.close();
			}
				
			
		}
		
		
	}
	
	
	
	private int sendMSG(String phoneNumber, String message){
		SmsManager smsManager = SmsManager.getDefault();
		ContentValues values = new ContentValues();
		
		if(message.length() > 70){
			ArrayList<String> msgs = smsManager.divideMessage(message);
			for (String string : msgs) {
				smsManager.sendTextMessage(phoneNumber, null, string, null, null);
			}
			
		}else {
			smsManager.sendTextMessage(phoneNumber, null, message, null, null);
		}
		
		values.put("address", phoneNumber);
		values.put("body", message);
		Uri uri =  mContentResolver.insert(Uri.parse("content://sms/sent"), values);
		return (int) ContentUris.parseId(uri);
	}
	
	private void deleteCurMsg(){
		ConversationEntity entity = (ConversationEntity)mAdapter.getItem(selectedIndex);
		int _id = entity.get_id();
		mAdapter.removeElement(selectedIndex);
		mAdapter.notifyDataSetChanged();
		
		new AsyncTask<Integer, Void, Void>() {
			@Override
			protected Void doInBackground(Integer... params) {
				getContentResolver().delete(Uri.parse("content://sms/conversations/" +  params[0].intValue()), 
						 "_id=" + params[1].intValue(), null);
				Log.i("jiang", "delete " + params[0].intValue() + " " + params[1].intValue());
				return null;
			}
		//}.execute(new Integer(mThread_id), new Integer(_id));
		}.execute(Integer.valueOf(mThread_id), Integer.valueOf(_id));
		
	}
}
