package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.rest.RestaurantRestController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;

/**
 * The Class PushNotificationScheduledTask.
 */
@Component
public class PushNotificationScheduledTask {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(PushNotificationScheduledTask.class);

	/** The push repo. */
	@Autowired
	private PushNotificationRepository pushRepo;

	/** The restaurant rest. */
	@Autowired
	private RestaurantRestController restaurantRest;

	/** The restauraunt repo. */
	@Autowired
	private RestaurantRepository restaurauntRepo;

	/** The executor. */
	ExecutorService executor = Executors.newFixedThreadPool(100);

	/**
	 * Check push notifications.
	 */
	@Scheduled(fixedRate = 200000)
	public void checkPushNotifications() {

		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
				"Starting check for push notifications."));

		List<PushNotification> activePushNotifications = pushRepo.findAll();

		int dayNumberToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		for (PushNotification p : activePushNotifications) {

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
				restaurantsNearbyList = restaurantRest.getAllRestaurants(p.getLongitude(), p.getLatitude(),
						p.getRadius());

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
					// Start new Thread to send the notification.
					executor.execute(new SendPushNotification(p, restaurantsForPushCount, pushKitchenTypeIds));

				}

			}
		}

		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
				"Check for push notifications finished."));

	}

}

class SendPushNotification implements Runnable {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(SendPushNotification.class);

	/** The sender id. */
	private final String SENDER_ID = "AIzaSyAhPUXTaIVu7aDOyKh2ulBt4et9Y0TmVUs";

	/** The collapse key. */
	private final String COLLAPSE_KEY = "findLunchDaily";

	/** The number of retries. */
	private final int NUMBER_OF_RETRIES = 1;

	/** The push repository. */
	@Autowired
	private PushNotificationRepository pushRepo;

	/** The PushNotification. */
	private PushNotification p;

	/** The restaurants for push count. */
	private Integer restaurantsForPushCount;

	/** The push kitchen type ids. */
	private List<Integer> pushKitchenTypeIds;

	public SendPushNotification(PushNotification p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds) {
		this.p = p;
		this.restaurantsForPushCount = restaurantsForPushCount;
		this.pushKitchenTypeIds = pushKitchenTypeIds;
	}

	public void run() {
		Sender sender = new Sender(SENDER_ID);

		Message message = new Message.Builder()
				// If multiple messages are sent while device is offline,
				// only receive the latest message is received.
				.collapseKey(COLLAPSE_KEY + "_" + p.getId())
				// TTL = 6 hours (if scheduled at 9h, push is received until
				// 15h)
				.timeToLive(21600).delayWhileIdle(true).addData("title", p.getTitle())
				.addData("numberOfRestaurants", restaurantsForPushCount.toString())
				.addData("longitude", String.valueOf(p.getLongitude()))
				.addData("latitude", String.valueOf(p.getLatitude())).addData("radius", String.valueOf(p.getRadius()))
				.addData("kitchenTypeIds", pushKitchenTypeIds.toString()).addData("pushId", String.valueOf(p.getId()))
				.build();

		try {

			Result result = sender.send(message, p.getGcmToken(), NUMBER_OF_RETRIES);

			if (result.getErrorCodeName() == null) {
				LOGGER.info(
						LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
								"GCM Notification was sent successfully: " + message.toString()));
			} else if (result.getErrorCodeName().equals("InvalidRegistration")) {
				pushRepo.delete(p);
				LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"GCM Token invalid: Push-Notification is deleted."));
			} else {
				LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Error occurred while sending push notification :" + result.getErrorCodeName()));
			}

		} catch (Exception e) {
			LOGGER.error(LogUtils.getExceptionMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		}

		return;
	}

}
