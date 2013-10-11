package com.jiahaoliuliu.android.myexpenses.util;

import java.text.NumberFormat;

public class TypeConverter {

	/**
	 * Convert double to integer and multiplies it to 100.
	 * @param quantityDouble: The double to be converted
	 * @return The int value of the param multiplies to 100.
	 */
	public static int doubleToIntConverter(double doubleNumber) {
		double quantityDouble100 = doubleNumber * 100;
		return (int)Math.round(quantityDouble100);
	}

	public static String intToCurrency(int param) {
		// Set the number of decimals in the editText
		NumberFormat format = NumberFormat.getCurrencyInstance();
		double quantityDouble = param/100.00;
		return String.valueOf(format.format(quantityDouble));
	}
}
