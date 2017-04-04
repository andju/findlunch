package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;


import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.App;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;



/**

LÖSCHEN!!!!!!!!!!!!!!!!!

17.11.2016
Tests schreiben JEDES Mal Daten aus dataIT.sql in findlunchIT DB per HibernateJPA:
country, day_of_week, kitchen_type, restaurant_type, user_type


Methoden schreiben extra (auch jedes Mal obwohl nur einzel Tests ausgewählt):
Kunde,Anbieter,PushMessage...


Einzel Push ans Handy:


Performance Test größre anzahl ans handy schicken,  
-Baut PushMessage


testGetSinglePushNotification ok (baut nur Nachricht)

18.11.2016
TestKlasse evtl löschen unnötig^^





Ganzes
@IntegrationTest()

Einzelner Test
@Test

*/



/**
 * The Class PushNotificationRestControllerIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class PushNotificationRestControllerIT_NEW {

	@Autowired
	DayOfWeekRepository dowRepository;
	
	@Autowired
	KitchenTypeRepository kitchenTypeRepository;
	
	/** The user repository. */
	@Autowired
	UserRepository userRepository;
	
	/** The user type repository. */
	@Autowired
	UserTypeRepository userTypeRepository;
	
	/** The push repository. */
	@Autowired
	PushNotificationRepository pushRepository;
	
	/** The password encoder. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	

	/** The Constant REGISTER_PUSH_API. */
	private static final String REGISTER_PUSH_API = "/api/register_push";
	

	/** The Constant UNREGISTER_PUSH_API. */
	private static final String UNREGISTER_PUSH_API = "/api/unregister_push/";
	
	/** The Constant GET_PUSH_API. */
	private static final String GET_PUSH_API = "/api/get_push";
	
	/** The server port. */
	@Value("${server.port}")
	private int serverPort;
	
	/**
	 * Sets the up.
	 */
	@Before
	@Sql({"/schemaIT.sql", "/dataIT.sql"})
	public void setUp() {
		
		RestAssured.baseURI="http://localhost";
		//RestAssured.baseURI="http://findlunch.ddns.net";
		RestAssured.port=serverPort;
		
	}

	

	
	/**
	 * Test Single Push nur registrieren mit RICHTIGER sender id
	 */
	@Test
	public void testRegisterSinglePushNotification() {
		RestAssured.registerParser("text/plain", Parser.TEXT);
			
		
		User user = new User();
		user.setUsername("android@android.com");
		user.setPassword(passwordEncoder.encode("android1Q!"));
		user.setPasswordconfirm("android1Q!");
		//type 2
		user.setUserType(userTypeRepository.findByName("Kunde"));
		userRepository.save(user);
		
		User savedUser = user; 
		userRepository.save(savedUser);
		
		
		
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		

		
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		
		//AIzaSyAhPUXTaIVu7aDOyKh2ulBt4et9Y0TmVUs
		//Push mit Param 1
		//PushNotification p = getPush(1);
		int tmp = 1;
		
		PushNotification p = new PushNotification();
		//dummy id
		//p.setId(i);
		p.setDayOfWeeks(dowRepository.findAll());
		
		for(DayOfWeek dow : p.getDayOfWeeks())
		{
			//initialize with default values
			dow.setOffers(new ArrayList<Offer>());
			dow.setPushNotifications(new ArrayList<PushNotification>());
			dow.setTimeSchedules(new ArrayList<TimeSchedule>());
		}
		
		p.setKitchenTypes(kitchenTypeRepository.findAll());
		for(KitchenType kt : p.getKitchenTypes())
		{
			//initialize with default values
			kt.setPushNotifications(new ArrayList<PushNotification>());
		}
		p.setRadius(1000 * tmp);
		p.setLatitude(10.2f + tmp);
		p.setLongitude(10.3f + tmp);
		//p.setGcmToken("AIzaSyAhPUXTaIVu7aDOyKh2ulBt4et9Y0TmVUs" + tmp);
		p.setFcmToken("AIzaSyAhPUXTaIVu7aDOyKh2ulBt4et9Y0TmVUs");

		p.setTitle("Title " + tmp);
		
		
		
		
		
		Response response = RestAssured.given()
			.contentType("application/json")
			.body(p)
			.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
			.then()
			.statusCode(200)
			.extract().response();
		
		System.out.println("====================================");
		//System.out.println(response.toString());
		System.out.println("====================================");

		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("0", resultCode);
		
		//check if push notification was saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(1, savedPushs.size());
		Assert.assertEquals(savedUser.getId(), savedPushs.get(0).getUser().getId());
		
		//Assert.assertEquals("TOKEN 1", savedPushs.get(0).getGcmToken());
		Assert.assertEquals("AIzaSyAhPUXTaIVu7aDOyKh2ulBt4et9Y0TmVUs", savedPushs.get(0).getFcmToken());
		
		
		Assert.assertEquals(1000, savedPushs.get(0).getRadius());
		Assert.assertEquals(11.3f, savedPushs.get(0).getLongitude(), 0.0001);
		Assert.assertEquals(11.2f, savedPushs.get(0).getLatitude(), 0.0001);
		Assert.assertEquals(p.getDayOfWeeks().size(), savedPushs.get(0).getDayOfWeeks().size());
		for(int i = 0; i < p.getDayOfWeeks().size(); i++) {
			Assert.assertEquals(p.getDayOfWeeks().get(i).getId(), savedPushs.get(0).getDayOfWeeks().get(i).getId());
			Assert.assertEquals(p.getDayOfWeeks().get(i).getDayNumber(), savedPushs.get(0).getDayOfWeeks().get(i).getDayNumber());
			Assert.assertEquals(p.getDayOfWeeks().get(i).getName(), savedPushs.get(0).getDayOfWeeks().get(i).getName());
		}
		Assert.assertEquals(p.getKitchenTypes().size(), savedPushs.get(0).getKitchenTypes().size());
		for(int i = 0; i < p.getKitchenTypes().size(); i++) {
			Assert.assertEquals(p.getKitchenTypes().get(i).getId(), savedPushs.get(0).getKitchenTypes().get(i).getId());
			Assert.assertEquals(p.getKitchenTypes().get(i).getName(), savedPushs.get(0).getKitchenTypes().get(i).getName());
		}
	}
	
	/**
	 * Test register multiple push notification.
	 */
	@Test
	public void testRegisterMultiplePushNotification()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		for(int i = 1; i <= 10; i++)
		{
			PushNotification p = getPush(i);
			
			Response response = RestAssured.given()
					.contentType("application/json")
					.body(p)
					.header("Authorization", "Basic " + encodedString)
				.when()
				.post(REGISTER_PUSH_API)
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
		}
		//check if push notification was saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(10, savedPushs.size());
		
		for(int i=1; i <= savedPushs.size();i++) {
			Assert.assertEquals(savedUser.getId(), savedPushs.get(i-1).getUser().getId());
			Assert.assertEquals("TOKEN " + i, savedPushs.get(i-1).getFcmToken());
			Assert.assertEquals("Title " + i, savedPushs.get(i-1).getTitle());
			Assert.assertEquals(1000 * i, savedPushs.get(i-1).getRadius());
			Assert.assertEquals(10.3f + i, savedPushs.get(i-1).getLongitude(), 0.0001);
			Assert.assertEquals(10.2f + i, savedPushs.get(i-1).getLatitude(), 0.0001);
			List<DayOfWeek> dowList = dowRepository.findAll();
			Assert.assertEquals(dowList.size(), savedPushs.get(i-1).getDayOfWeeks().size());
			for(int j = 0; j < dowList.size(); j++)
			{
				Assert.assertEquals(dowList.get(j).getId(), savedPushs.get(i-1).getDayOfWeeks().get(j).getId());
				Assert.assertEquals(dowList.get(j).getDayNumber(), savedPushs.get(i-1).getDayOfWeeks().get(j).getDayNumber());
				Assert.assertEquals(dowList.get(j).getName(), savedPushs.get(i-1).getDayOfWeeks().get(j).getName());
			}
			List<KitchenType> kitchenTypes = kitchenTypeRepository.findAll();
			Assert.assertEquals(kitchenTypes.size(), savedPushs.get(i-1).getKitchenTypes().size());
			for(int j = 0; j < kitchenTypes.size(); j++)
			{
				Assert.assertEquals(kitchenTypes.get(j).getId(), savedPushs.get(i-1).getKitchenTypes().get(j).getId());
				Assert.assertEquals(kitchenTypes.get(j).getName(), savedPushs.get(i-1).getKitchenTypes().get(j).getName());
			}
		}
	}
	
	
	/**
	 * Test register single push notification without kitchen types.
	 */
	@Test
	public void testRegisterSinglePushNotificationWithoutKitchenTypes()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		PushNotification p = getPush(1);
		p.setKitchenTypes(null);
		
		Response response = RestAssured.given()
				.contentType("application/json")
				.body(p)
				.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(200)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("0", resultCode);
		
		//check if push notification was saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(1, savedPushs.size());
		Assert.assertEquals(savedUser.getId(), savedPushs.get(0).getUser().getId());
		Assert.assertEquals("TOKEN 1", savedPushs.get(0).getFcmToken());
		Assert.assertEquals(1000, savedPushs.get(0).getRadius());
		Assert.assertEquals(11.3f, savedPushs.get(0).getLongitude(), 0.0001);
		Assert.assertEquals(11.2f, savedPushs.get(0).getLatitude(), 0.0001);
		Assert.assertEquals(p.getDayOfWeeks().size(), savedPushs.get(0).getDayOfWeeks().size());
		for(int i = 0; i < p.getDayOfWeeks().size(); i++)
		{
			Assert.assertEquals(p.getDayOfWeeks().get(i).getId(), savedPushs.get(0).getDayOfWeeks().get(i).getId());
			Assert.assertEquals(p.getDayOfWeeks().get(i).getDayNumber(), savedPushs.get(0).getDayOfWeeks().get(i).getDayNumber());
			Assert.assertEquals(p.getDayOfWeeks().get(i).getName(), savedPushs.get(0).getDayOfWeeks().get(i).getName());
		}
		Assert.assertEquals(0, savedPushs.get(0).getKitchenTypes().size());
	}
	
	
	/**
	 * Test register multiple push notification some without kitchen types.
	 */
	@Test
	public void testRegisterMultiplePushNotificationSomeWithoutKitchenTypes()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		for(int i = 1; i <= 10; i++)
		{
			System.out.println("i = " + i);
			PushNotification p = getPush(i);
			
			if(i % 2 == 0)
				p.setKitchenTypes(null);

			Response response = RestAssured.given()
					.contentType("application/json")
					.body(p)
					.header("Authorization", "Basic " + encodedString)
				.when()
				.post(REGISTER_PUSH_API)
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
		}
		//check if push notification was saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(10, savedPushs.size());
		
		for(int i=1; i <= savedPushs.size();i++) {
			
			Assert.assertEquals(savedUser.getId(), savedPushs.get(i-1).getUser().getId());
			Assert.assertEquals("TOKEN " + i, savedPushs.get(i-1).getFcmToken());
			Assert.assertEquals("Title " + i, savedPushs.get(i-1).getTitle());
			Assert.assertEquals(1000 * i, savedPushs.get(i-1).getRadius());
			Assert.assertEquals(10.3f + i, savedPushs.get(i-1).getLongitude(), 0.0001);
			Assert.assertEquals(10.2f + i, savedPushs.get(i-1).getLatitude(), 0.0001);
			List<DayOfWeek> dowList = dowRepository.findAll();
			Assert.assertEquals(dowList.size(), savedPushs.get(i-1).getDayOfWeeks().size());
			for(int j = 0; j < dowList.size(); j++)
			{
				Assert.assertEquals(dowList.get(j).getId(), savedPushs.get(i-1).getDayOfWeeks().get(j).getId());
				Assert.assertEquals(dowList.get(j).getDayNumber(), savedPushs.get(i-1).getDayOfWeeks().get(j).getDayNumber());
				Assert.assertEquals(dowList.get(j).getName(), savedPushs.get(i-1).getDayOfWeeks().get(j).getName());
			}

			if(i % 2 == 0)
			{
				Assert.assertEquals(0, savedPushs.get(i-1).getKitchenTypes().size());	
			}
			else
			{	
				List<KitchenType> kitchenTypes = kitchenTypeRepository.findAll();
				Assert.assertEquals(kitchenTypes.size(), savedPushs.get(i-1).getKitchenTypes().size());
				for(int j = 0; j < kitchenTypes.size(); j++)
				{
					Assert.assertEquals(kitchenTypes.get(j).getId(), savedPushs.get(i-1).getKitchenTypes().get(j).getId());
					Assert.assertEquals(kitchenTypes.get(j).getName(), savedPushs.get(i-1).getKitchenTypes().get(j).getName());
				}
			}
		}
	}
	
	
	/**
	 * Test register single working day push notification.
	 */
	@Test
	public void testRegisterSingleWorkingDayPushNotification()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		PushNotification p = getPushSingleDay(1);
		
		Response response = RestAssured.given()
				.contentType("application/json")
				.body(p)
				.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(200)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("0", resultCode);
		
		//check if push notification was saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(1, savedPushs.size());
		Assert.assertEquals(savedUser.getId(), savedPushs.get(0).getUser().getId());
		Assert.assertEquals("TOKEN 1", savedPushs.get(0).getFcmToken());
		Assert.assertEquals(1000, savedPushs.get(0).getRadius());
		Assert.assertEquals(11.3f, savedPushs.get(0).getLongitude(), 0.0001);
		Assert.assertEquals(11.2f, savedPushs.get(0).getLatitude(), 0.0001);
	}
	
	/**
	 * Test register multiple single working day push notification.
	 */
	@Test
	public void testRegisterMultipleSingleWorkingDayPushNotification()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		for(int i = 1; i <= 10; i++)
		{
			PushNotification p = getPushSingleDay(i);
			
			Response response = RestAssured.given()
					.contentType("application/json")
					.body(p)
					.header("Authorization", "Basic " + encodedString)
				.when()
				.post(REGISTER_PUSH_API)
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
			
		}
		//check if push notification was saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(10, savedPushs.size());
		
		for(int i=1; i <= savedPushs.size();i++) {
			Assert.assertEquals(savedUser.getId(), savedPushs.get(i-1).getUser().getId());
			Assert.assertEquals("TOKEN " + i, savedPushs.get(i-1).getFcmToken());
			Assert.assertEquals("Title " + i, savedPushs.get(i-1).getTitle());
			Assert.assertEquals(1000 * i, savedPushs.get(i-1).getRadius());
			Assert.assertEquals(10.3f + i, savedPushs.get(i-1).getLongitude(), 0.0001);
			Assert.assertEquals(10.2f + i, savedPushs.get(i-1).getLatitude(), 0.0001);
		}
	}
	
	
	
	
	
	
	/**
	 * Test unregister single push notification.
	 */
	@Test
	public void testUnregisterSinglePushNotification()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		PushNotification p = getPush(1);
		p.setUser(savedUser);
		PushNotification savedPush = pushRepository.save(p);
		
		Response response_unregister = RestAssured.given()
				.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_PUSH_API + savedPush.getId())
		.then()
			.statusCode(200)
			.extract().response();		
		
		
		//check result code
		String resultCode = response_unregister.as(String.class);
		Assert.assertEquals("0", resultCode);
		
		//check if push notification was deleted for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());

		Assert.assertEquals(0, savedPushs.size());
	}
	
	
	/**
	 * Test unregister multiple push notification.
	 */
	@Test
	public void testUnregisterMultiplePushNotification()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		for(int i = 1; i <= 10; i++)
		{
			PushNotification p = getPush(i);
			p.setUser(savedUser);
			pushRepository.save(p);
		}
		
		//check if all push notification were saved for user
		List<PushNotification> savedPushs = pushRepository.findByUser_id(savedUser.getId());
		Assert.assertEquals(10, savedPushs.size());
		
		for(int i = 1; i <= 10; i++)
		{
			Response response_unregister = RestAssured.given()
					.header("Authorization", "Basic " + encodedString)
				.when()
				.delete(UNREGISTER_PUSH_API + savedPushs.get(i - 1).getId())
			.then()
				.statusCode(200)
				.extract().response();		
			
			
			//check result code
			String resultCode = response_unregister.as(String.class);
			Assert.assertEquals("0", resultCode);
		
			//check if push notification was deleted for user
			List<PushNotification> currentPushs = pushRepository.findByUser_id(savedUser.getId());
			Assert.assertEquals(10 - i, currentPushs.size());
		}
		
		//check if all push notification were deleted for user
		List<PushNotification> remainingPushs = pushRepository.findByUser_id(savedUser.getId());
		Assert.assertEquals(0, remainingPushs.size());
	}

	
	/**
	 * Test get single push notification.
	 */
	@Test
	public void testGetSinglePushNotification() {	
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		PushNotification p = getPush(1);
		p.setUser(savedUser);
		PushNotification savedPush = pushRepository.save(p);
		
		
		List<PushNotification> expectedResult = new ArrayList<PushNotification>();
		expectedResult.add(savedPush);
		
		Response response = RestAssured
				.given()
				.header("Authorization", "Basic " + encodedString)
				.when()
				.get(GET_PUSH_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(1))
			.extract().response(); 
		
		PushNotification[] actualResult = response.as(PushNotification[].class);
		
		// Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.size();i++) {
				Assert.assertEquals(expectedResult.get(i).getId(), actualResult[i].getId());
				Assert.assertEquals("Title " + (i+1), actualResult[i].getTitle());
				Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().size(), actualResult[i].getDayOfWeeks().size());
				for(int j = 0; j < expectedResult.get(i).getDayOfWeeks().size(); j++)
				{
					Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().get(j).getId(), actualResult[i].getDayOfWeeks().get(j).getId());
					Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().get(j).getDayNumber(), actualResult[i].getDayOfWeeks().get(j).getDayNumber());
					Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().get(j).getName(), actualResult[i].getDayOfWeeks().get(j).getName());
				}
				Assert.assertEquals(expectedResult.get(i).getKitchenTypes().size(), actualResult[i].getKitchenTypes().size());
				for(int j = 0; j < expectedResult.get(i).getKitchenTypes().size(); j++)
				{
					Assert.assertEquals(expectedResult.get(i).getKitchenTypes().get(j).getId(), actualResult[i].getKitchenTypes().get(j).getId());
					Assert.assertEquals(expectedResult.get(i).getKitchenTypes().get(j).getName(), actualResult[i].getKitchenTypes().get(j).getName());
				}
		 }
	}
	
	
	/**
	 * Test get multiple push notifications.
	 */
	@Test
	public void testGetMultiplePushNotifications() {	
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		List<PushNotification> expectedResult = new ArrayList<PushNotification>();
		for(int i = 1; i <= 10; i++)
		{
			PushNotification p = getPush(i);
			p.setUser(savedUser);
			PushNotification savedPush = pushRepository.save(p);
			expectedResult.add(savedPush);
		}
		
		Response response = RestAssured
				.given()
				.header("Authorization", "Basic " + encodedString)
				.when()
				.get(GET_PUSH_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(10))
			.extract().response(); 
		
		PushNotification[] actualResult = response.as(PushNotification[].class);
		
		// Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.size();i++) {
				Assert.assertEquals(expectedResult.get(i).getId(), actualResult[i].getId());
				Assert.assertEquals("Title " + (i+1), actualResult[i].getTitle());
				Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().size(), actualResult[i].getDayOfWeeks().size());
				for(int j = 0; j < expectedResult.get(i).getDayOfWeeks().size(); j++)
				{
					Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().get(j).getId(), actualResult[i].getDayOfWeeks().get(j).getId());
					Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().get(j).getDayNumber(), actualResult[i].getDayOfWeeks().get(j).getDayNumber());
					Assert.assertEquals(expectedResult.get(i).getDayOfWeeks().get(j).getName(), actualResult[i].getDayOfWeeks().get(j).getName());
				}
				Assert.assertEquals(expectedResult.get(i).getKitchenTypes().size(), actualResult[i].getKitchenTypes().size());
				for(int j = 0; j < expectedResult.get(i).getKitchenTypes().size(); j++)
				{
					Assert.assertEquals(expectedResult.get(i).getKitchenTypes().get(j).getId(), actualResult[i].getKitchenTypes().get(j).getId());
					Assert.assertEquals(expectedResult.get(i).getKitchenTypes().get(j).getName(), actualResult[i].getKitchenTypes().get(j).getName());
				}
		 }
	}
	
	/**
	 * Gets the push.
	 *
	 * @param i the i
	 * @return the push
	 */
	private PushNotification getPush(int i)
	{
		PushNotification p = new PushNotification();
		//dummy id
		//p.setId(i);
		p.setDayOfWeeks(dowRepository.findAll());
		for(DayOfWeek dow : p.getDayOfWeeks())
		{
			//initialize with default values
			dow.setOffers(new ArrayList<Offer>());
			dow.setPushNotifications(new ArrayList<PushNotification>());
			dow.setTimeSchedules(new ArrayList<TimeSchedule>());
		}
		
		p.setKitchenTypes(kitchenTypeRepository.findAll());
		for(KitchenType kt : p.getKitchenTypes())
		{
			//initialize with default values
			kt.setPushNotifications(new ArrayList<PushNotification>());
		}
		p.setRadius(1000 * i);
		p.setLatitude(10.2f + i);
		p.setLongitude(10.3f + i);
		p.setFcmToken("TOKEN " + i);
		p.setTitle("Title " + i);
		return p;
	}
	
	private PushNotification getPushSingleDay(int i)
	{
		PushNotification p = new PushNotification();
		//dummy id
		//p.setId(i);
		
		DayOfWeek singleDay = new DayOfWeek();
		
		singleDay.setName("Montag");
		
		List<DayOfWeek> dayList = new ArrayList<DayOfWeek>();
		dayList.add(singleDay);
		
		p.setDayOfWeeks(dayList);
		for(DayOfWeek dow : p.getDayOfWeeks())
		{
			//initialize with default values
			dow.setOffers(new ArrayList<Offer>());
			dow.setPushNotifications(new ArrayList<PushNotification>());
			dow.setTimeSchedules(new ArrayList<TimeSchedule>());
		}
		
		p.setKitchenTypes(kitchenTypeRepository.findAll());
		for(KitchenType kt : p.getKitchenTypes())
		{
			//initialize with default values
			kt.setPushNotifications(new ArrayList<PushNotification>());
		}
		p.setRadius(1000 * i);
		p.setLatitude(10.2f + i);
		p.setLongitude(10.3f + i);
		p.setFcmToken("TOKEN " + i);
		p.setTitle("Title " + i);
		return p;
	}
	
	
	/**
	 * Gets user with usertype 'Anbieter'
	 *
	 * @return the user
	 */
	private User getUserWithUserTypeAnbieter()
	{
		User u = new User();
		u.setUsername("admin@admin.com");
		u.setPassword(passwordEncoder.encode("admin"));
		u.setPasswordconfirm("admin");
		u.setUserType(userTypeRepository.findByName("Anbieter"));
		return userRepository.save(u);
	}
	
	/**
	 * Gets user with usertype 'Kunde'
	 *
	 * @return the user
	 */
	private User getUserWithUserTypeKunde()
	{
		User u = new User();
		u.setUsername("admin@admin.com");
		u.setPassword(passwordEncoder.encode("admin"));
		u.setPasswordconfirm("admin");
		u.setUserType(userTypeRepository.findByName("Kunde"));
		return userRepository.save(u);
	}
	

}
