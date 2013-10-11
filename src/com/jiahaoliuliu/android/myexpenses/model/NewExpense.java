package com.jiahaoliuliu.android.myexpenses.model;

import java.util.Date;

// This class is used temporally to upgrade the database.

public class NewExpense implements Cloneable {

	// The row id in the database
	private int _id = 0;
	// The date when the expense has been recorded
	private Date date;
	
	private String comment;
	// The amount of expense
	private int quantity;

	public NewExpense() {
		super();
	}

	public NewExpense(int _id, Date date, String comment, int quantity) {
		super();
		this._id = _id;
		this.date = date;
		this.comment = comment;
		this.quantity = quantity;
	}

	public NewExpense(OldExpense oldExpense) {
		super();
		this._id = oldExpense.get_id();
		this.date = oldExpense.getDate();
		this.comment = oldExpense.getComment();
		this.quantity = quantityConverter(oldExpense.getQuantity());
	}

	private int quantityConverter(double quantityDouble) {
		double quantityDouble100 = quantityDouble * 100;
		return (int)Math.round(quantityDouble100);
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setQuanitty(double quantityDouble) {
		this.quantity = quantityConverter(quantityDouble);
	}

	public void setQuantity(Double quantityDouble) {
		this.quantity = quantityConverter(quantityDouble);
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
		NewExpense other = (NewExpense) obj;
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
	public NewExpense clone() {
		NewExpense object = null;
		try {
			object = (NewExpense)super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println("Clone not supported");
		}
		object.setDate((Date)date.clone());
		return object;
	}

}
