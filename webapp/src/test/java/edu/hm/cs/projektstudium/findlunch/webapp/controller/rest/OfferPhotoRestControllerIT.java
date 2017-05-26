package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CountryRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;

/**
 * The Class OfferPhotoRestControllerIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Sql({"/schemaIT.sql", "/dataIT.sql"})
@TestPropertySource("/application-test.properties")
@IntegrationTest()
public class OfferPhotoRestControllerIT {
	
	/** The offer repo. */
	@Autowired
	private OfferRepository offerRepo;
	
	/** The restaurant repo. */
	@Autowired
	private RestaurantRepository restaurantRepo;
	
	/** The country repo. */
	@Autowired
	private CountryRepository countryRepo;
	
	/** The restaurant type repo. */
	@Autowired
	private RestaurantTypeRepository restaurantTypeRepo;
	
	/** The kitchen type repo. */
	@Autowired
	private KitchenTypeRepository kitchenTypeRepo;
	
	/** Niklas Klotz */
	@Autowired
	private CourseTypeRepository courserTypeRepository;
	
	/** The Constant OFFERPHOTO_API. */
	private static final String OFFERPHOTO_API = "/api/offer_photos";
	
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
		JsonPath response = RestAssured.given().when().delete(OFFERPHOTO_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'DELETE' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().put(OFFERPHOTO_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PUT' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().post(OFFERPHOTO_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'POST' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));

		response = RestAssured.given().when().patch(OFFERPHOTO_API).then().statusCode(405).extract().jsonPath();
		Assert.assertEquals("Request method 'PATCH' not supported", response.getString("message"));
		Assert.assertEquals("Method Not Allowed", response.getString("error"));
	}
	
	/**
	 * Test no offer ID.
	 */
	@Test
	public void testNoOfferID() {			
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("offer_id", "")
		.when()
			.get(OFFERPHOTO_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	/**
	 * Test illegal offer id.
	 */
	@Test
	public void testIllegalOfferId() {	
		
		RestAssured
		.registerParser("text/plain", Parser.TEXT);

		RestAssured
		.given()
			.param("offer_id", "asdf")
		.when()
			.get(OFFERPHOTO_API)
		.then()
			.statusCode(200)
			.body(Matchers.containsString(MethodArgumentTypeMismatchException.class.toString()));
	}
	
	
	/**
	 * Test no offer with this ID.
	 */
	@Test
	public void testNoOfferWithThisID() {			
		RestAssured.given()
			.param("offer_id", 9999)
		.when()
			.get(OFFERPHOTO_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test no photo.
	 */
	@Test
	public void testNoPhoto() {		
		Restaurant r = getRestaurant();
		r.setOffers(new ArrayList<Offer>());
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		Offer o = getDefaultOffer();
		savedRestaurant.addOffer(o);
		Offer savedOffer = offerRepo.save(o);
		
		RestAssured.given()
			.param("offer_id", savedOffer.getId())
		.when()
			.get(OFFERPHOTO_API)
		.then()
			.statusCode(200)
			.body("isEmpty()", Matchers.is(true));
	}
	
	/**
	 * Test one photo.
	 */
	@Test
	public void testOnePhoto() {	
		Restaurant r = getRestaurant();
		r.setOffers(new ArrayList<Offer>());
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		Offer o = getDefaultOffer();
		savedRestaurant.addOffer(o);
	
		
		List<OfferPhoto> offerPhotos = new ArrayList<OfferPhoto>();
		offerPhotos.add(getOfferPhoto(o, 1));
		
		o.setOfferPhotos(offerPhotos);
		Offer savedOffer = offerRepo.save(o);
		
		RestAssured.given()
			.param("offer_id", savedOffer.getId())
		.when()
			.get(OFFERPHOTO_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(1))
			.body("[0].id", Matchers.is(savedOffer.getOfferPhotos().get(0).getId()))
			.body("[0].photo", Matchers.is("YmFzZTY0UGhvdG9TdHJpbmcgMQ=="))
			.body("[0].thumbnail", Matchers.is("YmFzZTY0VGh1bWJuYWlsU3RyaW5nIDE="));
			
	}
	
	/**
	 * Test multiple photos.
	 */
	@Test
	public void testMultiplePhotos() {	
		Restaurant r = getRestaurant();
		r.setOffers(new ArrayList<Offer>());
		Restaurant savedRestaurant = restaurantRepo.save(r);
		
		Offer o = getDefaultOffer();
		savedRestaurant.addOffer(o);
	
		
		List<OfferPhoto> offerPhotos = new ArrayList<OfferPhoto>();
		for(int i = 0; i < 100; i++)
		{
			offerPhotos.add(getOfferPhoto(o, i));
		}
		o.setOfferPhotos(offerPhotos);
		Offer savedOffer = offerRepo.save(o);
		
		Response response = RestAssured.given()
			.param("offer_id", savedOffer.getId())
		.when()
			.get(OFFERPHOTO_API)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("list.size()", Matchers.is(100))
		.extract().response();
		
		
		OfferPhoto[] actualResult = response.as(OfferPhoto[].class);
		
		// Check if correct results are present (Only fields that are sent via the rest interface are checked)
		 for(int i=0;i< offerPhotos.size();i++) {
			 Assert.assertEquals(savedOffer.getOfferPhotos().get(i).getId(), actualResult[i].getId());
			 Assert.assertEquals(Arrays.toString(("base64PhotoString " + i).getBytes()), Arrays.toString(actualResult[i].getPhoto()));
			 Assert.assertEquals(Arrays.toString(("base64ThumbnailString " + i).getBytes()), Arrays.toString(actualResult[i].getThumbnail()));
		 }
	}
	
	/**
	 * Gets the default offer.
	 *
	 * @return the default offer
	 */
	private Offer getDefaultOffer()
	{
		Offer o = new Offer();
		//dummy id
		o.setId(1);
		o.setTitle("Test offer");
		o.setPrice(1);
		o.setPreparationTime(10);
		o.setDescription("Test offer");
		o.setStartDate(new Date());
		o.setEndDate(new Date());
		o.setNeededPoints(30);
		//o.setCourseTypes(courserTypeRepository.getOne(1));

		return o;
	}
	

	/**
	 * Gets the offer photo.
	 *
	 * @param o the oFFER
	 * @param i the i
	 * @return the offer photo
	 */
	private OfferPhoto getOfferPhoto(Offer o, int i)
	{
		OfferPhoto op = new OfferPhoto();
		op.setOffer(o);
		op.setThumbnail(("base64ThumbnailString " + i).getBytes());
		op.setPhoto(("base64PhotoString " + i).getBytes());
		
		return op;
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
