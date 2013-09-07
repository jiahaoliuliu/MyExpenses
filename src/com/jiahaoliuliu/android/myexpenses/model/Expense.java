package com.jiahaoliuliu.android.myexpenses.model;

import java.util.Date;

import android.telephony.gsm.GsmCellLocation;

public class Expense {

	// The date when the expense has been recorded
	private Date date;
	// The information about the cell.
	// It contains the cell id and the location area code (LAC)
	private GsmCellLocation location;
	// The amount of expense
	private double quantity;

	public Expense() {
		super();
	}

	public Expense(Date date, GsmCellLocation location, double quantity) {
		super();
		this.date = date;
		this.location = location;
		this.quantity = quantity;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public GsmCellLocation getLocation() {
		return location;
	}

	public void setLocation(GsmCellLocation location) {
		this.location = location;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		long temp;
		temp = Double.doubleToLongBits(quantity);
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
		Expense other = (Expense) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (Double.doubleToLongBits(quantity) != Double
				.doubleToLongBits(other.quantity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Expense [" +
				"date=" + date +
				", location=" + "CellId: " + location.getCid() + ",LAC: " + location.getLac() +
				", quantity=" + quantity + "]";
	}
}
