package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import org.json.simple.JSONObject;

/**
 * The PushNotification Object to be send to the User.
 * @author Niklas Klotz
 *
 */
public class PushNotification {

	/** The Data of the Object */
	private JSONObject data = new JSONObject();
	
	/** The ID */
	private int id;
	
	/** The FCM Token of the receiver */
	private String fcmToken;

	/** Instantiates a new push notification. */
	public PushNotification(){
	}
	
	/**
	 * Puts data to the data object of the PushNotification.
	 * @param key
	 * @param value
	 */
	public void putData(String key, Object value){
		data.put(key, String.valueOf(value));
	}
	
	/**
	 * Returns the data which is stored in the object.
	 * @return
	 */
	public JSONObject getData(){
		return data;
	}
	
	/**
	 * Gets the fcm token.
	 *
	 * @return the fcmToken
	 */
	public String getFcmToken() {
		return fcmToken;
	}

	/**
	 * Sets the fcm token.
	 *
	 * @param fcmToken the new fcm token
	 */
	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
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
	 * Generate data from a regestration for the daily update.
	 * @param p the registration for the daily update.
	 */
	public void generateFromDaily(DailyPushNotificationData p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds){
		putData("title", p.getTitle());
		putData("numberOfRestaurants", restaurantsForPushCount.toString());
		putData("longitude", String.valueOf(p.getLongitude()));
		putData("latitude", String.valueOf(p.getLatitude()));
		putData("radius", String.valueOf(p.getRadius()));
		putData("kitchenTypeIds", pushKitchenTypeIds.toString());
		putData("pushId", String.valueOf(p.getId()));
		
		setFcmToken(p.getFcmToken());
		setId(p.getId());
	}
	
	public void generateOrderReceive(Reservation reservation, User user){
		putData("titel","Deine Bestellung: "+reservation.getId()+" wurde erfolgreich an das Restaurant: " +reservation.getRestaurant()+" übermittelt.");
		putData("reservation", String.valueOf(reservation.getId()));
		putData("pushId", String.valueOf(reservation.getId()));
		
		//setFcmToken(user.getFcmId());
		setId(reservation.getId());
	}
	
	public void generateWeb(){
		putData("data", "Hier ist eine Push Nachticht!");
		setId(1);
	}
	
	public void generateReservationConfirm(Reservation reservation){
		putData("titel","Deine Bestellung: "+reservation.getId());
		putData("data", "Deine Bestellung "+reservation.getId()+ " wurd durch das Restaurant "+reservation.getRestaurant()+" bestätigt");
	}
}
