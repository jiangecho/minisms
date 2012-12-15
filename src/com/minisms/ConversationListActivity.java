package com.minisms;

import com.minicontact.ContactListActivity;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ConversationListActivity extends Activity implements OnTouchListener{

    public static final Uri MMSSMS_FULL_CONVERSATION_URI = Uri.parse("content://mms-sms/conversations");
    public static final Uri CONVERSATION_URI = MMSSMS_FULL_CONVERSATION_URI.buildUpon().appendQueryParameter("simple", "true").build(); 
    private static final int REQUESTCODE = 1;
    
	private boolean needRefresh = true;
	private boolean alertUp = false;
	private ListView listView;
	private ConversationListAdapter adapter;
	private ContentObserver observer;
	private ContentResolver contentResolver;
	private ConversationsQueryHandler queryHandler;
	private GestureDetector mGestureDetector;
	
	private Intent startContactListActivityIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("jiang xx", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_list);
		
		startContactListActivityIntent = new Intent(this, ContactListActivity.class);
		
		contentResolver = getContentResolver();
		listView = (ListView)findViewById(R.id.lv_conversation_list);
		

		//TODO: when to use application context
		//only use application context when you know you need a context for something that may live longer than 
		//any other likely context.
		//adapter = ConversationListAdapter.getInstance(getApplicationContext());
		adapter = ConversationListAdapter.getInstance(this);
		adapter.init();
		queryHandler = new ConversationsQueryHandler(contentResolver);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ConversationListItemEntity entity = (ConversationListItemEntity) adapter.getItem(position);
				Log.i("jiang", "onItemClick");
				Intent intent = new Intent();
				intent.setClass(ConversationListActivity.this, ConversationActivity.class);
				intent.putExtra("THREAD_ID", entity.getThread_id());
				intent.putExtra("NAME", entity.getDisplayName());
				intent.putExtra("PHONENUMBER", entity.getPhoneNumber());
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}

		});
		
		listView.setOnTouchListener(this);
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TODO the selected item is friend or not
				Log.i("jiang", "ConversationListActivity onItemLongClick");
				final int curPosition = position;
				
				alertUp = true;
				ConversationListItemEntity entity = (ConversationListItemEntity)adapter.getItem(position);
				String[] optionsArray;
				String title;
				if (entity.getDisplayName() != null) {
					optionsArray = getResources().getStringArray(R.array.list_options);
					title = entity.getDisplayName();
				} else {
					optionsArray = getResources().getStringArray(R.array.list_options_stranger);
					title = entity.getPhoneNumber();
				}
				
	
				AlertDialog dialog = new AlertDialog.Builder(ConversationListActivity.this)
				.setItems(optionsArray, new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						//TODO 
						switch (which) {
						case 0:
							deleteConvertion(curPosition);
							break;
						case 1:
							call(curPosition);
							break;
							
						case 2:
							addToContact(curPosition);
							break;
						case 3:
							break;

						default:
							break;
						}
					}
				})
				.setTitle(title)
				.create();
				dialog.show();
				
				dialog.setCanceledOnTouchOutside(true);
				
				dialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						alertUp = false;
					}
				});
				// TODO Auto-generated method stub
				return true;
			}
		});
		observer = new ConversationObserver(new Handler());
		contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, observer);
		
        mGestureDetector = new GestureDetector(new MiniGestureListener(ConversationListActivity.this));
		startService(new Intent(this, MiniService.class));
	}
	

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("jiang xx", "onstart");
	}
	
	@Override
	protected void onResume() {
		
		Log.i("jiang xx", "onResume");
		super.onResume();
		if (needRefresh) {
			String[] projection = {"_id", "date", "snippet", "recipient_ids"};
			Log.i("jiang", "updateAdapterDataSet");
			needRefresh = false;
			adapter.clear();
			queryHandler.startQuery(0, null, CONVERSATION_URI, projection, null, null, "date desc");
		}
		
	}

	@Override
	protected void onPause() {
		Log.i("jiang xx", "onPause");
		super.onPause();
	}


	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		contentResolver.unregisterContentObserver(observer);
		stopService(new Intent(this, MiniService.class));
		queryHandler.cancelOperation(0);
		
		ConversationListAdapter.unInit();
		adapter = null;
		listView = null;
		contentResolver = null;
		observer = null;
		queryHandler = null;
		Log.i("jiang", "onDestroyyyyyyyyyyyyyyyyyyyyyy");
	}
	
	private class ConversationsQueryHandler extends AsyncQueryHandler{

		public ConversationsQueryHandler(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// TODO Auto-generated method stub
			//String[] projection = {"_id", "date", "snippet", "recipient_ids"};
			super.onQueryComplete(token, cookie, cursor);
			
			if (cursor != null) {
				//int phoneNumverIndex;
				Cursor cur = null;
				String[] projection1 = {"address"};
				//String phoneNumber;
				ConversationListItemEntity entity;
				
				Log.i("jiang", "updateAdapterDataSet");
				while (cursor.moveToNext()) {
					//TODO group sms
					//cur = getContentResolver().query(Uri.parse("content://mms-sms/canonical-addresses"),
					//		projection1, "_id=?", new String[]{cursor.getString(3)}, null);
					cur = contentResolver.query(Uri.parse("content://mms-sms/canonical-addresses"),
							projection1, "_id=" + cursor.getInt(3), null, null);
					if ((cur != null) && (cur.moveToNext())) {
						entity = new ConversationListItemEntity(cursor.getInt(0), cursor.getLong(1), cursor.getString(2), 
								getSenderName(cur.getString(1)), cur.getString(1));
							cur.close();
						
					} else {
						entity = new ConversationListItemEntity(cursor.getInt(0), cursor.getLong(1), cursor.getString(2), 
								null, null);
	
					}
					adapter.addElement(entity);
				}
				cursor.close();
			}
			adapter.notifyDataSetChanged();
		}	
	}
	
	private String getSenderName(String phoneNumber){
		
		ContentResolver cr = getContentResolver();
		String name = null;
		String[] projection = {PhoneLookup.DISPLAY_NAME};
		String selection = Phone.NUMBER + "= '" + phoneNumber +"'";
		
		if (phoneNumber.startsWith("+86")) {
			selection += " or " + Phone.NUMBER + "='" + phoneNumber.substring(3) + "'";
		}
		
		//Cursor cursor = cr.query(Phone.CONTENT_URI, projection, Phone.NUMBER +" = '" + phoneNumber + "'", null, null);
		Cursor cursor = cr.query(Phone.CONTENT_URI, projection, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				name = cursor.getString(0);
			}
			cursor.close();
		}
		return name;
	}
	
	private class ConversationObserver extends ContentObserver{

		public ConversationObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			Log.i("jiang", "ConversationListActivity onChange");
			super.onChange(selfChange);
			needRefresh = true;
		}
	}
	
	private void deleteConvertion(int position){
		ConversationListItemEntity entity = (ConversationListItemEntity)adapter.getItem(position);
		int thread_id = entity.getThread_id();
		adapter.remove(position);
		adapter.notifyDataSetChanged();
		
		
		new AsyncTask<Integer, Void, Void>(){
			@Override
			protected Void doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				contentResolver.delete(MMSSMS_FULL_CONVERSATION_URI, "thread_id=" + params[0].intValue(), null);
				return null;
			}
			
		}.execute(new Integer(thread_id));
		
	}
	
	private void call(int position){
		ConversationListItemEntity entity = (ConversationListItemEntity)adapter.getItem(position);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + entity.getPhoneNumber()));
		startActivity(intent);
		
	}
	
	private void addToContact(final int position){
		ConversationListItemEntity entity = (ConversationListItemEntity)adapter.getItem(position);
		Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
		intent.setType("vnd.android.cursor.dir/person");
		intent.putExtra(ContactsContract.Intents.Insert.PHONE, entity.getPhoneNumber());
		startActivity(intent);
		startActivityForResult(intent, REQUESTCODE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE) {
			this.needRefresh = true;
		}
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i("jiang", "conversationListActivity onTouch");
		if ((mGestureDetector != null) && (!alertUp)) {
			mGestureDetector.onTouchEvent(event);
		}
		return false;
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.out_to_right);
	}


	public void swipeToRight(){
		
		Log.i("jiang" , "haaaaaaaaaaaaaaaa");
		startActivity(startContactListActivityIntent);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	public void swipeToLeft(){
		
		Log.i("jiang" , "haaaaaaaaaaaaaaaa");
		startActivity(startContactListActivityIntent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	public void onHeadClick(View view){
		Integer poInteger = (Integer)view.getTag();
		int position = poInteger.intValue();
		Log.i("jiang", " onHeadClick" + position);
	}
}
