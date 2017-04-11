package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.util.List;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;

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
 */
public interface PushMessagingInterface {
	
	/**
	 * Send Amazon Device Messaging / Simple Notification Service push-notification.
	 * 
	 * @param p The push-notification to be sent.
	 * @param restaurantsForPushCount Restaurant id for push.
	 * @param pushKitchenTypeIds Kitchen types list for push.
	 */
	public void sendAdmNotification(PushNotification p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds);
	
	/**
	 * @param p The push-notification to be sent.
	 * @param restaurantsForPushCount Restaurant id for push.
	 * @param pushKitchenTypeIds Kitchen types list for push.
	 */
	public void sendFcmNotification(PushNotification p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds);

}
