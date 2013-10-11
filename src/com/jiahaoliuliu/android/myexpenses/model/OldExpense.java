package com.jiahaoliuliu.android.myexpenses.model;

import java.util.Date;

// The old Expense data
// it is used by the database to recover the old values and set it
// to the new database.
public class OldExpense implements Cloneable {

	// The row id in the database
	private int _id = 0;
	// The date when the expense has been recorded
	private Date date;
	
	private String comment;
	// The amount of expense
	private double quantity;

	public OldExpense() {
		super();
	}

	public OldExpense(int _id, Date date, String comment, double quantity) {
		super();
		this._id = _id;
		this.date = date;
		this.comment = comment;
		this.quantity = quantity;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
		result = prime * result + _id;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
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
		OldExpense other = (OldExpense) obj;
		if (_id != other._id)
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (Double.doubleToLongBits(quantity) != Double
				.doubleToLongBits(other.quantity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Expense [_id=" + _id + ", date=" + date + ", comment="
				+ comment + ", quantity=" + quantity + "]";
	}

	@Override
	public OldExpense clone() {
		OldExpense object = null;
		try {
			object = (OldExpense)super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println("Clone not supported");
		}
		object.setDate((Date)date.clone());
		return object;
	}

}
