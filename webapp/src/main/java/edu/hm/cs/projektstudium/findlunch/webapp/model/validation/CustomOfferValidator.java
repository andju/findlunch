package edu.hm.cs.projektstudium.findlunch.webapp.model.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;


/**
 * The Class CustomOfferValidator.
 */
@Component
public class CustomOfferValidator implements Validator {

	/** The day of week repository. */
	@Autowired
	private DayOfWeekRepository dayOfWeekRepository;

	@Autowired
	private CourseTypeRepository courseTypeRepository;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		
			return Offer.class.equals(clazz);
	
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object objectToValidate, Errors bindingResult) {

		Offer offer = (Offer) objectToValidate;
		checkDescription(offer.getDescription(), bindingResult);
		checkTitle(offer.getTitle(), bindingResult);
		checkCourseType(offer.getCourseType(), bindingResult);

		if (offer.getStartDate() != null && offer.getEndDate() != null) {
			boolean offerDatesAreValid = checkOfferDates(offer, bindingResult);
			if (offerDatesAreValid)
				checkOfferDaysOfWeek(offer, bindingResult);
		}

	}
	
	/**
	 * Check coursetype.
	 * 
	 * @param courseType
	 * @param bindingResult
	 */
	public void checkCourseType(int courseType, Errors bindingResult) {
		if(courseTypeRepository.findById(courseType)==null){
			bindingResult.rejectValue("courseType", "offer.coursetype.notNull");
		}
	}
	
	/**
	 * Check offer dates.
	 *
	 * @param offer the offer
	 * @param bindingResult the binding result
	 * @return true, if successful
	 */
	private boolean checkOfferDates(Offer offer, Errors bindingResult) {

		if (offer.getStartDate().after(offer.getEndDate())) {
			bindingResult.rejectValue("startDate", "offer.startDate.notAfterEndDate");
			return false;
		}
		return true;
	}

	/**
	 * Check offer days of week.
	 *
	 * @param offer the offer
	 * @param bindingResult the binding result
	 */
	private void checkOfferDaysOfWeek(Offer offer, Errors bindingResult) {

		if (offer.getDayOfWeeks() != null) {

			List<Integer> allowedDaysBasedOnSelectedOfferPeriod = getAllowedDaysBasedOnSelectedOfferPeriod(offer);
			List<Integer> allowedDaysBasedOnConfiguredRestaurantOfferTimes = getAllowedDaysBasedOnConfiguredRestaurantOfferTimes(offer);
			allowedDaysBasedOnSelectedOfferPeriod.retainAll(allowedDaysBasedOnConfiguredRestaurantOfferTimes);
			List<Integer> allowedDays = allowedDaysBasedOnSelectedOfferPeriod;

			boolean isDaySelectionValid = true;
			for (DayOfWeek dow : offer.getDayOfWeeks()) {

				if (!allowedDays.contains(dow.getDayNumber())) {
					isDaySelectionValid = false;
					break;
				}

			}

			if (!isDaySelectionValid) {
				DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
				Collections.sort(allowedDays);
				StringBuilder validDaysAsString = getValidDaysOfWeekAsString(allowedDays);
				
				bindingResult.rejectValue("dayOfWeeks", "offer.dayOfWeeks.invalidDaySelected",
						new String[] { timeFormat.format(offer.getStartDate()), timeFormat.format(offer.getEndDate()),
						validDaysAsString.toString() }, null);
			}
		}
	}

	/**
	 * Gets the allowed days based on selected offer period.
	 *
	 * @param offer the offer
	 * @return the allowed days based on selected offer period
	 */
	private List<Integer> getAllowedDaysBasedOnSelectedOfferPeriod(Offer offer) {

		List<Integer> allowedDays = new ArrayList<Integer>();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(offer.getStartDate());
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(offer.getEndDate());

		while (startDate.before(endDate) || startDate.equals(endDate)) {

			if (allowedDays.contains(startDate.get(Calendar.DAY_OF_WEEK))) {
				break;
			} else {
				allowedDays.add(startDate.get(Calendar.DAY_OF_WEEK));
				startDate.add(Calendar.DATE, 1);
			}
		}

		return allowedDays;
	}

	/**
	 * Gets the allowed days based on configured restaurant offer times.
	 *
	 * @param offer the offer
	 * @return the allowed days based on configured restaurant offer times
	 */
	private List<Integer> getAllowedDaysBasedOnConfiguredRestaurantOfferTimes(Offer offer) {

		List<TimeSchedule> timeSchedules = offer.getRestaurant().getTimeSchedules();
		List<Integer> allowedDaysConcerningConfiguredTimeSchedules = new ArrayList<Integer>();
		if (timeSchedules != null) {
			for (TimeSchedule timeSchedule : timeSchedules) {
				if (!allowedDaysConcerningConfiguredTimeSchedules.contains(timeSchedule.getDayOfWeek().getDayNumber())) {
					allowedDaysConcerningConfiguredTimeSchedules.add(timeSchedule.getDayOfWeek().getDayNumber());
				}
			}
		}
		return allowedDaysConcerningConfiguredTimeSchedules;
	}

	/**
	 * Check title.
	 *
	 * @param title the title
	 * @param bindingResult the binding result
	 */
	private void checkTitle(String title, Errors bindingResult) {
		if (!validateTitle(title))
			bindingResult.rejectValue("title", "offer.titleInvalid");
	}

	/**
	 * Check description.
	 *
	 * @param description the description
	 * @param bindingResult the binding result
	 */
	private void checkDescription(String description, Errors bindingResult) {
		if (!validateDescription(description))
			bindingResult.rejectValue("description", "offer.descriptionInvalid");
	}

	/** The Constant VALID_DESCRIPTION_PATTERN. */
	private static final Pattern VALID_DESCRIPTION_PATTERN = Pattern.compile("([ÖöÄäÜüßA-Z0-9]+[,()'&\". -]*)*",
			Pattern.CASE_INSENSITIVE);

	/** The Constant VALID_TITLE_PATTERN. */
	private static final Pattern VALID_TITLE_PATTERN = Pattern.compile("([ÖöÄäÜüßA-Z0-9]+[,&()'\". -]*)*",
			Pattern.CASE_INSENSITIVE);
	
	/**
	 * Validate title.
	 *
	 * @param title the title
	 * @return true, if successful
	 */
	private boolean validateTitle(String title) {
		Matcher matcher = VALID_TITLE_PATTERN.matcher(title);
		return matcher.matches();
	}

	/**
	 * Validate description.
	 *
	 * @param description the description
	 * @return true, if successful
	 */
	private boolean validateDescription(String description) {
		Matcher matcher = VALID_DESCRIPTION_PATTERN.matcher(description);
		return matcher.matches();
	}
	
	private StringBuilder getValidDaysOfWeekAsString(List<Integer> allowedDays){
		
		List<DayOfWeek> allDayOfWeeks = dayOfWeekRepository.findAll();
		StringBuilder validDaysAsString = new StringBuilder();

		
		for (int day : allowedDays) {
			for (DayOfWeek dow : allDayOfWeeks) {

				if (dow.getDayNumber() == day) {
					if (validDaysAsString.toString().equals("")) {
						validDaysAsString.append(dow.getName());
					} else {
						validDaysAsString.append(", " + dow.getName());
					}

				}

			}
		}
		
		return validDaysAsString;
		
	}

}
