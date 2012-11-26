package com.minisms;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversationListItemEntity {
	
	private String lastDate;
	private String lastMessage;
	private String displayName;
	private String phoneNumber;
	
	private int thread_id;
	
	
	public ConversationListItemEntity(int thread_id, long timeMillions, String lastMessage,
			String displayName, String phoneNumber) {
		super();
		String date = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date(timeMillions));
		this.thread_id = thread_id;
		this.lastDate = date;
		//this.lastMessage = (lastMessage.length() > 10 ? lastMessage.substring(0, 10) : lastMessage);
		this.lastMessage = lastMessage;
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
	}
	
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	public String getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getThread_id() {
		return thread_id;
	}

	public void setThread_id(int threadId) {
		thread_id = threadId;
	}
	
	
}
