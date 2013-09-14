package com.jiahaoliuliu.android.myexpenses;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Menu;
import com.jiahaoliuliu.android.myexpenses.model.Expense;
import com.jiahaoliuliu.android.myexpenses.util.ContentListAdapter;
import com.jiahaoliuliu.android.myexpenses.util.ExpenseDBAdapter;
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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

	private static final int MENU_ITEM_RIGHT_LIST_ID = 10000;

	// Variables
	private DrawerLayout mDrawerLayout;
	private LinearLayout mLeftLinearDrawer;
	private LinearLayout mRightLinearDrawer;
	private RelativeLayout noExpenseFoundRelativeLayout;
	private ScrollView expenseFoundScrollLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private TextView totalExpenseTV;
	private ListView contentListView;
	private int contentPositionSelected = -1;
	private ContentListAdapter contentListAdapter;
	private ActionMode editActionMode;

	private Context context;
	private Preferences preferences;
	
	private CharSequence mTitle;
	private CharSequence mLeftDrawerTitle;
	private CharSequence mRightDrawerTitle;

	private boolean showAddNewExpenseAtBeginning;
	// For the soft input
	private InputMethodManager imm;
	private TelephonyManager telephonyManager;

	private List<Expense> expenseList;

	// Layouts
	//  Left Drawer
	private EditText addNewExpenseEditText;
	private EditText addNewExpenseCommentEditText;
	private Button addNewExpenseButton;
	private CheckBox addNewExpenseCheckBox;
	
	//  Right Drawer
	private Button dateTitleButton;
	private Button dateButton;
	private DatePicker datePicker;
	private Button timeButton;
	private EditText quantityET;
	private EditText commentET;
	
	// Database
	private ExpenseDBAdapter expenseDBAdapter;

	// Set the number of decimals in the editText
	public DecimalFormat dec = new DecimalFormat("0.00");
	// The locale is set as us by default
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);

		context = this;
		
		// Lock the screen orientation
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Get the title
		mTitle = getTitle();
		mLeftDrawerTitle = getResources().getString(R.string.add_new_expense_title);
		mRightDrawerTitle = getResources().getString(R.string.edit_expense_title);
		preferences = new Preferences(context);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// Link the content
		//  Layouts
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mLeftLinearDrawer = (LinearLayout)findViewById(R.id.leftLinearDrawer);
		mRightLinearDrawer = (LinearLayout)findViewById(R.id.rightLinearDrawer);
		noExpenseFoundRelativeLayout = (RelativeLayout)findViewById(R.id.noExpenseFoundRelativeLayout);
		expenseFoundScrollLayout = (ScrollView)findViewById(R.id.expenseScrollView);

		//  Main layout
		totalExpenseTV = (TextView)findViewById(R.id.totalExpenseQuantityTextView);
		contentListView = (ListView)findViewById(R.id.contentListView);
		
		View listHeaderView = getLayoutInflater().inflate(R.layout.list_header_layout, null);
		totalExpenseTV = (TextView)listHeaderView.findViewById(R.id.totalExpenseQuantityTextView);
		contentListView.addHeaderView(listHeaderView, null, false);

		//  Left Drawer
		addNewExpenseEditText = (EditText)mLeftLinearDrawer.findViewById(R.id.addNewExpenseEditText);
		addNewExpenseCommentEditText = (EditText)mLeftLinearDrawer.findViewById(R.id.addNewExpenseCommentEditText);
		addNewExpenseButton = (Button)mLeftLinearDrawer.findViewById(R.id.addNewExpenseButton);
		addNewExpenseCheckBox = (CheckBox)mLeftLinearDrawer.findViewById(R.id.addNewExpenseButtonCheckBox);

		//  Right Drawer
		dateTitleButton = (Button)mRightLinearDrawer.findViewById(R.id.dateTitleButton);
		dateButton = (Button)mRightLinearDrawer.findViewById(R.id.dateButton);
		datePicker = (DatePicker)mRightLinearDrawer.findViewById(R.id.datePicker);
		timeButton = (Button)mRightLinearDrawer.findViewById(R.id.timeButton);
		quantityET = (EditText)mRightLinearDrawer.findViewById(R.id.quantityEditText);
		commentET = (EditText)mRightLinearDrawer.findViewById(R.id.commentEditText);
		
		// Set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Create the variables
		expenseDBAdapter = new ExpenseDBAdapter(context);
		expenseList = expenseDBAdapter.getAllExpenses();

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
				if (view.equals(mLeftLinearDrawer)) {
					addNewExpenseEditText.clearFocus();
	
					// Hide soft windows
					imm.hideSoftInputFromWindow(addNewExpenseEditText.getWindowToken(), 0);
				// Right drawer
				} else {
					// Save the new data to shared preferences
					if (editActionMode != null) {
						editActionMode.finish();
					}
					
					// Restore the content position
					contentPositionSelected = -1;
				}
			}
			
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (drawerView.equals(mLeftLinearDrawer)) {
					// Set the title on the action when drawer open
					getSupportActionBar().setTitle(mLeftDrawerTitle);
	
					// Force the keyboard to show on EditText focus
					addNewExpenseEditText.requestFocus();
	            	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	            // Right drawer
				} else {
					// Set the title on the action when drawer open
					getSupportActionBar().setTitle(mRightDrawerTitle);
					if (expenseList.isEmpty()) {
						// Show the no Expense Found layout
						noExpenseFoundRelativeLayout.setVisibility(View.VISIBLE);
						expenseFoundScrollLayout.setVisibility(View.GONE);
					} else {
						noExpenseFoundRelativeLayout.setVisibility(View.GONE);
						expenseFoundScrollLayout.setVisibility(View.VISIBLE);

						editActionMode = startActionMode(new EditExpenseActionMode());
						
						// Start editing
						Expense expenseToBeEdited;
						if (contentPositionSelected >= 0) {
							expenseToBeEdited = expenseList.get(contentPositionSelected-1);
						// Else get the last element
						} else {
							// TODO: Create a better queue to select the one of the last edition.(Rated?)
							expenseToBeEdited = expenseList.get(expenseList.size()-1);
						}

						// Date
						dateButton.setText(dateFormatter.format(expenseToBeEdited.getDate()));
						Calendar cal = Calendar.getInstance();
						cal.setTime(expenseToBeEdited.getDate());
						datePicker.init(cal.get(Calendar.YEAR),
								cal.get(Calendar.MONTH),
								cal.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
									
									@Override
									public void onDateChanged(DatePicker view, int year, int monthOfYear,
											int dayOfMonth) {
										dateButton.setText(
								                new StringBuilder()
								                        // Month is 0 based so add 1
								                		.append(year).append("-")
								                        .append(pad(monthOfYear + 1)).append("-")
								                        .append(pad(dayOfMonth)));
									}
								});
						// Change the action bar menus
						// Get the data from the preferences
						// If there is not data, get the data of the last element of the list (the newest)
						
						// If the user clicks on the accept, cancel or remove button, remove the data from shared preferences
					}
				}
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// If it is the first time that the application starts
		if (!preferences.getBoolean(BooleanId.ALREADY_STARTED)) {
			preferences.setBoolean(BooleanId.ALREADY_STARTED, true);
			preferences.setBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING, true);
		}

		// Draw the layout
		showAddNewExpenseAtBeginning = preferences.getBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING);
		addNewExpenseCheckBox.setChecked(showAddNewExpenseAtBeginning);
		totalExpenseTV.setText(String.valueOf(dec.format(calculateTotalExpense()).replace(",", ".")));
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
				showAddNewExpenseAtBeginning = isChecked;
				preferences.setBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING, showAddNewExpenseAtBeginning);
			}
		});

		addNewExpenseEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					addNewExpenseCommentEditText.requestFocus();
					return true;
				}
				return false;
			}
		});
		
		addNewExpenseCommentEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addNewExpense();
					return true;
				}
				return false;
			}
		});
		
		contentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> lparent, View view, int position,
					long id) {
				// Save the variable
				contentPositionSelected = position;
				// Open right drawer
				if (mDrawerLayout.isDrawerOpen(mLeftLinearDrawer)) {
					mDrawerLayout.closeDrawer(mLeftLinearDrawer);
				}
				mDrawerLayout.openDrawer(mRightLinearDrawer);
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_RIGHT_LIST_ID, Menu
        		.NONE, context.getResources().getString(R.string.action_bar_name_edit))
        	.setIcon(R.drawable.ic_drawer)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mLeftLinearDrawer)) {
				mDrawerLayout.closeDrawer(mLeftLinearDrawer);
			} else {
				if (mDrawerLayout.isDrawerOpen(mRightLinearDrawer)) {
					mDrawerLayout.closeDrawer(mRightLinearDrawer);
				}
				mDrawerLayout.openDrawer(mLeftLinearDrawer);
			}
		} else if (item.getItemId() == MENU_ITEM_RIGHT_LIST_ID) {
			if (mDrawerLayout.isDrawerOpen(mRightLinearDrawer)) {
				mDrawerLayout.closeDrawer(mRightLinearDrawer);
			} else {
				if (mDrawerLayout.isDrawerOpen(mLeftLinearDrawer)) {
					mDrawerLayout.closeDrawer(mLeftLinearDrawer);
				}
				mDrawerLayout.openDrawer(mRightLinearDrawer);
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
		// Format the quantity.
		String quantityStringFormatted = dec.format(Double.valueOf(quantityString)).replace(",", ".");
		
		Log.v (LOG_TAG, "Quantity after format: " + quantityStringFormatted);
		Expense expense = new Expense();
		expense.setDate(new Date());
		expense.setComment(addNewExpenseCommentEditText.getText().toString());
		expense.setLocation(getCellLocation());
		expense.setQuantity(Double.valueOf(quantityStringFormatted));
		expenseList.add(expense);
		
		// Update the layout of the main screen
		totalExpenseTV.setText(String.valueOf(dec.format(calculateTotalExpense()).replace(",", ".")));

		// Clear the edit text
		addNewExpenseEditText.setText("");
		addNewExpenseCommentEditText.setText("");
		// Return the focus to expense edit text
		addNewExpenseEditText.requestFocus();

		contentListAdapter.notifyDataSetChanged();

		expenseDBAdapter.insertNewExpense(expense);

		Toast.makeText(
				context,
				getResources().getString(R.string.add_new_expense_correctly),
				Toast.LENGTH_LONG
				).show();
	}

	// The action mode shown in the action bar when the user opens the right drawer
    private final class EditExpenseActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Used to put dark icons on light action bar
            menu.add("Cancel")
            	.setIcon(R.drawable.ic_cancel)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            menu.add("Delete")
            	.setIcon(R.drawable.ic_remove)
            	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Toast.makeText(MainActivity.this, "Got click: " + item, Toast.LENGTH_SHORT).show();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

// Life cycle
	@Override
	protected void onPause() {
		super.onPause();
		// Close the database on pause
		expenseDBAdapter.closeDatabase();
		// Hide soft windows if it is opened
		imm.hideSoftInputFromWindow(addNewExpenseEditText.getWindowToken(), 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//showAddNewExpenseAtBeginning = preferences.getBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING);
		if (showAddNewExpenseAtBeginning) {
			// Close the right drawer if it was open
			if (mDrawerLayout.isDrawerOpen(mRightLinearDrawer)) {
				mDrawerLayout.closeDrawer(mRightLinearDrawer);
			}
			mDrawerLayout.openDrawer(mLeftLinearDrawer);
        	getSupportActionBar().setTitle(mLeftDrawerTitle);
			addNewExpenseEditText.requestFocus();
			addNewExpenseEditText.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.showSoftInput(addNewExpenseEditText, InputMethodManager.SHOW_FORCED);
		        }   
		    }, 100);
		}
	}
	
    private String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}