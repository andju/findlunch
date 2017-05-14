package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.App;
import edu.hm.cs.projektstudium.findlunch.webapp.distance.DistanceCalculator;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Account;
import edu.hm.cs.projektstudium.findlunch.webapp.model.AccountType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Bill;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Country;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DonationPerMonth;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.comparison.RestaurantDistanceComparator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CountryRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;

/**
 * Integration-test class for the RestaurantRestController.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class RestaurantRestControllerIT {
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepo;
	
	/** The country repository. */
	@Autowired
	private CountryRepository countryRepo;
	
	/** The kitchentype repository. */
	@Autowired
	private KitchenTypeRepository kitchenTypeRepo;
	
	/** The restauranttype repository. */
	@Autowired
	private RestaurantTypeRepository restaurantTypeRepo;
	
	/** The user repository. */
	@Autowired
	UserRepository userRepository;
	
	/** The user type repository. */
	@Autowired
	UserTypeRepository userTypeRepository;
	
	/** Password encoder **/
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	AccountTypeRepository accountTypeRepository;
	
	@Autowired
	AccountRepository accountRepository;
	
	/** The Constant RESTAURANT_API. */
	private static final String RESTAURANT_API = "/api/restaurants";
	
	/** The server port. */
	@Value("${server.port}")
	private int serverPort;
	
	/** The restaurant name **/
	public static final String RESTAURANT_NAME = "Test restaurant";
	
	/** The restaurant name **/
	public static final String RESTAURANT_CITY = "Testcity";
	
	/** The restaurant name **/
	public static final String RESTAURANT_EMAIL = "email@email.de";

	/** The restaurant name **/
	public static final String RESTAURANT_PHONE = "0138473831";
	
	/** The restaurant name **/
	public static final String RESTAURANT_STREET = "Test street";
	
	/** The restaurant name **/
	public static final String RESTAURANT_STREETNUMBER = "1";
	
	/** The restaurant name **/
	public static final String RESTAURANT_URL = "http://www.test.de";
	
	/** The restaurant name **/
	public static final String RESTAURANT_ZIP = "12345";
	
	public static final String RESTAURANT_UUID = "5b36b3e3-054d-41b9-be3c-19fcece09f";
	  
	/**
	 * Run before each test.
	 */
	@Before
	@Sql({"/schemaIT.sql", "/dataIT.sql"})
	public void setUp() {
		
		RestAssured.baseURI="http://localhost";
		RestAssured.port=serverPort;
	}
	
	
	/**
	 * Test illegal method types.
	 */
	@Test
	public void testIllegalMethodTypes() {	
		JsonPath response = RestAssured.given().when().delete(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().put(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().post(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().patch(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test illegal latitude.
	 */
	@Test
	public void testIllegalLatitude() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", 0)
			.param("longitude", -1)
			.param("latitude", "asdf")
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test illegal longitude.
	 */
	@Test
	public void testIllegalLongitude() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", 0)
			.param("longitude", "asdf")
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test illegal radius.
	 */
	@Test
	public void testIllegalRadius() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", "asdf")
			.param("longitude", -1)
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test empty latitude.
	 */
	@Test
	public void tesLatitudeEmpty() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", 0)
			.param("longitude", -1)
			.param("latitude", "")
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test empty longitude.
	 */
	@Test
	public void testLongitudeEmpty() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", 0)
			.param("longitude", "")
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test empty radius.
	 */
	@Test
	public void testRadiusEmpty() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", "")
			.param("longitude", -1)
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test required parameter missing.
	 */
	@Test
	public void testRequiredParameterMissing() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("radius", 0)
			.param("longitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MissingServletRequestParameterException.class.toString()));
	}
	
	/**
	 * Test no restaurant in database.
	 */
	@Test
	public void testNoRestaurantInDatabase() {			
		RestAssured.given()
			.param("longitude", -1)
			.param("latitude", -1)
			.param("radius", 0)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}	
	
	/**
	 * Test no restaurant within given radius.
	 */
	@Test
	public void testNoRestaurantWithinGivenRadius() {
		
		float consumerLocationLongitude = Float.parseFloat("10.1");
		float consumerLocationLatitude = Float.parseFloat("10.5");
		
		Restaurant restaurant = getRestaurant(Float.parseFloat("48.1"), Float.parseFloat("10.4"), 1);
		
		Restaurant savedRestaurant = restaurantRepo.save(restaurant);
		savedRestaurant.setDistance(DistanceCalculator.calculateDistance(consumerLocationLatitude, consumerLocationLongitude, savedRestaurant.getLocationLatitude(), savedRestaurant.getLocationLongitude()));

		RestAssured.given()
			.param("longitude", consumerLocationLongitude)
			.param("latitude", consumerLocationLatitude)
			 // Make sure the radius is smaller than the distance between the restaurant and the user
			.param("radius", savedRestaurant.getDistance()-100)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
		
	}
	
	/**
	 * Test one restaurant distance equals radius.
	 */
	@Test
	public void testOneRestaurantDistanceEqualsRadius() {
		
		float consumerLocationLongitude = Float.parseFloat("10.1");
		float consumerLocationLatitude = Float.parseFloat("10.5");
		
		Restaurant restaurant = getRestaurant(Float.parseFloat("48.1"), Float.parseFloat("10.4"), 1);
		Restaurant savedRestaurant = restaurantRepo.save(restaurant);
		savedRestaurant.setDistance(DistanceCalculator.calculateDistance(consumerLocationLatitude, consumerLocationLongitude, savedRestaurant.getLocationLatitude(), savedRestaurant.getLocationLongitude()));

		int radius = savedRestaurant.getDistance();
				
		RestAssured.given()
			.param("longitude", consumerLocationLongitude)
			.param("latitude", consumerLocationLatitude)
			.param("radius", radius)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(1))
			.body("[0].id", Matchers.is(savedRestaurant.getId()))
			.body("[0].name", Matchers.is(RESTAURANT_NAME+savedRestaurant.getId()))
			.body("[0].city", Matchers.is(RESTAURANT_CITY))
			.body("[0].country.name", Matchers.is(savedRestaurant.getCountry().getName()))
			.body("[0].email", Matchers.is(RESTAURANT_EMAIL+savedRestaurant.getId()))
			.body("[0].kitchenTypes[0].name", Matchers.is(savedRestaurant.getKitchenTypes().get(0).getName()))
			.body("[0].url", Matchers.is(RESTAURANT_URL+savedRestaurant.getId()))
			.body("[0].street", Matchers.is(RESTAURANT_STREET+savedRestaurant.getId()))
			.body("[0].streetNumber", Matchers.is(RESTAURANT_STREETNUMBER+savedRestaurant.getId()))
			.body("[0].locationLatitude", Matchers.is(savedRestaurant.getLocationLatitude()))
			.body("[0].locationLongitude", Matchers.is(savedRestaurant.getLocationLongitude()))
			.body("[0].distance", Matchers.is(savedRestaurant.getDistance()))
			.body("[0].distance", Matchers.is(radius));

			}
	
	/**
	 * Test five of fifty restaurants within given radius.
	 */
	@Test
	public void testFiveOfFiftyRestaurantsWithinGivenRadius() {
		
		float consumerLocationLongitude = Float.parseFloat("10.1");
		float consumerLocationLatitude = Float.parseFloat("10.5");
		
		List<Restaurant> restaurantsWithinDatabase = getFiveOfFiftyRestaurantsWithinRadius(consumerLocationLongitude, consumerLocationLatitude);
		
		// Sort savedRestaurants by distance (ascending)
		restaurantsWithinDatabase.sort(new RestaurantDistanceComparator());
		
		// set Radius to be the 5th smallest distance to obtain 5 restaurants
		int radius = restaurantsWithinDatabase.get(4).getDistance();

		 Response response = RestAssured.given()
			.param("longitude", consumerLocationLongitude)
			.param("latitude", consumerLocationLatitude)
			.param("radius", radius)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(5))
			.extract().response(); 
		 
		 Restaurant[] expectedResult = restaurantsWithinDatabase.subList(0, 5).toArray(new Restaurant[0]);
		 Restaurant[] actualResult = response.as(Restaurant[].class);
		 
		 // Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.length;i++) {
			 
			 Assert.assertEquals(expectedResult[i].getId(), actualResult[i].getId());
			 Assert.assertEquals(RESTAURANT_NAME+expectedResult[i].getId(), actualResult[i].getName());
			 Assert.assertEquals(RESTAURANT_CITY, actualResult[i].getCity());
			 Assert.assertEquals(RESTAURANT_EMAIL+expectedResult[i].getId(), actualResult[i].getEmail());
			 Assert.assertEquals(RESTAURANT_PHONE+expectedResult[i].getId(), actualResult[i].getPhone());
			 Assert.assertEquals(RESTAURANT_STREET+expectedResult[i].getId(), actualResult[i].getStreet());
			 Assert.assertEquals(RESTAURANT_STREETNUMBER+expectedResult[i].getId(), actualResult[i].getStreetNumber());
			 Assert.assertEquals(RESTAURANT_URL+expectedResult[i].getId(), actualResult[i].getUrl());
			 Assert.assertEquals(RESTAURANT_ZIP, actualResult[i].getZip());
			 // Due to rounding of long/lat, we chose an acceptance delta of 0.0001 to pass the test
			 Assert.assertEquals(expectedResult[i].getLocationLatitude(), actualResult[i].getLocationLatitude(), 0.0001);
			 Assert.assertEquals(expectedResult[i].getLocationLongitude(), actualResult[i].getLocationLongitude(), 0.0001);
			 // Due to rounding from long / lat, an acceptance range of 1 m is chosen to check the distance
			 Assert.assertTrue(actualResult[i].getDistance()-1 <= expectedResult[i].getDistance() && actualResult[i].getDistance()+1 >= expectedResult[i].getDistance());

			 // Check kitchentypes
			 List<KitchenType> expectedKitchenTypes = expectedResult[i].getKitchenTypes();
			 List<KitchenType> actualKitchenTypes = actualResult[i].getKitchenTypes();
			 for(int j=0; j < expectedKitchenTypes.size(); j++) {
				 Assert.assertEquals(expectedKitchenTypes.get(j).getId(), actualKitchenTypes.get(j).getId());
				 Assert.assertEquals(expectedKitchenTypes.get(j).getName(), actualKitchenTypes.get(j).getName());
			 }
			 
			 // Check restaurant type
			 RestaurantType expectedRestaurantType = expectedResult[i].getRestaurantType();
			 RestaurantType actualRestaurantType = actualResult[i].getRestaurantType();
			 Assert.assertEquals(expectedRestaurantType.getId(), actualRestaurantType.getId());
			 Assert.assertEquals(expectedRestaurantType.getName(), actualRestaurantType.getName());
			 
			 // Check country
			 Country expectedCountry = expectedResult[i].getCountry();
			 Country actualCountry = actualResult[i].getCountry();
			 Assert.assertEquals(expectedCountry.getName(), actualCountry.getName());
		 }
		 
		 // Check is sort order is correct (distance ascending)
		 for(int i=1; i < actualResult.length-1;i++) {
			 
			 Assert.assertTrue(actualResult[i].getDistance() < actualResult[i+1].getDistance());
		 }
	}
	
	/**
	 * Test all restaurants within given radius.
	 */
	@Test
	public void testAllRestaurantsWithinGivenRadius() {
		
		float consumerLocationLongitude = Float.parseFloat("10.1");
		float consumerLocationLatitude = Float.parseFloat("10.5");
		int radius = 0;
		
		List<Restaurant> restaurantsWithinDatabase = new ArrayList<Restaurant>();
		float restaurantLocationLatitude = Float.parseFloat("5.1");
		float restaurantLocationLongitude = Float.parseFloat("10.5");
		
		// Save 50 restaurants to database, all of them are within the given radius (radius == distance from first restaurant, only latitude is incremented that all restaurants lay within the diameter of the search. The first 25 restaurants have a decreasing distance to the consumer location, the last 25 an incresing distance to the consumer.)
		for(int i=1; i<=50;i++) {
			
			Restaurant restaurant = getRestaurant(restaurantLocationLatitude, restaurantLocationLongitude, i);
			Restaurant savedRestaurant = restaurantRepo.save(restaurant);
			savedRestaurant.setDistance(DistanceCalculator.calculateDistance(consumerLocationLatitude, consumerLocationLongitude, savedRestaurant.getLocationLatitude(), savedRestaurant.getLocationLongitude()));
			if(i==1) {
				radius = savedRestaurant.getDistance();
			}
			restaurantsWithinDatabase.add(savedRestaurant);
			
			// Generate new latitude values
			restaurantLocationLatitude += 0.2;
		}
		
		// Sort savedRestaurants by distance (ascending)
		restaurantsWithinDatabase.sort(new RestaurantDistanceComparator());

		 Response response = RestAssured.given()
			.param("longitude", consumerLocationLongitude)
			.param("latitude", consumerLocationLatitude)
			.param("radius", radius)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(50))
			.extract().response(); 
		 
		 Restaurant[] expectedResult = restaurantsWithinDatabase.toArray(new Restaurant[0]);
		 Restaurant[] actualResult = response.as(Restaurant[].class);
		 
		 // Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.length;i++) {
			 
			 Assert.assertEquals(expectedResult[i].getId(), actualResult[i].getId());
			 Assert.assertEquals(RESTAURANT_NAME+expectedResult[i].getId(), actualResult[i].getName());
			 Assert.assertEquals(RESTAURANT_CITY, actualResult[i].getCity());
			 Assert.assertEquals(RESTAURANT_EMAIL+expectedResult[i].getId(), actualResult[i].getEmail());
			 Assert.assertEquals(RESTAURANT_PHONE+expectedResult[i].getId(), actualResult[i].getPhone());
			 Assert.assertEquals(RESTAURANT_STREET+expectedResult[i].getId(), actualResult[i].getStreet());
			 Assert.assertEquals(RESTAURANT_STREETNUMBER+expectedResult[i].getId(), actualResult[i].getStreetNumber());
			 Assert.assertEquals(RESTAURANT_URL+expectedResult[i].getId(), actualResult[i].getUrl());
			 Assert.assertEquals(RESTAURANT_ZIP, actualResult[i].getZip());
			 // Due to rounding of long/lat, we chose an acceptance delta of 0.0001 to pass the test
			 Assert.assertEquals(expectedResult[i].getLocationLatitude(), actualResult[i].getLocationLatitude(), 0.0001);
			 Assert.assertEquals(expectedResult[i].getLocationLongitude(), actualResult[i].getLocationLongitude(), 0.0001);			
			 // Due to rounding from long / lat, an acceptance range of 1 m is chosen to check the distance
			 Assert.assertTrue(actualResult[i].getDistance()-1 <= expectedResult[i].getDistance() && actualResult[i].getDistance()+1 >= expectedResult[i].getDistance());

			 // Check kitchentypes
			 List<KitchenType> expectedKitchenTypes = expectedResult[i].getKitchenTypes();
			 List<KitchenType> actualKitchenTypes = actualResult[i].getKitchenTypes();
			 for(int j=0; j < expectedKitchenTypes.size(); j++) {
				 Assert.assertEquals(expectedKitchenTypes.get(j).getId(), actualKitchenTypes.get(j).getId());
				 Assert.assertEquals(expectedKitchenTypes.get(j).getName(), actualKitchenTypes.get(j).getName());
			 }
			 
			 // Check restaurant type
			 RestaurantType expectedRestaurantType = expectedResult[i].getRestaurantType();
			 RestaurantType actualRestaurantType = actualResult[i].getRestaurantType();
			 Assert.assertEquals(expectedRestaurantType.getId(), actualRestaurantType.getId());
			 Assert.assertEquals(expectedRestaurantType.getName(), actualRestaurantType.getName());
			 
			 // Check country
			 Country expectedCountry = expectedResult[i].getCountry();
			 Country actualCountry = actualResult[i].getCountry();
			 Assert.assertEquals(expectedCountry.getName(), actualCountry.getName());
		 }
		 
		 // Check is sort order is correct (distance ascending)
		 for(int i=1; i < actualResult.length-1;i++) {
			 
			 Assert.assertTrue(actualResult[i].getDistance() < actualResult[i+1].getDistance());
		 }
		
	}
	
	// ------------- Checks for authorized restaurant calls --------------------------
	
	/**
	 * Test invalid authorization header.
	 */
	@Test
	public void testInvalidAuthorizationHeader()
	{
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic invalid")
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();
		
		Assert.assertEquals("Invalid basic authentication token", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));

	}
	
	/**
	 * Test if a user of invalid usertype (Anbieter) tries to login.
	 */
	@Test
	public void testInvalidUserType()
	{
		
		User user = getUserWithUserTypeAnbieter();
		userRepository.save(user);
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();
		
		Assert.assertEquals("UserDetailsService returned null, which is an interface contract violation", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));

	}
	
	/**
	 * Test with user not present within database.
	 */
	@Test
	public void testUserNotExisting()
	{
		
		User user = getUserWithUserTypeAnbieter();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();
		
		Assert.assertEquals("UserDetailsService returned null, which is an interface contract violation", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test with invalid password for user.
	 */
	@Test
	public void testUserInvalidPassword()
	{
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":invalidPass";
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();
		
		Assert.assertEquals("Bad credentials", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test illegal method types.
	 */
	@Test
	public void testIllegalMethodTypesAuthorizedCall() {	
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().delete(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().put(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().post(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().patch(RESTAURANT_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test illegal latitude.
	 */
	@Test
	public void testIllegalLatitudeAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", 0)
			.param("longitude", -1)
			.param("latitude", "asdf")
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test illegal longitude.
	 */
	@Test
	public void testIllegalLongitudeAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", 0)
			.param("longitude", "asdf")
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test illegal radius.
	 */
	@Test
	public void testIllegalRadiusAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", "asdf")
			.param("longitude", -1)
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test empty latitude.
	 */
	@Test
	public void testLatitudeEmptyAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", 0)
			.param("longitude", -1)
			.param("latitude", "")
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test empty longitude.
	 */
	@Test
	public void testLongitudeEmptyAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", 0)
			.param("longitude", "")
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test empty radius.
	 */
	@Test
	public void testRadiusEmptyAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", "")
			.param("longitude", -1)
			.param("latitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test required parameter missing.
	 */
	@Test
	public void testRequiredParameterMissingAuthorizedCall() {
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.param("radius", 0)
			.param("longitude", -1)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MissingServletRequestParameterException.class.toString()));
	}
	
	/**
	 * Test with valid user and no favorites set. (5 of 50 restaurants within the database are within radius)
	 */
	@Test
	public void testValidUserNoFavoriteSetFiveOfFiftyRestaurantsWithinRadius()
	{
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		float consumerLocationLongitude = Float.parseFloat("10.1");
		float consumerLocationLatitude = Float.parseFloat("10.5");
		
		List<Restaurant> restaurantsWithinDatabase = getFiveOfFiftyRestaurantsWithinRadius(consumerLocationLongitude, consumerLocationLatitude);
		
		// Sort savedRestaurants by distance (ascending)
		restaurantsWithinDatabase.sort(new RestaurantDistanceComparator());
		
		// set Radius to be the 5th smallest distance to obtain 5 restaurants
		int radius = restaurantsWithinDatabase.get(4).getDistance();

		 Response response = RestAssured.given()
			.param("longitude", consumerLocationLongitude)
			.param("latitude", consumerLocationLatitude)
			.param("radius", radius)
			.header("Authorization", "Basic " + encodedString)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(5))
			.extract().response(); 
		 
		 Restaurant[] expectedResult = restaurantsWithinDatabase.subList(0, 5).toArray(new Restaurant[0]);
		 Restaurant[] actualResult = response.as(Restaurant[].class);
		 
		 // Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.length;i++) {
			 
			 Assert.assertEquals(expectedResult[i].getId(), actualResult[i].getId());
			 Assert.assertEquals(RESTAURANT_NAME+expectedResult[i].getId(), actualResult[i].getName());
			 Assert.assertEquals(RESTAURANT_CITY, actualResult[i].getCity());
			 Assert.assertEquals(RESTAURANT_EMAIL+expectedResult[i].getId(), actualResult[i].getEmail());
			 Assert.assertEquals(RESTAURANT_PHONE+expectedResult[i].getId(), actualResult[i].getPhone());
			 Assert.assertEquals(RESTAURANT_STREET+expectedResult[i].getId(), actualResult[i].getStreet());
			 Assert.assertEquals(RESTAURANT_STREETNUMBER+expectedResult[i].getId(), actualResult[i].getStreetNumber());
			 Assert.assertEquals(RESTAURANT_URL+expectedResult[i].getId(), actualResult[i].getUrl());
			 Assert.assertEquals(RESTAURANT_ZIP, actualResult[i].getZip());
			 // Due to rounding of long/lat, we chose an acceptance delta of 0.0001 to pass the test
			 Assert.assertEquals(expectedResult[i].getLocationLatitude(), actualResult[i].getLocationLatitude(), 0.0001);
			 Assert.assertEquals(expectedResult[i].getLocationLongitude(), actualResult[i].getLocationLongitude(), 0.0001);
			 // Due to rounding from long / lat, an acceptance range of 1 m is chosen to check the distance
			 Assert.assertTrue(actualResult[i].getDistance()-1 <= expectedResult[i].getDistance() && actualResult[i].getDistance()+1 >= expectedResult[i].getDistance());

			 // Check kitchentypes
			 List<KitchenType> expectedKitchenTypes = expectedResult[i].getKitchenTypes();
			 List<KitchenType> actualKitchenTypes = actualResult[i].getKitchenTypes();
			 for(int j=0; j < expectedKitchenTypes.size(); j++) {
				 Assert.assertEquals(expectedKitchenTypes.get(j).getId(), actualKitchenTypes.get(j).getId());
				 Assert.assertEquals(expectedKitchenTypes.get(j).getName(), actualKitchenTypes.get(j).getName());
			 }
			 
			 // Check restaurant type
			 RestaurantType expectedRestaurantType = expectedResult[i].getRestaurantType();
			 RestaurantType actualRestaurantType = actualResult[i].getRestaurantType();
			 Assert.assertEquals(expectedRestaurantType.getId(), actualRestaurantType.getId());
			 Assert.assertEquals(expectedRestaurantType.getName(), actualRestaurantType.getName());
			 
			 // Check country
			 Country expectedCountry = expectedResult[i].getCountry();
			 Country actualCountry = actualResult[i].getCountry();
			 Assert.assertEquals(expectedCountry.getName(), actualCountry.getName());
			 // Check favorites
			 Assert.assertEquals(false, actualResult[i].isFavorite());
		 }
		 
		 // Check is sort order is correct (distance ascending)
		 for(int i=1; i < actualResult.length-1;i++) {
			 
			 Assert.assertTrue(actualResult[i].getDistance() < actualResult[i+1].getDistance());
		 }
	}
	
	/**
	 * Test with valid user and three favorites set. (5 of 50 restaurants within the database are within radius)
	 */
	@Test
	public void testValidUserThreeFavoriteSetFiveOfFiftyRestaurantsWithinRadius()
	{
		float consumerLocationLongitude = Float.parseFloat("10.1");
		float consumerLocationLatitude = Float.parseFloat("10.5");
		// The second, the fourth and the fifth restaurant based on the distance should be a favorite of the user
		HashSet<Integer> restaurantIndexToFavorite = new HashSet<Integer>();
		restaurantIndexToFavorite.add(1);
		restaurantIndexToFavorite.add(3);
		restaurantIndexToFavorite.add(4);

		
		List<Restaurant> restaurantsWithinDatabase = getFiveOfFiftyRestaurantsWithinRadius(consumerLocationLongitude, consumerLocationLatitude);
		// Sort savedRestaurants by distance (ascending)
		restaurantsWithinDatabase.sort(new RestaurantDistanceComparator());
		
		User user = getUserWithUserTypeKunde();
		for(int i : restaurantIndexToFavorite) {
			user.getFavorites().add(restaurantsWithinDatabase.get(i));
		}
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);

		// set Radius to be the 5th smallest distance to obtain 5 restaurants
		int radius = restaurantsWithinDatabase.get(4).getDistance();

		 Response response = RestAssured.given()
			.param("longitude", consumerLocationLongitude)
			.param("latitude", consumerLocationLatitude)
			.param("radius", radius)
			.header("Authorization", "Basic " + encodedString)
		.when()
			.get(RESTAURANT_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(5))
			.extract().response(); 
		 
		 Restaurant[] expectedResult = restaurantsWithinDatabase.subList(0, 5).toArray(new Restaurant[0]);
		 Restaurant[] actualResult = response.as(Restaurant[].class);
		 
		 // Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.length;i++) {
			 
			 Assert.assertEquals(expectedResult[i].getId(), actualResult[i].getId());
			 Assert.assertEquals(RESTAURANT_NAME+expectedResult[i].getId(), actualResult[i].getName());
			 Assert.assertEquals(RESTAURANT_CITY, actualResult[i].getCity());
			 Assert.assertEquals(RESTAURANT_EMAIL+expectedResult[i].getId(), actualResult[i].getEmail());
			 Assert.assertEquals(RESTAURANT_PHONE+expectedResult[i].getId(), actualResult[i].getPhone());
			 Assert.assertEquals(RESTAURANT_STREET+expectedResult[i].getId(), actualResult[i].getStreet());
			 Assert.assertEquals(RESTAURANT_STREETNUMBER+expectedResult[i].getId(), actualResult[i].getStreetNumber());
			 Assert.assertEquals(RESTAURANT_URL+expectedResult[i].getId(), actualResult[i].getUrl());
			 Assert.assertEquals(RESTAURANT_ZIP, actualResult[i].getZip());
			 // Due to rounding of long/lat, we chose an acceptance delta of 0.0001 to pass the test
			 Assert.assertEquals(expectedResult[i].getLocationLatitude(), actualResult[i].getLocationLatitude(), 0.0001);
			 Assert.assertEquals(expectedResult[i].getLocationLongitude(), actualResult[i].getLocationLongitude(), 0.0001);
			 // Due to rounding from long / lat, an acceptance range of 1 m is chosen to check the distance
			 Assert.assertTrue(actualResult[i].getDistance()-1 <= expectedResult[i].getDistance() && actualResult[i].getDistance()+1 >= expectedResult[i].getDistance());

			 // Check kitchentypes
			 List<KitchenType> expectedKitchenTypes = expectedResult[i].getKitchenTypes();
			 List<KitchenType> actualKitchenTypes = actualResult[i].getKitchenTypes();
			 for(int j=0; j < expectedKitchenTypes.size(); j++) {
				 Assert.assertEquals(expectedKitchenTypes.get(j).getId(), actualKitchenTypes.get(j).getId());
				 Assert.assertEquals(expectedKitchenTypes.get(j).getName(), actualKitchenTypes.get(j).getName());
			 }
			 
			 // Check restaurant type
			 RestaurantType expectedRestaurantType = expectedResult[i].getRestaurantType();
			 RestaurantType actualRestaurantType = actualResult[i].getRestaurantType();
			 Assert.assertEquals(expectedRestaurantType.getId(), actualRestaurantType.getId());
			 Assert.assertEquals(expectedRestaurantType.getName(), actualRestaurantType.getName());
			 
			 // Check country
			 Country expectedCountry = expectedResult[i].getCountry();
			 Country actualCountry = actualResult[i].getCountry();
			 Assert.assertEquals(expectedCountry.getName(), actualCountry.getName());
			 // Check favorites
			 if(restaurantIndexToFavorite.contains(i)) {
				 Assert.assertEquals(true, actualResult[i].isFavorite());
			 }
			 else {
				 Assert.assertEquals(false, actualResult[i].isFavorite());
			 }
		 }
		 
		 // Check is sort order is correct (distance ascending)
		 for(int i=1; i < actualResult.length-1;i++) {
			 
			 Assert.assertTrue(actualResult[i].getDistance() < actualResult[i+1].getDistance());
		 }
	}
	
	/**
	 * Gets the restaurant.
	 *
	 * @param locationLatitude the location latitude
	 * @param locationLongitude the location longitude
	 * @param i the i
	 * @return the restaurant
	 */
	private Restaurant getRestaurant(float locationLatitude, float locationLongitude, int i) {
		Restaurant r = new Restaurant();
		//dummy id
		r.setId(i);
		r.setName(RESTAURANT_NAME + i);
		r.setCustomerId(i);
		r.setCity(RESTAURANT_CITY);
		r.setCountry(countryRepo.findAll().get(0));
		r.setEmail(RESTAURANT_EMAIL + i);
		r.setKitchenTypes(kitchenTypeRepo.findAll());
		r.setPhone(RESTAURANT_PHONE + i);
		r.setRestaurantType(restaurantTypeRepo.findAll().get(0));
		r.setStreet(RESTAURANT_STREET + i);
		r.setStreetNumber(RESTAURANT_STREETNUMBER + i);
		r.setUrl(RESTAURANT_URL + i);
		r.setZip(RESTAURANT_ZIP);
		r.setRestaurantUuid(RESTAURANT_UUID +i);
		r.setCustomerId(i);
		try {
			r.setQrUuid(createQRCode(r.getRestaurantUuid()));
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		r.setLocationLatitude(locationLatitude);
		r.setLocationLongitude(locationLongitude);
		r.setTimeSchedules(new ArrayList<TimeSchedule>());
		r.setReservation(new ArrayList<Reservation>());
		r.setBills(new ArrayList<Bill>());
		r.setDonations(new ArrayList<DonationPerMonth>());
		r.setRestaurantPoints(new ArrayList<Points>());
		return r;
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
		AccountType accountType = new AccountType();
		accountType.setName("Kundenkonto");
		accountTypeRepository.save(accountType);
		
		Account account = new Account();
		account.setAccountNumber(2);
		account.setAccountType(accountType);
		accountRepository.save(account);
		u.setAccount(account);
		
		return u;
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
		u.setFavorites(new ArrayList<Restaurant>());
		u.setUserType(userTypeRepository.findByName("Kunde"));
		return u;
	}
	
	/**
	 * Gets a list of restaurants with a size of 50, from which 5 are in the given radius.
	 * 
	 * @return restaurant list
	 */
	private List<Restaurant> getFiveOfFiftyRestaurantsWithinRadius(float consumerLocationLongitude, float consumerLocationLatitude) {
		
		List<Restaurant> restaurantsWithinDatabase = new ArrayList<Restaurant>();
		float restaurantLocationLatitude = Float.parseFloat("48.1");
		float restaurantLocationLongitude = Float.parseFloat("10.4");
		
		// Save 50 restaurants to database
		for(int i=1; i<=50;i++) {
			
			Restaurant restaurant = getRestaurant(restaurantLocationLatitude, restaurantLocationLongitude, i);
			Restaurant savedRestaurant = restaurantRepo.save(restaurant);
			savedRestaurant.setDistance(DistanceCalculator.calculateDistance(consumerLocationLatitude, consumerLocationLongitude, savedRestaurant.getLocationLatitude(), savedRestaurant.getLocationLongitude()));
			restaurantsWithinDatabase.add(savedRestaurant);
			
			// Generate new longitude / latitude values
			restaurantLocationLatitude += 1.2;
			restaurantLocationLongitude += 1.2;
		}
		
		return restaurantsWithinDatabase;
		
	}
	
	/**
	 * Create a new QR-Code
	 * @param qrCodeData Data for the QR-Code
	 * @return Image in Byte
	 * @throws WriterException
	 * @throws IOException
	 */
	private byte[] createQRCode(String qrCodeData) throws WriterException, IOException{
		File dir = new File("QRCodes");
		if(!dir.exists()){
			dir.mkdir();
		}
		 
		String filePath = "QRCodes/"+qrCodeData+".png";
		String charset = "UTF-8"; // or "ISO-8859-1"
		Map<EncodeHintType, Object> hintMap = new HashMap<EncodeHintType, Object>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		
		//create the QR-Code and safe it
		String information = new String(qrCodeData.getBytes(charset), charset);
		BitMatrix matrix = new MultiFormatWriter().encode(information, BarcodeFormat.QR_CODE, 250, 250, hintMap);
		MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
		
		//convert to byte
		BufferedImage bm = ImageIO.read(new File(filePath));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bm, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		File file = new File(filePath);
		file.delete();
		
		return imageInByte;
	}
	
}
