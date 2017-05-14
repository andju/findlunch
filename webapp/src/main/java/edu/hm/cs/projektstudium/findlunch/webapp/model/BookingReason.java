package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * The Class BookingReason.
 */
@Entity
public class BookingReason {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The reason. */
	private String reason;
	
	/** The bookings. */
	@OneToMany(mappedBy="bookingReason")
	private List<Booking> bookings;

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
	 * Gets the reason for the booking.
	 * @return The Reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Sets the reason for the booking.
	 * @param reason Reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * Gets the List of bookings.
	 * @return List of booking
	 */
	public List<Booking> getBookings() {
		return bookings;
	}

	/**
	 * Sets the List of bookings.
	 * @param bookings The List of bookings to set
	 */
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
}
