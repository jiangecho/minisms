package com.minisms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.apache.http.cookie.SM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MiniSMSActivity extends Activity {
	
	Dialog dialog;
	TextView tvMessage;
	TextView tvFrom;
	TextView tvTime;
	TextView tvMsgCount;
	EditText editText;
	Queue<MiniMsg> msgsQueue = new LinkedList<MiniMsg>();
	MiniMsg curmsg;
	GestureDetector mGestureDetector;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        mGestureDetector = new GestureDetector(new MiniGestureListener(MiniSMSActivity.this));
        View popupView = getLayoutInflater().inflate(R.layout.popup_item, (ViewGroup)findViewById(R.id.popup_item));
        //View layout = new MyView(this, MiniSMSActivity.this, mGestureDetector).getInstance();
        
        popupView.setLongClickable(true);
        popupView.setOnTouchListener(onTouchListener);
        
        tvMessage = (TextView)popupView.findViewById(R.id.tvMessage);
        tvFrom = (TextView)popupView.findViewById(R.id.tvFrom);
        tvTime = (TextView)popupView.findViewById(R.id.tvTime);
        tvMsgCount = (TextView)popupView.findViewById(R.id.tvMsgCount);
        editText = (EditText)popupView.findViewById(R.id.message);
        
        
        getMsg(getIntent());
        
        if(!msgsQueue.isEmpty()){
        	updateMsgPopUp(msgsQueue.poll());
        }
        
        
        
        Button sendButton = (Button)popupView.findViewById(R.id.sendBtn);
        sendButton.setOnClickListener(onClickListener);
        
        ImageButton deleteButton = (ImageButton)popupView.findViewById(R.id.ibClose);
        deleteButton.setOnClickListener(onClickListener);
        
        ImageView ivFrom = (ImageView)popupView.findViewById(R.id.ivFrom);
        ivFrom.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + curmsg.getAddr()));
				startActivity(callIntent);
				
			}
		});
        
        dialog = new AlertDialog.Builder(this).setView(popupView).create();
        dialog.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if((keyCode == KeyEvent.KEYCODE_BACK) 
						&& (event.getAction() == KeyEvent.ACTION_UP)) {
					dialog.dismiss();
					finish();
					return true;
				}else {
					return false;
				}
			}
		});
        dialog.show();
    }
    
    
    
    @Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
    	Log.i("jiang", "onNewIntent");
		super.onNewIntent(intent);
		getMsg(intent);
		
		updatetvMsgCount();
	}


	OnClickListener onClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.sendBtn:
			{
				Editable text = editText.getText();
				editText.setText("");
				if((text != null) && (text.length() > 0)){
					sendMSG(curmsg.getAddr(), text.toString());
					updateMSG(curmsg.getContent());
					//TODO reply the current msg at first.
					if(!msgsQueue.isEmpty()){
						updateMsgPopUp(msgsQueue.poll());
					}else {
						dialog.dismiss();
						finish();
					}
				}else {
					Toast.makeText(MiniSMSActivity.this, "please input", Toast.LENGTH_SHORT).show();
				}
				
				break;
			}
			
			case R.id.ibClose:
			{
				deleteMSG(curmsg.getContent());
				if(!msgsQueue.isEmpty()){
					updateMsgPopUp(msgsQueue.poll());
				}else {
					dialog.dismiss();
					finish();
				}
				break;
			}

			default:
				Toast.makeText(MiniSMSActivity.this, "default", Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
	};
	
	OnTouchListener onTouchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mGestureDetector.onTouchEvent(event);
		}
	};
	
	
	/**
     * Read the PDUs out of an {@link #SMS_RECEIVED_ACTION} or a
     * {@link #DATA_SMS_RECEIVED_ACTION} intent.
     *
     * @param intent the intent to read from
     * @return an array of SmsMessages for the PDUs
     */
//    private static final SmsMessage[] getMessagesFromIntent(
//            Intent intent) {
//        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
//        byte[][] pduObjs = new byte[messages.length][];
//
//        for (int i = 0; i < messages.length; i++) {
//            pduObjs[i] = (byte[]) messages[i];
//        }
//        byte[][] pdus = new byte[pduObjs.length][];
//        int pduCount = pdus.length;
//        SmsMessage[] msgs = new SmsMessage[pduCount];
//        for (int i = 0; i < pduCount; i++) {
//            pdus[i] = pduObjs[i];
//            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
//        }
//        return msgs;
//    }
	
    
	private void getMsg(Intent intent){
		
		Bundle bundle = intent.getExtras();
		MiniMsg miniMsg;
		SmsMessage msg;
		String addr = null;
		long time = 0;
		StringBuffer strBuffer = new StringBuffer();
		
		Log.i("jiang", "getMsg");
		if(bundle != null){
			Object[] messages = (Object[])bundle.get("pdus");
			for(int i = 0; i < messages.length; i++){
				msg = SmsMessage.createFromPdu((byte[])messages[i]);
				strBuffer.append(msg.getDisplayMessageBody());
				addr = msg.getDisplayOriginatingAddress();
				time = msg.getTimestampMillis();
			}
			miniMsg = new MiniMsg(addr, strBuffer.toString(), time);
		msgsQueue.offer(miniMsg);
		}
		
	}
	
	private void sendMSG(String phoneNumber, String message){
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
		getContentResolver().insert(Uri.parse("content://sms/sent"), values);
		
	}
	
	private void updateMSG(String body){
		ContentResolver contentResolver = getContentResolver();
		Uri uri = Uri.parse("content://sms/inbox");
		String[] projection = {"body", "thread_id", "_id"}; 
		ContentValues values = new ContentValues();
		int thread_id;
		int _id;
		Cursor cursor = contentResolver.query(uri, projection, "read=" + 0, null, null);
		
		values.put("read", 1);
		if(cursor != null){
			Log.i("jiang", "updateMSG" + cursor.getCount());
			while (cursor.moveToNext()) {
				String test = cursor.getString(0);
				if(test.equals(body)){
					Log.i("jiang", "updateMSG" + test);
					thread_id = cursor.getInt(1);
					_id = cursor.getInt(2);
					contentResolver.update(Uri.parse("content://sms/conversations/" + thread_id), values, "_id=" + _id, null);
					break;
				}
			}
			cursor.close();
		}
	}
	
	private void deleteMSG(String body){
		ContentResolver contentResolver = getContentResolver();
		Uri uri = Uri.parse("content://sms/inbox");
		String[] projection = {"body", "thread_id", "_id"}; 
		int thread_id;
		int _id;
		Cursor cursor = contentResolver.query(uri, projection, "read=" + 0, null, null);
		
		if(cursor != null){
			Log.i("jiang", "deleteMSG " + cursor.getCount());
			while (cursor.moveToNext()) {
				String test = cursor.getString(0);
				if(test.equals(body)){
					thread_id = cursor.getInt(1);
					_id = cursor.getInt(2);
					contentResolver.delete(Uri.parse("content://sms/conversations/" + thread_id), "_id=" + _id, null);
					break;
				}
				Log.i("jiang", "deleteMSG " + test);
			}
		cursor.close();
		}
		
	}
    
	private String getSenderName(String phoneNumber){
		Log.i("jiang", "getSenderName");
		ContentResolver cr = getContentResolver();
		String name = null;
		String[] projection = {PhoneLookup.DISPLAY_NAME};
		String selection = Phone.NUMBER + "= '" + phoneNumber +"'";
		
		if (phoneNumber.startsWith("+86")) {
			selection += " or " + Phone.NUMBER + "='" + phoneNumber.substring(3) + "'";
		}
		
		//Cursor cursor = cr.query(Phone.CONTENT_URI, projection, Phone.NUMBER +" = '" + phoneNumber + "'", null, null);
		Cursor cursor = cr.query(Phone.CONTENT_URI, projection, selection, null, null);
		if ((cursor != null)) {
			if (cursor.moveToFirst()) {
				name = cursor.getString(0);
			}
			cursor.close();
		}
		return name;
	}
	
	private void updatetvFrom(){
    	String name = getSenderName(curmsg.getAddr());
    	Log.i("jiang", curmsg.getAddr());
        	
    	if(name != null){
    		tvFrom.setText(name);
    	}else {
			tvFrom.setText(curmsg.getAddr());
		}
	}
	
	private void updatetvTime(){
    	String date = new SimpleDateFormat("hh:mm").format(new Date(curmsg.getTimestamp()));
    	tvTime.setText(date);
	}
	
	private void updatetvMessage(){
		tvMessage.setText(curmsg.getContent());
	}
	
	private void updatetvMsgCount(){
		int msgCount = msgsQueue.size();
		if (msgCount > 0) {
			tvMsgCount.setText("U: " + msgCount);
		}else {
			tvMsgCount.setText("");
		}
	}
	
	private void updateMsgPopUp(MiniMsg curMessage){
		curmsg = curMessage;
		updatetvFrom();
		updatetvMessage();
		updatetvTime();
		updatetvMsgCount();
	}
	
	private void swipe(int exitAnim){
		updateMSG(curmsg.getContent());
		
		if(!msgsQueue.isEmpty()){
			updateMSG(curmsg.getContent());
			updateMsgPopUp(msgsQueue.poll());
		}else {
			dialog.dismiss();
			finish();
			overridePendingTransition(0, exitAnim);
		}
	}
	
	public void swipeToLeft(){
		swipe(R.anim.out_to_left);
		
	}
	public void swipeToRight(){
		swipe(R.anim.out_to_right);
	}
	public void swipeToTop(){
		swipe(R.anim.out_to_top);
	}
	public void swipeToBottom(){
		swipe(R.anim.out_to_bottom);
	}
	
	private class MiniMsg{
		String addr;
		String content;
		long time;
		public MiniMsg(String addr, String content, long time) {
			super();
			this.addr = addr;
			this.content = content;
			this.time = time;
		}
		String getAddr() {
			return addr;
		}
		String getContent() {
			return content;
		}
		long getTimestamp() {
			return time;
		}
		
	}
	
}