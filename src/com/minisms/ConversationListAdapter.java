package com.minisms;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationListAdapter extends BaseAdapter{

	private List<ConversationListItemEntity> list;
	private LayoutInflater layoutInflater;
	
	
//	public ConversationListAdapter(List<ConversationListItemEntity> list,
//			Context context) {
//		super();
//		this.list = list;
//		layoutInflater = LayoutInflater.from(context);
//	}

	public ConversationListAdapter(Context context){
		super();
		list = new ArrayList<ConversationListItemEntity>();
		layoutInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
		ConversationListItemHolder holder;
		String display;
		ConversationListItemEntity entity = list.get(position);
		
		display = entity.getDisplayName();
		if (display == null) {
			display = entity.getPhoneNumber();
		} 
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.conversation_list_item, null);
			holder = new ConversationListItemHolder((TextView)convertView.findViewById(R.id.tvDisplayName),
					(TextView)convertView.findViewById(R.id.tvLastDate),
					(TextView)convertView.findViewById(R.id.tvLastMessage),
					(ImageView)convertView.findViewById(R.id.head));
			convertView.setTag(holder);
		} else {
			holder = (ConversationListItemHolder)convertView.getTag();
		}
		
		holder.getTvDisplayName().setText(display);
		holder.getTvLastDate().setText(entity.getLastDate());
		holder.getTvLastMessage().setText(entity.getLastMessage());
		holder.getIvHead().setImageResource(R.drawable.default_head);
		
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
	
	public int moveToHead(int position){
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
	
	public void clearAllElements(){
		list.clear();
		Log.i("jiang"	,"clearAllElements " + list.size());
	}
	
	public void remove(int position){
		list.remove(position);
	}
}
