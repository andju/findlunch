package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.util.List;

import org.json.simple.JSONObject;

import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;

/**
 * Base interface / abstract API for sending push-based notifications.
 * 
 * Implementations: 
 * **Amazon Simple Notification Service
 * **Google Firebase Cloud Messaging Service
 *  
 * Created by Maxmilian Haag on 12.12.2016.
 * @author Maxmilian Haag
 * 
 * Extended by Niklas Klotz 21.04.2017.
 * @author Niklas Klotz
 *
 */
public interface PushMessagingInterface {
	
	/**
	 * Send Amazon Device Messaging / Simple Notification Service push-notification.
	 * 
	 * @param p The push-notification to be sent.
	 * @param restaurantsForPushCount Restaurant id for push.
	 * @param pushKitchenTypeIds Kitchen types list for push.
	 */
	public void sendAdmNotification(DailyPushNotificationData p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds);
	
	/**
	 * Sends FCM pushNotification to a customer.
	 * @author Niklas Klotz.
	 * @param p The push-notification to be sent.
	 * @throws InterruptedException 
	 */
	public void sendFcmNotification(JSONObject p);
}
