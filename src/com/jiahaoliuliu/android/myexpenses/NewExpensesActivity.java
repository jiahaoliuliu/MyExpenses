package com.jiahaoliuliu.android.myexpenses;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewExpensesActivity extends Activity {

	private static final String LOG_TAG = NewExpensesActivity.class.getSimpleName();

	// Context
	private Context context;

	// Components
	private ActionBar actionBar;
	// The final is needed to show the soft key
	private EditText newExpenseQuantityET;
	private Button addNewExpenseButton;
	private String currencyUnit;
	// TODO: Put the current unit at the beginning or at the end
	// according to this
	private Boolean currencyUnitAtTheEnd = true;

    private Boolean showingCompleteAddScreen = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expenses);
        
        context = this;

        // Action bar
        actionBar = (ActionBar) findViewById(R.id.actionbar_new_expenses);
        // Title
        actionBar.setTitle(R.string.title_new_expenses);
        actionBar.setTitleGravity(Gravity.CENTER);
        actionBar.setTitleSize(22.0f);
        
        actionBar.setHomeAction(new TransformAddAction());

        actionBar.addAction(new IntentAction(this,
                                             new Intent(this, MyExpensesActivity.class)
                                             .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                             R.drawable.collections_view_as_list));

        // Link the components
        newExpenseQuantityET = (EditText) findViewById(R.id.quantity_et);
        newExpenseQuantityET.setOnClickListener(onClickListener);
        
        // Set the editor action listener (Done button)
        newExpenseQuantityET.setOnEditorActionListener(onEditorActionListener);
        // Set the text change listener
        TextWatcher textWatcher = new CustomTextWatcher(newExpenseQuantityET);
        newExpenseQuantityET.addTextChangedListener(textWatcher);
        
        addNewExpenseButton = (Button)findViewById(R.id.add_new_expense_button);
        addNewExpenseButton.setOnClickListener(onClickListener);
        
        currencyUnit = getCurrencyByLocale();
        currencyUnitAtTheEnd = isCurrencyUnitAtTheEnd();
        
    }    
    
    View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case (R.id.quantity_et): {
				// newExpenseQuantityET.setHint("");
				break;
			}
			case (R.id.add_new_expense_button): {
				String quantityWithSymbol = newExpenseQuantityET.getText().toString();
				if (quantityWithSymbol != null ) {
					if (quantityWithSymbol.equalsIgnoreCase("") ||
							quantityWithSymbol.equalsIgnoreCase(
									context.getResources().getString(R.string.add_new_expense_textview_text))) {
						Log.w(LOG_TAG, "The quantity is not correct");
						Toast.makeText(context, 
									   context.getResources().getString(
											   R.string.new_quantity_wrong_value), 
									   Toast.LENGTH_LONG
									   ).show();
						return;
					} else {
						addNewExpenseFromEditText();
					}
				}
				break;
			}
			default: {
				Log.e(LOG_TAG, "Button not recognized");
			}
			}
		}
	};
	
	EditText.OnEditorActionListener onEditorActionListener = 
			new EditText.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				addNewExpenseFromEditText();
				return true;
			}
			return false;
		}
	};
	
	View.OnFocusChangeListener onFocusChangeListener =
			new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
			        if (hasFocus) {
			        	Log.d(LOG_TAG, "On focus");
			        	//dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
				}
			};
	
	// Check and add the new expense quantity in the database
	private void addNewExpenseFromEditText() {
		// Try to get the value from the edit text
		String inputText = newExpenseQuantityET.getText().toString();
		Log.v(LOG_TAG, "Input text: " + inputText);
		inputText = inputText.replace(currencyUnit, "");
		Log.v(LOG_TAG, "Input text relaced: " + inputText);
		if (isFloatNumber(inputText)) {
			newExpenseQuantityET.setText("");
			Float inputQuantity = Float.parseFloat(inputText);
			addNewExpenseFromEditText(inputQuantity);
		} else {
			Log.w(LOG_TAG, "The quantity is not float");
			Toast.makeText(context, 
						   context.getResources().getString(
								   R.string.new_quantity_wrong_value), 
						   Toast.LENGTH_LONG
						   ).show();
		}
	}
	
	// Add the new expense to the database
	private void addNewExpenseFromEditText(Float quantity) {
		// TODO: Try to add the quantity to the database
		// Get the cell id and time
		Log.v(LOG_TAG, quantity + " added");
		Toast.makeText(context, 
				   context.getResources().getString(
						   R.string.new_quantity_added), 
				   Toast.LENGTH_LONG
				   ).show();
	}
	
	// Return the currency according to the local setting
    // TODO: implement it
	private String getCurrencyByLocale() {
        String localeCurrency = getString(R.string.default_currency);
        return localeCurrency;
	}
	
	// Check if the currency unit should be at the end
	// TODO: implement it
	private boolean isCurrencyUnitAtTheEnd() {
		return true;
	}
	
	// Class to listen the text changes
	private class CustomTextWatcher implements TextWatcher {
	    private EditText mEditText;
	    private boolean addCurrencyUnit = true;

	    public CustomTextWatcher(EditText editText) {
	        mEditText = editText;
	    }
	    
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	    }

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    }

	    @Override
	    public void afterTextChanged(Editable s) {
	    	// Reset the value of addCurrencyUnit
	    	if (s.length() == 0) {
	    		addCurrencyUnit = true;
	    		Log.v(LOG_TAG, "Text length equals to zero. Set addCurencyUnit to true");
	    		return;
	    	}
	    	
	    	// If it should add currency unit, add it
	    	if (addCurrencyUnit) {
	    		Log.v(LOG_TAG, "Adding the currency unit");
	    		// Warning: To avoid set cycle, the boolean should be
	    		// set as false in the first place
	    		addCurrencyUnit = false;
	    		mEditText.setText(s.toString() + currencyUnit);
	    		mEditText.setSelection(s.length());
	    		return;
	    	}
	    	
	    	if (s.length() == currencyUnit.length()) {
	    		if (s.toString().equalsIgnoreCase(currencyUnit)) {
	    			mEditText.setText("");
	    		}
	    	}
	    	
	    	
	    	// If the currency unit has been removed
	    	// then don't do anything
	    	if (!s.toString().contains(currencyUnit)) {
	    		Log.w(LOG_TAG, "The currency unit has been removed");
	    		return;
	    	}

	    	/*
	    	if (currencyUnitAtTheEnd) {
	    		Log.v(LOG_TAG, "Currency at the end");
	    		if (s.charAt(s.length() - 1 ) == currencyUnit.toCharArray()[0]) {
	    			Log.v(LOG_TAG, "Currency unit already in the end " + s);
	    		} else {
		    		Log.v(LOG_TAG, "Edit text before replacing " + inputText);
		    		Log.v(LOG_TAG, "Currency unit " + currencyUnit);
		    		inputText.replace(currencyUnit, "");
		    		Log.v(LOG_TAG, "Edit text replaced " + inputText);
		    		mEditText.setText(inputText + currencyUnit);
	    		}
	    	}
	    	*/
	    }
	}
	
    private boolean isFloatNumber(String string) {
    	try {
    		Float.parseFloat(string);
    	} catch (NumberFormatException nfe) {
    		return false;
    	}
		return true;
    }
    
    //---- Life cycle
    @Override
    protected void onResume() {
    	super.onResume();
        // To request the focus
        newExpenseQuantityET.requestFocus();

        (new Handler()).postDelayed(new Runnable() {
            public void run() {
            	newExpenseQuantityET.dispatchTouchEvent(
            			MotionEvent.obtain(
            					SystemClock.uptimeMillis(), 
            					SystemClock.uptimeMillis(), 
            					MotionEvent.ACTION_DOWN , 
            					0, 
            					0, 
            					0));
            	newExpenseQuantityET.dispatchTouchEvent(
            			MotionEvent.obtain(
            					SystemClock.uptimeMillis(), 
            					SystemClock.uptimeMillis(), 
            					MotionEvent.ACTION_UP , 
            					0, 
            					0, 
            					0));  
                // Set the cursor in the edit text, if it already contains texts
                String inputText = newExpenseQuantityET.getText().toString();
                if (inputText.length() > 0) {
                	Log.v(LOG_TAG, "the edit text contains some string");
                	int cursorPosition = 0;
                	if (inputText.contains(currencyUnit)) {
                		Log.v(LOG_TAG, "The input text contains the currency unit");
                		cursorPosition = inputText.length() - currencyUnit.length();
                	} else {
                		Log.v(LOG_TAG, "The input text not contains the currency unit");
                		cursorPosition = inputText.length();
                	}
                	Log.v(LOG_TAG, "The cursor number is " + cursorPosition);
                	newExpenseQuantityET.setSelection(cursorPosition);
                }
            }
        }, 200);        
   }

    /**
     * Creates a null action which does not do anything
     * @author jiahao
     *
     */
    private class TransformAddAction extends AbstractAction {

        public TransformAddAction() {
            super(R.drawable.av_full_screen);
        }

        @Override
        public void performAction(View view) {
            if (showingCompleteAddScreen) {
                /*
                 * Show simplified screen
                 */
                Toast.makeText(context, R.string.simplified_new_expenses_screen, Toast.LENGTH_SHORT).show();
                actionBar.setHomeLogo(R.drawable.av_full_screen);
            } else {
                /*
                 * Show expanded screen
                 */
                Toast.makeText(context, R.string.expanded_new_expnses_screen, Toast.LENGTH_SHORT).show();
                actionBar.setHomeLogo(R.drawable.av_return_from_full_screen);
            }
            showingCompleteAddScreen = !showingCompleteAddScreen;
        }

    }

}