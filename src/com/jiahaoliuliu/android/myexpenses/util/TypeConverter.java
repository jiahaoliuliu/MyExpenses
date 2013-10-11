package com.jiahaoliuliu.android.myexpenses.util;

import java.text.DecimalFormat;
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
	
	public static double intToDoubleConverter(int intNumber) {
		return intNumber/100.00;
	}
	
	// Set the number of decimals in the editText
	// The default quantity (double) to shown when the user has removed the quantity
	public static String quantityToBeShownConverter(double doubleNumber) {
		DecimalFormat dec = new DecimalFormat("0.00");
		return dec.format(doubleNumber).replace(",", ".");
	}
}
