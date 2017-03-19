package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.App;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CountryRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;


/**
 * The Class FavoritesRestControllerIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class FavoritesRestControllerIT {

	/** The country repo. */
	@Autowired
	private CountryRepository countryRepo;
	
	/** The restaurant type repo. */
	@Autowired
	private RestaurantTypeRepository restaurantTypeRepo;
	
	/** The restaurant repo. */
	@Autowired
	private RestaurantRepository restaurantRepo;
	
	/** The kitchen type repo. */
	@Autowired
	private KitchenTypeRepository kitchenTypeRepo;
	
	/** The user repository. */
	@Autowired
	UserRepository userRepository;
	
	/** The user type repository. */
	@Autowired
	UserTypeRepository userTypeRepository;
	
	/** The password encoder. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	/** The Constant REGISTER_FAVORITE_API. */
	private static final String REGISTER_FAVORITE_API = "/api/register_favorite/";
	
	/** The Constant UNREGISTER_FAVORITE_API. */
	private static final String UNREGISTER_FAVORITE_API = "/api/unregister_favorite/";
	
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
		RestAssured.port=serverPort;
		
	}

	/**
	 * Test missing authorization for register.
	 */
	@Test
	public void testMissingAuthorizationForRegister()
	{
		JsonPath response = RestAssured
		.given()
		.when()
			.put(REGISTER_FAVORITE_API + 1)
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
			.delete(UNREGISTER_FAVORITE_API + 1)
		.then()
			.statusCode(401).extract().jsonPath();
		
		Assert.assertEquals("Full authentication is required to access this resource", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	
	/**
	 * Test missing parameter for register.
	 */
	@Test
	public void testMissingParameterForRegister() {	
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API + "")
		.then()
			.statusCode(405).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.HttpRequestMethodNotSupportedException", response.getString("exception"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
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
			.delete(UNREGISTER_FAVORITE_API + "")
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
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeAnbieter();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API + savedRestaurant.getId())
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
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeAnbieter();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API + savedRestaurant.getId())
		.then()
			.statusCode(401)
			.extract().jsonPath();

		Assert.assertEquals("UserDetailsService returned null, which is an interface contract violation", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test wrong authorization for register.
	 */
	@Test
	public void testWrongAuthorizationForRegister() {	
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":invalidPassword";
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API + savedRestaurant.getId())
		.then()
			.statusCode(401)
			.extract().jsonPath();
	
		Assert.assertEquals("Bad credentials", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test wrong authorization for unregister.
	 */
	@Test
	public void testWrongAuthorizationForUnregister() {	
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		// Since we need the password within the header as cleartext, it is extracted from the passwordconfirm field
		String authString = user.getUsername() + ":invalidPassword";
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API + savedRestaurant.getId())
		.then()
			.statusCode(401)
			.extract().jsonPath();

		Assert.assertEquals("Bad credentials", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	
	/**
	 * Test invalid restaurant id for register.
	 */
	@Test
	public void testInvalidRestaurantIdForRegister() {	
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		Response response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API + (savedRestaurant.getId() + 1))
		.then()
			.statusCode(409)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("3", resultCode);
	}
	
	/**
	 * Test illegal restaurant id for register.
	 */
	@Test
	public void testIllegalRestaurantIdForRegister() {	
		restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API + "FALSCH")
		.then()
			.statusCode(400).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException", response.getString("exception"));
		Assert.assertEquals("Bad Request", response.getString("error"));
	}
	
	/**
	 * Test illegal restaurant id for unregister.
	 */
	@Test
	public void testIllegalRestaurantIdForUnregister() {	
		restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API + "FALSCH")
		.then()
			.statusCode(400).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException", response.getString("exception"));
		Assert.assertEquals("Bad Request", response.getString("error"));
	}
	
	/**
	 * Test invalid restaurant id for unregister.
	 */
	@Test
	public void testInvalidRestaurantIdForUnregister() {	
		
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		Response response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API + (savedRestaurant.getId() + 1))
		.then()
			.statusCode(409)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("3", resultCode);
	}
	
	/**
	 * Test restaurant not in favorites for unregister.
	 */
	@Test
	public void testRestaurantNotInFavoritesForUnregister() {	
		
		List<Restaurant> restaurantsWithinDB = new ArrayList<Restaurant>();
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		
		//add 2 favorites
		user.setFavorites(new ArrayList<Restaurant>());
		for(int i = 1; i <= 2; i++)
		{
			Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(i));
			user.getFavorites().add(savedRestaurant);
			restaurantsWithinDB.add(savedRestaurant);
		}
		userRepository.save(user);
		
		//add a restaurant, that is not a users favorite
		Restaurant noFavoriteRestaurant = restaurantRepo.save(getRestaurant(3));
		
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		Response response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API + (noFavoriteRestaurant.getId()))
		.then()
			.statusCode(200)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("0", resultCode);
	}
	

	/**
	 * Test no restaurant id for register.
	 */
	@Test
	public void testNoRestaurantIdForRegister() {	
		restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API)
		.then()
			.statusCode(405).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.HttpRequestMethodNotSupportedException", response.getString("exception"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test no restaurant id for unregister.
	 */
	@Test
	public void testNoRestaurantIdForUnregister() {	
		restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API)
		.then()
			.statusCode(405).extract().jsonPath();
		
		Assert.assertEquals("org.springframework.web.HttpRequestMethodNotSupportedException", response.getString("exception"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test illegal method types for register.
	 */
	@Test
	public void testIllegalMethodTypesForRegister() {	
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().delete(REGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).get(REGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'GET' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).post(REGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).patch(REGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	

	/**
	 * Test illegal method types for unregister.
	 */
	@Test
	public void testIllegalMethodTypesForUnregister() {	
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		JsonPath response = RestAssured.given().header("Authorization", "Basic " + encodedString).when().put(UNREGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).get(UNREGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'GET' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).post(UNREGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().header("Authorization", "Basic " + encodedString).patch(UNREGISTER_FAVORITE_API + savedRestaurant.getId()).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test add single favorite.
	 */
	@Test
	public void testAddSingleFavorite() {	
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		Response response = RestAssured.given()
		.header("Authorization", "Basic " + encodedString)
			.when()
			.put(REGISTER_FAVORITE_API + savedRestaurant.getId())
		.then()
			.statusCode(200)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("0", resultCode);
		
		//check if favorite was saved for user
		User u = userRepository.findByUsername(user.getUsername());
		Assert.assertEquals(1, u.getFavorites().size());
		Assert.assertEquals(savedRestaurant.getId(), u.getFavorites().get(0).getId());
		
	}
	
	/**
	 * Test add same favorite multiple times.
	 */
	@Test
	public void testAddSameFavoriteMultipleTimes() {	
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		for(int i=0; i < 10; i++)
		{
			RestAssured
			.registerParser("text/plain", Parser.TEXT);
			
			Response response = RestAssured.given()
				.header("Authorization", "Basic " + encodedString)
				.when()
				.put(REGISTER_FAVORITE_API + savedRestaurant.getId())
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
		}
		
		//check if favorite was saved for user
		User u = userRepository.findByUsername(user.getUsername());
		Assert.assertEquals(1, u.getFavorites().size());
		Assert.assertEquals(savedRestaurant.getId(), u.getFavorites().get(0).getId());
		
	}
	
	/**
	 * Test add multiple favorites.
	 */
	@Test
	public void testAddMultipleFavorites() {	
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		List<Restaurant> restaurantsWithinDB = new ArrayList<Restaurant>();
		
		for(int i = 1; i <= 10; i++)
		{
			Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(i));
			restaurantsWithinDB.add(savedRestaurant);
			
			RestAssured
			.registerParser("text/plain", Parser.TEXT);
			
			Response response = RestAssured.given()
				.header("Authorization", "Basic " + encodedString)
				.when()
				.put(REGISTER_FAVORITE_API + savedRestaurant.getId())
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
		}

		//check if favorites were saved for user
		User u = userRepository.findByUsername(user.getUsername());
		Assert.assertEquals(10, restaurantsWithinDB.size());
		Assert.assertEquals(10, u.getFavorites().size());
		
		for(int i=0;i< u.getFavorites().size();i++) {
			Assert.assertEquals(restaurantsWithinDB.get(i).getId(), u.getFavorites().get(i).getId());
		}
		
	}
	
	/**
	 * Test remove single favorite.
	 */
	@Test
	public void testRemoveSingleFavorite() {	
		
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		user.setFavorites(new ArrayList<Restaurant>());
		user.getFavorites().add(savedRestaurant);
		User savedUser = userRepository.save(user);
		
		//check if favorite was saved for user
		User userInDB = userRepository.findByUsername(savedUser.getUsername());
		Assert.assertEquals(1, userInDB.getFavorites().size());
		Assert.assertEquals(savedRestaurant.getId(), userInDB.getFavorites().get(0).getId());
		
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		Response response = RestAssured.given()
			.header("Authorization", "Basic " + encodedString)
			.when()
			.delete(UNREGISTER_FAVORITE_API + savedRestaurant.getId())
		.then()
			.statusCode(200)
			.extract().response();
		
		//check result code
		String resultCode = response.as(String.class);
		Assert.assertEquals("0", resultCode);
		
		//check if favorite was removed for user
		User userAfterDelete = userRepository.findByUsername(savedUser.getUsername());
		Assert.assertEquals(0, userAfterDelete.getFavorites().size());
	}
	
	/**
	 * Test remove single favorite multiple times.
	 */
	@Test
	public void testRemoveSingleFavoriteMultipleTimes() {	
		
		Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(1));
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		user.setFavorites(new ArrayList<Restaurant>());
		user.getFavorites().add(savedRestaurant);
		User savedUser = userRepository.save(user);
		
		//check if favorite was saved for user
		User userInDB = userRepository.findByUsername(savedUser.getUsername());
		Assert.assertEquals(1, userInDB.getFavorites().size());
		Assert.assertEquals(savedRestaurant.getId(), userInDB.getFavorites().get(0).getId());
		
		for(int i=0; i < 10; i++)
		{
			RestAssured
			.registerParser("text/plain", Parser.TEXT);
			
			Response response = RestAssured.given()
				.header("Authorization", "Basic " + encodedString)
				.when()
				.delete(UNREGISTER_FAVORITE_API + savedRestaurant.getId())
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
		}
		
		//check if favorite was removed for user
		User userAfterDelete = userRepository.findByUsername(savedUser.getUsername());
		Assert.assertEquals(0, userAfterDelete.getFavorites().size());
	}
	
	/**
	 * Test remove multiple favorites.
	 */
	@Test
	public void testRemoveMultipleFavorites() {	
		List<Restaurant> restaurantsWithinDB = new ArrayList<Restaurant>();
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		user.setFavorites(new ArrayList<Restaurant>());
		for(int i = 1; i <= 10; i++)
		{
			Restaurant savedRestaurant = restaurantRepo.save(getRestaurant(i));
			user.getFavorites().add(savedRestaurant);
			restaurantsWithinDB.add(savedRestaurant);
		}
		User savedUser = userRepository.save(user);
		
		//check if favorites were saved for user
		User userInDB = userRepository.findByUsername(savedUser.getUsername());
		Assert.assertEquals(10, userInDB.getFavorites().size());
		for(int i = 0; i < userInDB.getFavorites().size(); i++)
		{
			Assert.assertEquals(restaurantsWithinDB.get(i).getId(), userInDB.getFavorites().get(i).getId());
		}
		
		for(int i = 0; i < userInDB.getFavorites().size(); i++)
		{
			RestAssured
			.registerParser("text/plain", Parser.TEXT);
			
			Response response = RestAssured.given()
				.header("Authorization", "Basic " + encodedString)
				.when()
				.delete(UNREGISTER_FAVORITE_API + userInDB.getFavorites().get(i).getId())
			.then()
				.statusCode(200)
				.extract().response();
			
			//check result code
			String resultCode = response.as(String.class);
			Assert.assertEquals("0", resultCode);
		}
		
		//check if favorites were removed
		User userAfterDelete = userRepository.findByUsername(savedUser.getUsername());
		Assert.assertEquals(0, userAfterDelete.getFavorites().size());
	}
	
	/**
	 * Gets the restaurant.
	 *
	 * @param i the i
	 * @return the restaurant
	 */
	private Restaurant getRestaurant(int i)
	{
		Restaurant r = new Restaurant();
		//dummy id
		r.setId(i);
		r.setName("Test restaurant");
		r.setCity("Test city");
		r.setCountry(countryRepo.findAll().get(0));
		r.setEmail("Test email");
		r.setKitchenTypes(kitchenTypeRepo.findAll());
		r.setLocationLatitude(123456789);
		r.setLocationLongitude(987654321);
		r.setPhone("0138473831");
		r.setRestaurantType(restaurantTypeRepo.findAll().get(0));
		r.setStreet("Test street");
		r.setStreetNumber("1");
		r.setUrl("http://www.test.de");
		r.setZip("12345");
		r.setCustomerId(i);
		r.setRestaurantUuid("5b36b3e3-054d-41b9-be3c-19fcece09f1");
		try {
			r.setQrUuid(createQRCode(r.getRestaurantUuid()));
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
