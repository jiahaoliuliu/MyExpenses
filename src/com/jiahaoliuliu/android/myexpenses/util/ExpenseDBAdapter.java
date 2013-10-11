package com.jiahaoliuliu.android.myexpenses.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jiahaoliuliu.android.myexpenses.model.ExpenseListTotal;
import com.jiahaoliuliu.android.myexpenses.model.NewExpense;
import com.jiahaoliuliu.android.myexpenses.model.OldExpense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpenseDBAdapter {

	private static final String LOG_TAG = ExpenseDBAdapter.class.getSimpleName();
	
	private static final String DATABASE_NAME = "Expenses";
	
	private static final int DATABASE_VERSION = 5;

	private static final String DATABASE_TABLE = "expenses";
	
	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
	//Database fields
	private static final String KEY_ROW_ID = "_id";
	private static final String KEY_DATE_ID = "date_id";
	private static final String KEY_COMMENT_ID = "comment_id";
	private static final String KEY_QUANTITY = "quantity";
	
	private Context context;
	private SQLiteDatabase database;
	private ExpenseDbHelper dbHelper;
	
	public ExpenseDBAdapter (Context context) {
		this.context = context;
		dbHelper = new ExpenseDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void openDatabase() throws SQLException {
		try {
			Log.i(LOG_TAG, "Creating a new table in the database if needed" + DATABASE_NAME);
			database = dbHelper.getWritableDatabase();
			Log.i(LOG_TAG, "Writable db get");
		} catch (SQLiteException ex) {
			database = dbHelper.getReadableDatabase();
			Log.i(LOG_TAG, "Readable db get");
		}
	}

	public void closeDatabase() {
		dbHelper.close();
	}
	
	/**
	 * Register a new expense inside of the database.
	 */
	// TODO: Use Expense instead of NewExpense
	public boolean insertNewExpense (NewExpense expense) {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		ContentValues expenseValues = createExpenseValues(expense);
		int rowId =  (int)database.insert(DATABASE_TABLE, null, expenseValues);
		expense.set_id(rowId);
		return rowId > 0;
	}

	
	/**
	 * Update an existence expense
	 * @param expense The expense to be updated
	 * @return        True if the expense has been updated
	 *                False otherwise
	 */
	public boolean updateExpense (NewExpense newExpense) {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		ContentValues updateValues = createExpenseValues(newExpense);
		return (database.update
				(DATABASE_TABLE, updateValues, KEY_ROW_ID + "=" + newExpense.get_id(), null) > 0);
	}

	/**
	 * Delete an expense from the database
	 * @param row_id The row id of the expense
	 * @return       The number of row removed
	 */
	public boolean deleteExpenseByRowId (int row_id) {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		return database.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + row_id, null) > 0;
	}

	public int deleteAll() {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		int result = 0;

		if (database.delete(DATABASE_TABLE, "1" , null) >0) {
			result = 0;
		} else {
			result = 1;
		}
		
		return result;
	}
	
	//Return an Expense. If there is any error, return null
	public NewExpense getExpnseByRowId (int row_id) throws SQLException  {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		NewExpense result = null;

		Cursor mCursor = 
				database.query(DATABASE_TABLE,
							   new String[] {KEY_ROW_ID,
											 KEY_DATE_ID,
											 KEY_COMMENT_ID,
											 KEY_QUANTITY
							                 },
				                KEY_ROW_ID + "=" + row_id,
				                null,
				                null,
				                null,
				                null);
		result = getNewExpenseFromCursor (mCursor, 0);
		mCursor.close();
		return result;
	}

	/*
	 * Returns all the expenses from the database
	 */
	public ExpenseListTotal getAllExpenses() {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		Cursor mCursor = 
				database.query(DATABASE_TABLE,
						   new String[] {KEY_ROW_ID,
										 KEY_DATE_ID,
										 KEY_COMMENT_ID,
										 KEY_QUANTITY
		                 				},
							    null,
				                null,
				                null,
				                null,
				                null);
		ExpenseListTotal expenseListTotal = getAllExpensesFromCursor(mCursor);
		mCursor.close();
		return expenseListTotal;
	}
	
	public class ExpenseDbHelper extends SQLiteOpenHelper{
			
		public ExpenseDbHelper (Context context, String databaseName, CursorFactory factory, int version) {
			super (context, databaseName, null, version);
		}
		
		private String CREATE_TABLE =
				"create table if not exists " + DATABASE_TABLE + " ( " +
										   KEY_ROW_ID + " integer primary key autoincrement, " +
										   KEY_DATE_ID + " text not null, " +
										   KEY_COMMENT_ID + " text, " +
										   KEY_QUANTITY + " integer not null);";
	
		// Method is called during creation of the database
		@Override
		public void onCreate (SQLiteDatabase database) {
			Log.i(LOG_TAG, "Creating a new database " + DATABASE_TABLE);
			database.execSQL(CREATE_TABLE);
		}
		
		// Method is called when the database has been opened
		@Override
		public void onOpen(SQLiteDatabase database) {
			Log.i(LOG_TAG, "Opening the database " + DATABASE_TABLE);
			database.execSQL(CREATE_TABLE);
		}
		
		// Method is called during an upgrade of the database, e.g. if you increase the database version
		@Override
		public void onUpgrade (SQLiteDatabase database, int oldVersion, int newVersion ){
			Log.w(LOG_TAG, "Upgrading database from version " + 
				  oldVersion + " to " + newVersion + " , which will destroy all old data");
			
			// Upgrade from the version 4 or older
			if (oldVersion <= 4) {
				// Get all the content of the old database
				Cursor mCursor = 
						database.query(DATABASE_TABLE,
								   new String[] {KEY_ROW_ID,
												 KEY_DATE_ID,
												 KEY_COMMENT_ID,
												 KEY_QUANTITY
				                 				},
									    null,
						                null,
						                null,
						                null,
						                null);
				
				ArrayList<OldExpense> oldExpenseList = getAllOldExpensesFromCursor(mCursor);
				mCursor.close();

				Log.v(LOG_TAG, "Get the content of the database on Upgrade. \n" + 
						oldExpenseList.size()
						);
				
				ArrayList<NewExpense> newExpenseList = new ArrayList<NewExpense>();
				for (OldExpense oldExpense: oldExpenseList) {
					NewExpense newExpenseTmp = new NewExpense(oldExpense);
					Log.v(LOG_TAG, "OldExpense: \n" + oldExpense.toString());
					Log.v(LOG_TAG, "NewExpense: \n" + newExpenseTmp.toString());
					newExpenseList.add(newExpenseTmp);
				}
				

				// Drop the old database
				database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
				
				// Creates the new database
				onCreate(database);

				//Insert the old values to the new database.
				for (NewExpense newExpense: newExpenseList) {
					ContentValues expenseValues = createExpenseValues(newExpense);
					int rowId =  (int)database.insert(DATABASE_TABLE, null, expenseValues);
					if (rowId < 0) {
						Log.e(LOG_TAG, "Error inserting the follow expense to the database.\n" + 
								newExpense.toString());
					}
				}
			}
		}
	}

	//Private methods
	//Create a content values for the database based on a Expense
	// All the values are checked.
	private ContentValues createExpenseValues (NewExpense newExpense) {
		
		Log.i(LOG_TAG, "Creating the content values from a expense");
		ContentValues expenseValues = new ContentValues();
	
		//Get each one of the fields of the expense
		// Date
		String dateString = iso8601Format.format(newExpense.getDate());
		expenseValues.put(KEY_DATE_ID, dateString);

		// Comment
		String comment = newExpense.getComment();
		expenseValues.put(KEY_COMMENT_ID, comment);

		// Quantity
		int quantity = newExpense.getQuantity();
		expenseValues.put(KEY_QUANTITY, quantity);

		return expenseValues;
	}

	//Return an expense. If there is any error, return null
	private NewExpense getNewExpenseFromCursor (Cursor mCursor, int position) {
		NewExpense result = null;
		
		// Get basic data
		if (mCursor == null) {
			Log.w(LOG_TAG, "Cursor = null. Not expense consumption found");
		} else if (!mCursor.moveToPosition(position)){
			Log.w(LOG_TAG, "Position not reachable. The number of elements is " + mCursor.getCount() + "  but requested to access " +
					"to the position " + position);
		} else {	
	
			//Row id
			int row_id = mCursor.getInt(mCursor.getColumnIndex(KEY_ROW_ID));
			
			//Date
			String dateString = mCursor.getString(mCursor.getColumnIndex(KEY_DATE_ID));
			Date date;
			try {
				date = iso8601Format.parse(dateString);
			} catch (ParseException e) {
				Log.e(LOG_TAG, "Error parsing date " + e.getLocalizedMessage(), e);
				return null;
			}

			// Comment
			String comment = mCursor.getString(mCursor.getColumnIndex(KEY_COMMENT_ID));

			// Quantity
			int quantity = mCursor.getInt(mCursor.getColumnIndex(KEY_QUANTITY));

			result = new NewExpense(row_id, date, comment, quantity);
		}
		
		return result;
	}

	// TODO: Review the follow code
	private ExpenseListTotal getAllExpensesFromCursor(Cursor mCursor) {
		ExpenseListTotal expenseListTotal = new ExpenseListTotal();
		
		int count = mCursor.getCount();
		for (int i = 0; i < count; i++) {
			//Expense tempExpense = getExpenseFromCursor(mCursor, i);
			//expenseListTotal.addExpense(tempExpense);
		}
		
		return expenseListTotal;
	}

	//==================================================== For Database upgrade from v4 =================================
	// Changes: From v5, the quantity is integer
	// For the old database to upgrade it
	//Return an expense. If there is any error, return null
	private OldExpense getOldExpenseFromCursor (Cursor mCursor, int position) {
		OldExpense result = null;
		
		// Get basic data
		if (mCursor == null) {
			Log.w(LOG_TAG, "Cursor = null. Not expense consumption found");
		} else if (!mCursor.moveToPosition(position)){
			Log.w(LOG_TAG, "Position not reachable. The number of elements is " + mCursor.getCount() + "  but requested to access " +
					"to the position " + position);
		} else {	
	
			//Row id
			int row_id = mCursor.getInt(mCursor.getColumnIndex(KEY_ROW_ID));
			
			//Date
			String dateString = mCursor.getString(mCursor.getColumnIndex(KEY_DATE_ID));
			Date date;
			try {
				date = iso8601Format.parse(dateString);
			} catch (ParseException e) {
				Log.e(LOG_TAG, "Error parsing date " + e.getLocalizedMessage(), e);
				return null;
			}

			// Comment
			String comment = mCursor.getString(mCursor.getColumnIndex(KEY_COMMENT_ID));

			// Quantity
			double quantity = mCursor.getDouble(mCursor.getColumnIndex(KEY_QUANTITY));

			result = new OldExpense(row_id, date, comment, quantity);
		}
		
		return result;
	}

	private ArrayList<OldExpense> getAllOldExpensesFromCursor(Cursor mCursor) {
		ArrayList<OldExpense> oldExpensesList = new ArrayList<OldExpense>();
		
		int count = mCursor.getCount();
		for (int i = 0; i < count; i++) {
			OldExpense tempOldExpense = getOldExpenseFromCursor(mCursor, i);
			oldExpensesList.add(tempOldExpense);
		}
		
		return oldExpensesList;
	}

}