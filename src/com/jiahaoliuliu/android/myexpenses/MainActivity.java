package com.jiahaoliuliu.android.myexpenses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.jiahaoliuliu.android.myexpenses.model.Expense;
import com.jiahaoliuliu.android.myexpenses.util.Preferences;
import com.jiahaoliuliu.android.myexpenses.util.Preferences.BooleanId;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v4.view.GravityCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class MainActivity extends SherlockFragmentActivity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	// Variables
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerLinearLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private TextView totalExpenseTV;
	private ListView contentListView;
	private ContentListAdapter contentListAdapter;

	private Context context;
	private Preferences preferences;
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	// For the soft input
	private InputMethodManager imm;
	private TelephonyManager telephonyManager;

	private List<Expense> expenseList;

	// Layouts
	//  Drawer
	private EditText addNewExpenseEditText;
	private Button addNewExpenseButton;
	private CheckBox addNewExpenseCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);

		// Get the title
		mTitle = getTitle();
		mDrawerTitle = getResources().getString(R.string.add_new_expense_title);

		context = this;
		preferences = new Preferences(context);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// Create the empty array
		expenseList = new ArrayList<Expense>();

		// Link the content
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLinearLayout = (LinearLayout)findViewById(R.id.linearLayoutDrawer);

		totalExpenseTV = (TextView)findViewById(R.id.totalExpenseQuantityTextView);
		contentListView = (ListView)findViewById(R.id.contentListView);
		
		View listHeaderView = getLayoutInflater().inflate(R.layout.list_header_layout, null);
		totalExpenseTV = (TextView)listHeaderView.findViewById(R.id.totalExpenseQuantityTextView);
		contentListView.addHeaderView(listHeaderView, null, false);

		addNewExpenseEditText = (EditText)mDrawerLinearLayout.findViewById(R.id.addNewExpenseEditText);
		addNewExpenseButton = (Button)mDrawerLinearLayout.findViewById(R.id.addNewExpenseButton);
		addNewExpenseCheckBox = (CheckBox)mDrawerLinearLayout.findViewById(R.id.addNewExpenseButtonCheckBox);

		// Set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// ActionBarDrawerToggle ties together the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close) {
			
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				addNewExpenseEditText.clearFocus();

				// Hide soft windows
				imm.hideSoftInputFromWindow(addNewExpenseEditText.getWindowToken(), 0);
			}
			
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// Set the title on the action when drawer open
				getSupportActionBar().setTitle(mDrawerTitle);

				// Force the keyboard to show on EditText focus
				addNewExpenseEditText.requestFocus();
            	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// If it is the first time that the application starts
		if (!preferences.getBoolean(BooleanId.ALREADY_STARTED)) {
			preferences.setBoolean(BooleanId.ALREADY_STARTED, true);
			preferences.setBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING, true);
		}

		boolean showAddNewExpenseAtBeginning = preferences.getBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING);
		if (showAddNewExpenseAtBeginning) {
			mDrawerLayout.openDrawer(mDrawerLinearLayout);
			addNewExpenseEditText.requestFocus();
        	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        	getSupportActionBar().setTitle(mDrawerTitle);
		}

		// Draw the layout
		addNewExpenseCheckBox.setChecked(showAddNewExpenseAtBeginning);
		totalExpenseTV.setText(String.valueOf(calculateTotalExpense()));
		contentListAdapter = new ContentListAdapter(context, R.layout.date_row_layout, expenseList);
	    contentListView.setAdapter(contentListAdapter);

		// Layout logic
		addNewExpenseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addNewExpense();
			}
		});
		
		addNewExpenseCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.setBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING, isChecked);
			}
		});

		addNewExpenseEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addNewExpense();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerLinearLayout)) {
				mDrawerLayout.closeDrawer(mDrawerLinearLayout);
			} else {
				mDrawerLayout.openDrawer(mDrawerLinearLayout);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	/*
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}*/

	private GsmCellLocation getCellLocation() {
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
		    final GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
		    return location;
		} else {
			Log.e(LOG_TAG, "The phone type is not gsm");
			return null;
		}
	}
	
	private double calculateTotalExpense() {
		double result = 0.0;
		for (Expense expense: expenseList) {
			result += expense.getQuantity();
		}

		return result;
	}
	
	private class ContentListAdapter extends ArrayAdapter<String> {
		
		private Context context;
		private List<Expense> expenseList;
		private SimpleDateFormat format = new SimpleDateFormat("y-M-d HH:mm");

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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.date_row_layout, parent, false);

			TextView expenseDateTV = (TextView)rowView.findViewById(R.id.expenseDateTextView);
			
			expenseDateTV.setText(format.format(expenseList.get(position).getDate()));

			TextView expenseQuantityTV = (TextView)rowView.findViewById(R.id.expenseQuantityTextView);
			expenseQuantityTV.setText(String.valueOf(expenseList.get(position).getQuantity()));

			return rowView;
		}
		
	}

	private void addNewExpense() {
		// If the user has not entered any data
		String quantityString = addNewExpenseEditText.getText().toString();
		if (quantityString == null || quantityString.equals("")) {
			Toast.makeText(
					context,
					getResources().getString(R.string.error_add_new_expense_empty),
					Toast.LENGTH_LONG).show();
			return;
		}

		Log.v(LOG_TAG, "Adding new quantity: " + quantityString);
		Expense expense = new Expense();
		expense.setDate(new Date());
		expense.setLocation(getCellLocation());
		expense.setQuantity(Double.valueOf(quantityString));
		expenseList.add(expense);
		
		// Update the layout of the main screen
		totalExpenseTV.setText(String.valueOf(calculateTotalExpense()));

		// Clear the edit text
		addNewExpenseEditText.setText("");
		contentListAdapter.notifyDataSetChanged();

		Toast.makeText(
				context,
				getResources().getString(R.string.add_new_expense_correctly),
				Toast.LENGTH_LONG
				).show();
	}
}