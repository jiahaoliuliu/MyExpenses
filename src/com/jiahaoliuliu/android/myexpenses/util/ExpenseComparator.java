package com.jiahaoliuliu.android.myexpenses.util;

import java.util.Comparator;

import com.jiahaoliuliu.android.myexpenses.model.NewExpense;

public class ExpenseComparator implements Comparator<NewExpense>{

	@Override
	public int compare(NewExpense leftExpense, NewExpense rightExpense) {
		return leftExpense.getDate().before(rightExpense.getDate())?-1:1;
	}

}
