package com.jiahaoliuliu.android.myexpenses;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v4.view.GravityCompat;

public class MainActivity extends SherlockFragmentActivity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	// Variables
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerLinearLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private Context context;
	private Preferences preferences;
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	// For the soft input
	private InputMethodManager imm;

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
		mTitle = mDrawerTitle = getTitle();
		context = this;
		preferences = new Preferences(context);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// Link the content
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLinearLayout = (LinearLayout)findViewById(R.id.linearLayoutDrawer);

		addNewExpenseEditText = (EditText)mDrawerLinearLayout.findViewById(R.id.addNewExpenseEditText);
		addNewExpenseButton = (Button)mDrawerLinearLayout.findViewById(R.id.addNewExpenseButton);
		addNewExpenseCheckBox = (CheckBox)mDrawerLinearLayout.findViewById(R.id.addNewExpenseButtonCheckBox);

		// Set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		//mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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
				addNewExpenseEditText.clearFocus();

				// Hide soft windows
				imm.hideSoftInputFromWindow(addNewExpenseEditText.getWindowToken(), 0);

			}
			
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// Set the title on the action when drawer open
				getSupportActionBar().setTitle(mDrawerTitle);
				
				// Force the keyboard to show on EditText focus
				// TODO: Show the soft keyboard after onResume
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
		}

		// Draw the layout
		addNewExpenseCheckBox.setChecked(showAddNewExpenseAtBeginning);
		
		// Layout logic
		addNewExpenseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
			}
		});
		
		addNewExpenseCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.setBoolean(BooleanId.SHOWN_ADD_NEW_EXPENSE_AT_BEGINNING, isChecked);
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
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//
		}
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
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

}
