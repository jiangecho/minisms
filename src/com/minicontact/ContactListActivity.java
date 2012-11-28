package com.minicontact;

import com.minisms.R;

import android.R.bool;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ContactListActivity extends Activity implements OnItemClickListener, OnItemLongClickListener{

	private ListView listView;
	private ContactListAdapter adapter;
	private AsyncQueryHandler asyncQueryHandler;
	private boolean needRefresh = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.contact_list);
		listView = (ListView)findViewById(R.id.lv_contact_list);
		listView.setOnItemClickListener(this);
		adapter = ContactListAdapter.getInstance(this);
		adapter.clear();
		
		asyncQueryHandler = new MyAsynQueryHandler(getContentResolver());
		
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
		}
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ContactEntity entity = (ContactEntity)adapter.getItem(position);
		Intent intent = new Intent(this, ContactInfoActivity.class);
		intent.putExtra("name", entity.getName());
		intent.putExtra("number", entity.getNumber());
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
