package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Account;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OpeningTime;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomRestaurantValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AccountTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CountryRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.security.RestaurantUserDetailsService;

/**
 * The class is responsible for handling http calls related to the process of adding a restaurant.
 */
@Controller
public class RestaurantController {

	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/** The country repository. */
	@Autowired
	private CountryRepository countryRepository;
	
	/** The kitchen type repository. */
	@Autowired
	private KitchenTypeRepository kitchenTypeRepository;
	
	/** The restaurant type repository. */
	@Autowired
	private RestaurantTypeRepository restaurantTypeRepository;
	
	/** The day of week repository. */
	@Autowired
	private DayOfWeekRepository dayOfWeekRepository;
	
	/** The user repository. */
	@Autowired
	private UserRepository userRepository;
	
	/** The custom user details service for restaurant users. Used to refresh the SecurityContextHolder after a restaurant is added */
	@Autowired
	private RestaurantUserDetailsService customUserDetailsService;
	
	/** The custom restaurant validator. Handled enhanced checks not handled by the hibernate annotation */
	@Autowired
	private CustomRestaurantValidator customRestaurantValidator;
	
	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	


	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);
	
	/**
	 * Gets the page for adding a new restaurant to the user.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal
	 * 			Currently logged in user.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/add" }, method = RequestMethod.GET)
	public String addRestaurant(Model model, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();

		if (authenticatedUser.getAdministratedRestaurant() != null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " already has a restaurant. Another restaurant cannot be added."));
			return "redirect:/offer";
		} else {
			Restaurant r = getNewRestaurant();
			r.setEmail(authenticatedUser.getUsername());
			
			model.addAttribute("restaurant", r);
			model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
			model.addAttribute("restaurantTypes", getRestaurantTypes());
			model.addAttribute("countries", countryRepository.findAll());
			
			return "restaurant";
		}
	}

	/**
	 * Gets the restaurant types.
	 *
	 * @return the restaurant types
	 */
	private List<RestaurantType> getRestaurantTypes() {
		List<RestaurantType> result = new ArrayList<RestaurantType>();
		result.addAll(restaurantTypeRepository.findAllByOrderByNameAsc());

		RestaurantType noType = new RestaurantType();
		noType.setName("------");
		noType.setId(-1);
		result.add(0, noType);

		return result;
	}
	
	/**
	 * Gets the new restaurant.
	 *
	 * @return the new restaurant
	 */
	private Restaurant getNewRestaurant() {
		Restaurant restaurant = new Restaurant();
		
		//set the customer Id of the restaurant
		restaurant.setCustomerId(generateId());
		
		String qrCodeData = UUID.randomUUID().toString();
		restaurant.setRestaurantUuid(qrCodeData);
		
		//create a QR-code for the restaurant
		try {
			restaurant.setQrUuid(createQRCode(qrCodeData));
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setBase64(restaurant);

		// add TimeSchedule entry for each day of week
		ArrayList<TimeSchedule> times = new ArrayList<TimeSchedule>();
		List<DayOfWeek> days = dayOfWeekRepository.findAll();
		for (DayOfWeek day : days) {

			TimeSchedule t = new TimeSchedule();
			t.setDayOfWeek(day);
			t.setRestaurant(restaurant);
			times.add(t);
		}

		restaurant.setTimeSchedules(times);
		return restaurant;
	}

	/**
	 * Gets the page for editing a restaurant from the user.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal
	 * 			Currently logged in user.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/edit" }, method = RequestMethod.GET)
	public String editRestaurant(Model model, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers it can be edited."));
			return "redirect:/restaurant/add";
		}
		
		Restaurant restaurant = restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId());

		if(restaurant == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers it can be edited."));
			return "redirect:/restaurant/add";
		}
		
		List<TimeSchedule> existingTimes = new ArrayList<TimeSchedule>();
		existingTimes.addAll(restaurant.getTimeSchedules());
		
		// all elements have to be cleared and copied to a new list
		// to avoid "no longer referenced by the owning entity
		// instance"-exception
		restaurant.getTimeSchedules().clear();
		
		for (DayOfWeek day : dayOfWeekRepository.findAll()) {
			TimeSchedule t = new TimeSchedule();
			t.setDayOfWeek(day);
			
			for (TimeSchedule ts : existingTimes) {
				if (ts.getDayOfWeek().getId() == day.getId()) {
					// copy values
					t.setOfferStartTime(ts.getOfferStartTime());
					t.setOfferEndTime(ts.getOfferEndTime());
					
					for (OpeningTime ot : ts.getOpeningTimes()) {
						handleOpeningTime(ot, t);
					}
					break;
				}
			}
			restaurant.addTimeSchedule(t);
		}
		
		setBase64(restaurant);
		
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
		model.addAttribute("restaurantTypes", getRestaurantTypes());
		model.addAttribute("countries", countryRepository.findAll());
		return "restaurant";
	}
	
	/**
	 * Handle opening time.
	 *
	 * @param ot the OpeningTime
	 * @param t the TimeSchedule
	 */
	private void handleOpeningTime(OpeningTime ot, TimeSchedule t)
	{
		// all elements have to be cleared and copied to a new list
		// to avoid "no longer referenced by the owning entity
		// instance"-exception
		
		OpeningTime o = new OpeningTime();
		o.setClosingTime(ot.getClosingTime());
		o.setOpeningTime(ot.getOpeningTime());
		
		if (t.getOpeningTimes() == null) {
			t.setOpeningTimes(new ArrayList<OpeningTime>());
		}
		
		t.addOpeningTime(o);
	}

	/**
	 * Adds a new opening time object to the restaurant.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurant
	 * 			Restaurant object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param req
	 * 			The request sent by the user
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/add", "/restaurant/edit" }, params = { "addOpeningTime" })
	public String addOpeningTime(final Restaurant restaurant, final BindingResult bindingResult, final Model model, final HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		setBase64(restaurant);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
		model.addAttribute("restaurantTypes", getRestaurantTypes());
		model.addAttribute("countries", countryRepository.findAll());

		final Integer timeScheduleId = Integer.valueOf(request.getParameter("addOpeningTime"));
		TimeSchedule t = restaurant.getTimeSchedules().get(timeScheduleId);

		if (t != null) {
			if (t.getOpeningTimes() == null) {
				// first OpeningTime for that day
				// initialize list
				t.setOpeningTimes(new ArrayList<OpeningTime>());
			}

			// add new OpeningTime
			OpeningTime o = new OpeningTime();
			o.setOpeningTime(timeToDate(0, 0));
			o.setClosingTime(timeToDate(0, 0));
			o.setTimeSchedule(t);
			t.addOpeningTime(o);
		}

		return "restaurant";
	}

	/**
	 * Removes an opening time object to the restaurant.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurant
	 * 			Restaurant object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param req
	 * 			The request sent by the user
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/add", "/restaurant/edit" }, params = { "removeOpeningTime" })
	public String removeOpeningTime(final Restaurant restaurant, final BindingResult bindingResult, final Model model, final HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
		model.addAttribute("restaurantTypes", getRestaurantTypes());
		model.addAttribute("countries", countryRepository.findAll());

		// get id of TimeSchedule and OpeningTime
		final String ids = request.getParameter("removeOpeningTime");
		String[] idParts = ids.split(Pattern.quote("."));

		int timeScheduleId = Integer.valueOf(idParts[0]);
		int openingTimeId = Integer.valueOf(idParts[1]);

		TimeSchedule t = restaurant.getTimeSchedules().get(timeScheduleId);
		if (t != null) {
			// remove OpeningTime
			OpeningTime toRemove = t.getOpeningTimes().get(openingTimeId);
			t.removeOpeningTime(toRemove);
		}
		
		setBase64(restaurant);
		model.addAttribute("restaurant", restaurant);
		return "restaurant";
	}

	/**
	 * Removes an offer time from the time schedule of the restaurant.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurant
	 * 			Restaurant object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param req
	 * 			The request sent by the user
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/add", "/restaurant/edit" }, params = { "removeOfferTime" })
	public String removeOfferTime(final Restaurant restaurant, final BindingResult bindingResult, final Model model, final HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		setBase64(restaurant);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
		model.addAttribute("restaurantTypes", getRestaurantTypes());
		model.addAttribute("countries", countryRepository.findAll());

		final Integer timeScheduleId = Integer.valueOf(request.getParameter("removeOfferTime"));
		TimeSchedule t = restaurant.getTimeSchedules().get(timeScheduleId);

		if (t != null) {
			// remove OfferTimes
			t.setOfferStartTime(null);
			t.setOfferEndTime(null);
		}

		return "restaurant";
	}

	/**
	 * Adds an offer time to the time schedule of the restaurant.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurant
	 * 			Restaurant object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param req
	 * 			The request sent by the user
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/add", "/restaurant/edit" }, params = { "addOfferTime" })
	public String addOfferTime(final Restaurant restaurant, final BindingResult bindingResult, final Model model, final HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		setBase64(restaurant);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
		model.addAttribute("restaurantTypes", getRestaurantTypes());
		model.addAttribute("countries", countryRepository.findAll());

		final Integer timeScheduleId = Integer.valueOf(request.getParameter("addOfferTime"));
		TimeSchedule t = restaurant.getTimeSchedules().get(timeScheduleId);

		if (t != null) {
			// set OfferTimes
			t.setOfferStartTime(timeToDate(0, 0));
			t.setOfferEndTime(timeToDate(0, 0));
		}

		return "restaurant";
	}

	/**
	 * Save the restaurant to the database.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurant
	 * 			Restaurant object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal
	 * 			Currently logged in user.
	 * @return  the string for the corresponding HTML page
	 */
	@RequestMapping(method = RequestMethod.POST, path = { "/restaurant/add", "/restaurant/edit" }, params = { "saveRestaurant" })
	public String saveRestaurant(@Valid final Restaurant restaurant, BindingResult bindingResult, final Model model, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		setBase64(restaurant);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("kitchenTypes", kitchenTypeRepository.findAllByOrderByNameAsc());
		model.addAttribute("restaurantTypes", getRestaurantTypes());
		model.addAttribute("countries", countryRepository.findAll());

		String result = getLocationOfRestaurant(restaurant, request);
		if (result != null) {
			model.addAttribute("geocodingException", result);
			
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The Location of the restaurant could not be retrieved."));
			return "restaurant";
		}
		
		// Checks not handled by Hibernate annotations
		customRestaurantValidator.validate(restaurant, bindingResult);

		if (bindingResult.hasErrors()) {
			LOGGER.error(LogUtils.getValidationErrorString(request, bindingResult, Thread.currentThread().getStackTrace()[1].getMethodName()));
			return "restaurant";
		}

		for (int i = restaurant.getTimeSchedules().size() - 1; i >= 0; i--) {
			handleTimeSchedule(restaurant, i);
		}

		if (restaurant.getRestaurantType() != null && restaurant.getRestaurantType().getId() == -1) {
			restaurant.setRestaurantType(null);
		}

		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		User u = userRepository.findOne(authenticatedUser.getId());
		u.setAdministratedRestaurant(restaurant);
		restaurant.addAdmin(u);
		
		//
		Account account = accountRepository.findByUsers(restaurant.getAdmins());
		if(account == null){
			account = new Account();
			account.setAccountType(accountTypeRepository.findOne(2));
			account.addUser(u);
			account.setAccountNumber(generateId());
			accountRepository.save(account);
		}
		//

		restaurantRepository.save(restaurant);
		
		// Update UserDetails
		User updatedUserDetails = customUserDetailsService.loadUserByUsername(authenticatedUser.getUsername());
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);

		return "redirect:/home?success";
	}

	/**
	 * Handles a time schedule from a restaurant. Removes TimeSchedules with no OfferTimes;
	 * Sets the restaurant for each valid TimeSchedule;
	 * Sets the reference (TimeSchedule) to each valid OpeningTime
	 *
	 * @param restaurant
	 * 			Restaurant for which the time schedules should be handled.
	 * @param i
	 * 			Index of the timeSchedule to be handled.
	 */
	private void handleTimeSchedule(Restaurant restaurant, int i)
	{
		TimeSchedule t = restaurant.getTimeSchedules().get(i);
		if (t.getOfferStartTime() == null && t.getOfferEndTime() == null && (t.getOpeningTimes() == null || t.getOpeningTimes().size() == 0)) {
			// remove entry
			restaurant.removeTimeSchedule(t);
		} else {
			// set restaurant
			restaurant.getTimeSchedules().get(i).setRestaurant(restaurant);

			if (t.getOpeningTimes() != null) {

				if (t.getOpeningTimes().size() == 0)
					t.getOpeningTimes().clear();
				else {
					for (int j = t.getOpeningTimes().size() - 1; j >= 0; j--) {
						// set TimeSchedule
						t.getOpeningTimes().get(j).setTimeSchedule(t);
					}
				}
			}
		}
	}
	
	/**
	 * Cancel restaurant.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/restaurant/add", "/restaurant/edit" }, method = RequestMethod.POST, params = { "cancel" })
	public String cancelRestaurant(Model model, HttpServletRequest request) {
		LOGGER.info(LogUtils.getCancelInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		return "redirect:/home";
	}

	/**
	 * Converts a given time to a Date object.
	 *
	 * @param hours the hours
	 * @param min the minutes
	 * @return the date
	 */
	private Date timeToDate(int hours, int min) {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 1, 0, hours, min, 0);
		return cal.getTime();
	}

	/**
	 * Gets the location of restaurant using the Google Geocoding API.
	 *
	 * @param restaurant
	 * 			Restaurant from which to get the location.
	 * @return the location of restaurant
	 */
	private String getLocationOfRestaurant(Restaurant restaurant, HttpServletRequest request) {
		
		// Replace the API key below with a valid API key.
		GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAvO9bl1Yi2hn7mkTSniv5lXaPRii1JxjI");
		GeocodingApiRequest req = GeocodingApi.newRequest(context).address(String.format("%1$s %2$s, %3$s %4$s", restaurant.getStreetNumber(), restaurant.getStreet(), restaurant.getZip(), restaurant.getCity()));

		try {
			GeocodingResult[] result = req.await();
			if (result != null && result.length > 0) {
				// Handle successful request.
				GeocodingResult firstMatch = result[0];
				if (firstMatch.geometry != null && firstMatch.geometry.location != null) {
					restaurant.setLocationLatitude((float) firstMatch.geometry.location.lat);
					restaurant.setLocationLongitude((float) firstMatch.geometry.location.lng);
				} else {
					return messageSource.getMessage("restaurant.addressNotResolveable", null, Locale.getDefault());
				}
			} else {
				return messageSource.getMessage("restaurant.addressNotFound", null, Locale.getDefault());
			}
		} catch (Exception e) {
			LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
			return messageSource.getMessage("restaurant.googleApiError", new String[] { e.getMessage() }, Locale.getDefault());
		}
		return null;
	}
	
	/**
	 * Set base 64 for img
	 * @param restaurant
	 */
	private void setBase64(Restaurant restaurant){
		String base64Encoded = Base64.getEncoder().encodeToString(restaurant.getQrUuid()); 
		restaurant.setBase64Encoded(base64Encoded);
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
	
	/**
	 * Generate a unique id. Used to generate a random customerId and accountNumber.
	 * @return unique Integer
	 */
	//See Stackoverflow: http://stackoverflow.com/questions/12659572/how-to-generate-a-random-9-digit-number-in-java
	private int generateId(){
        long timeSeed = System.nanoTime();
        double randSeed = Math.random() * 1000; // random number generation
        long midSeed = (long) (timeSeed * randSeed);	// mixing up the time and rand number.
        												// variable timeSeed will be unique
                                                    	// variable rand will  ensure no relation between the numbers
        String rN = Long.toString(midSeed).substring(0, 9);
        return Integer.parseInt(rN);
    }

}
