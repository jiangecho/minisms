package com.minisms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ConversationListActivity extends Activity{

    public static final Uri MMSSMS_FULL_CONVERSATION_URI = Uri.parse("content://mms-sms/conversations");
    public static final Uri CONVERSATION_URI = MMSSMS_FULL_CONVERSATION_URI.buildUpon().appendQueryParameter("simple", "true").build(); 
    private static final int REQUESTCODE = 1;
    
	private ListView listView;
	private ConversationListAdapter adapter;
	private boolean needRefresh = true;
	private ContentObserver observer;
	private ContentResolver contentResolver;
	private ConversationsQueryHandler queryHandler;
	
	private int currentSelectedItem = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_list);
		
		contentResolver = getContentResolver();
		listView = (ListView)findViewById(R.id.lv_conversation_list);
		
		adapter = ConversationListAdapter.getInstance(this);
		adapter.init();
		
		//updateAdapterDataSet(adapter);
		//listView.setAdapter(adapter);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.i("jiang", "onItemClick");
				Intent intent = new Intent();
				intent.setClass(ConversationListActivity.this, ConversationActivity.class);
				intent.putExtra("THREAD_ID", ((ConversationListItemEntity)(adapter.getItem(position))).getThread_id());
				intent.putExtra("PHONENUMBER", ((ConversationListItemEntity)(adapter.getItem(position))).getPhoneNumber());
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}

		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TODO the selected item is friend or not
				Log.i("jiang", "ConversationListActivity onItemLongClick");
				currentSelectedItem = position;
				
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
							deleteConvertion(currentSelectedItem);
							currentSelectedItem = -1;
							break;
						case 1:
							call(currentSelectedItem);
							currentSelectedItem = -1;
							break;
							
						case 2:
							addToContact();
							currentSelectedItem = -1;
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
				// TODO Auto-generated method stub
				return true;
			}
		});
		observer = new ConversationObserver(new Handler());
		contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, observer);
		
	}
	

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		String[] projection = {"_id", "date", "snippet", "recipient_ids"};
		
		super.onResume();
		if (needRefresh) {
			Log.i("jiang", "updateAdapterDataSet");
			needRefresh = false;
			adapter.clear();
			queryHandler = new ConversationsQueryHandler(contentResolver);
			queryHandler.startQuery(0, null, CONVERSATION_URI, projection, null, null, "date desc");
			
			//updateAdapterDataSet(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
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
		Log.i("jiang", "onDestroyyyyyyyyyyyyyyyyyyyyyy");
	}

	public void onHeadClick(View view){
		//TODO implement the onHeadClick method
		Log.i("jiang", "onHeadClick " + view.toString());
	}

	private void updateAdapterDataSet(ConversationListAdapter adapter){
		
		String[] projection = {"_id", "date", "snippet", "recipient_ids"};
		ConversationListItemEntity entity;
		Cursor cursor = contentResolver.query(CONVERSATION_URI, projection, null, null, "date desc");
		
		if (cursor != null) {
			Cursor cur = null;
			String[] projection1 = {"address"};
			String phoneNumber;
			int phoneNumverIndex;
			
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
	}
	
	private class ConversationsQueryHandler extends AsyncQueryHandler{

		public ConversationsQueryHandler(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// TODO Auto-generated method stub
			super.onQueryComplete(token, cookie, cursor);
			
			if (cursor != null) {
				int phoneNumverIndex;
				Cursor cur = null;
				String[] projection1 = {"address"};
				String phoneNumber;
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
			listView.setAdapter(adapter);
		}	
	}
	
	private String getSenderName(String phoneNumber){
		Log.i("jiang", "getSenderName " + phoneNumber);
		ContentResolver cr = getContentResolver();
		String name = null;
		String[] projection = {PhoneLookup.DISPLAY_NAME};
		String selection = Phone.NUMBER + "= '" + phoneNumber +"'";
		
		if (phoneNumber.startsWith("+86")) {
			selection += " or " + Phone.NUMBER + "='" + phoneNumber.substring(3) + "'";
		}
		
		//Cursor cursor = cr.query(Phone.CONTENT_URI, projection, Phone.NUMBER +" = '" + phoneNumber + "'", null, null);
		Cursor cursor = cr.query(Phone.CONTENT_URI, projection, selection, null, null);
		if ((cursor != null) && (cursor.moveToFirst())) {
			name = cursor.getString(0);
		}
		cursor.close();
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
	
	private void addToContact(){
		ConversationListItemEntity entity = (ConversationListItemEntity)adapter.getItem(currentSelectedItem);
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

	
	
}
