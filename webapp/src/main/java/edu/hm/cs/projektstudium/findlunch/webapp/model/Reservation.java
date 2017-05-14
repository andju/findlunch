package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Reservation {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The amount. */
	private int amount;
	
	/** The donation. */
	private float donation;

	/** The total price. */
	private float totalPrice;
	
	/** The reservation time. */
	@JsonIgnore
	private Date reservationTime;
	
	/** Is confirmed. */
	private boolean confirmed;
	
	/** Is rejected. */
	private boolean rejected;
	
	/** Is used points. */
	private boolean usedPoints;
	
	/** The reservation number*/
	private int reservationNumber;

	/** The user.*/
	//	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	
	/** The offer.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="offer_id")
	private Offer offer;
	
	/** The euroPerPoint.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="euro_per_point_id")
	private EuroPerPoint euroPerPoint;
	
	/** The restaurant.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="restaurant_id")
	private Restaurant restaurant;
	
	/** The bill.*/
	@ManyToOne(fetch=FetchType.EAGER)
	private Bill bill;

	/**
	 * Gets the euroPerPoint.
	 * @return The euroPerPoint
	 */
	public EuroPerPoint getEuroPerPoint() {
		return euroPerPoint;
	}
	
	/**
	 * Sets the euroPerPoint
	 * @param euroPerPoint The new euroPerPoint
	 */
	public void setEuroPerPoint(EuroPerPoint euroPerPoint) {
		this.euroPerPoint = euroPerPoint;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the amount.
	 * @return The amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the new amount of reservation.
	 * @param amount The amount.
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Gets the reservation time.
	 * @return The reservation time
	 */
	public Date getReservationTime() {
		return reservationTime;
	}

	/**
	 * Sets the reservation Time.
	 * @param reservationTime The reservation time to set
	 */
	public void setReservationTime(Date reservationTime) {
		this.reservationTime = reservationTime;
	}

	/**
	 * Gets the user.
	 * @return The user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the new user.
	 * @param user The user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the offer.
	 * @return The offer
	 */
	public Offer getOffer() {
		return offer;
	}

	/**
	 * Sets the new offer.
	 * @param offer The offer
	 */
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
	
	/**
	 * Checks if a reservation confirmed.
	 * @return true, if it is confirmed
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * Sets confirm of the reservation
	 * @param confirmed true, if it is confirmed
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	
	
	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	/**
	 * Gets the donation.
	 * @return The donation
	 */
	public float getDonation() {
		return donation;
	}

	/**
	 * Sets the donation.
	 * @param donation The new donation to set
	 */
	public void setDonation(float donation) {
		this.donation = donation;
	}

	/**
	 * Gets the total price.
	 * @return The total price
	 */
	public float getTotalPrice() {
		return totalPrice;
	}

	/**
	 * Sets the total price.
	 * @param totalPrice The new total price
	 */
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	/**
	 * Checks if points are used.
	 * @return true, if points are used
	 */
	public boolean isUsedPoints() {
		return usedPoints;
	}

	/**
	 * Set the flag of used points.
	 * @param usedPoints true, if points are used
	 */
	public void setUsedPoints(boolean usedPoints) {
		this.usedPoints = usedPoints;
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
	 * @param restaurant The restaurant to set
	 */
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	/**
	 * Gets the bill.
	 * @return The bill
	 */
	public Bill getBill() {
		return bill;
	}

	/**
	 * Sets the new bill to reservation.
	 * @param bill The bill to set
	 */
	public void setBill(Bill bill) {
		this.bill = bill;
	}

	/**
	 * Gets the reservation number.
	 * @return The reservation number
	 */
	public int getReservationNumber() {
		return reservationNumber;
	}

	/**
	 * Sets the new reservation number.
	 * @param reservationNumber The new reservation number to set
	 */
	public void setReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}
}
