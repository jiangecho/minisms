package com.minisms;

import java.util.ArrayList;
import java.util.List;


import android.R.integer;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationListAdapter extends BaseAdapter{

	private List<ConversationListItemEntity> list;
	private LayoutInflater layoutInflater;
	private static ConversationListAdapter adapter;
	
	public static ConversationListAdapter getInstance(Context context){
		if (adapter == null) {
			adapter = new ConversationListAdapter(context);
		}
		return adapter;
	}
	
	public ConversationListAdapter(Context context){
		super();
		list = new ArrayList<ConversationListItemEntity>();
		layoutInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		// TODO Auto-generated method stub
		if (list != null) {
			return list.size();
		}else {
			return -1;
		}
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		String display;
		final ConversationListItemEntity entity = list.get(position);
		
		display = entity.getDisplayName();
		if (display == null) {
			display = entity.getPhoneNumber();
		} 
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.conversation_list_item, null);
			holder = new Holder((TextView)convertView.findViewById(R.id.tvDisplayName),
					(TextView)convertView.findViewById(R.id.tvLastDate),
					(TextView)convertView.findViewById(R.id.tvLastMessage),
					(ImageView)convertView.findViewById(R.id.head));
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}
		
		holder.getTvDisplayName().setText(display);
		holder.getTvLastDate().setText(entity.getLastDate());
		holder.getTvLastMessage().setText(entity.getLastMessage());
		holder.getIvHead().setImageResource(R.drawable.default_head);
		holder.getIvHead().setTag(Integer.valueOf(position));
		
		final TextPaint tp = holder.getTvDisplayName().getPaint();
		tp.setFakeBoldText(true);
		
		return convertView;
	}
	
	public void addElement(ConversationListItemEntity entity) {
		list.add(entity);
	}
	
	public int contains(int thread_id){
		int position = -1;
		ConversationListItemEntity entity;
		
		for (int i = 0; i < list.size(); i++) {
			entity = list.get(i);
			if (entity.getThread_id() == thread_id) {
				position = i;
				break;
			}
		}
		return position;
	}
	
	public int moveToHead(final int position){
		int ret = -1;
		if ((position == 0) || (position >= list.size())) {
			ret = -1;
		} else {
			list.add(0, list.get(position));
			list.remove(position);
			ret = 0;
		}
		return ret;
	}

	public void init(){
		list.clear();
	}
	
	public void clear(){
		list.clear();
	}
	
	public void remove(final int position){
		list.remove(position);
	}
	
	public static void unInit(){
		adapter = null;
	}
	
	private class Holder {
		
		private TextView tvDisplayName;
		private TextView tvLastDate;
		private TextView tvLastMessage;
		private ImageView ivHead;
		
		public Holder(TextView tvDisplayName,
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
		
		public TextView getTvLastDate() {
			return tvLastDate;
		}
		
		public TextView getTvLastMessage() {
			return tvLastMessage;
		}
		
		public ImageView getIvHead() {
			return ivHead;
		}
		

	}

}
