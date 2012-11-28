package com.minicontact;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter implements SectionIndexer{

	private List<ContactEntity> list;
	private static ContactListAdapter adapter; 
	private LayoutInflater layoutInflater;
	
	private List<String> sections;
	private List<Integer> startIndexs;
	
	private ContactListAdapter(Context context){
		list = new ArrayList<ContactEntity>();
		sections = new ArrayList<String>();
		startIndexs = new ArrayList<Integer>();
		
		layoutInflater = LayoutInflater.from(context);
	}
	
	public static ContactListAdapter getInstance(Context context){
		if (adapter == null) {
			adapter = new ContactListAdapter(context);
		}
		
		return adapter;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		if (convertView == null) {
			convertView = (View)layoutInflater.inflate(com.minisms.R.layout.contact_list_item, null);
			holder = new Holder((TextView)convertView.findViewById(com.minisms.R.id.tv_alpha),
								(ImageView)convertView.findViewById(com.minisms.R.id.iv_contact_head),
								(TextView)convertView.findViewById(com.minisms.R.id.tvContactName));
			convertView.setTag(holder);
		}else {
			holder = (Holder)convertView.getTag();
		}
		
		if (position == getPositionForSection(getSectionForPosition(position))){
			String sortKey = sections.get(getSectionForPosition(position));
			holder.tvAlpha.setText(sortKey );
			holder.tvAlpha.setVisibility(View.VISIBLE);
		}else {
			holder.tvAlpha.setVisibility(View.GONE);
		}
		holder.ivHead.setImageResource(com.minisms.R.drawable.default_head);
		holder.tvName.setText(list.get(position).getName());
		return convertView;
	}
	
	public void addElement(ContactEntity entity){
		String sortKey = entity.getSortKey();
		ContactEntity preEntity;
		
		if (!list.isEmpty()) {
			preEntity = list.get(list.size() - 1);
			if (!sortKey.equals(preEntity.getSortKey())) {
				sections.add(sortKey);
				startIndexs.add(Integer.valueOf(list.size()));
			}
		}else {
				sections.add(sortKey);
				startIndexs.add(Integer.valueOf(list.size()));
		}
		
		list.add(entity);
	}
	
	public void removeElement(int position){
		list.remove(position);
		
		//TODO maybe we should remove one section
	}

	
	private class Holder{
		public TextView tvAlpha;
		public ImageView ivHead;
		public TextView tvName;
		
		public Holder(TextView tvAlpha, ImageView ivHead, TextView tvName) {
			super();
			this.tvAlpha = tvAlpha;
			this.ivHead = ivHead;
			this.tvName = tvName;
		}
		
	}


	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return startIndexs.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		ContactEntity entity = list.get(position);
		return sections.indexOf(entity.getSortKey());
	}

	@Override
	public Object[] getSections() {
		return sections.toArray();
	}
	
	public void clear(){
		list.clear();
		sections.clear();
		startIndexs.clear();
	}
}
