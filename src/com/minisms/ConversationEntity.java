package com.minisms;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversationEntity {
	
	private long time;
	private String date;
	private String content;
	private boolean isComingMsg;
	private int _id;
	
	

	public ConversationEntity(String time, String content, boolean isComingMsg) {
		super();
		this.date = time;
		this.content = content;
		this.isComingMsg = isComingMsg;
	}
	
	public ConversationEntity(String time, String content, int type, int _id) {
		super();
		this.date = time;
		this.content = content;
		this._id = _id;
		
		setChatEntityType(type);
	}
	
	public ConversationEntity(long time, String content, int type, int _id) {
		super();
		String date = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date(time));
		this.time = time;
		this.date = date;
		this.content = content;
		this._id = _id;
		
		setChatEntityType(type);
	}

	public String getmDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setmContent(String mContent) {
		this.content = mContent;
	}
	
	public boolean isComingMsg() {
		return isComingMsg;
	}
	
	public int getChatEntityType() {
		if (isComingMsg) {
			return 0;
		} else {
			return 1;
		}
	}
	
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setChatEntityType(int type){
		if(type == 1){
			isComingMsg = true;
		}else if(type == 2){
			isComingMsg = false;
		}
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

}
