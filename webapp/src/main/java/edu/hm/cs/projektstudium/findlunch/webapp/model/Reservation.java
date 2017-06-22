package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;

@Entity
public class Reservation {
	
	/** The id. */
	@Id
	@JsonView({ReservationView.ReservationRest.class})
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The donation. */
	@JsonView({ReservationView.ReservationRest.class})
	private float donation;

	/** The total price. */
	@JsonView({ReservationView.ReservationRest.class})
	private float totalPrice;
	
	/** Is used points. */
	@JsonView({ReservationView.ReservationRest.class})
	private boolean usedPoints;
	
	/** Points are Collected by the cusomer */
	@Column(name="points_collected")
	private boolean pointsCollected;
	
	/** The reservation number*/
	@JsonView({ReservationView.ReservationRest.class})
	private int reservationNumber;

	/** The user.*/
	// @JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	
	/** The offers within the reservation */
	@OneToMany(mappedBy="reservation", cascade=CascadeType.ALL)
	@JsonView({ReservationView.ReservationRest.class})
	private List<ReservationOffers> reservation_offers;
	
	/** The euroPerPoint.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="euro_per_point_id")
	private EuroPerPoint euroPerPoint;
	
	/** The restaurant.*/
	@JsonView({ReservationView.ReservationRest.class})
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="restaurant_id")
	private Restaurant restaurant;
	
	/** The bill.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonView({ReservationView.ReservationRest.class})
	private Bill bill;

	/** The reservationStatus.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonView({ReservationView.ReservationRest.class})
	@JoinColumn(name="reservation_status_id")
	private ReservationStatus reservationStatus;
	
	/** The collect_time. */
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="collect_time")
	private Date collectTime;
	
	/** The reservation time. */
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="timestamp_received")
	private Date timestampReceived;
	
	/** The reservation time. */
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="timestamp_responded")
	private Date timestampResponded;
	
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

	public List<ReservationOffers> getReservation_offers() {
		return reservation_offers;
	}

	public void setReservation_offers(List<ReservationOffers> reservation_offers) {
		this.reservation_offers = reservation_offers;
	}

	/**
	 * @return the reservationStatus
	 */
	public ReservationStatus getReservationStatus() {
		return reservationStatus;
	}

	/**
	 * @param reservationStatus the reservationStatus to set
	 */
	public void setReservationStatus(ReservationStatus reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
	
	/**
	 * @return the collectTime
	 */
	public Date getCollectTime() {
		return collectTime;
	}

	/**
	 * @param collectTime the collectTime to set
	 */
	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	/**
	 * @return the timestampReceived
	 */
	public Date getTimestampReceived() {
		return timestampReceived;
	}

	/**
	 * @param timestampReceived the timestampReceived to set
	 */
	public void setTimestampReceived(Date timestampReceived) {
		this.timestampReceived = timestampReceived;
	}

	/**
	 * @return the timestampResponded
	 */
	public Date getTimestampResponded() {
		return timestampResponded;
	}

	/**
	 * @param timestampResponded the timestampResponded to set
	 */
	public void setTimestampResponded(Date timestampResponded) {
		this.timestampResponded = timestampResponded;
	}

	/**
	 * @return boolean true when Reservation is Confirmed
	 */
	public boolean isConfirmed(){
		return reservationStatus.getKey() == ReservationStatus.RESERVATION_KEY_CONFIRMED;
	}
	
	/**
	 * @return boolean true when Reservation is Rejected
	 */
	public boolean isRejected(){
		return reservationStatus.getKey() == ReservationStatus.RESERVATION_KEY_REJECTED;
	}
	
	/**
	 * @return boolean true when Reservation is Unprocessed
	 */
	public boolean isUnprocessed(){
		return reservationStatus.getKey() == ReservationStatus.RESERVATION_KEY_UNPROCESSED;
	}

	public boolean isPointsCollected() {
		return pointsCollected;
	}

	public void setPointsCollected(boolean pointsCollected) {
		this.pointsCollected = pointsCollected;
	}
}
