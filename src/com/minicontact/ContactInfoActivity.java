package com.minicontact;

import com.minisms.ConversationActivity;
import com.minisms.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ContactInfoActivity extends Activity{
	private TextView tvName;
	private TextView tvNumber;
	
	private String name;
	private String number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_info);
		
		tvName = (TextView)findViewById(R.id.tv_contact_info_name);
		tvNumber = (TextView)findViewById(R.id.tv_contact_info_number);
		
		name = getIntent().getStringExtra("name");
		number = getIntent().getStringExtra("number");
		tvName.setText(name);
		tvNumber.setText(number);
		
	}
	
	public void back(View view){
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	public void sendMsg(View view){
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra("THREAD_ID", -1);
		intent.putExtra("PHONENUMBER", number);
		
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	/** Default constructor */
	public ContactInfoActivity() {
	}
}
