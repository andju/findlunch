package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.util.Base64;

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
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import edu.hm.cs.projektstudium.findlunch.webapp.App;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;

/**
 * The Class LoginUserRestControllerIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class LoginUserRestControllerIT {

	/** The Constant LOGIN_USER_API. */
	private static final String LOGIN_USER_API = "/api/login_user";
	
	/** The server port. */
	@Value("${server.port}")
	private int serverPort;
	
	/** The user repository. */
	@Autowired
	UserRepository userRepository;
	
	/** The user type repository. */
	@Autowired
	UserTypeRepository userTypeRepository;
	
	/** Password encoder **/
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
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
	 * Test illegal method types.
	 */
	@Test
	public void testIllegalMethodTypes() {	
		JsonPath response = RestAssured.given().when().delete(LOGIN_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().put(LOGIN_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().post(LOGIN_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().patch(LOGIN_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test missing authorization header.
	 */
	@Test
	public void testNoAuthorizationHeaderSet()
	{
		JsonPath response = RestAssured
		.when()
			.get(LOGIN_USER_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();
		
		Assert.assertEquals("Full authentication is required to access this resource", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));

	}
	
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
			.get(LOGIN_USER_API)
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
			.get(LOGIN_USER_API)
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
			.get(LOGIN_USER_API)
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
			.get(LOGIN_USER_API)
		.then()
			.statusCode(401)
			.extract().jsonPath();
		
		Assert.assertEquals("Bad credentials", response.getString("message"));
		Assert.assertEquals("Unauthorized", response.getString("error"));
	}
	
	/**
	 * Test login with valid user.
	 */
	@Test
	public void testLoginValidUser()
	{
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithUserTypeKunde();
		userRepository.save(user);
		String authString = user.getUsername() + ":" + user.getPasswordconfirm();
		byte[] base64Encoded = Base64.getEncoder().encode(authString.getBytes());
		String encodedString = new String(base64Encoded);
		
		Response reponse = RestAssured
		.given()
			.header("Authorization", "Basic " + encodedString)
		.when()
			.get(LOGIN_USER_API)
		.then()
			.statusCode(200)
			.extract().response();
		
		Assert.assertEquals("0", reponse.asString());
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
		u.setUserType(userTypeRepository.findByName("Kunde"));
		return u;
	}
}

