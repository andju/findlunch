package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;
/**
 * 
 * Class PushNotificationScheduleBase. 
 * Called from scheduled class "PushNotificationScheduledTask".
 * 
 * Handles live-operation Amazon SNS and Google FCM push-notification messaging.
 * Requires provider credentials for live-operation:
 * #Credentials configuration:
 * **Configure LiveOpCredentials.conf, representing Amazon/Google identification
 * 
 * Base class for sending Amazon or Google push message.
 * 
 * Created by Maxmilian Haag on 15.01.2017.
 * @author Maximilian Haag
 *
 */
public class PushNotificationScheduleBase implements PushMessagingInterface {
	
	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(PushNotificationScheduleBase.class);
	
	/**
	 * Google FCM / Amazon AWS identification credentials read from /src/main/resources/LiveOpCredentials.conf 
	 */
	protected static String FCM_SENDER_ID;
	protected static String AWS_CLIENT_ID; 
	protected static String AWS_CLIENT_SECRET;
	protected static String AWS_APPLICATION_NAME;
	protected static String AWS_ENDPOINT_USERDATA;

	/** The collapse key. */
	protected final String COLLAPSE_KEY = "findLunchDaily";

	/** The number of retries. */
	protected final int NUMBER_OF_RETRIES = 1;

	/** The push repository. */
	@Autowired
	protected PushNotificationRepository pushRepo;

	/** The PushNotification. */
	protected PushNotification p;

	/** The restaurants for push count. */
	protected Integer restaurantsForPushCount;

	/** The push kitchen type ids. */
	protected List<Integer> pushKitchenTypeIds;

	/**
	 * Executor service for executing pushes.
	 */
	private ExecutorService executor = Executors.newFixedThreadPool(100);
	

	/**
	 * Initialize base credentials only once for inherited runnables.
	 */
	public PushNotificationScheduleBase() {
		if(FCM_SENDER_ID == null || AWS_CLIENT_ID == null || AWS_CLIENT_SECRET == null ||  AWS_APPLICATION_NAME == null || 
				AWS_ENDPOINT_USERDATA == null || AWS_ENDPOINT_USERDATA == null) {
			checkLiveOpCredentialsFile();
		}
	}
	
	/**
	 * Load FCM and SNS identification credentials from file at 
	 * src/main/resources/LiveOpCredentials.conf. 
	 */
	private void checkLiveOpCredentialsFile() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();;
		InputStream resourceAsStream = classloader.getResourceAsStream("LiveOpCredentials.conf");
		BufferedReader resReader = null; 
		try {
			resReader = new BufferedReader(new InputStreamReader(resourceAsStream));
			
			//Lines 0-5, data part
			FCM_SENDER_ID = resReader.readLine().split("=")[1];
			AWS_CLIENT_ID = resReader.readLine().split("=")[1];
			AWS_CLIENT_SECRET = resReader.readLine().split("=")[1];
			AWS_APPLICATION_NAME = resReader.readLine().split("=")[1];
			AWS_ENDPOINT_USERDATA = resReader.readLine().split("=")[1];
			
		} catch (FileNotFoundException e) {
			LOGGER.error("File not found.");
		} catch (IOException e) {
			LOGGER.error("I/O error.");
		}
	}	



	/**
	 * Execute FCM push, called by scheduler class "PushNotificationScheduledTask".
	 */
	/* (non-Javadoc)
	 * @see edu.hm.cs.projektstudium.findlunch.webapp.push.PushMessagingInterface#sendFcmNotification(edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification, java.lang.Integer, java.util.List)
	 */
	@Override
	public void sendFcmNotification(PushNotification p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds) {
		executor.execute(new SendFcmNotification(p, restaurantsForPushCount, pushKitchenTypeIds));
	}

	/**
	 * Execute ADM push, called by scheduler class "PushNotificationScheduledTask".
	 */
	/* (non-Javadoc)
	 * @see edu.hm.cs.projektstudium.findlunch.webapp.push.PushMessagingInterface#sendAdmNotification(edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification, java.lang.Integer, java.util.List)
	 */
	@Override
	public void sendAdmNotification(PushNotification p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds) {
		executor.execute(new SendAdmNotification(p, restaurantsForPushCount, pushKitchenTypeIds));		
	}


	/**
	 * Get FCM id.
	 * @return FCM id.
	 */
	public static String getFcmSenderId() {
		return FCM_SENDER_ID;
	}


	/**
	 * Get AWS client id.
	 * @return AWS client id.
	 */
	public static String getAwsClientId() {
		return AWS_CLIENT_ID;
	}


	/**
	 * Get AWS client secret.
	 * @return AWS client secret.
	 */
	public static String getAwsClientSecret() {
		return AWS_CLIENT_SECRET;
	}


	/**
	 * Get AWS app name.
	 * @return AWS app name.
	 */
	public static String getAwsApplicationName() {
		return AWS_APPLICATION_NAME;
	}


	/**
	 * Get AWS endpoint user data.
	 * @return AWS endpoint user data.
	 */
	public static String getAwsEndpointUserdata() {
		return AWS_ENDPOINT_USERDATA;
	}
}
