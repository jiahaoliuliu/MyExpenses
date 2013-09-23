package com.jiahaoliuliu.android.myexpenses;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Menu;
import com.jiahaoliuliu.android.myexpenses.model.Expense;
import com.jiahaoliuliu.android.myexpenses.util.ContentListAdapter;
import com.jiahaoliuliu.android.myexpenses.util.ExpenseComparator;
import com.jiahaoliuliu.android.myexpenses.util.ExpenseDBAdapter;
import com.jiahaoliuliu.android.myexpenses.util.Preferences;
import com.jiahaoliuliu.android.myexpenses.util.Preferences.BooleanId;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v4.view.GravityCompat;

public class MainActivity extends SherlockFragmentActivity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int MENU_ITEM_RIGHT_LIST_ID = 10000;
	private static final int MENU_SAVE_BUTTON_ID = 10001;
	private static final int MENU_REMOVE_BUTTON_ID = 10002;

	// The list of errors returned
	public enum OperationResult {
		CORRECT, CORRECT_DATA_INTEGROUS,
		
		// Error from the main activity
		ERROR_DATA_EMPTY,
		
		// Errors from the saved data, it need to be refreshed
		ERROR_QUANTITY_INCORRECT, ERROR_DATA_NOT_EXISTS, ERROR_DATA_NOT_INTEGROUS, ERROR_ADDING_INCORRECT, ERROR_REMOVING_INCORRECT;
	}

	// Variables
	private DrawerLayout mDrawerLayout;
	private LinearLayout mLeftLinearDrawer;
	private LinearLayout mRightLinearDrawer;
	private RelativeLayout noExpenseFoundRelativeLayout;
	private ScrollView expenseFoundScrollLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private TextView totalExpenseTV;
	private ListView expenseListView;
	private int contentPositionSelected = -1;
	private ContentListAdapter expenseListAdapter;

	private Context context;
	private Preferences preferences;
	
	private CharSequence mTitle;
	private CharSequence mLeftDrawerTitle;
	private CharSequence mRightDrawerTitle;
	private boolean showAddNewExpenseAtBeginning;

	private Expense expenseToBeEdited;
	private Expense expenseToBeEditedCloned;
	private Calendar calendar;
	private AlertDialog removeExpenseAlertDialog;

	// For the soft input
	private InputMethodManager imm;

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
	private Button timeTitleButton;
	private Button timeButton;
	private TimePicker timePicker;
	private EditText quantityET;
	private EditText commentET;
	
	// The action menu
	private Menu actionBarMenu;

	// Database
	private ExpenseDBAdapter expenseDBAdapter;

	// Set the number of decimals in the editText
	public DecimalFormat dec = new DecimalFormat("0.00");
	// The locale is set as us by default
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat timeFormatter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_main);

		context = this;

		Locale currentLocale = getResources().getConfiguration().locale;
		dateFormatter = new SimpleDateFormat("dd MMM yyyy,EEE", currentLocale);
		timeFormatter = new SimpleDateFormat("HH:mm", currentLocale);
		// Lock the screen orientation
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Get the title
		mTitle = getTitle();
		mLeftDrawerTitle = getResources().getString(R.string.add_new_expense_title);
		mRightDrawerTitle = getResources().getString(R.string.edit_expense_title);
		preferences = new Preferences(context);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// Link the content
		//  Layouts
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mLeftLinearDrawer = (LinearLayout)findViewById(R.id.leftLinearDrawer);
		mRightLinearDrawer = (LinearLayout)findViewById(R.id.rightLinearDrawer);
		noExpenseFoundRelativeLayout = (RelativeLayout)findViewById(R.id.noExpenseFoundRelativeLayout);
		expenseFoundScrollLayout = (ScrollView)findViewById(R.id.expenseScrollView);

		//  Main layout
		totalExpenseTV = (TextView)findViewById(R.id.totalExpenseQuantityTextView);
		expenseListView = (ListView)findViewById(R.id.contentListView);
		TextView emptyView = (TextView)findViewById(R.id.emptyView);
		expenseListView.setEmptyView(emptyView);
		
		View listHeaderView = getLayoutInflater().inflate(R.layout.list_header_layout, null);
		totalExpenseTV = (TextView)listHeaderView.findViewById(R.id.totalExpenseQuantityTextView);
		expenseListView.addHeaderView(listHeaderView, null, false);

		//  Left Drawer
		addNewExpenseEditText = (EditText)mLeftLinearDrawer.findViewById(R.id.addNewExpenseEditText);
		addNewExpenseCommentEditText = (EditText)mLeftLinearDrawer.findViewById(R.id.addNewExpenseCommentEditText);
		addNewExpenseButton = (Button)mLeftLinearDrawer.findViewById(R.id.addNewExpenseButton);
		addNewExpenseCheckBox = (CheckBox)mLeftLinearDrawer.findViewById(R.id.addNewExpenseButtonCheckBox);

		//  Right Drawer
		dateTitleButton = (Button)mRightLinearDrawer.findViewById(R.id.dateTitleButton);
		dateButton = (Button)mRightLinearDrawer.findViewById(R.id.dateButton);
		datePicker = (DatePicker)mRightLinearDrawer.findViewById(R.id.datePicker);
		
		timeTitleButton = (Button)mRightLinearDrawer.findViewById(R.id.timeTitleButton);
		timeButton = (Button)mRightLinearDrawer.findViewById(R.id.timeButton);
		timePicker = (TimePicker)mRightLinearDrawer.findViewById(R.id.timePicker);
		
		quantityET = (EditText)mRightLinearDrawer.findViewById(R.id.quantityEditText);
		commentET = (EditText)mRightLinearDrawer.findViewById(R.id.commentEditText);
		
		// Set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// Create the variables
		expenseDBAdapter = new ExpenseDBAdapter(context);
		expenseList = expenseDBAdapter.getAllExpenses();
		Collections.sort(expenseList, new ExpenseComparator());

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
				createMainMenu();

				if (view.equals(mLeftLinearDrawer)) {
					addNewExpenseEditText.clearFocus();
	
					// Hide soft windows
					imm.hideSoftInputFromWindow(addNewExpenseEditText.getWindowToken(), 0);
				// Right drawer
				} else {
					// Restore the content position
					contentPositionSelected = -1;
					// Hide soft windows
					imm.hideSoftInputFromWindow(addNewExpenseEditText.getWindowToken(), 0);
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

						createEditExpenseMenu();

						// If the user has selected any position
						if (contentPositionSelected < 0) {
							contentPositionSelected = expenseList.size() -1;
							// TODO: Check if the last element clicked on the list has been saved
							// if so, use it
							// Otherwise, do get the last element in the queue (The newest)
							// TODO: Create a better queue to select the one of the last edition.(Rated?)
						}

						// Clone the expense to be edited
						expenseToBeEdited = expenseList.get(contentPositionSelected);
						expenseToBeEditedCloned = expenseToBeEdited.clone();
						if (expenseToBeEditedCloned == null) {
							Log.e(LOG_TAG, "Error cloning the expense to be edited. Returned null.");
							return;
						}

						// Date
						Date dateToBeEdited = expenseToBeEditedCloned.getDate();
						calendar = Calendar.getInstance();
						calendar.setTime(dateToBeEdited);

						dateTitleButton.setOnClickListener(rightDrawerOnClickListener);
						dateButton.setOnClickListener(rightDrawerOnClickListener);
						dateButton.setText(dateFormatter.format(dateToBeEdited));
						datePicker.init(calendar.get(Calendar.YEAR),
								calendar.get(Calendar.MONTH),
								calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
									
									@Override
									public void onDateChanged(DatePicker view, int year, int monthOfYear,
											int dayOfMonth) {
										// Update the date in the expense
										calendar.set(year, monthOfYear, dayOfMonth);
										// Update the display
										dateButton.setText(
								                new StringBuilder()
								                        // Month is 0 based so add 1
								                		.append(dateFormatter.format(calendar.getTime())));
										expenseToBeEditedCloned.setDate(calendar.getTime());
									}
								});
						
						// Time
						timeTitleButton.setOnClickListener(rightDrawerOnClickListener);
						timeButton.setOnClickListener(rightDrawerOnClickListener);
						timeButton.setText(timeFormatter.format(dateToBeEdited));
						timePicker.setIs24HourView(true);
						timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
						timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
						timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
							
							@Override
							public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
								timeButton.setText(
										new StringBuilder()
											.append(pad(hourOfDay)).append(":")
											.append(pad(minute))
										);
								calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
								calendar.set(Calendar.MINUTE, minute);
								expenseToBeEditedCloned.setDate(calendar.getTime());
							}
						});
						
						// Quantity.
						quantityET.setText(String.valueOf(expenseToBeEditedCloned.getQuantity()));

						//Format the quantity after user editing
						quantityET.setOnFocusChangeListener(new OnFocusChangeListener() {
							
							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (!hasFocus) {
									String quantityStringFormatted = "0.00";
									String quantityString = quantityET.getText().toString();
									if (quantityString != null && !quantityString.equals("")) {
										quantityStringFormatted =
											dec.format(
													Double.valueOf(
															quantityET.getText().toString()
															)).replace(",", ".");
									}

									quantityET.setText(quantityStringFormatted);
								}
							}
						});
						
						// Comment
						commentET.setText(expenseToBeEditedCloned.getComment());
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
		expenseListAdapter = new ContentListAdapter(context, R.layout.date_row_layout, expenseList);
		expenseListView.setAdapter(expenseListAdapter);

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
		
		expenseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> lparent, View view, int position,
					long id) {
				// Save the variable
				// The position starts from 1, and the list starts from 0
				contentPositionSelected = position-1;
				// Open right drawer
				if (mDrawerLayout.isDrawerOpen(mLeftLinearDrawer)) {
					mDrawerLayout.closeDrawer(mLeftLinearDrawer);
				}
				mDrawerLayout.openDrawer(mRightLinearDrawer);
			}
		});
		
		removeExpenseAlertDialog = createRemoveAlertDialog();
	}

	private View.OnClickListener rightDrawerOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.dateTitleButton:
			case R.id.dateButton:
				// Show the date picker
				datePicker.setVisibility(View.VISIBLE);
				// unShow the time picker
				timePicker.setVisibility(View.GONE);
				break;
			case R.id.timeTitleButton:
			case R.id.timeButton:
				// UnShow the date picker
				datePicker.setVisibility(View.GONE);
				// Show the time picker
				timePicker.setVisibility(View.VISIBLE);
			}
		}
	};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	actionBarMenu = menu;
    	createMainMenu();
        return true;
    }

    private void createMainMenu() {
    	actionBarMenu.clear();

    	actionBarMenu.add(Menu.NONE, MENU_ITEM_RIGHT_LIST_ID, Menu
        		.NONE, context.getResources().getString(R.string.action_bar_name_edit))
        	.setIcon(R.drawable.ic_menu_edit)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    private void createEditExpenseMenu() {
    	actionBarMenu.clear();
    	
        //Used to put dark icons on light action bar
    	actionBarMenu.add(Menu.NONE, MENU_SAVE_BUTTON_ID, Menu
        		.NONE, getResources().getString(R.string.action_bar_save))
        	.setIcon(R.drawable.ic_menu_save)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
    	actionBarMenu.add(Menu.NONE, MENU_REMOVE_BUTTON_ID, Menu
        		.NONE, getResources().getString(R.string.action_bar_remove))
        	.setIcon(R.drawable.ic_menu_remove)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    	actionBarMenu.add(Menu.NONE, MENU_ITEM_RIGHT_LIST_ID, Menu
        		.NONE, context.getResources().getString(R.string.action_bar_name_edit))
        	.setIcon(R.drawable.ic_menu_edit)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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
		} else if (item.getItemId() == MENU_SAVE_BUTTON_ID) {
	    	expenseToBeEditedCloned.setQuantity(Double.valueOf(quantityET.getText().toString()));
	    	expenseToBeEditedCloned.setComment(commentET.getText().toString());
			Log.v(LOG_TAG, "Date changed");
			Log.v(LOG_TAG, "Cloned: " + expenseToBeEditedCloned.toString());
			Log.v(LOG_TAG, "Original: " + expenseToBeEdited.toString());
			printExpenseList();

	    	if (updateExpense(expenseToBeEditedCloned)) {
	    		// Close the drawer
	    		if (mDrawerLayout.isDrawerOpen(mRightLinearDrawer)) {
	    			mDrawerLayout.closeDrawer(mRightLinearDrawer);
	    		}
	    	}
    		// Remove the expense to be edited
    		expenseToBeEdited = null;
		} else if (item.getItemId() == MENU_REMOVE_BUTTON_ID) {
    		removeExpenseAlertDialog.show();
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
		expense.setQuantity(Double.valueOf(quantityStringFormatted));

		// Try to add the expense
		if (addExpenseToList(expense)) {
			addNewExpenseEditText.setText("");
			addNewExpenseCommentEditText.setText("");
			// Return the focus to expense edit text
			addNewExpenseEditText.requestFocus();
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

    private AlertDialog createRemoveAlertDialog() {
    	AlertDialog confirmRemovingDialog = new AlertDialog.Builder(MainActivity.this)
		.setIconAttribute(android.R.attr.alertDialogIcon)
		.setTitle(getResources().getString(R.string.expense_removing_dialog_title))
		.setMessage(getResources().getString(R.string.expense_removing_dialog_message))
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	// Try to remove the expense
            	if (removeExpenseFromList(expenseToBeEdited)) {
	        		expenseToBeEdited = null;
	
	            	// Close the drawer
	            	if (mDrawerLayout.isDrawerOpen(mRightLinearDrawer)) {
	            		mDrawerLayout.closeDrawer(mRightLinearDrawer);
	            	}
            	}
            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Log.v(LOG_TAG, "Remove cancelated");
            }
        }).create();
    	
    	return confirmRemovingDialog;
    }
    
    // Generic methods to modify the expense list
    private boolean addExpenseToList(Expense newExpense) {
    	if (newExpense == null) {
    		Log.e(LOG_TAG, "Error adding a new expense. It is null");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.add_new_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
    		return false;
    	}
    	

    	Log.v(LOG_TAG, "Adding new expense to the list");
    	Log.v(LOG_TAG, "\t" + newExpense.toString());
    	// 1. Expense list
		if (!expenseList.add(newExpense)) {
			Log.e(LOG_TAG, "Error adding a new expense to the list layout");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.add_new_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
			return false;
		}

		Collections.sort(expenseList, new ExpenseComparator());
		// 2. Database
		if(!expenseDBAdapter.insertNewExpense(newExpense)) {
			Log.e(LOG_TAG, "Error inserting the expense to the database");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.add_new_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
			return false;
		}
		
		// 3. Total
		totalExpenseTV.setText(String.valueOf(dec.format(calculateTotalExpense()).replace(",", ".")));
		// 4. List Adapter
		expenseListAdapter.notifyDataSetChanged();
		// 5. Correct Message
		Toast.makeText(
				context,
				getResources().getString(R.string.add_new_expense_correctly),
				Toast.LENGTH_LONG
				).show();
		return true;
    }
    
    private boolean removeExpenseFromList(Expense expense) {
    	if (expense == null) {
    		Log.e(LOG_TAG, "Error removing the expense. It is null");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.remove_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
    		return false;
    	}

    	Log.v(LOG_TAG, "Removing the follow expense from the list");
    	Log.v(LOG_TAG, "\t" + expense.toString());
    	
		// 1. Database
		if (!expenseDBAdapter.deleteExpenseByRowId(expense.get_id())) {
			Log.e(LOG_TAG, "Error removing the expense from the database");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.remove_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
			return false;
		}

    	// 2. Expense list
		if (!expenseList.remove(expense)) {
			Log.e(LOG_TAG, "Error removing the expense from the list layout");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.remove_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
			return false;
		}
		Collections.sort(expenseList, new ExpenseComparator());

		// 3. Total
		totalExpenseTV.setText(String.valueOf(dec.format(calculateTotalExpense()).replace(",", ".")));
		// 4. List adapter
		expenseListAdapter.notifyDataSetChanged();
		// 5. Correct message
		Toast.makeText(
				context,
				getResources().getString(R.string.remove_expense_correctly),
				Toast.LENGTH_LONG
				).show();

		return true;
    }
    
    private boolean updateExpense(Expense expenseEdited) {
    	if (expenseEdited == null) {
    		Log.e(LOG_TAG, "Error Editing the expense. It is null");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.update_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
    		return false;
    	}

    	Log.v(LOG_TAG, "Updating the expense list: " + expenseEdited.toString());
    	// Check changes
    	if (expenseToBeEdited.equals(expenseEdited)) {
    		Log.v(LOG_TAG, "No change was made");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.update_expense_correctly),
    				Toast.LENGTH_LONG
    				).show();
    	}

    	// 1. Expense list
    	Log.v(LOG_TAG, "Expense to be removed: " + expenseToBeEdited.toString());
    	Log.v(LOG_TAG, "Expense to be added: " + expenseEdited.toString());
    	expenseList.remove(expenseToBeEdited);
    	expenseList.add(expenseEdited);
    	printExpenseList();
    	Log.v(LOG_TAG, "Expense list:");
    	// Sort the content
    	Collections.sort(expenseList, new ExpenseComparator());
    	
    	// 2. Database
    	if (!expenseDBAdapter.updateExpense(expenseEdited)) {
    		Log.e(LOG_TAG, "Error updating the expense to the database");
    		Toast.makeText(
    				context,
    				getResources().getString(R.string.update_expense_wrongly),
    				Toast.LENGTH_LONG
    				).show();
    		return false;
    	}
		// 3. Total
		totalExpenseTV.setText(String.valueOf(dec.format(calculateTotalExpense()).replace(",", ".")));
		// 4. List adapter
		expenseListAdapter.notifyDataSetChanged();
		// 5. Correct message
		Toast.makeText(
				context,
				getResources().getString(R.string.update_expense_correctly),
				Toast.LENGTH_LONG
				).show();
    	return true;
    }
    
    private void printExpenseList() {
    	for (Expense expense: expenseList) {
    		Log.v(LOG_TAG, expense.toString());
    	}
    }
}