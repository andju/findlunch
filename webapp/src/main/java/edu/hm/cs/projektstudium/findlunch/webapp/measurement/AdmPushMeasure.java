package edu.hm.cs.projektstudium.findlunch.webapp.measurement;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;

/**
 * The AdmPushMeasure, sends Amazon SNS based push-notifications for measurement (not at live-operation).
 * Reading additionally Amazon properties credentials from file (requires setup):
 * "/src/main/resources/AwsCredentials.properties"
 *  
 * Creates push-notification with title containing name, id and current push count for later exactly identification.
 * (Format ex. testpush5 100) 
 * 
 * Sends Amazon SNS push via endpoint to mobile device, based on token inside of dummy notification. 
 * (No collapse key required at measure).
 * 
 * 
 * Created by Maxmilian Haag on 20.12.2016.
 * @author Maximilian Haag
 *
 */
public class AdmPushMeasure extends PushMeasureBase implements Runnable {
	
	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(AdmPushMeasure.class);

	
	/**
	 * Platform Amazon Device Messaging.
	 */
	private final String platform = "ADM";
	
	/**
	 * Current push count.
	 */
	private final int count;
	
	/**
	 * Token of current sending push. 
	 */
	private String token;
	
	/**
	 * Current push-notification 
	 */
	private DailyPushNotificationData p;
	
	/**
	 * Current identification credentials for Amazon Simple Notification Service.
	 */
	private AmazonSNS sns;
	

	/**
	 * Initialization of measure with push-notification to be sent and current count.
	 * @param p Push notification
	 * @param count Current count
	 */
	public AdmPushMeasure(DailyPushNotificationData p, int count) {
		this.count = count;
		token = p.getSnsToken();
		this.p = p;
	
	}
	
	/**
	 * Create Amazon Push Notification and send it to mobile device.
	 * 
	 * @param count Current number of push
	 * @return Ready push as kindle message map to be sent.
	 */
	private Map<String, Object> createTitleAndTimestamp(int count) {

		DailyPushNotificationData p = new DailyPushNotificationData();
		
		//Create title with titlename, userid and current push count for later identification at receiver.
		//Only one string to reduce overhead (not more required)
		p.setTitle(this.p.getTitle() + this.p.getUser().getId() + " " + count);
		p.setSnsToken(token);
		
		//Dummy data for measure push
		p.setLongitude(11.5544F);
		p.setLatitude(48.1537F);
		p.setRadius(2000);
		p.setId(1);
		p.setKitchenTypes(new ArrayList<>());

		//Content of Push
		Map<String, Object> kindleMessageMap = new HashMap<String, Object>();
		
		//Info: Collapse key not required for measurement

		//Can be used to track messages for testing
		//kindleMessageMap.put("message_variation_id", 1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("title", p.getTitle());
		data.put("numberOfRestaurants", "1");
		data.put("longitude", String.valueOf(p.getLongitude()));
		data.put("latitude", String.valueOf(p.getLatitude()));
		data.put("radius", String.valueOf(p.getRadius()));
		data.put("pushId", String.valueOf(p.getId()));
		data.put("kitchenTypeIds", p.getKitchenTypes().toString());

		//Timestamp additionally
		data.put("timestamp", String.valueOf(System.currentTimeMillis()));
		
		//Add complete data
		kindleMessageMap.put("data", data);
		return kindleMessageMap;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

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
			LOGGER.error(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
					"No credentials available."));
		}
		sns = new AmazonSNSClient(admCred);
		
		/**
		 * Create application platform with AwsCredentials (leaves endpoints if already created application/endpoints)
		 */
		CreatePlatformApplicationResult platformApplicationResult = new CreatePlatformApplicationResult();

		//Creates platform application request with AwsCredentials.
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
		//Creates platform endpoint request with userdata, token and using application arn for measure.
		CreatePlatformEndpointResult cpeRes = null;
		CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
		platformEndpointRequest.setCustomUserData(AWS_ENDPOINT_USERDATA);
		platformEndpointRequest.setToken(token);
		platformEndpointRequest.setPlatformApplicationArn(aws_platformApplicationArn);
		cpeRes = sns.createPlatformEndpoint(platformEndpointRequest);

		
		
		// Publish Request with standard time to live.
		PublishRequest publishRequest = new PublishRequest();
		Map<String, MessageAttributeValue> attributeMap = new HashMap<>();
		
		// Setting TTL at 21600.
		attributeMap.put("AWS.SNS.MOBILE.ADM.TTL", new MessageAttributeValue().withDataType("String").withStringValue("21600"));
		if (attributeMap != null && !attributeMap.isEmpty()) {
			publishRequest.setMessageAttributes(attributeMap);
		}
		publishRequest.setMessageStructure("json");

		// Create request and fill data.
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> messageMap = new HashMap<String, String>();
		// Create and timestamp
		Map<String, Object> kindleMessageMap = createTitleAndTimestamp(count);
		String message = null;
		try {
			message = objectMapper.writeValueAsString(kindleMessageMap);
		} catch (JsonProcessingException e) {
			LOGGER.error("ERROR");
		}
		messageMap.put(platform, message);
		try {
			message = objectMapper.writeValueAsString(messageMap);
		} catch (JsonProcessingException e) {
			LOGGER.error("ERROR");
		}

		//Print push message to console
		LOGGER.info("PUSH-Measure: " + message.toString());
		
		// Internal Amazon publish
		publishRequest.setTargetArn(cpeRes.getEndpointArn());
		publishRequest.setMessage(message);
		sns.publish(publishRequest);
	}
}