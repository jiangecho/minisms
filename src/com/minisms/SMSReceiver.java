package com.minisms;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver{

	private final String RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String fromAddr;
		String currentAddr;
		
		if(intent.getAction().equals(RECEIVE_SMS)) {
			//intent.setClassName("com.test", "TestActivity");
			SharedPreferences sharedPreferences = context.getSharedPreferences("currentPhoneNumber", 0);
			currentAddr = sharedPreferences.getString("currentPhoneNumber", null);
			
			if ((currentAddr != null) && (currentAddr.startsWith("+86"))) {
				currentAddr = currentAddr.substring(3);
			}
			
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] messages = (Object[])bundle.get("pdus");
				//just handle the first one
				SmsMessage msg = SmsMessage.createFromPdu((byte[])messages[0]);
				fromAddr = msg.getDisplayOriginatingAddress();
				
				if (fromAddr.startsWith("+86")) {
					fromAddr = fromAddr.substring(3);
				}
				
				Log.i("SMSReceiver", msg.getDisplayOriginatingAddress());
				if ((currentAddr != null) 
						&& (currentAddr.equals(fromAddr))
						&& (isActivityRunning(context, "com.minisms.ConversationActivity"))) {
					Log.i("SMSReceiver", "current conversation num " + currentAddr);
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
			Log.i("jiang ++++", runningTaskInfo.topActivity.getClassName());
			if (runningTaskInfo.topActivity.getClassName().equals(className)) {
				isRunning = true;
				break;
			}
		}
		
		Log.i("SMSReceiver", className + " " + isRunning);
		return isRunning;
	}
	

}
