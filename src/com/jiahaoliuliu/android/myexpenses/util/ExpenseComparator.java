package com.jiahaoliuliu.android.myexpenses.util;

import java.util.Comparator;

import com.jiahaoliuliu.android.myexpenses.model.Expense;

public class ExpenseComparator implements Comparator<Expense>{

	@Override
	public int compare(Expense leftExpense, Expense rightExpense) {
		return leftExpense.getDate().before(rightExpense.getDate())?-1:1;
	}

}
