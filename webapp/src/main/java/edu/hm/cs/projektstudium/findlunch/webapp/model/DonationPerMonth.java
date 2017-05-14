package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * The Class DonationPerMonth.
 */
@Entity
public class DonationPerMonth {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Id;
	
	/** The date. */
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd.MM.yyy")
	private Date date;
	
	/** The update time with date. */
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd.MM.yyy HH:mm")
	private Date datetimeOfUpdate;
	
	/** The amount. */
	@NumberFormat(style=Style.DEFAULT)
	@DecimalMin(value="0.0", message="{donationPerMonth.amount.invalidMinValue}")
	@DecimalMax(value="999.99", message="{donationPerMonth.amount.invalidMaxValue}")
	private float amount;
	
	/** The restaurant. */
	@ManyToOne(fetch=FetchType.EAGER)
	private Restaurant restaurant;
	
	/** The bill. */
	@ManyToOne(fetch = FetchType.EAGER)
	private Bill bill;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		Id = id;
	}

	/**
	 * Gets the date.
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * @param date Date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the amount.
	 * @return The amount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 * @param amount Amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * Gets the restaurant.
	 * @return The restaurant
	 */
	public Restaurant getRestaurant() {
		return restaurant;
	}

	/**
	 * Sets the restaurant.
	 * @param restaurant The new Restaurant to set
	 */
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	/**
	 * Gets the update time.
	 * @return the update time
	 */
	public Date getDatetimeOfUpdate() {
		return datetimeOfUpdate;
	}

	/**
	 * Sets the update Time.
	 * @param datetimeOfUpdate The date time of Update to set
	 */
	public void setDatetimeOfUpdate(Date datetimeOfUpdate) {
		this.datetimeOfUpdate = datetimeOfUpdate;
	}
	
	/**
	 * Gets the bill.
	 * @return The bill
	 */
	public Bill getBill() {
		return bill;
	}

	/**
	 * Sets the bill.
	 * @param bill Bill to set
	 */
	public void setBill(Bill bill) {
		this.bill = bill;
	}
}
