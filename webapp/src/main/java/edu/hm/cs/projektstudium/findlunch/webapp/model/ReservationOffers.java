package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ReservationOffers {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	//private int reservationId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="reservation_id")
	private Reservation reservation;
	
	private int offer_id;
	
	private int amount;

	/*
	public int getReservation_id() {
		return reservationId;
	}

	public void setReservation_id(int reservation_id) {
		this.reservationId = reservation_id;
	}
	*/

	public int getOffer_id() {
		return offer_id;
	}

	public void setOffer_id(int offer_id) {
		this.offer_id = offer_id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	
}
