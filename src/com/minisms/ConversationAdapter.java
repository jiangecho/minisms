package com.minisms;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationAdapter extends BaseAdapter{

	private String from;
	private List<ConversationEntity> list;
	private Context context;
public ConversationAdapter() {
	// TODO Auto-generated constructor stub
}	
	private LayoutInflater layoutInflater;
	
	private final int OUT = 1;
	private final int IN = 0;
	
	
	public ConversationAdapter(String from, List<ConversationEntity> list, Context context) {
		super();
		
		this.from = from;
		this.list = list;
		this.context = context;
		
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

	

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		//return super.getViewTypeCount();
		return 2;
	}

	
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ConversationEntity conversationEntity = list.get(position);
		
		if (conversationEntity.isComingMsg()) {
			return IN;
		} else {
			return OUT;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ConversationEntity chatEntity = list.get(position);
		ConversationHolder chatHolderIn = null;
		ConversationHolder chatHolderOut = null;
		
		if (convertView == null) {
			if (chatEntity.isComingMsg()) {
				chatHolderIn = new ConversationHolder();
				convertView = layoutInflater.inflate(R.layout.chat_from_item, null);
			} else {
				convertView = layoutInflater.inflate(R.layout.chat_to_item, null);
				chatHolderOut = new ConversationHolder();
			}
			
			if (getItemViewType(position) == IN) {
				chatHolderIn.setIvUerImage((ImageView)(convertView.findViewById(R.id.iv_user_image)));
				chatHolderIn.setTvContent((TextView)(convertView.findViewById(R.id.tv_content)));
				chatHolderIn.setTvTime((TextView)(convertView.findViewById(R.id.tv_time)));
				convertView.setTag(chatHolderIn);
				
			} else {
				chatHolderOut.setIvUerImage((ImageView)(convertView.findViewById(R.id.iv_user_image)));
				chatHolderOut.setTvContent((TextView)(convertView.findViewById(R.id.tv_content)));
				chatHolderOut.setTvTime((TextView)(convertView.findViewById(R.id.tv_time)));
				convertView.setTag(chatHolderOut);

			}
			
			
		} else {
			if (getItemViewType(position) == IN) {
				chatHolderIn = (ConversationHolder)convertView.getTag();
			} else {
				chatHolderOut = (ConversationHolder)convertView.getTag();
			}
		}
		
		if (getItemViewType(position) == IN) {
			chatHolderIn.getIvUerImage().setImageResource(R.drawable.default_head);
			chatHolderIn.getTvContent().setText(chatEntity.getContent());
			
			if ((position == 0) || (chatEntity.getTime() - ((ConversationEntity)list.get(position - 1)).getTime() > 60000)) {
				chatHolderIn.getTvTime().setText(chatEntity.getmDate());
				chatHolderIn.getTvTime().getBackground().setAlpha(10);
			} else {
				chatHolderIn.getTvTime().setVisibility(View.GONE);
			}
		} else {
			chatHolderOut.getIvUerImage().setImageResource(R.drawable.default_head);
			chatHolderOut.getTvContent().setText(chatEntity.getContent());
			
			if ((position == 0) || (chatEntity.getTime() - ((ConversationEntity)list.get(position - 1)).getTime() > 60000)) {
				chatHolderOut.getTvTime().setText(chatEntity.getmDate());
				chatHolderOut.getTvTime().getBackground().setAlpha(10);
			} else {
				chatHolderOut.getTvTime().setVisibility(View.GONE);
			}
		}
		
		
		return convertView;
	}

	public void removeElement(int postion){
		list.remove(postion);
	}
	
	public void addConversationEntity(ConversationEntity conversationEntity){
		list.add(conversationEntity);
		Log.i("jiang", "ConversationAdapter  list size " + list.size());
		//notifyDataSetChanged();
	}
}
