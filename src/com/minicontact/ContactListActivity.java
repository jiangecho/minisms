package com.minicontact;

import com.minisms.R;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Window;
import android.widget.ListView;

public class ContactListActivity extends Activity{

	private ListView listView;
	private ContactListAdapter adapter;
	private AsyncQueryHandler asyncQueryHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.contact_list);
		listView = (ListView)findViewById(R.id.lv_contact_list);
		adapter = ContactListAdapter.getInstance(this);
		
		asyncQueryHandler = new MyAsynQueryHandler(getContentResolver());
		
	}
	
	
	@Override
	protected void onResume() {
		String[] projection = {Phone.DISPLAY_NAME, Phone.DATA1, "sort_key"};
		super.onResume();
		
		asyncQueryHandler.startQuery(0, null, Phone.CONTENT_URI, projection, 
				null, null, "sort_key COLLATE LOCALIZED asc");
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
		}

		
		
	}
}
