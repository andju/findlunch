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
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;



/*

Tests schreiben JEDES Mal Daten aus dataIT.sql in findlunchIT DB per HibernateJPA:
country, day_of_week, kitchen_type, restaurant_type, user_type


Methoden schreiben extra (auch jedes Mal obwohl nur einzel Tests ausgewählt):
Kunde,Anbieter,PushMessage...


Einzel Push ans Handy:


TODO: Performance Test größre anzahl ans handy schicken,  
-Baut PushMessage


testGetSinglePushNotification ok (baut nur Nachricht)






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
public class PushNotificationRestControllerIT {

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
	 * Test illegal method types for register.
	 */
	@Test
	public void testIllegalMethodTypesForRegister() {	
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		DailyPushNotificationData p = getPush(1);
		p.setUser(user);
		pushRepository.save(p);
		
		JsonPath response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().delete(REGISTER_PUSH_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).get(REGISTER_PUSH_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'GET' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).put(REGISTER_PUSH_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).patch(REGISTER_PUSH_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	

	/**
	 * Test illegal method types for unregister.
	 */
	@Test
	public void testIllegalMethodTypesForUnregister() {	

		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		DailyPushNotificationData p = getPush(1);
		p.setUser(user);
		DailyPushNotificationData savedPush = pushRepository.save(p);

		
		JsonPath response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().put(UNREGISTER_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).get(UNREGISTER_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'GET' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).post(UNREGISTER_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).patch(UNREGISTER_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test illegal method types for get.
	 */
	@Test
	public void testIllegalMethodTypesForGet() {	

		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		DailyPushNotificationData p = getPush(1);
		p.setUser(user);
		DailyPushNotificationData savedPush = pushRepository.save(p);

		
		JsonPath response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().put(GET_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).delete(GET_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).post(GET_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).patch(GET_PUSH_API + savedPush.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test missing authorization for register.
	 */
	@Test
	public void testMissingAuthorizationForRegister()
	{
		DailyPushNotificationData p = getPush(1);
		
		JsonPath response = RestAssured
		.given()
			.contentType("application/json")
			.body(p)
		.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(401).extract().jsonPath();
		
		Assert.assertEquals("Full authentication is required to access this resource", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test missing authorization for unregister.
	 */
	@Test
	public void testMissingAuthorizationForUnregister()
	{	
		JsonPath response = RestAssured
				.given()
				.when()
					.delete(UNREGISTER_PUSH_API + 1)
				.then()
					.statusCode(401).extract().jsonPath();
				
				Assert.assertEquals("Full authentication is required to access this resource", response.getString("message"));
				Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test missing authorization for get.
	 */
	@Test
	public void testMissingAuthorizationForGet()
	{	
		JsonPath response = RestAssured
				.given()
				.when()
					.get(GET_PUSH_API)
				.then()
					.statusCode(401).extract().jsonPath();
				
				Assert.assertEquals("Full authentication is required to access this resource", response.getString("message"));
				Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	
	/**
	 * Test missing parameter for unregister.
	 */
	@Test
	public void testMissingParameterForUnregister() {	
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_PUSH_API + "")
		.then()
			.statusCode(405).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.HttpRequestMethodNotSupportedException", response.getString("exception"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test wrong user type for register.
	 */
	@Test
	public void testWrongUserTypeForRegister() {	
		DailyPushNotificationData p = getPush(1);
		
		User user = getUserWithUserTypeAnbieter();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
				.contentType("application/json")
				.body(p)
				.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();

		Assert.assertEquals("UserDetailsService returned null, which is an interface contract violation", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	

	/**
	 * Test wrong user type for unregister.
	 */
	@Test
	public void testWrongUserTypeForUnregister() {			
		User user = getUserWithUserTypeAnbieter();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_PUSH_API + 1)
		.then()
			.statusCode(401)
			.extract().jsonPath();

		Assert.assertEquals("UserDetailsService returned null, which is an interface contract violation", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test wrong user type for get.
	 */
	@Test
	public void testWrongUserTypeForGet() {			
		User user = getUserWithUserTypeAnbieter();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.get(GET_PUSH_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();

		Assert.assertEquals("UserDetailsService returned null, which is an interface contract violation", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	
	/**
	 * Test missing body for register.
	 */
	@Test
	public void testMissingBodyForRegister() {	
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.contentType("application/json")
		.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(400).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.http.converter.HttpMessageNotReadableException", response.getString("exception"));
		Assert.assertEquals("Bad Request", response.getString("error"));
	}
	
	/**
	 * Test wrong body for register.
	 */
	@Test
	public void testWrongBodyForRegister() {	
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.contentType("application/json")
		.header("Authorization", "Basic " + encodedString)
		.body("test")
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(400).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.http.converter.HttpMessageNotReadableException", response.getString("exception"));
		Assert.assertEquals("Bad Request", response.getString("error"));
	}
	
	

	/**
	 * Test illegal push notification id for unregister.
	 */
	@Test
	public void testIllegalPushNotificationIdForUnregister() {	
		DailyPushNotificationData p = getPush(1);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		p.setUser(user);
		pushRepository.save(p);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_PUSH_API + "FALSCH")
		.then()
			.statusCode(400).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException", response.getString("exception"));
		Assert.assertEquals("Bad Request", response.getString("error"));
	}
	

	/**
	 * Test invalid push notification id for unregister.
	 */
	@Test
	public void testInvalidPushNotificationIdForUnregister() {	
		
		DailyPushNotificationData p = getPush(1);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		p.setUser(user);
		DailyPushNotificationData savedPush = pushRepository.save(p);
		
		Response response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_PUSH_API + (savedPush.getId() + 1))
		.then()
			.statusCode(409)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("3", resultCode);
	}
	
	
	/**
	 * Test push notification does not belong to authenticated user for unregister.
	 */
	@Test
	public void testPushNotificationDoesNotBelongToAuthenticatedUserForUnregister() {	
		
		//DifferentUser
		User different = new User();
		different.setUsername("different@admin.com");
		different.setPassword(passwordEncoder.encode("different"));
		different.setUserType(userTypeRepository.findByName("Kunde"));
		User differentUserSaved = userRepository.save(different);
		
		
		DailyPushNotificationData p = getPush(1);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		p.setUser(differentUserSaved);
		DailyPushNotificationData savedPush = pushRepository.save(p);
		
		Response response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_PUSH_API + (savedPush.getId()))
		.then()
			.statusCode(409)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("3", resultCode);
	}
	
	
	/**
	 * Test register push notification day of weeks null.
	 */
	@Test
	public void testRegisterPushNotificationDayOfWeeksNull()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		DailyPushNotificationData p = getPush(1);
		p.setDayOfWeeks(null);
		
		Response response = RestAssured.given()
				.contentType("application/json")
				.body(p)
				.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("4", resultCode);
	}
	
	
	/**
	 * Test register push notification day of weeks empty.
	 */
	@Test
	public void testRegisterPushNotificationDayOfWeeksEmpty()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		DailyPushNotificationData p = getPush(1);
		p.setDayOfWeeks(new ArrayList<DayOfWeek>());
		
		Response response = RestAssured.given()
				.contentType("application/json")
				.body(p)
				.header("Authorization", "Basic " + encodedString)
			.when()
			.post(REGISTER_PUSH_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("4", resultCode);
	}
	
	

	
	

	
	
	
	
	
	
	
	
	
	
	/**
	 * Test register single push notification.
	 */
	@Test
	public void testRegisterSinglePushNotification()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		User savedUser = userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		DailyPushNotificationData p = getPush(1);
		
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
		Assert.assertEquals(p.getKitchenTypes().size(), savedPushs.get(0).getKitchenTypes().size());
		for(int i = 0; i < p.getKitchenTypes().size(); i++)
		{
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
			DailyPushNotificationData p = getPush(i);
			
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
		
		DailyPushNotificationData p = getPush(1);
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
			DailyPushNotificationData p = getPush(i);
			
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
		
		DailyPushNotificationData p = getPushSingleDay(1);
		
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
			DailyPushNotificationData p = getPushSingleDay(i);
			
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
		
		DailyPushNotificationData p = getPush(1);
		p.setUser(savedUser);
		DailyPushNotificationData savedPush = pushRepository.save(p);
		
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
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());

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
			DailyPushNotificationData p = getPush(i);
			p.setUser(savedUser);
			pushRepository.save(p);
		}
		
		//check if all push notification were saved for user
		List<DailyPushNotificationData> savedPushs = pushRepository.findByUser_id(savedUser.getId());
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
			List<DailyPushNotificationData> currentPushs = pushRepository.findByUser_id(savedUser.getId());
			Assert.assertEquals(10 - i, currentPushs.size());
		}
		
		//check if all push notification were deleted for user
		List<DailyPushNotificationData> remainingPushs = pushRepository.findByUser_id(savedUser.getId());
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
		
		DailyPushNotificationData p = getPush(1);
		p.setUser(savedUser);
		DailyPushNotificationData savedPush = pushRepository.save(p);
		
		
		List<DailyPushNotificationData> expectedResult = new ArrayList<DailyPushNotificationData>();
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
		
		DailyPushNotificationData[] actualResult = response.as(DailyPushNotificationData[].class);
		
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
		
		List<DailyPushNotificationData> expectedResult = new ArrayList<DailyPushNotificationData>();
		for(int i = 1; i <= 10; i++)
		{
			DailyPushNotificationData p = getPush(i);
			p.setUser(savedUser);
			DailyPushNotificationData savedPush = pushRepository.save(p);
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
		
		DailyPushNotificationData[] actualResult = response.as(DailyPushNotificationData[].class);
		
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
	private DailyPushNotificationData getPush(int i)
	{
		DailyPushNotificationData p = new DailyPushNotificationData();
		//dummy id
		//p.setId(i);
		p.setDayOfWeeks(dowRepository.findAll());
		for(DayOfWeek dow : p.getDayOfWeeks())
		{
			//initialize with default values
			dow.setOffers(new ArrayList<Offer>());
			dow.setPushNotifications(new ArrayList<DailyPushNotificationData>());
			dow.setTimeSchedules(new ArrayList<TimeSchedule>());
		}
		
		p.setKitchenTypes(kitchenTypeRepository.findAll());
		for(KitchenType kt : p.getKitchenTypes())
		{
			//initialize with default values
			kt.setPushNotifications(new ArrayList<DailyPushNotificationData>());
		}
		p.setRadius(1000 * i);
		p.setLatitude(10.2f + i);
		p.setLongitude(10.3f + i);
		p.setFcmToken("TOKEN " + i);
		p.setTitle("Title " + i);
		return p;
	}
	
	private DailyPushNotificationData getPushSingleDay(int i)
	{
		DailyPushNotificationData p = new DailyPushNotificationData();
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
			dow.setPushNotifications(new ArrayList<DailyPushNotificationData>());
			dow.setTimeSchedules(new ArrayList<TimeSchedule>());
		}
		
		p.setKitchenTypes(kitchenTypeRepository.findAll());
		for(KitchenType kt : p.getKitchenTypes())
		{
			//initialize with default values
			kt.setPushNotifications(new ArrayList<DailyPushNotificationData>());
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
