package edu.hm.cs.projektstudium.findlunch.webapp.model;

/**
 * The class AccountResult.
 */
public class AccountResult {
	
	/** The account Number.*/
	private int accountNumber;
	
	/** The sum of Amount.*/
	private float sumOfAmount;
	
	/** The customer id .*/
	private int customerId;

	/**
	 * Gets the accountNumber
	 * @return The accountNumber
	 */
	public int getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Sets the accountNumber
	 * @param accountNumber AccountNumber to set
	 */
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Gets the sumOfAmount.
	 * @return the sumOfAmount
	 */
	public float getSumOfAmount() {
		return sumOfAmount;
	}

	/**
	 * Sets the sumOfAmount.
	 * @param sumOfAmount the sumOfAmount
	 */
	public void setSumOfAmount(float sumOfAmount) {
		this.sumOfAmount = sumOfAmount;
	}

	/**
	 * Gets the customerId.
	 * @return the customerId
	 */
	public int getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customerId.
	 * @param customerId The cusomerId to set
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
}
