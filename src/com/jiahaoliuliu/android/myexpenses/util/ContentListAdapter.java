package com.jiahaoliuliu.android.myexpenses.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiahaoliuliu.android.myexpenses.R;
import com.jiahaoliuliu.android.myexpenses.model.ExpenseListTotal;
import com.jiahaoliuliu.android.myexpenses.model.Expense;

public class ContentListAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private ExpenseListTotal expenseListTotal;
	private SimpleDateFormat dayOfWeekFormatter;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat hourFormtter;
	private LayoutInflater inflater;
	private Locale locale;

	public ContentListAdapter(Context context, int resource, ExpenseListTotal expenseListTotal) {
		super(context, resource);
		this.context = context;
		this.expenseListTotal = expenseListTotal;
		Locale currentLocale = context.getResources().getConfiguration().locale;
		dayOfWeekFormatter = new SimpleDateFormat("EEEE", currentLocale);
		dateFormatter = new SimpleDateFormat("dd MMMM yyyy", currentLocale);
		hourFormtter = new SimpleDateFormat("HH:mm", currentLocale);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		locale = context.getResources().getConfiguration().locale;
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
			dailyTotalTV.setText(TypeConverter.intToCurrency(expenseListTotal.getDailyTotal(expenseDate), locale));
		} else {
			RelativeLayout rowHeaderLayout = (RelativeLayout)convertView.findViewById(R.id.expenseHeaderLayout);
			rowHeaderLayout.setVisibility(View.GONE);
		}
		TextView expenseHourTV = (TextView)convertView.findViewById(R.id.expenseHoursTextView);
		expenseHourTV.setText(hourFormtter.format(expenseDate));

		TextView expenseCommentTV = (TextView)convertView.findViewById(R.id.expenseCommentTextView);
		expenseCommentTV.setText(expense.getComment());

		TextView expenseQuantityTV = (TextView)convertView.findViewById(R.id.expenseQuantityTextView);
		expenseQuantityTV.setText(TypeConverter.intToCurrency(expense.getQuantity(), locale));

		return convertView;
	}
	
}