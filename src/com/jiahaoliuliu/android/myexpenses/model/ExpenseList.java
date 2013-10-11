package com.jiahaoliuliu.android.myexpenses.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.util.Log;

import com.jiahaoliuliu.android.myexpenses.util.ExpenseComparator;
import com.jiahaoliuliu.android.myexpenses.MainActivity.OperationResult;


// The database should return a list of this
public class ExpenseList implements Cloneable {

	private static final String LOG_TAG = ExpenseList.class.getSimpleName();

	// The sub total sum per day
	private int subTotalSum;

	// the list of sorted expenses
	private ArrayList<NewExpense> expenseArray;
	
	// The map of expenses where the key is the row id of the expense.
	// Since the key is set by the database, it expects to be unique
	private HashMap<Integer, NewExpense> expenseMap;
	
	// The collection comparator
	private ExpenseComparator comparator; 

	public ExpenseList() {
		super();
		subTotalSum = 0;
		expenseArray = new ArrayList<NewExpense>();
		expenseMap = new HashMap<Integer, NewExpense>();
		comparator = new ExpenseComparator();
	}

	public ExpenseList(int subTotalSum, ArrayList<NewExpense> expenseArray, HashMap<Integer, NewExpense> expenseMap, ExpenseComparator comparator) {
		super();
		this.expenseArray = expenseArray;
		this.subTotalSum = subTotalSum;
		this.expenseMap = expenseMap;
		this.comparator = comparator;
	}

	// The data cannot be empty
	public OperationResult addExpense(NewExpense expenseToBeAdded) {
		if (expenseToBeAdded == null) {
			Log.e(LOG_TAG, "Error adding new expense. It is empty");
			return OperationResult.ERROR_DATA_EMPTY;
		}
		
		Log.v(LOG_TAG, "Adding the follow expense to the list: \n" +
				expenseToBeAdded.toString());

		subTotalSum += expenseToBeAdded.getQuantity();

		expenseMap.put(expenseToBeAdded.get_id(), expenseToBeAdded);

		// Add the data to expense array and sort it
		if (!expenseArray.add(expenseToBeAdded)) {
			Log.e(LOG_TAG, "Error adding the expense to the list.");
			return OperationResult.ERROR_ADDING_INCORRECT;
		}

		Collections.sort(expenseArray, comparator);
		return OperationResult.CORRECT;
	}

	public OperationResult removeExpense(NewExpense expenseToBeRemoved) {
		if (expenseToBeRemoved == null) {
			Log.e(LOG_TAG, "Error removing expense. It is empty");
			return OperationResult.ERROR_DATA_EMPTY;
		}

		Log.v(LOG_TAG, "Removing the follow expense from the list: \n" +
				expenseToBeRemoved.toString());

		if (subTotalSum < expenseToBeRemoved.getQuantity()) {
			Log.e(LOG_TAG, "Error removing an expense. Its quantity " + expenseToBeRemoved.getQuantity() + 
					" is bigger thant the subTotalSum " + subTotalSum);
			return OperationResult.ERROR_QUANTITY_INCORRECT;
		}

		subTotalSum -= expenseToBeRemoved.getQuantity();
		NewExpense expenseRemoved = expenseMap.remove(expenseToBeRemoved.get_id());
		if (!expenseToBeRemoved.equals(expenseRemoved)) {
			Log.e(LOG_TAG, "Error removing the expense. The removed expense does not match with the expense to be removed " +
					"\nExpense to be removed: " + expenseToBeRemoved.toString() + 
					"\nExpense removed:       " + expenseRemoved);
			return OperationResult.ERROR_REMOVING_INCORRECT;
		}

		// Remove the data
		// It is not need to sort it because it has been sorted when it has been
		// added or updated
		if (!expenseArray.remove(expenseToBeRemoved)) {
			Log.e(LOG_TAG, "Error removing the expense from the list. ");
			return OperationResult.ERROR_REMOVING_INCORRECT;
		}
		
		return OperationResult.CORRECT;
	}

	/**
	 * Check if the data matches
	 * @return
	 */
	public boolean isIntegrous() {
		if (expenseArray.size() != expenseMap.size()) {
			Log.e(LOG_TAG, "The size of data does not matches. \n Array: " + expenseArray.size() + ", Map:" + expenseMap.size());
			return false;
		}

		for (NewExpense expenseSavedInArray: expenseArray) {
			NewExpense expenseSavedInMap = expenseMap.get(expenseSavedInArray.get_id());
			if (expenseSavedInMap == null) {
				Log.e(LOG_TAG, "The expense with id " + expenseSavedInArray.get_id() + " does not exists in the map. \n" + expenseSavedInArray);
				return false;
			}
			
			if (!expenseSavedInArray.equals(expenseSavedInMap)) {
				Log.e(LOG_TAG, "The expense with id " + expenseSavedInArray.get_id() + " are not the same in the array and in the map. " +
						"\nArray:" + expenseSavedInArray.toString() + 
						"\nMap:  " + expenseSavedInMap.toString());
			}
		}
		
		return true;
	}

	public boolean isHeader(NewExpense expense) {
		if (expense == null) {
			Log.e(LOG_TAG, "Error checking if an expense is a header. It is null");
			return false;
		}

		Log.v(LOG_TAG, "Checking if the follow expense is header or not. \n" +
				expense.toString());

		if (!expenseArray.contains(expense)) {
			Log.e(LOG_TAG, "Error checking if an expense is the header. This expense list does not contain the expense. \n" +
					toString());
			return false;
		}
		
		return expenseArray.indexOf(expense) == 0;
	}

	// The data must be integrous.
	public int size() {
		return expenseArray.size();
	}

	// Get expense
	public NewExpense getExpenseById(int expenseId) {
		if (expenseMap == null) {
			return null;
		} else return expenseMap.get(expenseId);
	}

	public NewExpense getExpenseByPosition(int position) {
		if (position < 0 || position > expenseArray.size()-1) {
			Log.e(LOG_TAG, "Error getting the expense by position. The position is not correct " + position);
			return null;
		}
		
		return expenseArray.get(position);
	}

	public int getSubTotalSum() {
		return subTotalSum;
	}

	public void setSubTotalSum(int subTotalSum) {
		this.subTotalSum = subTotalSum;
	}

	public ArrayList<NewExpense> getExpenseArray() {
		return expenseArray;
	}

	public void setExpenseArray(ArrayList<NewExpense> expenseArray) {
		this.expenseArray = expenseArray;
	}

	public HashMap<Integer, NewExpense> getExpenseMap() {
		return expenseMap;
	}

	public void setExpenseMap(HashMap<Integer, NewExpense> expenseMap) {
		this.expenseMap = expenseMap;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expenseArray == null) ? 0 : expenseArray.hashCode());
		result = prime * result
				+ ((expenseMap == null) ? 0 : expenseMap.hashCode());
		result = prime * result + subTotalSum;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpenseList other = (ExpenseList) obj;
		if (expenseArray == null) {
			if (other.expenseArray != null)
				return false;
		} else if (!expenseArray.equals(other.expenseArray))
			return false;
		if (expenseMap == null) {
			if (other.expenseMap != null)
				return false;
		} else if (!expenseMap.equals(other.expenseMap))
			return false;
		if (subTotalSum != other.subTotalSum)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpenseList [subTotalSum=" + subTotalSum + "\n\t\t"
				+ ", expenseArray=" + expenseArray + "\n\t\t"
				+ ", expenseMap=" + expenseMap + "]";
	}

}
