package com.minisms;

import android.widget.ImageView;
import android.widget.TextView;

public class ConversationListItemHolder {
	
	private TextView tvDisplayName;
	private TextView tvLastDate;
	private TextView tvLastMessage;
	private ImageView ivHead;
	
	public ConversationListItemHolder(TextView tvDisplayName,
			TextView tvLastDate, TextView tvLastMessage, ImageView ivHead) {
		super();
		this.tvDisplayName = tvDisplayName;
		this.tvLastDate = tvLastDate;
		this.tvLastMessage = tvLastMessage;
		this.ivHead = ivHead;
	}
	
	public TextView getTvDisplayName() {
		return tvDisplayName;
	}
	
	public void setTvDisplayName(TextView tvDisplayName) {
		this.tvDisplayName = tvDisplayName;
	}
	
	public TextView getTvLastDate() {
		return tvLastDate;
	}
	
	public void setTvLastDate(TextView tvLastDate) {
		this.tvLastDate = tvLastDate;
	}
	
	public TextView getTvLastMessage() {
		return tvLastMessage;
	}
	
	public void setTvLastMessage(TextView tvLastMessage) {
		this.tvLastMessage = tvLastMessage;
	}
	
	public ImageView getIvHead() {
		return ivHead;
	}
	
	public void setIvHead(ImageView ivHead) {
		this.ivHead = ivHead;
	}
	
}
