package com.minisms;

import android.widget.ImageView;
import android.widget.TextView;

public class ConversationHolder {
	
	private TextView tvTime;
	private ImageView ivUerImage;
	private TextView tvContent;
	
	public TextView getTvTime() {
		return tvTime;
	}
	public void setTvTime(TextView tvTime) {
		this.tvTime = tvTime;
	}
	public ImageView getIvUerImage() {
		return ivUerImage;
	}
	public void setIvUerImage(ImageView ivUerImage) {
		this.ivUerImage = ivUerImage;
	}
	public TextView getTvContent() {
		return tvContent;
	}
	public void setTvContent(TextView tvContent) {
		this.tvContent = tvContent;
	}
}
