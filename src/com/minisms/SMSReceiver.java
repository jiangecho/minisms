package com.minisms;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver{

	private final String RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if(intent.getAction().equals(RECEIVE_SMS)) {
			//intent.setClassName("com.test", "TestActivity");
			SharedPreferences sharedPreferences = context.getSharedPreferences("currentPhoneNumber", 0);
			String currentPh = sharedPreferences.getString("currentPhoneNumber", null);
			
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] messages = (Object[])bundle.get("pdus");
				//just handle the first one
				SmsMessage msg = SmsMessage.createFromPdu((byte[])messages[0]);
				msg.getDisplayOriginatingAddress();
				
				Log.i("SMSReceiver", msg.getDisplayOriginatingAddress());
				if ((currentPh != null) && (currentPh.equals(msg.getDisplayOriginatingAddress()))) {
					Log.i("SMSReceiver", "current conversation num " + currentPh);
					; // do nothing
				} else {
					intent.setClass(context, MiniSMSActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
	
				}
			}
			
		}
	}

	
	private boolean isActivityRunning(Context context, String className){
		
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = activityManager.getRunningTasks(Integer.MAX_VALUE);
		
		for (RunningTaskInfo runningTaskInfo : taskList) {
			Log.i("jiang", runningTaskInfo.topActivity.getClassName());
			if (runningTaskInfo.topActivity.getClassName().equals(className)) {
				isRunning = true;
			}
		}
		
		Log.i("SMSReceiver", className + " " + isRunning);
		return isRunning;
	}
	

}
