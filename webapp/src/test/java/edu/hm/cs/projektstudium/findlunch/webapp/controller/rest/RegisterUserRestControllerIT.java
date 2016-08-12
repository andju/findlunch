package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

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
 * The Class RegisterUserRestController.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class RegisterUserRestControllerIT {

	/** The Constant REGISTER_USER_API. */
	private static final String REGISTER_USER_API = "/api/register_user";
	
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
		JsonPath response = RestAssured.given().when().delete(REGISTER_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().put(REGISTER_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().get(REGISTER_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'GET' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().patch(REGISTER_USER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test missing User within RequestBody for register.
	 */
	@Test
	public void testMissingUserInRequestBody()
	{
		JsonPath response = RestAssured
		.given()
	   		.contentType("application/json")
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(400)
			.extract().jsonPath();
		
		Assert.assertEquals("org.springframework.http.converter.HttpMessageNotReadableException", response.getString("exception"));
		Assert.assertEquals("Bad Request", response.getString("error"));

	}
	
	/**
	 * Test username alrady exists.
	 */
	@Test
	public void testUsernameAlreadyExists() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		userRepository.save(user);
		// To test a real life scenaria, the user type is set to null, since the app does not send an user type within the json.
		// It was only set to pre-save the user to the database (data integrity)
		user.setUserType(null);
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("3", response.asString());
	}
	
	/**
	 * Test username no valid email.
	 */
	@Test
	public void testUsernameNoValidEMail() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithInvalidEmailAsUsername();
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("1", response.asString());
		
	}
	
	/**
	 * Test user with empty passwords.
	 */
	@Test
	public void testUserWithEmptyPasswords() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUserWithEmptyPassword();
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("2", response.asString());
		
	}
	

	/**
	 * Test user with invalid password length.
	 */
	@Test
	public void testUserWithInvalidPasswordLength() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		// Set invalid password
		user.setPassword("uL1%");
		// To test a real life scenario, the user type is set to null, since the app does not send an user type within the json.
		user.setUserType(null);
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("2", response.asString());
		
	}
	
	/**
	 * Test user with invalid password valid length capital letter missing.
	 */
	@Test
	public void testUserWithInvalidPasswordValidLengthCapitalLetterMissing() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		// Set invalid password
		user.setPassword("uop1%");
		// To test a real life scenario, the user type is set to null, since the app does not send an user type within the json.
		user.setUserType(null);
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("2", response.asString());
		
	}
	
	/**
	 * Test user with invalid password valid length small letter missing.
	 */
	@Test
	public void testUserWithInvalidPasswordValidLengthSmallLetterMissing() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		// Set invalid password
		user.setPassword("UOP1%");
		// To test a real life scenario, the user type is set to null, since the app does not send an user type within the json.
		user.setUserType(null);
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("2", response.asString());
		
	}
	
	/**
	 * Test user with invalid password valid length digit missing.
	 */
	@Test
	public void testUserWithInvalidPasswordValidLengthDigitMissing() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		// Set invalid password
		user.setPassword("UOPz%");
		// To test a real life scenario, the user type is set to null, since the app does not send an user type within the json.
		user.setUserType(null);
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("2", response.asString());
		
	}
	
	/**
	 * Test user with invalid password valid length special character missing.
	 */
	@Test
	public void testUserWithInvalidPasswordValidLengthSpecialCharacterMissing() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		// Set invalid password
		user.setPassword("UOP1z");
		// To test a real life scenario, the user type is set to null, since the app does not send an user type within the json.
		user.setUserType(null);
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(409)
			.extract().response();
		
		Assert.assertEquals("2", response.asString());
		
	}
	
	
	
	/**
	 * Test register user with correct information.
	 */
	@Test
	public void testRegisterUser() {
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);
		
		User user = getUser();
		// To test a real life scenario, the user type is set to null, since the app does not send an user type within the json.
		user.setUserType(null);
		
		// Proof that the user does not already exist within the database
		Assert.assertEquals(null, userRepository.findByUsername(user.getUsername()));
		
		Response response = RestAssured
		.given()
		   	.contentType("application/json")
			.body(user)
		.when()
			.post(REGISTER_USER_API)
		.then()
			.statusCode(200)
			.extract().response();
		
		Assert.assertEquals("0", response.asString());
		
		User registeredUser = userRepository.findByUsername(user.getUsername());
		Assert.assertEquals(user.getUsername(), registeredUser.getUsername());
		Assert.assertEquals(userTypeRepository.findByName("Kunde").getName(),registeredUser.getUserType().getName());
	}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	private User getUser()
	{
		User u = new User();
		u.setUsername("admin@admin.com");
		u.setPassword("Admin1234!");
		u.setUserType(userTypeRepository.findByName("Kunde"));
		return u;
	}
	
	/**
	 * Gets user with invalid email as username
	 *
	 * @return the user
	 */
	private User getUserWithInvalidEmailAsUsername()
	{
		User u = new User();
		u.setUsername("admin123dasD");
		u.setPassword("admin");
		u.setPasswordconfirm("admin");
		return u;
	}

	/**
	 * Gets user with an empty password set
	 *
	 * @return the user
	 */
	private User getUserWithEmptyPassword()
	{
		User u = new User();
		u.setUsername("admin@admin.com");
		u.setPassword("");
		u.setPasswordconfirm("");
		return u;
	}
	
}
