package com.minisms;

import com.minicontact.ContactEntity;
import com.minicontact.ContactListAdapter;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class MiniService extends Service{
	
	private ContactListAdapter mAdapter;
	private final IBinder mBinder = new MiniBinder();
	
	public class MiniBinder extends Binder{
		public MiniService getService(){
			return MiniService.this;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.i("jiang Service ", "onCreate");
		mAdapter = ContactListAdapter.getInstance(getApplicationContext());
		mAdapter.clear();
		
		super.onCreate();
		String[] projection = {Phone.DISPLAY_NAME, Phone.DATA1, "sort_key"};
		
			new MyAsynQueryHandler(getContentResolver()).startQuery(0, null, Phone.CONTENT_URI, projection, 
					null, null, "sort_key COLLATE LOCALIZED asc");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ContactListAdapter.unInit();
		mAdapter = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	public ContactListAdapter getContactListAdapter(){
		synchronized (this) {
			return mAdapter;
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
					entity = new ContactEntity(cursor.getString(0), cursor.getString(1), getSortKey(cursor.getString(2)));
					synchronized (MiniService.this) {
						mAdapter.addElement(entity);
					}
				}
				cursor.close();
			}
			
		}
	}
	private char getSortKey(String str){
		
		char sortKey = str.toUpperCase().charAt(0);
		
		if (sortKey < 'A') { 
			sortKey = '@';
		}else if (sortKey > 'z') {
			sortKey = '#';
		}
		
		return sortKey;
	}
}
