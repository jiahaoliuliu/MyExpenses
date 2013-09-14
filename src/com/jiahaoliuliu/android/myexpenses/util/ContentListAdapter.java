package com.jiahaoliuliu.android.myexpenses.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jiahaoliuliu.android.myexpenses.R;
import com.jiahaoliuliu.android.myexpenses.model.Expense;

public class ContentListAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private List<Expense> expenseList;
	private SimpleDateFormat format = new SimpleDateFormat("y-M-d HH:mm");
	private ViewHolder viewHolder;
	// Set the number of decimals in the editText
	private DecimalFormat dec = new DecimalFormat("0.00");

	class ViewHolder {
		TextView expenseDateTV;
		TextView expenseCommentTV;
		TextView expenseQuantityTV;
	}

	public ContentListAdapter(Context context, int resource, List<Expense> expenseList) {
		super(context, resource);
		this.context = context;
		this.expenseList = expenseList;
	}
	
	@Override
	public int getCount() {
		return this.expenseList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.date_row_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.expenseDateTV = (TextView)convertView.findViewById(R.id.expenseDateTextView);
			viewHolder.expenseCommentTV = (TextView)convertView.findViewById(R.id.expenseCommentTextView);
			viewHolder.expenseQuantityTV = (TextView)convertView.findViewById(R.id.expenseQuantityTextView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		viewHolder.expenseDateTV.setText(format.format(expenseList.get(position).getDate()));
		viewHolder.expenseCommentTV.setText(expenseList.get(position).getComment());
		viewHolder.expenseQuantityTV.setText(String.valueOf(dec.format(expenseList.get(position).getQuantity()).replace(",", ".")));

		return convertView;
	}
	
}