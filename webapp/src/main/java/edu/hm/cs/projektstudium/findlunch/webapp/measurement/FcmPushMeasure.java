package edu.hm.cs.projektstudium.findlunch.webapp.measurement;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;

/**
 * The FcmPushMeasure, sends Google FCM based push-notifications for measurement (not at live-operation).
 * 
 * Creates push-notification with title containing name, id and current push count for later exactly identification.
 * (Format ex. testpush5 100) 
 * 
 * Sends Amazon SNS push to mobile device, based on token inside of dummy notification. 
 * 
 * Created by Maxmilian Haag on 20.12.2016 / 07.02.2017
 * @author Maximilian Haag
 *
 */
public class FcmPushMeasure extends PushMeasureBase implements Runnable {
	
	/** 
	 * The logger. 
	 */
	private Logger LOGGER = LoggerFactory.getLogger(FcmPushMeasure.class);

	/**
	 * Current push-notification 
	 */
	private DailyPushNotificationData p;
	
	/**
	 * Current push count.
	 */
	private int count;

	/**
	 * Url for sending Google FCM notification.
	 */
	private final String FCM_NOT_URL = "https://fcm.googleapis.com/fcm/send";
	

	/**
	 * Initialization of measure with push-notification to be sent and current count.
	 * @param p Push notification
	 * @param count Current count
	 */
	public FcmPushMeasure(DailyPushNotificationData p, int count) {
		this.p = p;
		this.count = count;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		
		//Creating json data for message.
		JSONObject messageObject = new JSONObject();
		JSONObject data = new JSONObject();
		
		//Addressing token for push
		messageObject.put("to", p.getFcmToken());
		
		//Collapse key not required for measurement (insert here if required)
		//messageObject.put("collapse_key", COLLAPSE_KEY + "_" + p.getId());
		
		// TTL = 6 hours (if scheduled at 9h, push is received until 15h)
		messageObject.put("time_to_live", 21600);

		//Create title with titlename, userid and current push count for later identification at receiver.
		//Only one string to reduce overhead (not more required)
		data.put("title", p.getTitle() + p.getUser().getId() + " " + count);
		
		//Dummy data for measure push
		data.put("numberOfRestaurants", "1");
		data.put("longitude", String.valueOf(p.getLongitude()));
		data.put("latitude", String.valueOf(p.getLatitude()));
		data.put("radius", String.valueOf(p.getRadius()));
		data.put("kitchenTypeIds", p.getKitchenTypes().toString());
		data.put("pushId", String.valueOf(p.getId()));

		//Recording timestamp for later round-trip-time calculation
		long currentTime = System.currentTimeMillis();
		data.put("timestamp", String.valueOf(currentTime));
		messageObject.put("data", data);
		
		//Sending push-notification via Google FCM url.
		RequestBody body = RequestBody.create(mediaType, messageObject.toString());
		Request request = new Request.Builder().url(FCM_NOT_URL).post(body)
				.addHeader("Content-type", mediaType.toString()).addHeader("Authorization", "key=" + FCM_SENDER_ID).build();

		//Console info
		System.out.println("PUSH-Measure: " + messageObject.toString());
		try {
			Response response = client.newCall(request).execute();
			response.body().close();
			if (response.message().equals("OK")) {
				LOGGER.info(
						LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
								"FCM Notification was sent successfully: " + response.message()));
			} else if (response.message().equals("Bad Request")) {
				LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Invalid JSON Format."));
			} else {
				LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Error occurred while sending push notification :" + response.message()));
			}
		} catch (Exception e) {
			LOGGER.error(LogUtils.getExceptionMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		}
	}
}
