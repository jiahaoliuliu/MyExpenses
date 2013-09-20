package com.jiahaoliuliu.android.myexpenses.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiahaoliuliu.android.myexpenses.R;
import com.jiahaoliuliu.android.myexpenses.model.Expense;

public class ContentListAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private List<Expense> expenseList;
	private SimpleDateFormat dayOfWeekFormatter;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat hourFormtter;
	//private ViewHolder viewHolder;
	// Set the number of decimals in the editText
	private DecimalFormat dec = new DecimalFormat("0.00");
	// The date of the last group to determine if new group should be created or not
	private Date lastGroupDate = null;
	private LayoutInflater inflater;

	public ContentListAdapter(Context context, int resource, List<Expense> expenseList) {
		super(context, resource);
		this.context = context;
		this.expenseList = expenseList;
		Locale currentLocale = context.getResources().getConfiguration().locale;
		dayOfWeekFormatter = new SimpleDateFormat("EEEE", currentLocale);
		dateFormatter = new SimpleDateFormat("dd MMMM yyyy", currentLocale);
		hourFormtter = new SimpleDateFormat("HH:mm", currentLocale);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return this.expenseList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.date_row_layout, parent, false);
		Date date = expenseList.get(position).getDate();

		if (isNewGroup(position)) {
			// Remove the upper divider if it is in the first position
			if (position == 0) {
				View upperDivider = (View)convertView.findViewById(R.id.groupDivider);
				upperDivider.setVisibility(View.GONE);
			}
			TextView expenseDayOfWeekTV = (TextView)convertView.findViewById(R.id.expenseDayOfWeekTextView);
			expenseDayOfWeekTV.setText(dayOfWeekFormatter.format(date));

			TextView expenseDateTV = (TextView)convertView.findViewById(R.id.expenseDateTextView);
			expenseDateTV.setText(dateFormatter.format(date));
		} else {
			RelativeLayout rowHeaderLayout = (RelativeLayout)convertView.findViewById(R.id.expenseHeaderLayout);
			rowHeaderLayout.setVisibility(View.GONE);
		}
		TextView expenseHourTV = (TextView)convertView.findViewById(R.id.expenseHoursTextView);
		expenseHourTV.setText(hourFormtter.format(date));

		TextView expenseCommentTV = (TextView)convertView.findViewById(R.id.expenseCommentTextView);
		expenseCommentTV.setText(expenseList.get(position).getComment());

		TextView expenseQuantityTV = (TextView)convertView.findViewById(R.id.expenseQuantityTextView);
		expenseQuantityTV.setText(String.valueOf(dec.format(expenseList.get(position).getQuantity()).replace(",", ".")));

		return convertView;
	}
	
	 private boolean isNewGroup(int position) {
		 // If the group has never been created, create it (and set the date)
		 if (lastGroupDate == null) {
			 lastGroupDate = expenseList.get(position).getDate();
			 return true;
		 }

         // Compare date values, ignore time values
         Calendar calThis = Calendar.getInstance(context.getResources().getConfiguration().locale);
         Date thisDate = expenseList.get(position).getDate();
         calThis.setTime(thisDate);

         Calendar calPrev = Calendar.getInstance(context.getResources().getConfiguration().locale);
         calPrev.setTime(lastGroupDate);

         int nDayThis = calThis.get(Calendar.DAY_OF_YEAR);
         int nDayPrev = calPrev.get(Calendar.DAY_OF_YEAR);

         if (nDayThis != nDayPrev || calThis.get(Calendar.YEAR) != calPrev.get(Calendar.YEAR)) {
        	 lastGroupDate = thisDate;
             return true;
         }

		 return false;
     }

}