package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CountryRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;

/**
 * The Class OfferRestControllerIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class OfferRestControllerIT {
	
	/** The restaurant repo. */
	@Autowired
	private RestaurantRepository restaurantRepo;
	
	/** The country repo. */
	@Autowired
	private CountryRepository countryRepo;
	
	/** The restaurant type repo. */
	@Autowired
	private RestaurantTypeRepository restaurantTypeRepo;
	
	/** The dow repo. */
	@Autowired
	private DayOfWeekRepository dowRepo;
	
	/** The offer repo. */
	@Autowired
	private OfferRepository offerRepo;
	
	/** The kitchen type repo. */
	@Autowired
	private KitchenTypeRepository kitchenTypeRepo;
	
	/** The Constant OFFER_API. */
	private static final String OFFER_API = "/api/offers";
	
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
	 * Test illegal method types.
	 */
	@Test
	public void testIllegalMethodTypes() {	
		JsonPath response = RestAssured.given().when().delete(OFFER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().put(OFFER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().post(OFFER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().patch(OFFER_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test no restaurant ID.
	 */
	@Test
	public void testNoRestaurantID() {			
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("restaurant_id", "")
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test illegal restaurant id.
	 */
	@Test
	public void testIllegalRestaurantId() {	
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("restaurant_id", "asdf")
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	
	/**
	 * Test no restaurant with this ID.
	 */
	@Test
	public void testNoRestaurantWithThisID() {			
		RestAssured.given()
			.param("restaurant_id", 9999)
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	
	/**
	 * Test no time schedule.
	 */
	@Test
	public void testNoTimeSchedule() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//empty timeSchedule
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	
	/**
	 * Test one time schedule day of week is not today.
	 */
	@Test
	public void testOneTimeScheduleDayOfWeekIsNotToday() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week != today
		tsList.add(getTimeSchedule(getDayOfWeek(+1), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test one time schedule day of week is today.
	 */
	@Test
	public void testOneTimeScheduleDayOfWeekIsToday() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		Offer savedOffer = offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(1))
			.body("[0].id", Matchers.is(savedOffer.getId()))
			.body("[0].title", Matchers.is("Test offer 1"))
			.body("[0].description", Matchers.is("Test offer 1"))
			.body("[0].preparationTime", Matchers.is(10))
			.body("[0].price", Matchers.is(1.0f))
			.body("[0].defaultPhoto", Matchers.isEmptyOrNullString());
	}
	
	/**
	 * Test one time schedule day of week is today offer time over.
	 */
	@Test
	public void testOneTimeScheduleDayOfWeekIsTodayOfferTimeOver() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//day of week = today, offer times invalid
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-2), getHour(-1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test one time schedule day of week is today offer time not started.
	 */
	@Test
	public void testOneTimeScheduleDayOfWeekIsTodayOfferTimeNotStarted() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//day of week = today, offer times invalid
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(+1), getHour(+2), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}

	
	/**
	 * Test multiple time schedules excluding today.
	 */
	@Test
	public void testMultipleTimeSchedulesExcludingToday() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week != today
		tsList.add(getTimeSchedule(getDayOfWeek(+1), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+2), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+3), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+4), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+5), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+6), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test multiple time schedules including today.
	 */
	@Test
	public void testMultipleTimeSchedulesIncludingToday() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+1), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+2), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+3), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+4), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+5), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+6), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		Offer savedOffer = offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(1))
			.body("[0].id", Matchers.is(savedOffer.getId()))
			.body("[0].title", Matchers.is("Test offer 1"))
			.body("[0].description", Matchers.is("Test offer 1"))
			.body("[0].preparationTime", Matchers.is(10))
			.body("[0].price", Matchers.is(1.0f))
			.body("[0].defaultPhoto", Matchers.isEmptyOrNullString());
	}
	
	/**
	 * Test multiple time schedules including today offer time not started.
	 */
	@Test
	public void testMultipleTimeSchedulesIncludingTodayOfferTimeNotStarted() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//including today but offer time for today invalid
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(+1), getHour(+2), r));
		//rest: valid times
		tsList.add(getTimeSchedule(getDayOfWeek(+1), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+2), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+3), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+4), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+5), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+6), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test multiple time schedules including today offer time over.
	 */
	@Test
	public void testMultipleTimeSchedulesIncludingTodayOfferTimeOver() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//including today but offer time invalid
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-2), getHour(-1), r));
		//rest: valid times
		tsList.add(getTimeSchedule(getDayOfWeek(+1), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+2), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+3), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+4), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+5), getHour(-1), getHour(+1), r));
		tsList.add(getTimeSchedule(getDayOfWeek(+6), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid offer
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	
	/**
	 * Test offer dates already over.
	 */
	@Test
	public void testOfferDatesAlreadyOver() {
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//offer dates over
		Offer o = getOffer(savedRestaurant, getDate(-2), getDate(-1), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test offer dates in future.
	 */
	@Test
	public void testOfferDatesInFuture() {
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//offer dates in future
		Offer o = getOffer(savedRestaurant, getDate(+1), getDate(+2), 1);
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}

	/**
	 * Test offer day of weeks exclude today.
	 */
	@Test
	public void testOfferDayOfWeeksExcludeToday() {
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid date range
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		
		//remove current DayOfWeek
		DayOfWeek toRemove = o.getDayOfWeeks().stream().filter(item -> item.getDayNumber() == getDayOfWeek(0).getDayNumber()).findFirst().orElse(null);
		o.getDayOfWeeks().remove(toRemove);
		
		
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test offer day of weeks is empty.
	 */
	@Test
	public void testOfferDayOfWeeksIsEmpty() {
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		//valid date range
		Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), 1);
		
		//remove all DayOfWeek
		o.getDayOfWeeks().clear();
		
		List<Offer> offerList = new ArrayList<Offer>();
		offerList.add(o);
		savedRestaurant.setOffers(offerList);
		offerRepo.save(o);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	

	/**
	 * Test no offers.
	 */
	@Test
	public void testNoOffers() {		
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	
	/**
	 * Test multiple offers all valid.
	 */
	@Test
	public void testMultipleOffersAllValid() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		List<Offer> offerList = new ArrayList<Offer>();
		
		//valid offers
		for(int i=1; i<= 100; i++)
		{
			//valid date range
			Offer o = getOffer(savedRestaurant, getDate(-1), getDate(+1), i);
			offerList.add(o);
			savedRestaurant.setOffers(offerList);
			offerRepo.save(o);
		}
		
		Response response = RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(100))
		.extract().response();
		
		Offer[] actualResult = response.as(Offer[].class);
		
		// Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< offerList.size();i++) {
			 Assert.assertEquals(savedRestaurant.getOffers().get(i).getId(), actualResult[i].getId());
			 Assert.assertEquals(("Test offer " + (i+1)),  actualResult[i].getTitle());
			 Assert.assertEquals(("Test offer " + (i+1)),  actualResult[i].getDescription());
			 Assert.assertEquals(10 * (i+1),  actualResult[i].getPreparationTime());
			 Assert.assertEquals(1f * (i+1),  actualResult[i].getPrice(), 0.01);
			 Assert.assertEquals(null,  actualResult[i].getDefaultPhoto());
		 }
	}
	
	/**
	 * Test multiple offers not all valid.
	 */
	@Test
	public void testMultipleOffersNotAllValid() {			
		Restaurant r = getRestaurant();
		
		List<TimeSchedule> tsList = new ArrayList<TimeSchedule>();
		//valid time schedule, day of week = today
		tsList.add(getTimeSchedule(getDayOfWeek(0), getHour(-1), getHour(+1), r));
		r.setTimeSchedules(tsList);	
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		List<Offer> offerList = new ArrayList<Offer>();
		
		//valid offers
		for(int i=1; i <= 100; i++)
		{
			Offer o;
			if(i % 2 == 0)
			{
				//valid date range
				o = getOffer(savedRestaurant, getDate(-1), getDate(+1), i);
			}else{
				//invalid date range
				o = getOffer(savedRestaurant, getDate(-2), getDate(-1), i);
			}
			
			offerList.add(o);
			savedRestaurant.setOffers(offerList);
			offerRepo.save(o);
		}
		
		Response response = RestAssured.given()
			.param("restaurant_id", savedRestaurant.getId())
		.when()
			.get(OFFER_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(50))
		.extract().response();
		
		Offer[] actualResult = response.as(Offer[].class);
		
		// Check if correct results are present (Only fields that are sent via the rest interface are checked)
		int j = 1;
		int k = 2;
		 for(int i=0;i< actualResult.length;i++) {
			 Assert.assertEquals(savedRestaurant.getOffers().get(i + j).getId(), actualResult[i].getId());
			 Assert.assertEquals(("Test offer " + k),  actualResult[i].getTitle());
			 Assert.assertEquals(("Test offer " + k),  actualResult[i].getDescription());
			 Assert.assertEquals(10 * k,  actualResult[i].getPreparationTime());
			 Assert.assertEquals(1f * k,  actualResult[i].getPrice(), 0.01);
			 Assert.assertEquals(null,  actualResult[i].getDefaultPhoto());
			 j++;
			 k = k+2;
		 }
		 
	}
	
	
	/**
	 * Gets the restaurant.
	 *
	 * @return the restaurant
	 */
	private Restaurant getRestaurant()
	{
		Restaurant r = new Restaurant();
		//dummy id
		r.setId(1);
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
		r.setCustomerId(1);
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
	 * Gets the time schedule.
	 *
	 * @param dow the dow
	 * @param startHour the start hour
	 * @param endHour the end hour
	 * @param r the r
	 * @return the time schedule
	 */
	private TimeSchedule getTimeSchedule(DayOfWeek dow, int startHour, int endHour, Restaurant r)
	{
		TimeSchedule ts = new TimeSchedule();
		ts.setDayOfWeek(dow);
		
		ts.setOfferStartTime(timeToDate(startHour, getMinute(0)));
		ts.setOfferEndTime(timeToDate(endHour, getMinute(0)));
		
		ts.setRestaurant(r);

		
		return ts;
	}
	
	/**
	 * Gets the offer.
	 *
	 * @param r the r
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param i the i
	 * @return the offer
	 */
	private Offer getOffer(Restaurant r, Date startDate, Date endDate, int i)
	{
		Offer o = new Offer();
		
		o.setTitle("Test offer " + i);
		o.setPrice(1 * i);
		o.setPreparationTime(10 * i);
		o.setDescription("Test offer " + i);
		o.setStartDate(startDate);
		o.setEndDate(endDate);
		o.setDayOfWeeks(dowRepo.findAll());
		o.setRestaurant(r);
		o.setNeededPoints(i);
		
		return o;
	}
	
	/**
	 * Gets the date.
	 *
	 * @param difference the difference
	 * @return the date
	 */
	private Date getDate(int difference)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, difference);
		
		return cal.getTime();
	}
	
	/**
	 * Gets the hour.
	 *
	 * @param difference the difference
	 * @return the hour
	 */
	private int getHour(int difference)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, difference);
		
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * Gets the minute.
	 *
	 * @param difference the difference
	 * @return the minute
	 */
	private int getMinute(int difference)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, difference);
		
		return cal.get(Calendar.MINUTE);
	}
	
	/**
	 * Gets the day of week.
	 *
	 * @param difference the difference
	 * @return the day of week
	 */
	private DayOfWeek getDayOfWeek(int difference)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, difference);
		DayOfWeek dow = dowRepo.findAll().stream().filter(item -> item.getDayNumber() == cal.get(Calendar.DAY_OF_WEEK)).findFirst().orElse(null);
		
		return dow;
	}
	
	/**
	 * Time to date.
	 *
	 * @param hours the hours
	 * @param min the min
	 * @return the date
	 */
	private Date timeToDate(int hours, int min) {
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 1, 0, hours, min, 0);
		
		return cal.getTime();
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
