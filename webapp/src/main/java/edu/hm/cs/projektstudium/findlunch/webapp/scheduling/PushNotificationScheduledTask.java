package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.rest.RestaurantRestController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushMessagingInterface;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushNotificationManager;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;


/**
 * The Class PushNotificationScheduledTask.
 * 
 * Scheduled check if there are push-notifications in database to be sent.
 * Differencing between Amazon and Google push-notifications.
 * Push-notifications with valid tokens are processed.
 * 
 * Extended by Maxmilian Haag on 18.12.2016.
 * Extended by Niklas Klotz on 21.04.2017.
 */
@Component
public class PushNotificationScheduledTask {
	

	/**
	 * Identification string in database for not valid token.
	 * Other service will be used and processed.
	 */
	private final static String NOT_AVAILABLE = "notAvailable";

	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(PushNotificationScheduledTask.class);

	/**
	 * The push repo.
	 */
	@Autowired
	private PushNotificationRepository pushRepo;

	/**
	 * The restaurant rest.
	 */
	@Autowired
	private RestaurantRestController restaurantRest;

	/**
	 * The restaurant repo.
	 */
	@Autowired
	private RestaurantRepository restaurauntRepo;

	@Autowired
	private PushTokenRepository tokenRepo;
	
	/**
	 * ########################################
	 * # COMMENT "@Scheduled" FOR MEASUREMENT!#
	 * ########################################
	 * 
	 * Checking available push-notifications in database each 200 seconds (~3.5 min).
	 *  
	 */
	@Scheduled(fixedRate = 200000)
	public void checkPushNotifications()  {

		//Log info
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
				"Starting check for push notifications."));

		//Extracting all push-notifications from database.
		List<DailyPushNotificationData> activePushNotifications = pushRepo.findAll();
		int dayNumberToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		
		//Check all push notifications
		for (DailyPushNotificationData p : activePushNotifications) {

			// Determine if the push notification shall be sent today.
			List<DayOfWeek> daysOfWeekPushList = p.getDayOfWeeks();
			Boolean sendPushToday = false;
			for (int i = 0; i < daysOfWeekPushList.size() && sendPushToday == false; i++) {
				int dayOfWeekPush = daysOfWeekPushList.get(i).getDayNumber();
				sendPushToday = (dayOfWeekPush == dayNumberToday);
			}
			if (sendPushToday) {
				// Get list of Restaurants matching push notification location.
				List<Restaurant> restaurantsNearbyList = new ArrayList<Restaurant>();
				restaurantsNearbyList = restaurantRest.getAllRestaurants(p.getLongitude(), p.getLatitude(), p.getRadius());
				
				Integer restaurantsForPushCount = 0;
				List<Integer> pushKitchenTypeIds = new ArrayList<Integer>();
				for (KitchenType k : p.getKitchenTypes()) {
					pushKitchenTypeIds.add(k.getId());
				}
				if (p.getKitchenTypes().size() > 0) {
					/*
					 * If Kitchen Types are specified for push notification:
					 * Only Restaurants with the resp. kitchen types count for
					 * push notification.
					 */
					List<Restaurant> restaurantsForPush = restaurauntRepo.findByKitchenTypes_idIn(pushKitchenTypeIds);
					restaurantsForPushCount = restaurantsForPush.size();
				} else {
					/*
					 * If no Kitchen Types are specified for push notification:
					 * All Restaurants count for push notification.
					 */
					restaurantsForPushCount = restaurantsNearbyList.size();
				}

				// Check if there are restaurants for the push notification.
				if (restaurantsForPushCount > 0) {
					
					//Create push notification sender base for further push-message processing.
					PushMessagingInterface senderBase = new PushNotificationManager();
					PushNotificationManager manager = new PushNotificationManager();
					
					User receiver = p.getUser();
					p.setFcmToken(tokenRepo.findById(receiver.getId()).toString());
					
					JSONObject notification = manager.generateFromDaily(p, restaurantsForPushCount, pushKitchenTypeIds, tokenRepo.findById(receiver.getId()).toString());
					
					//Check which push notification token is valid, process data at sender manager.
					if(!p.getFcmToken().equals(NOT_AVAILABLE)) {
						
						senderBase.sendFcmNotification(notification);
						//senderBase.sendFcmDailyNotification(p, restaurantsForPushCount, pushKitchenTypeIds);
					}
					if(!p.getSnsToken().equals(NOT_AVAILABLE)) {
						senderBase.sendAdmNotification(p, restaurantsForPushCount, pushKitchenTypeIds);
					}
				}

			}
		}
		//Console log info
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), "Check for push notifications finished."));
	}
}

