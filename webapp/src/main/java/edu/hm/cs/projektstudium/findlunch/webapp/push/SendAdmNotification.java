package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;

/**
 *  
 * Class SendAdmNotification.
 * Prepares and sends Amazon SNS push messages.
 * 
 * Reading additionally Amazon properties credentials from file (requires setup):
 * "/src/main/resources/AwsCredentials.properties"
 * 
 * Contains also methods for further extension of services.
 * 
 * Created by Maxmilian Haag on 15.01.2017.
 * @author Maximilian Haag
 *
 */

public class SendAdmNotification extends PushNotificationManager implements Runnable {

	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(SendAdmNotification.class);
	
	/**
	 * Platform Amazon Device Messaging.
	 */
	private final String platform = "ADM";
	
	/**
	 * Object mapper for jsonify.
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	
	/**
	 * Current identification credentials for Amazon Simple Notification Service.
	 */
	private AmazonSNS sns;
	
	
	/**
	 * Map with additional attributes for push-notification.
	 */
	private Map<Platform, Map<String, MessageAttributeValue>> attributesMap = new HashMap<>();


	/**
	 * @param p Push-notification to be sent.
	 * @param restaurantsForPushCount Restaurant for push.
	 * @param pushKitchenTypeIds List of kichen types for push.
	 */
	public SendAdmNotification(DailyPushNotificationData p, Integer restaurantsForPushCount, List<Integer> pushKitchenTypeIds) {
		this.p = p;
		this.restaurantsForPushCount = restaurantsForPushCount;
		this.pushKitchenTypeIds = pushKitchenTypeIds;
	}

	/**
	 * Creates kindle message map for sending push.
	 * @return Prepared kindle message map.
	 */
	private Map<String, Object> createKindleMessageMap() {
		
		//Create kindle notification message.
		Map<String, Object> kindleMessageMap = new HashMap<String, Object>();
		
		// TTL value, standard is 6 hours (21600s).
		Map<String, MessageAttributeValue> entry = new HashMap<>();
		entry.put("AWS.SNS.MOBILE.ADM.TTL", new MessageAttributeValue().withDataType("String").withStringValue("21600"));
		
		//Platform: Amazon Device Messaging.
		attributesMap.put(Platform.ADM, entry);

		// If multiple messages are sent while device is offline, only receive the latest message is received.
		kindleMessageMap.put("collapse_key", COLLAPSE_KEY);

		//Fill push with data from push-notification info stored in database.
		Map<String, String> data = new HashMap<String, String>();
		data.put("title", p.getTitle());
		data.put("numberOfRestaurants", restaurantsForPushCount.toString());
		data.put("longitude", String.valueOf(p.getLongitude()));
		data.put("latitude", String.valueOf(p.getLatitude()));
		data.put("radius", String.valueOf(p.getRadius()));
		data.put("kitchenTypeIds", pushKitchenTypeIds.toString());
		data.put("pushId", String.valueOf(p.getId()));
		kindleMessageMap.put("data", data);
		return kindleMessageMap;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		//Push data map to be sent
		Map<String, Object> kindleMessageMap = createKindleMessageMap();

		/**
		 * Load IAM Amazon credentials, customizable to created users 
		 * Reading Amazon secret credentials from /src/main/resources/AwsCredentials.properties
		 */
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream resourceAsStream = classloader.getResourceAsStream("AwsCredentials.properties");
		PropertiesCredentials admCred = null;
		try {
			admCred = (new PropertiesCredentials(resourceAsStream));
			
		} catch (IOException e) {
			LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), "No credentials available."));
		}
		sns = new AmazonSNSClient(admCred);
		
		/**
		 * Create application platform with AwsCredentials (leaves endpoints if already created application/endpoints)
		 */
		CreatePlatformApplicationResult platformApplicationResult = new CreatePlatformApplicationResult();
		
		//Create Platform Application. This corresponds to an app on a platform.
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", AWS_CLIENT_ID);
		attributes.put("PlatformCredential", AWS_CLIENT_SECRET);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(AWS_APPLICATION_NAME);
		platformApplicationRequest.setPlatform(platform);
		platformApplicationResult = sns.createPlatformApplication(platformApplicationRequest);
		
		/**
		 * The Platform Application Arn can be used to uniquely identify the Platform Application.
		 * Reads the belonging Application Arn belonging to the created application.
		 * 
		 */
		String aws_platformApplicationArn = platformApplicationResult.getPlatformApplicationArn();

		
		/**
		 * Create an Endpoint. This corresponds to an app on a device.
		 * Each user (registrationId/token) needs an own endpoint.
		 * 
		 */
		CreatePlatformEndpointResult cpeRes = null;
		// Create an Endpoint. This corresponds to an app on a device.
 		CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
 		platformEndpointRequest.setCustomUserData(AWS_ENDPOINT_USERDATA);
		platformEndpointRequest.setToken(p.getSnsToken());
		platformEndpointRequest.setPlatformApplicationArn(aws_platformApplicationArn);
		cpeRes = sns.createPlatformEndpoint(platformEndpointRequest);
 		
		/**
		 * Load attributes and publish a push notification to an Endpoint.
		 */
		PublishRequest publishRequest = new PublishRequest();
		Map<String, MessageAttributeValue> notificationAttributes = getValidNotidficationAttributes(attributesMap.get(platform));

		// Set measure attributes
		if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
			publishRequest.setMessageAttributes(notificationAttributes);
		}
		publishRequest.setMessageStructure("json");

		//Create message map to put message inside.
		Map<String, String> messageMap = new HashMap<String, String>();

		// If the message attributes are not set in the requisite method, notification is sent with default attributes
		String message = chosePlatformForMessage(Platform.ADM, kindleMessageMap);
		messageMap.put(platform, message);
		message = jsonify(messageMap);
		
		// Internal publish
		publishRequest.setMessage(message);
		publishRequest.setTargetArn(cpeRes.getEndpointArn());
		sns.publish(publishRequest);

	}
	
	
	/**
	 * Deletion of application implemented, but not required.
	 * 
	 * @param applicationArn The application arn to identify the application to be deleted.
	 */
	@SuppressWarnings("unused")
	private void deletePlatformApplication(String applicationArn) {
		DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
		request.setPlatformApplicationArn(applicationArn);
		sns.deletePlatformApplication(request);
	}

	/**
	 * For possible future extensions 
	 * 
	 * @param platform The used platform.
	 * @return Message data map (json format).
	 */
	private String chosePlatformForMessage(Platform platform, Map<?,?> messageDataMap) {
		switch (platform) {
		case APNS:
			//Not supported in this case, extendable.
		case APNS_SANDBOX:
			//Not supported in this case, extendable.
		case GCM:
			//GCM obsolete! FCM up-to-date.
		case ADM:
			//Process Amazon push data.
			return jsonify(messageDataMap);
		case BAIDU:
			//Not supported in this case, extendable.
		case WNS:
			//Not supported in this case, extendable.
		case MPNS:
			//Not supported in this case, extendable.
		default:
			throw new IllegalArgumentException("Platform not supported : " + platform.name());
		}
	}


	/**
	 * Helper to load valid notification attributes for push-message.
	 * @param notificationAttributes The available notification attributes
	 * @return Valid notification attributes
	 */
	private Map<String, MessageAttributeValue> getValidNotidficationAttributes(Map<String, MessageAttributeValue> notificationAttributes) {
		Map<String, MessageAttributeValue> validAttributes = new HashMap<String, MessageAttributeValue>();
		if (notificationAttributes == null)
			return validAttributes;
		for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes.entrySet()) {
			if (!isBlank(entry.getValue().getStringValue())) {
				validAttributes.put(entry.getKey(), entry.getValue());
			}
		}
		return validAttributes;
	}
	
	/**
	 * For possible future extensions. 
	 * 
	 * @param platform The chosen platform.
	 * @return Chosen platform.
	 */
	private static enum Platform {
		// Apple Push Notification Service
		APNS,
		// Sandbox version of Apple Push Notification Service
		APNS_SANDBOX,
		// Amazon Device Messaging
		ADM,
		// Google Cloud Messaging
		GCM,
		// Baidu CloudMessaging Service
		BAIDU,
		// Windows Notification Service
		WNS,
		// Microsoft Push Notificaion Service
		MPNS;
	}


	/**
	 * Convertoing message object into json string.
	 * @param message Object message data.
	 * @return Json string of message data.
	 */
	private String jsonify(Object message) {
		try {
			return objectMapper.writeValueAsString(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;
		}
	}

	/**
	 * Check if string is empty.
	 * @param s String to check.
	 * @return Empty status.
	 */
	private boolean isEmpty(String s) {
		if (s == null) {
			return true;
		}

		if (s.length() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * Check if string is blank
	 * @param s String to check
	 * @return Blank status.
	 */
	private boolean isBlank(String s) {
		if (isEmpty(s)) {
			return true;
		}

		if (isEmpty(s.trim())) {
			return true;
		}
		return false;
	}
}

