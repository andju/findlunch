package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
/**
 * The Class ReservationList.
 */
/*
 * Wrapper Klasse, da man sonst keinen zugriff auf die reservation Objekt hat
 * , da java.util.ArrayList nicht erkannt wird von Thymeleaf*/
public class ReservationList {

	public ReservationList() {
	}
	
	public ReservationList(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}

	/** The list of reservations.*/
	private ArrayList<Reservation> reservations = new ArrayList<Reservation>();
	
	/**
	 * Gets the list of reservations.
	 * @return List of reservation
	 */
	public ArrayList<Reservation> getReservations() {
		return reservations;
	}

	/**
	 * Sets the list of reservations.
	 * @param reservations The list of reservations to set
	 */
	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}
}
