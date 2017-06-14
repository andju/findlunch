package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;

/**
 * 
 * Class SendFcmNotification.
 * Prepares and sends Google FCM push messages via Google Url (JSON-HTTP Request) to mobile device.
 * 
 * Created by Maxmilian Haag on 07.02.2017
 * @author Maximilian Haag
 * 
 * Extended by Niklas Klotz on 21.04.2017
 * @author Niklas Klotz
 * 
 * Edits done:
 * - Handle different type of Notifications.
 *
 */
public class SendFcmNotification extends PushNotificationManager implements Runnable {
	
	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(SendFcmNotification.class);
	
	//TEST
	@Autowired
	PushTokenRepository pushRepo;
	
	/**
	 * Url for sending Google FCM notification.
	 */
	private final String FCM_NOT_URL = "https://fcm.googleapis.com/fcm/send";
	
	public SendFcmNotification(JSONObject push){
		this.push = push;
	}
	
	public void run() {

		//Direct HTTP-client usable for sending push json-based.
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");

		//Adding required properties to generate push-request.
		//RequestBody body = RequestBody.create(mediaType, messageObject.toString());
		RequestBody body = RequestBody.create(mediaType, push.toString());
		Request request = new Request.Builder()
				.url(FCM_NOT_URL)
				.post(body)
				.addHeader("Content-type", mediaType.toString())
				.addHeader("Authorization", "key="+FCM_SENDER_ID).build();
		try {
			
			//Console log info
			//LOGGER.info("Push sent: " + messageObject.toString());
			LOGGER.info("Push sent: " + push.toString());
			
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

