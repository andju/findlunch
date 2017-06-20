package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;

@Entity
@Table(name="reservation_status")
public class ReservationStatus {
	
	public static final int RESERVATION_KEY_NEW = 0;
	public static final int RESERVATION_KEY_CONFIRMED = 1;
	public static final int RESERVATION_KEY_REJECTED = 2;
	
	/** The id. */
	@Id
	@JsonView({ReservationView.ReservationRest.class})
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The status. */
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="status")
	private String status;
	
	/** The key. */
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="statuskey")
	private int key;

	/** The reservations.*/
	@OneToMany(mappedBy="reservationStatus")
	private List<Reservation> reservation;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the statuskey
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @param statuskey the statuskey to set
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * @return the reservation
	 */
	public List<Reservation> getReservation() {
		return reservation;
	}

	/**
	 * @param reservation the reservation to set
	 */
	public void setReservation(List<Reservation> reservation) {
		this.reservation = reservation;
	}
	
}
