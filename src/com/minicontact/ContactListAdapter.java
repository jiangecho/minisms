package com.minicontact;

import java.util.ArrayList;
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

public class ContactListAdapter extends BaseAdapter {

	private List<ContactEntity> list;
	private static ContactListAdapter adapter; 
	private LayoutInflater layoutInflater;
	
	private List<String> sections;
	private List<Integer> startIndexs;
	private List<DataSetObserver> mRegisteredObservers;
	
	private ContactListAdapter(Context context){
		list = new ArrayList<ContactEntity>();
		sections = new ArrayList<String>();
		startIndexs = new ArrayList<Integer>();
		
		layoutInflater = LayoutInflater.from(context);
		mRegisteredObservers = new ArrayList<DataSetObserver>();
		
	}
	
	public static ContactListAdapter getInstance(Context context){
		if (adapter == null) {
			Log.i("jiang aaaaa", "new ContactListAdapter");
			adapter = new ContactListAdapter(context);
		}
		return adapter;
	}
	
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		super.registerDataSetObserver(observer);
		//to prevent memory leak
		mRegisteredObservers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		//super.unregisterDataSetObserver(observer);
	}
	
	public void unRegisterDataSetObserver(){
		if (mRegisteredObservers != null) {
			for (DataSetObserver observer : mRegisteredObservers) {
				super.unregisterDataSetObserver(observer);
			}
			mRegisteredObservers.clear();
		}
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (list != null) {
			return list.get(position);
		}else {
			return null;
		}
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
		//holder.ivHead.setImageDrawable(defaultHead);
		holder.tvName.setText(list.get(position).getName());
		return convertView;
	}
	
	public void addElement(ContactEntity entity){
		char[] ch = {(char) entity.getSortKey()};
		String sortKey = new String(ch);
		ContactEntity preEntity;
		
		if (!list.isEmpty()) {
			preEntity = list.get(list.size() - 1);
			if (entity.getSortKey() != preEntity.getSortKey()) {
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

	public int getPositionForSection(String section){
		
		int sectionIndex = sections.indexOf(section);
		if (sectionIndex != -1) {
			return startIndexs.get(sectionIndex);
		}else {
			return -1;
		}
	}

	private int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		return startIndexs.get(section);
	}

	private int getSectionForPosition(int position) {
		ContactEntity entity = list.get(position);
		char[] ch = {(char) entity.getSortKey()};
		String sortKey = new String(ch);
		return sections.indexOf(sortKey);
	}

//	private Object[] getSections() {
//		return sections.toArray();
//	}
	
	public void clear(){
		list.clear();
		sections.clear();
		startIndexs.clear();
	}
	
	// we should call this method to prevent memory leak
	public static void unInit(){
		adapter = null;
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
}
