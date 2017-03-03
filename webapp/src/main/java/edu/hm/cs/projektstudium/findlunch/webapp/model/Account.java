package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The class Account.
 *
 */
@Entity
public class Account {

	/** The id */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The account number.*/
	private int accountNumber;
	
	/** The account type.*/
	@ManyToOne(fetch = FetchType.EAGER)
	private AccountType accountType;
	
	/** The bookings from this account.*/
	@OneToMany(mappedBy="account")
	private List<Booking> bookings;
	
	/** The admins of this account.*/
	@OneToMany(mappedBy = "account")
	private List<User> users;
	
	/**
	 * Instantiates a new account.
	 */
	public Account(){
		this.users = new ArrayList<User>();
	}

	/**
	 * Gets the id of an Account.
	 * @return Id of the account
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id of an account.
	 * @param id Id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the account number of an account.
	 * @return accountNumber of the account
	 */
	public int getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Sets the account number of an account
	 * @param accountNumber Account number to set
	 */
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Gets the accountType.
	 * @return The AccountType
	 */
	public AccountType getAccountType() {
		return accountType;
	}

	/**
	 * Sets the accountType of an account.
	 * @param accountType AccountType to set
	 */
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	/**
	 * Gets the bookings from an account.
	 * @return List of booking
	 */
	public List<Booking> getBookings() {
		return bookings;
	}

	/**
	 * Sets the booking of an account.
	 * @param bookings List of booking
	 */
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	/**
	 * Gets the admins of the account.
	 * @return List of user
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Sets the admins.
	 * @param users List of user to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	/**
	 * Add a user as Admin
	 * @param user The user
	 * @return Added user
	 */
	public User addUser(User user){
		getUsers().add(user);
		user.setAccount(this);
		
		return user;
	}
}
