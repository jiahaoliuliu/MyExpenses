package com.jiahaoliuliu.android.myexpenses;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	// Declare variables
	Context context;
	String[] mTitle;
	String[] mSubTitle;
	int[] mIcon;
	LayoutInflater inflater;
	
	public MenuListAdapter(Context context, String[] title, String[] subtitle, int[] icon) {
		this.context = context;
		this.mTitle = title;
		this.mSubTitle = subtitle;
		mIcon = icon;
	}
	
	@Override
	public int getCount() {
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Declare Variables
		TextView txtTitle;
		TextView txtSubTitle;
		ImageView imgIcon;
		
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);
		
		// Locate the TextViews
		txtTitle = (TextView)itemView.findViewById(R.id.title);
		txtSubTitle = (TextView)itemView.findViewById(R.id.subtitle);
		
		// Locate the ImageView
		imgIcon = (ImageView)itemView.findViewById(R.id.icon);
		
		// Set the data
		txtTitle.setText(mTitle[position]);
		txtSubTitle.setText(mSubTitle[position]);
		
		imgIcon.setImageResource(mIcon[position]);
		
		return itemView;
		
	}

}
