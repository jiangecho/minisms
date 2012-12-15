package com.minisms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationAdapter extends BaseAdapter{

	private List<ConversationEntity> list;
	private static ConversationAdapter adapter;
	
	private LayoutInflater layoutInflater;
	
	private final int OUT = 1;
	private final int IN = 0;
	
	public static ConversationAdapter getInstance(Context context){
		if (adapter == null) {
			adapter = new ConversationAdapter(context); 
		}
		return adapter;
	}
	
	public ConversationAdapter(Context context){
		layoutInflater = LayoutInflater.from(context);
		list = new ArrayList<ConversationEntity>();
	}
	
	public void init(){
		list.clear();
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
		int ret;
		final ConversationEntity entity = list.get(position);
		
		if (entity.isComingMsg()) {
			ret = IN;
		} else {
			ret = OUT;
		}
		
		return ret;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ConversationEntity chatEntity = list.get(position);
		Holder chatHolderIn = null;
		Holder chatHolderOut = null;
		
		if (convertView == null) {
			if (chatEntity.isComingMsg()) {
				chatHolderIn = new Holder();
				convertView = layoutInflater.inflate(R.layout.chat_from_item, null);
			} else {
				convertView = layoutInflater.inflate(R.layout.chat_to_item, null);
				chatHolderOut = new Holder();
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
				chatHolderIn = (Holder)convertView.getTag();
			} else {
				chatHolderOut = (Holder)convertView.getTag();
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

	public void removeElement(final int postion){
		list.remove(postion);
	}
	
	public void addElement(final ConversationEntity entity){
		list.add(entity);
		//Log.i("jiang", "ConversationAdapter  list size " + list.size());
		//notifyDataSetChanged();
	}
	
	public static void unInit(){
		adapter = null;
	}
	
	
	private class Holder {
		
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
}
