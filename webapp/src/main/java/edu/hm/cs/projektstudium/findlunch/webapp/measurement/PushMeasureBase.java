package edu.hm.cs.projektstudium.findlunch.webapp.measurement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;


/**
 * 
 * PushMeasureBase class (Main class for measurement!)
 * 
 * Separate base class for sending push notification measures (completely separated from live-operation).
 * **Live operation has to be disabled at package "scheduling", class "PushNotificationScheduledTask".
 * **Required measurement has to be enabled in this class.
 * 
 * Measures FCM and ADM/SNS push-notifications.
 * Can be used with other Amazon/Google credentials than live-operation if required.
 * #Credentials configuration:
 * **Configure MeasureCredentials.conf, representing Amazon/Google identification
 * **Configure AwsCredentials.properties, representing Amazon accesskey and secretkey. 
 * 
 * #Measure configuration:
 * Uncomment annotation "@PostConstruct" or "@Scheduled" for executing current measure.
 * Only one measure possible at simultaneously.
 * 
 * Speed and performance measure of FCM/SNS require one registred push-notification with title "testpush" in database.
 * Can be specified for one or more users.
 * Scaled measure only possible with FCM service.
 * Scaled FCM measure, collect all pushes starting with "testpush" of different users for multi-push-message measure.
 * 
 * UnitTest not suitable at this case, using PostConstruct for measures.
 * Uncomment for measure execution.
 * 
 * Created by Maxmilian Haag on 18.12.2016.
 * @author Maximilian Haag
 * 
 *
 */

@SuppressWarnings("unused")
@Component
public class PushMeasureBase implements PushMeasureInterface {

	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(PushMeasureBase.class);
	
	
	//measure adjustment (change here) #########################
	public final static int NUMBER_OF_PUSHES = 100;
	public final static int USER_DEVICES = 1;
	public final static int NUMBER_OF_ITERATIONS = 1;
	public final static boolean SORT_SCALED_MEASURE_WHEN_READY = false;
	//measure adjustment (change here) #########################
	

	/**
	 * Google / Amazon identification credentials read from /src/main/resources/MeasureCredentials.conf 
	 */
	protected static String FCM_SENDER_ID;
	protected static String AWS_CLIENT_ID; 
	protected static String AWS_CLIENT_SECRET;
	protected static String AWS_APPLICATION_NAME;
	protected static String AWS_ENDPOINT_USERDATA;


	
	/**
	 * Current registred pushes in database.
	 */
	@Autowired
	private PushNotificationRepository pushRepo;

	/**
	 * Single threaded executor for sequency.
	 */
	private ExecutorService singleEx = Executors.newSingleThreadExecutor();
	
	/**
	 * Current sent number of pushes.
	 */
	private int pushCount = 0;
	
	
	/**
	 * Initialize base credentials only once for inherited runnables.
	 */
	public PushMeasureBase() {
		if(FCM_SENDER_ID == null || AWS_CLIENT_ID == null || AWS_CLIENT_SECRET == null ||  AWS_APPLICATION_NAME == null || 
				AWS_ENDPOINT_USERDATA == null || AWS_ENDPOINT_USERDATA == null) {
			checkMeasureCredentialsFile();
		}
	}


	/**
	 * ##############################################
	 * # UNCOMMENT "@PostConstruct" FOR MEASUREMENT!#
	 * ##############################################
	 * 
	 * Launches SNS performance measure for high message amount.
	 * Requires registred Amazon SNS push (SNS token). 
	 */
	//@PostConstruct	
	public void launchPerformanceSNSMeasure() {
		LOGGER.info("Launching SNS/ADM Performance Measure");

		//Load dummy push notification from db
		DailyPushNotificationData dummyNotification = extractFromDB();
	
		for(int j = 0; j < NUMBER_OF_ITERATIONS; j++) {
			for(int i = 0; i < NUMBER_OF_PUSHES; i++) {
				singleEx.execute(new AdmPushMeasure(dummyNotification, i));
			}
		}
	}
	

	/**
	 * ##############################################
	 * # UNCOMMENT "@PostConstruct" FOR MEASUREMENT!#
	 * ##############################################
	 * 
	 * Launches FCM performance measure for high message amount.
	 * Requires registred Google FCM push (FCM token). 
	 */
	//@PostConstruct
	public void launchPerformanceFCMMeasure() {
		LOGGER.info("Launching FCM Performance Measure");
		
		//Load dummy push notification from db
		DailyPushNotificationData dummyNotification = extractFromDB();
		
		for(int j = 0; j < NUMBER_OF_ITERATIONS; j++) {
			for(int i = 0; i < NUMBER_OF_PUSHES; i++) {
				singleEx.execute(new FcmPushMeasure(dummyNotification, i));
			}
		}
		
	}
	

	
	/**
	 * ##########################################
	 * # UNCOMMENT "@Scheduled" FOR MEASUREMENT!#
	 * ##########################################
	 * 
	 * Launches FCM single speed measure. 
	 * Long time measure for single sending and receiving of messages to evaluate single RTT speed.
	 * NOT for high amount of messages (use performance measure above).
	 * Requires registred Google FCM push (FCM token). 
	 * Sending rate adjustable (current: each 10000 ms)
	 */
	//@Scheduled(fixedRate = 10000)
	public void launchSpeedFCMMeasure() {
		LOGGER.info("Launching FCM (Single) Speed Measure");

		//Load dummy push notification from db
		DailyPushNotificationData dummyNotification = extractFromDB();
		
		if(pushCount < NUMBER_OF_PUSHES) {
			singleEx.execute(new FcmPushMeasure(dummyNotification, pushCount));
			pushCount++;
		}
	}
	
	
	/**
	 * ##########################################
	 * # UNCOMMENT "@Scheduled" FOR MEASUREMENT!#
	 * ##########################################
	 * 
	 * Launches SNS single speed measure. 
	 * Long time measure for single sending and receiving of messages to evaluate single RTT speed.
	 * NOT for high amount of messages (use performance measure above).
	 * Requires registred Amazon SNS push (SNS token). 
	 * Sending rate adjustable (current: each 10000 ms)
	 */
	//@Scheduled(fixedRate = 10000)
	public void launchSpeedSNSMeasure() {
		LOGGER.info("Launching ADM/SNS (Single) Speed Measure");

		//Load dummy push notification from db
		DailyPushNotificationData dummyNotification = extractFromDB();
		
		if(pushCount < NUMBER_OF_PUSHES) {
			singleEx.execute(new AdmPushMeasure(dummyNotification, pushCount));
			pushCount++;
		}
	}


	/**
	 * ##############################################
	 * # UNCOMMENT "@PostConstruct" FOR MEASUREMENT!#
	 * ##############################################
	 * 
	 * Launches scaled test for each "testpush" addressed one or more different user ids.
	 * Requires registred pushes called "testpush" registred by different users/devices.
	 * Number of registred "testpush" is number of devices.
	 */
	//@PostConstruct
	public void launchDeviceScaledFCMMeasure() {
		LOGGER.info("Launching Device scaled FCM Measure");
		
		List<DailyPushNotificationData> toSend = new ArrayList<>();

		//FCM Token extract from Database by registred name "testpush".
		List<DailyPushNotificationData> activePushNotifications = pushRepo.findAll();
		for(DailyPushNotificationData pu : activePushNotifications) {
			if(pu.getTitle().equals("testpush")) {
				toSend.add(pu);
			}
		}
		for(int j = 0; j < NUMBER_OF_ITERATIONS; j++) {
			for(int i = 0; i < NUMBER_OF_PUSHES; i++) {
				for(int k = 0; k < toSend.size(); k++) {
					DailyPushNotificationData p = toSend.get(k);
					singleEx.execute(new FcmPushMeasure(p, NUMBER_OF_PUSHES));
				}
			}
		}
	}


	
	
	
	
	/**
	 * Extract one (first) dummy push-notification found in database.
	 * @return The push-notification to extract.
	 */
	private DailyPushNotificationData extractFromDB() {
		//Token extract from Database by registred name "testpush".
		List<DailyPushNotificationData> activePushNotifications = pushRepo.findAll();
		DailyPushNotificationData p = null;
		for(DailyPushNotificationData pu : activePushNotifications) {
			if(pu.getTitle().equals("testpush")) {
				p = pu;
			}
		}
		return p;
	}
	
	
	/**
	 * Load FCM and SNS identification credentials from file at 
	 * src/main/resources/MeasureCredentials.conf. 
	 */
	private void checkMeasureCredentialsFile() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();;
		InputStream resourceAsStream = classloader.getResourceAsStream("MeasureCredentials.conf");
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
	 * Current number of pushes.
	 * @return Current number of pushes.
	 */
	public static int getNumberOfPushes() {
		return NUMBER_OF_PUSHES;
	}



	/**
	 * Current number of operating devices.
	 * @return Current number of operating devices.
	 */
	public static int getUserDevices() {
		return USER_DEVICES;
	}



	/**
	 * Number of measure iterations.
	 * @return Number of measure iterations.
	 */
	public static int getNumberOfIterations() {
		return NUMBER_OF_ITERATIONS;
	}



	/**
	 * Sort measure if measure ready.
	 * @return Sort measure.
	 */
	public static boolean isSortMeasureWhenReady() {
		return SORT_SCALED_MEASURE_WHEN_READY;
	}
	
}


