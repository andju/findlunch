package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
/**
 * The Class ReservationStatistik.
 */
/*
 * Wrapper Klasse, da man sonst keinen zugriff auf die reservation Objekt hat
 * , da java.util.ArrayList nicht erkannt wird von Thymeleaf*/
public class ReservationStatistik {

	
	public ReservationStatistik(ArrayList<Reservation> reservations, String label, int countAll) {
		this.setLabel(label);
		this.setAverageRespondeTime(calculateAverageRespondeTime(reservations));
		this.setPercent(calculatePercent(reservations.size(), countAll));
		this.setReservationCount(reservations.size());
		this.setReservations(reservations);
		this.setTotalValue(calculateTotalValue(reservations));
	}

	private String label = "";
	private int reservationCount = 0;
	private float totalValue = 0;
	private float averageRespondeTime = 0;
	private float percent = 0;
	private ArrayList<Reservation> reservations = new ArrayList<Reservation>();
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the reservationCount
	 */
	public int getReservationCount() {
		return reservationCount;
	}
	/**
	 * @param reservationCount the reservationCount to set
	 */
	public void setReservationCount(int reservationCount) {
		this.reservationCount = reservationCount;
	}
	/**
	 * @return the totalValue
	 */
	public float getTotalValue() {
		return totalValue;
	}
	/**
	 * @param totalValue the totalValue to set
	 */
	public void setTotalValue(float totalValue) {
		this.totalValue = totalValue;
	}
	/**
	 * @return the averageRespondeTime
	 */
	public float getAverageRespondeTime() {
		return averageRespondeTime;
	}
	/**
	 * @param averageRespondeTime the averageRespondeTime to set
	 */
	public void setAverageRespondeTime(float averageRespondeTime) {
		this.averageRespondeTime = averageRespondeTime;
	}
	/**
	 * @return the percentPoints
	 */
	public float getPercent() {
		return percent;
	}
	/**
	 * @param percent the percentPoints to set
	 */
	public void setPercent(float percent) {
		this.percent = percent;
	}
	/**
	 * @return the reservations
	 */
	public ArrayList<Reservation> getReservations() {
		return reservations;
	}
	/**
	 * @param reservations the reservations to set
	 */
	public void setReservations(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}
	
	private float calculateTotalValue(ArrayList<Reservation> reservations){
		float sumPrice = 0;
		for (Reservation reservation : reservations) {
			sumPrice += reservation.getTotalPrice();
		}
		return sumPrice;
	}
	
	private float calculateAverageRespondeTime(ArrayList<Reservation> reservations){
		float avrgTime = 0;
		float totalTime = 0;
		int counter = 1;

		for (Reservation reservation : reservations) {
			if(reservation.getTimestampReceived() != null && reservation.getTimestampResponded() != null){
				long milliseconds = reservation.getTimestampResponded().getTime()-reservation.getTimestampReceived().getTime();
				long minutes =  milliseconds/60000;
				totalTime += minutes;
				counter++;
			}
			avrgTime = (float)totalTime/counter;
		}
		
		return avrgTime;
	}
	
	private float calculatePercent(int countBase, int countAll){
		if(countAll > 0 && countBase >0){
			float percent = 0;
			percent = (float)countBase/countAll;
			percent = percent*100;
			return percent;
		}
		return 0;
	}
}
