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
import com.jiahaoliuliu.android.myexpenses.model.ExpenseListTotal;

public class ContentListAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private ExpenseListTotal expenseListTotal;
	private SimpleDateFormat dayOfWeekFormatter;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat hourFormtter;
	// Set the number of decimals in the editText
	private DecimalFormat dec = new DecimalFormat("0.00");
	private LayoutInflater inflater;

	public ContentListAdapter(Context context, int resource, ExpenseListTotal expenseListTotal) {
		super(context, resource);
		this.context = context;
		this.expenseListTotal = expenseListTotal;
		Locale currentLocale = context.getResources().getConfiguration().locale;
		dayOfWeekFormatter = new SimpleDateFormat("EEEE", currentLocale);
		dateFormatter = new SimpleDateFormat("dd MMMM yyyy", currentLocale);
		hourFormtter = new SimpleDateFormat("HH:mm", currentLocale);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return this.expenseListTotal.getTotalExpenses();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.date_row_layout, parent, false);
		Expense expense = expenseListTotal.getExpense(position);
		Date expenseDate = expense.getDate();
		
		if (expenseListTotal.isHeader(expense)) {
			// Remove the upper divider if it is in the first position
			if (position == 0) {
				View upperDivider = (View)convertView.findViewById(R.id.groupDivider);
				upperDivider.setVisibility(View.GONE);
			}
			TextView expenseDayOfWeekTV = (TextView)convertView.findViewById(R.id.expenseDayOfWeekTextView);
			expenseDayOfWeekTV.setText(dayOfWeekFormatter.format(expenseDate));

			TextView expenseDateTV = (TextView)convertView.findViewById(R.id.expenseDateTextView);
			expenseDateTV.setText(dateFormatter.format(expenseDate));
			
			TextView dailyTotalTV = (TextView)convertView.findViewById(R.id.dailyTotal);
			dailyTotalTV.setText(expenseListTotal.getDailyTotal(expenseDate));
		} else {
			RelativeLayout rowHeaderLayout = (RelativeLayout)convertView.findViewById(R.id.expenseHeaderLayout);
			rowHeaderLayout.setVisibility(View.GONE);
		}
		TextView expenseHourTV = (TextView)convertView.findViewById(R.id.expenseHoursTextView);
		expenseHourTV.setText(hourFormtter.format(expenseDate));

		TextView expenseCommentTV = (TextView)convertView.findViewById(R.id.expenseCommentTextView);
		expenseCommentTV.setText(expense.getComment());

		TextView expenseQuantityTV = (TextView)convertView.findViewById(R.id.expenseQuantityTextView);
		expenseQuantityTV.setText(String.valueOf(dec.format(expense.getQuantity()).replace(",", ".")));

		return convertView;
	}
	
}