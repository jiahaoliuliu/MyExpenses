package com.jiahaoliuliu.android.myexpenses.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;

import com.jiahaoliuliu.android.myexpenses.MainActivity.OperationResult;

public class ExpenseListTotal {

	private static final String LOG_TAG = ExpenseListTotal.class.getSimpleName();

	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	// Set the number of decimals in the editText
	public final DecimalFormat dec = new DecimalFormat("0.00");

	// The total number of expenses
	private int totalExpenses;
	// 
	private double totalSum;
	
	// The hashMap  of expenses per day, where the key is the date 
	// formatted with year-month-day
	private HashMap<String, ExpenseList> expenses;

	// An arrayList of the keys of the expenses sorted
	private ArrayList<Integer> expensesKeysSorted;

	// The total number of operations
	private int totalOperations;
	
	// The number of operations per each integrity check
	private static final int OPERATIONS_PER_INTEGRITY_CHECK = 6;

	public ExpenseListTotal() {
		super();
		totalExpenses = 0;
		totalSum = 0.0;
		expenses = new HashMap<String, ExpenseList>();
		expensesKeysSorted = new ArrayList<Integer>();
	}

	public ExpenseListTotal(int totalExpenses,
			HashMap<String, ExpenseList> expenses, ArrayList<Integer> expensesKeysSorted) {
		super();
		this.totalExpenses = totalExpenses;
		this.expenses = expenses;
		this.expensesKeysSorted = expensesKeysSorted;
	}

	// Additional methods
	public OperationResult addExpense(Expense expenseToBeAdded) {
		if (expenseToBeAdded == null) {
			Log.e(LOG_TAG, "Error adding new expense. It is empty");
			return OperationResult.ERROR_DATA_EMPTY;
		}

		Log.v(LOG_TAG, "Adding the follow expense to the list total: \n" +
				expenseToBeAdded.toString());

		totalExpenses++;
		totalSum += expenseToBeAdded.getQuantity();
		String key = formatter.format(expenseToBeAdded.getDate());
		ExpenseList expenseList = expenses.get(key);
		if (expenseList == null) {
			expenseList = new ExpenseList();
			expenses.put(key, expenseList);
			expensesKeysSorted.add(Integer.valueOf(key));
			Collections.sort(expensesKeysSorted);
		}

		OperationResult addExpenseResult = expenseList.addExpense(expenseToBeAdded);
		if (addExpenseResult != OperationResult.CORRECT) {
			Log.e(LOG_TAG, "Error adding result to the expense list.");
			return addExpenseResult;
		}

		return periodicallyIntegrityCheck();
	}

	public OperationResult removeExpense(Expense expenseToBeRemoved) {
		if (expenseToBeRemoved == null) {
			Log.e(LOG_TAG, "Error removing new expense. It is empty");
			return OperationResult.ERROR_DATA_EMPTY;
		}

		Log.v(LOG_TAG, "Removing the follow expense from the list total: \n" +
				expenseToBeRemoved.toString());

		if (totalSum < expenseToBeRemoved.getQuantity()) {
			Log.e(LOG_TAG, "Error removing new expense. The total is less than expense");
			// It needs a refresh
			return OperationResult.ERROR_QUANTITY_INCORRECT;
		}

		totalExpenses--;
		totalSum -= expenseToBeRemoved.getQuantity();

		String key = formatter.format(expenseToBeRemoved.getDate());
		ExpenseList expenseList = expenses.get(key);
		if (expenseList == null) {
			Log.e(LOG_TAG, "Error removing the expense. The expense list does not exist.");
			return OperationResult.ERROR_DATA_NOT_EXISTS;
		}
		
		OperationResult removeExpenseResult = expenseList.removeExpense(expenseToBeRemoved);
		if (removeExpenseResult != OperationResult.CORRECT && removeExpenseResult != OperationResult.CORRECT_DATA_INTEGROUS) {
			Log.e(LOG_TAG, "Error removing result to the expense list.");
			return removeExpenseResult;
		}

		if (expenseList.size() == 0) {
			expenses.remove(key);
			expensesKeysSorted.remove(expensesKeysSorted.indexOf(Integer.valueOf(key)));
		}

		return periodicallyIntegrityCheck();
	}
	
	// Update expense is remove the old expense and add the new expense
	public OperationResult updateExpense(Expense oldExpense, Expense newExpense) {
		if (oldExpense == null || newExpense == null) {
			Log.e(LOG_TAG, "Error Updating the expense. At least one of the data is empty");
			return OperationResult.ERROR_DATA_EMPTY;
		}
		
		OperationResult removingExpenseResult = removeExpense(oldExpense);
		if (removingExpenseResult != OperationResult.CORRECT && removingExpenseResult != OperationResult.CORRECT_DATA_INTEGROUS) {
			Log.e(LOG_TAG, "Error updating the expense. The old expense cannot be removed. " + removingExpenseResult.toString());
			return removingExpenseResult;
		}
		
		return addExpense(newExpense);
	}

	private OperationResult periodicallyIntegrityCheck() {
		totalOperations++;
		if (totalOperations % OPERATIONS_PER_INTEGRITY_CHECK == 0) {
			return checkIntegrity();
		} else {
			return OperationResult.CORRECT;
		}
	}

	public boolean isHeader(Expense expense) {
		if (expense == null) {
			Log.e(LOG_TAG, "Error checking if an expense is header. It is null");
			return false;
		}
		
		Log.v(LOG_TAG, "Checking if the follow expense is a header: \n" +
				expense.toString());
		
		String key = formatter.format(expense.getDate());
		ExpenseList expenseList = expenses.get(key);
		if (expenseList == null) {
			Log.e(LOG_TAG, "Error checking if an expense is header. The expense list does not exist.");
			return false;
		}
		
		return expenseList.isHeader(expense);
	}

	// Get an expense by the position
	public Expense getExpense(int position) {
		if (position < 0 || position > totalExpenses) {
			Log.e(LOG_TAG, "Error Getting the expense. The position is not correct " + position);
			return null;
		}
		
		int particialExpensesInit = 0;
		int particialExpensesFinal = 0;
		ExpenseList expenseListTmp;
		for (Integer keyInteger: expensesKeysSorted) {
			String key = String.valueOf(keyInteger);
			expenseListTmp= expenses.get(key);
			if (expenseListTmp == null) {
				Log.e(LOG_TAG, "Error getting the expense. The expense list for the key " + key + " is null.");
				return null;
			}

			particialExpensesFinal += expenseListTmp.size();
			// We found the expense list
			if (position >= particialExpensesInit && position < particialExpensesFinal) {
				int relPos = position - particialExpensesInit;
				return expenseListTmp.getExpenseByPosition(relPos);
			}
			
			// Re assign the start point
			particialExpensesInit = particialExpensesFinal;
		}
		
		// Some error happens
		Log.e(LOG_TAG, "Position not found in the total list of expenses");
		return null;
	}

	/**
	 * Method used to check the integrity of all the data.
	 * Depending on the number of data and the CPU capacity, this operation
	 * could delay certain time. So, call it with caution (Callback??)
	 * @return OperationResult.CORRECT_DATA_INTEGROUS  If the data are INTEGROUS
	 *         OperationResult.ERROR_DATA_NOT_INTEGROUS Otherwise
	 */
	private OperationResult checkIntegrity() {
		// TODO: Implement it
		return OperationResult.CORRECT_DATA_INTEGROUS;
	}

	public boolean isEmpty() {
		return totalExpenses == 0;
	}
	
	public String getDailyTotal(Date date) {
		if (date == null) {
			Log.e(LOG_TAG, "Error getting the daily total. The date is null");
			return (dec.format(0.0));
		}
		
		ExpenseList expenseList = expenses.get(formatter.format(date));
		if (expenseList == null) {
			Log.e(LOG_TAG, "Error getting the daily total. The expense list does not exist");
			return (dec.format(0.0));
		}
		
		return dec.format(expenseList.getSubTotalSum());
	}

	public int getTotalExpenses() {
		return totalExpenses;
	}

	// The total expenses is dynamic generated and it cannot be set from outside
	/*
	public void setTotalExpenses(int totalExpenses) {
		this.totalExpenses = totalExpenses;
	}*/

	public String getTotalSum() {
		return dec.format(totalSum);
	}

	// The total sum is dynamic generated and it cannot be set from outside
	/*
	public void setTotalSum(double totalSum) {
		this.totalSum = totalSum;
	}*/

	public HashMap<String, ExpenseList> getExpenses() {
		return expenses;
	}

	public void setExpenses(HashMap<String, ExpenseList> expenses) {
		this.expenses = expenses;
	}

	public ArrayList<Integer> getExpensesKeysSorted() {
		return expensesKeysSorted;
	}

	public void setExpensesKeysSorted(ArrayList<Integer> expensesKeysSorted) {
		this.expensesKeysSorted = expensesKeysSorted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dec == null) ? 0 : dec.hashCode());
		result = prime * result
				+ ((expenses == null) ? 0 : expenses.hashCode());
		result = prime
				* result
				+ ((expensesKeysSorted == null) ? 0 : expensesKeysSorted
						.hashCode());
		result = prime * result
				+ ((formatter == null) ? 0 : formatter.hashCode());
		result = prime * result + totalExpenses;
		result = prime * result + totalOperations;
		long temp;
		temp = Double.doubleToLongBits(totalSum);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ExpenseListTotal other = (ExpenseListTotal) obj;
		if (dec == null) {
			if (other.dec != null)
				return false;
		} else if (!dec.equals(other.dec))
			return false;
		if (expenses == null) {
			if (other.expenses != null)
				return false;
		} else if (!expenses.equals(other.expenses))
			return false;
		if (expensesKeysSorted == null) {
			if (other.expensesKeysSorted != null)
				return false;
		} else if (!expensesKeysSorted.equals(other.expensesKeysSorted))
			return false;
		if (formatter == null) {
			if (other.formatter != null)
				return false;
		} else if (!formatter.equals(other.formatter))
			return false;
		if (totalExpenses != other.totalExpenses)
			return false;
		if (totalOperations != other.totalOperations)
			return false;
		if (Double.doubleToLongBits(totalSum) != Double
				.doubleToLongBits(other.totalSum))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpenseListTotal [ totalExpenses=" + totalExpenses + ", totalSum=" + totalSum
				+ ", totalOperations=" + totalOperations + "\n\t"
				+ ", expenses=" + expenses + "\n\t"
				+ ", expensesKeysSorted=" + expensesKeysSorted + "]";
	}

}
