package com.jiahaoliuliu.android.myexpenses.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jiahaoliuliu.android.myexpenses.model.Expense;

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
	
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_TABLE = "expense";
	
	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
	//Database fields
	private static final String KEY_ROW_ID = "_id";
	private static final String KEY_DATE_ID = "date_id";
	private static final String KEY_LOCATION_CELL_ID = "cell_id";
	private static final String KEY_LOCATION_LOCAL_AREA_CODE = "local_are_code";
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
			Log.i(LOG_TAG, "Creating a new table in the database" + DATABASE_NAME);
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
	public Expense insertNewExpense (Expense expense) {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		ContentValues expenseValues = createExpenseValues(expense);
		int rowId =  (int)database.insert(DATABASE_TABLE, null, expenseValues);
		expense.setId(rowId);
		return expense;
	}
	
	public int updateExpense (Expense expense) {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		int result = 0;

		ContentValues updateValues = createExpenseValues(expense);
		if (database.update(DATABASE_TABLE, updateValues, KEY_ROW_ID + "=" + expense.getId(), null) > 0) {
			result = 0;
		} else {
			Log.e(LOG_TAG, "Database not updated correctly " + expense.toString());
			result = 1;
		}
		
		return result;
	}

	public int deleteExpenseByRowId (int row_id) {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		int result = 0;

		if (database.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + row_id, null) >0) {
			result = 0;
		} else {
			result = 1;
		}
		return result;
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
	
	//Return a Energy consumption. If there is any error, return null
	public Expense getEnergyConsumptionByRowId (int row_id) throws SQLException  {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		Expense result = null;

		Cursor mCursor = 
				database.query(DATABASE_TABLE,
							   new String[] {KEY_ROW_ID,
											 KEY_DATE_ID,
											 KEY_LOCATION_CELL_ID,
											 KEY_LOCATION_LOCAL_AREA_CODE,
											 KEY_QUANTITY
							                 },
				                KEY_ROW_ID + "=" + row_id,
				                null,
				                null,
				                null,
				                null);
		result = getExpenseFromCursor (mCursor, 0);
		mCursor.close();
		return result;
	}

	/*
	 * Returns all the expenses from the database
	 */
	public List<Expense> getAllExpenses() {
		if (database == null || !database.isOpen()) {
			openDatabase();
		}

		List<Expense> allEnergyConsumption = new ArrayList<Expense>();

		Cursor mCursor = 
				database.query(DATABASE_TABLE,
						   new String[] {KEY_ROW_ID,
										 KEY_DATE_ID,
										 KEY_LOCATION_CELL_ID,
										 KEY_LOCATION_LOCAL_AREA_CODE,
										 KEY_QUANTITY
		                 				},
							    null,
				                null,
				                null,
				                null,
				                null);
		allEnergyConsumption = getAllExpensesFromCursor(mCursor);
		mCursor.close();
		return allEnergyConsumption;
	}
	
	public class ExpenseDbHelper extends SQLiteOpenHelper{
			
		public ExpenseDbHelper (Context context, String databaseName, CursorFactory factory, int version) {
			super (context, databaseName, null, version);
		}
		
		private String CREATE_TABLE =
				"create table if not exists " + DATABASE_TABLE + " ( " +
										   KEY_ROW_ID + " integer primary key autoincrement, " +
										   KEY_DATE_ID + " text not null, " +
										   KEY_LOCATION_CELL_ID + " integer not null default 0, " +
										   KEY_LOCATION_LOCAL_AREA_CODE + " integer not null default 0, " +
										   KEY_QUANTITY + " real not null);";
	
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
			
			database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(database);
		}
	}

	//Private methods
	//Create a content values for the database based on a Expense
	// All the values are checked.
	private ContentValues createExpenseValues (Expense expense) {
		
		Log.i(LOG_TAG, "Creating the content values from a expense");
		ContentValues expenseValues = new ContentValues();
	
		//Get each one of the fields of the expense
		// Date
		String dateString = iso8601Format.format(expense.getDate());
		expenseValues.put(KEY_DATE_ID, dateString);

		// Location
		int locationCellId = expense.getLocation().getCid();
		expenseValues.put(KEY_LOCATION_CELL_ID, locationCellId);
		
		int locationLAC = expense.getLocation().getLac();
		expenseValues.put(KEY_LOCATION_LOCAL_AREA_CODE, locationLAC);
		
		double quantity = expense.getQuantity();
		expenseValues.put(KEY_QUANTITY, quantity);

		return expenseValues;
	}

	//Return an expense. If there is any error, return null
	private Expense getExpenseFromCursor (Cursor mCursor, int position) {
		Expense result = null;
		
		// Get basic data
		if (mCursor == null) {
			Log.w(LOG_TAG, "Cursor = null. Not energy consumption found");
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

			// Location
			int locationCellId = mCursor.getInt(mCursor.getColumnIndex(KEY_LOCATION_CELL_ID));

			int locationLAC = mCursor.getInt(mCursor.getColumnIndex(KEY_LOCATION_LOCAL_AREA_CODE));
			
			double quantity = mCursor.getDouble(mCursor.getColumnIndex(KEY_QUANTITY));

			result = new Expense(row_id, date, locationCellId, locationLAC, quantity);
		}
		
		return result;
	}


	private List<Expense> getAllExpensesFromCursor(Cursor mCursor) {
		List<Expense> allExpenses = new ArrayList<Expense>();
		
		int count = mCursor.getCount();
		for (int i = 0; i < count; i++) {
			Expense tempExpense = getExpenseFromCursor(mCursor, i);
			allExpenses.add(tempExpense);
		}
		
		return allExpenses;
	}

}