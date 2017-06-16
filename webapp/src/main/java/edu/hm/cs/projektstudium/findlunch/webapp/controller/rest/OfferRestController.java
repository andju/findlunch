package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;

/**
 * The Class OfferRestController. The class is responsible for handling rest
 * calls related to offers
 */
@RestController
public class OfferRestController {

	/** The offer repository. */
	@Autowired
	private OfferRepository offerRepo;

	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepo;
	
	@Autowired
	private CourseTypeRepository courseTypeRepo;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferRestController.class);
	
	/**
	 * Gets the offers of a given restaurant.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurantId
	 *            the id of the restaurant
	 * @return the offers of the given restaurant with the corresponding coursetype
	 */
	@CrossOrigin
	@JsonView(OfferView.OfferRest.class)
	@RequestMapping(path = "/api/offers", method = RequestMethod.GET)
	public Map<String, List<Offer>> getOffers(@RequestParam(name = "restaurant_id", required = true) int restaurantId, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		List<Offer> result = new ArrayList<Offer>();
		Restaurant restaurant = restaurantRepo.findById(restaurantId);
		if (restaurant != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());

			// check if restaurant has a TimeSchedule for today
			TimeSchedule ts = restaurant.getTimeSchedules().stream()
					.filter(item -> item.getDayOfWeek().getDayNumber() == c.get(Calendar.DAY_OF_WEEK)).findFirst()
					.orElse(null);

			// only get offers, that are valid at the moment
			if (ts != null) {
				getValidOffers(c, ts, restaurantId, result);
			}
		}
		
		Map<String, List<Offer>> offers = new LinkedHashMap<String, List<Offer>>();
		
		// Put the orders together with the coursetypes and order them by their sort by value
		for(CourseTypes course : courseTypeRepo.findByRestaurantIdOrderBySortByAsc(restaurantId)){
			
			List<Offer> offersInCourse = new LinkedList<Offer>();
			for(Offer offer  : result){
				if(offer.getCourseType() == course.getId() && !offer.getSold_out()){
					offersInCourse.add(offer);
				}
			}
			if(!offersInCourse.isEmpty()){
				offers.put(course.getName(), offersInCourse);
			}
		}
		return offers;
	}

	/**
	 * Gets the valid offers of a restaurant.
	 *
	 * @param c
	 *            the Calendar with the day and time to check (preferred: now)
	 * @param ts
	 *            the TimeSchedule that has to be checked
	 * @param restaurantId
	 *            the id of the Restaurant
	 * @param result
	 *            the result, where the valid offers should be stored
	 */
	private void getValidOffers(Calendar c, TimeSchedule ts, int restaurantId, List<Offer> result) {

		int currentHour = c.get(Calendar.HOUR_OF_DAY);
		int currentMin = c.get(Calendar.MINUTE);
		int currentTime = currentHour * 60 + currentMin;

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(ts.getOfferStartTime());
		int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
		int startMin = startCalendar.get(Calendar.MINUTE);
		int startTime = startHour * 60 + startMin;

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(ts.getOfferEndTime());
		int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
		int endMin = endCalendar.get(Calendar.MINUTE);
		int endTime = endHour * 60 + endMin;

		if (startTime <= currentTime && endTime >= currentTime) {
			Date today = getZeroTimeDate(new Date());

			for (Offer o : offerRepo.findByRestaurant_idOrderByOrderAsc(restaurantId)) {
				Date startDate = getZeroTimeDate(o.getStartDate());
				Date endDate = getZeroTimeDate(o.getEndDate());

				if (today.equals(startDate) || today.equals(endDate)
						|| (today.after(startDate) && today.before(endDate))) {

					if (o.getDayOfWeeks().stream().filter(item -> item.getDayNumber() == c.get(Calendar.DAY_OF_WEEK))
							.findFirst().orElse(null) != null) {
						result.add(o);
					}
				}
			}
		}
	}

	/**
	 * Exception handler for MethodArgumentTypeMismatchException.
	 *
	 * @param request the HttpServletRequest
	 * @param e
	 *            the exception
	 * @return the name of the exception class
	 */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public String exceptionHandler(Exception e, HttpServletRequest request) {
		LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		return e.getClass().toString();
	}

	/**
	 * Removes the time (hour, minute, second, millisecond) of a given date and
	 * returns the time value
	 *
	 * @param date
	 *            the date, where the time should be set to zero
	 * @return the time value of the date, where the time was set to zero
	 */
	public static Date getZeroTimeDate(Date date) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}
}