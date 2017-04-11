package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;

/**
 * 
 * Class SendFcmNotification.
 * Prepares and sends Google FCM push messages via Google Url (JSON-HTTP Request) to mobile device.
 * 
 * Created by Maxmilian Haag on 07.02.2017
 * @author Maximilian Haag
 *
 */
public class SendFcmNotification extends PushNotificationScheduleBase implements Runnable {
	
	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(SendFcmNotification.class);
	
	/**
	 * Url for sending Google FCM notification.
	 */
	private final String FCM_NOT_URL = "https://fcm.googleapis.com/fcm/send";
	
	/**
	 * @param p Push-notification to be sent.
	 * @param restaurantsForPushCount Restaurant for push.
	 * @param pushKitchenTypeIds List of kichen types for push.
	 */
	public SendFcmNotification(PushNotification p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds) {
		this.p = p;
		this.restaurantsForPushCount = restaurantsForPushCount;
		this.pushKitchenTypeIds = pushKitchenTypeIds;
	}


	@SuppressWarnings("unchecked")
	public void run() {

		//Direct HTTP-client usable for sending push json-based.
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");

		//Creating message object and push data inside.
		JSONObject messageObject = new JSONObject();
		JSONObject data = new JSONObject();
		messageObject.put("to", p.getFcmToken());

		//If multiple messages are sent while device is offline,
		//only receive the latest message is received.
		messageObject.put("collapse_key", COLLAPSE_KEY + "_" + p.getId());

		//TTL = 6 hours (if scheduled at 9h, push is received until 15h)
		messageObject.put("time_to_live", 21600);

		//Add data from database to push data.
		data.put("title", p.getTitle());
		data.put("numberOfRestaurants", restaurantsForPushCount.toString());
		data.put("longitude", String.valueOf(p.getLongitude()));
		data.put("latitude", String.valueOf(p.getLatitude()));
		data.put("radius", String.valueOf(p.getRadius()));
		data.put("kitchenTypeIds", pushKitchenTypeIds.toString());
		data.put("pushId", String.valueOf(p.getId()));
		messageObject.put("data", data);


		//Adding required properties to generate push-request.
		RequestBody body = RequestBody.create(mediaType, messageObject.toString());
		Request request = new Request.Builder()
				.url(FCM_NOT_URL)
				.post(body)
				.addHeader("Content-type", mediaType.toString())
				.addHeader("Authorization", "key="+FCM_SENDER_ID).build();
		try {

			//Console log info
			LOGGER.info("Push sent: " + messageObject.toString());
			
			//Execute request.
			Response response = client.newCall(request).execute();
			response.body().close();

			if (response.message().equals("OK")) {
				LOGGER.info(
						LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
								"FCM Notification was sent successfully: " + response.message()));
			}
			else if (response.message().equals("Bad Request")) {
				LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Invalid JSON Format."));
			}
			else {
				LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Error occurred while sending push notification :" + response.message()));
			}
		} catch (Exception e) {
			LOGGER.error(LogUtils.getExceptionMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		}
		return;
	}
}

