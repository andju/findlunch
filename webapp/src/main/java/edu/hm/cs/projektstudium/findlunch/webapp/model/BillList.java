package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;

/**
 * The Class BillList.
 */
public class BillList {
	
	/** The list of bills */
	private ArrayList<Bill> bills = new ArrayList<Bill>();

	/**
	 * Gets the list of bills.
	 * @return List of bill
	 */
	public ArrayList<Bill> getBills() {
		return bills;
	}

	/**
	 * Sets the list of bills.
	 * @param bills List of bill to set
	 */
	public void setBills(ArrayList<Bill> bills) {
		this.bills = bills;
	}
}
