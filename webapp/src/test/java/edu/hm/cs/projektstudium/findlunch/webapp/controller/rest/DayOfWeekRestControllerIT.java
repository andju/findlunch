package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import edu.hm.cs.projektstudium.findlunch.webapp.App;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;


/*
Benutzt JUnit facilities
@RunWith(SpringJUnit4ClassRunner.class)

Rest klar...
*/


/**
 * The Class DayOfWeekRestControllerIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class DayOfWeekRestControllerIT {

	/** The dow repo. */
	@Autowired
	private DayOfWeekRepository dowRepo;
	
	/** The Constant DOW_API. */
	private static final String DOW_API = "/api/days_of_week";
	
	/** The server port. */
	@Value("${server.port}")
	private int serverPort;
	
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
	 * Test day of week rest controller.
	 */
	@Test
	public void testDayOfWeekRestController() {	
		List<DayOfWeek> expectedResult = dowRepo.findAll();
		
		Response response = RestAssured.given()
			.when()
			.get(DOW_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(expectedResult.size()))
			.extract().response(); 
		
		DayOfWeek[] actualResult = response.as(DayOfWeek[].class);
		
		// Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< expectedResult.size();i++) {
			 Assert.assertEquals(expectedResult.get(i).getId(), actualResult[i].getId());
			 Assert.assertEquals(expectedResult.get(i).getDayNumber(), actualResult[i].getDayNumber());
			 Assert.assertEquals(expectedResult.get(i).getName(), actualResult[i].getName());
		 }
		
	}
	
	/**
	 * Test illegal method types.
	 */
	@Test
	public void testIllegalMethodTypes() {

		JsonPath response = RestAssured.given().when().delete(DOW_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().put(DOW_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().post(DOW_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().patch(DOW_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
}
