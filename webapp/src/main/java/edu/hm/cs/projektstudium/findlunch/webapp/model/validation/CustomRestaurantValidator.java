package edu.hm.cs.projektstudium.findlunch.webapp.model.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OpeningTime;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;


/**
 * The Class CustomRestaurantValidator.
 */
@Component
public class CustomRestaurantValidator implements Validator {

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

		Restaurant restaurant = (Restaurant)objectToValidate;
		checkEmail(restaurant, bindingResult);
		checkStreet(restaurant, bindingResult);
		checkTimeSchedules(restaurant, bindingResult);
	}

	/**
	 * Check email.
	 *
	 * @param restaurant the restaurant
	 * @param bindingResult the binding result
	 */
	private void checkEmail(Restaurant restaurant, Errors bindingResult) {
		if (!validateEmail(restaurant.getEmail()))
			bindingResult.rejectValue("email", "restaurant.emailInvalid");
	}
	
	/**
	 * Check street.
	 *
	 * @param restaurant the restaurant
	 * @param bindingResult the binding result
	 */
	private void checkStreet(Restaurant restaurant, Errors bindingResult) {
		if(!validateStreet(restaurant.getStreet())) 
			bindingResult.rejectValue("street", "restaurant.streetInvalid");
	}
	
	/** The Constant VALID_STREET_PATTERN. */
	private static final Pattern VALID_STREET_PATTERN = Pattern
			.compile("([ÖöÄäÜüßA-Z]+[- ._]?)*", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Validate street.
	 *
	 * @param street the street
	 * @return true, if successful
	 */
	private boolean validateStreet(String street) {
		Matcher matcher = VALID_STREET_PATTERN.matcher(street);
		return matcher.matches();
	}

	/**
	 * Check time schedules.
	 *
	 * @param restaurant the restaurant
	 * @param bindingResult the binding result
	 */
	private void checkTimeSchedules(Restaurant restaurant, Errors bindingResult) {
		List<TimeSchedule> timeSchedules = restaurant.getTimeSchedules();
		boolean atLeastOneOfferTime = false;
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		
		if (timeSchedules != null) {
			for (TimeSchedule ts : timeSchedules) {
				if(checkTimeScheduleHasAtLeastOneOfferTime(ts, bindingResult, timeFormat))
					atLeastOneOfferTime = true;
			}
		}
		
		if (!atLeastOneOfferTime)
			bindingResult.rejectValue("timeSchedules", "timeSchedules.offerTimes.atLeastOneOfferTime");
	}

	/**
	 * Check time schedule has at least one offer time.
	 *
	 * @param ts the TimeSchedule
	 * @param bindingResult the binding result
	 * @param timeFormat the time format
	 * @return true, if successful
	 */
	private boolean checkTimeScheduleHasAtLeastOneOfferTime(TimeSchedule ts, Errors bindingResult, DateFormat timeFormat)
	{
		boolean atLeastOneOfferTime = false;
		boolean dayHasOfferTime = false;
		boolean offerTimeIsWithinOneOpeningTime = false;
		
		if (ts.getOfferStartTime() != null)
		{
			dayHasOfferTime = true;
			atLeastOneOfferTime = true;
			
			Calendar offerStartTime = Calendar.getInstance();
			offerStartTime.setTime(ts.getOfferStartTime());
			
			Calendar offerEndTime = Calendar.getInstance();
			offerEndTime.setTime(ts.getOfferEndTime());
			
			long thirtyMinutesInMillis = 1800000;
			
			if(offerStartTime.getTimeInMillis() > offerEndTime.getTimeInMillis())
				bindingResult.rejectValue("timeSchedules", "timeSchedules.offerTimes.startTimeAfterEndTime", new String[]{timeFormat.format(ts.getOfferStartTime()), ts.getDayOfWeek().getName(), timeFormat.format(ts.getOfferEndTime())}, null);
			else if(offerStartTime.getTimeInMillis() + thirtyMinutesInMillis > offerEndTime.getTimeInMillis())
				bindingResult.rejectValue("timeSchedules", "timeSchedules.offerTimes.periodTooShort", new String[]{timeFormat.format(ts.getOfferStartTime()), timeFormat.format(ts.getOfferEndTime()), ts.getDayOfWeek().getName()}, null);
		}
		
		if (ts.getOpeningTimes() != null) {
			for(OpeningTime ot : ts.getOpeningTimes())
			{
				if(checkOfferTimeIsWithinOneOpeningTime(bindingResult, ot, ts, dayHasOfferTime, timeFormat))
					offerTimeIsWithinOneOpeningTime = true;
			}
		}else{
			//if no opening time is set, all offer times are valid
			offerTimeIsWithinOneOpeningTime = true;
		}
		
		if(dayHasOfferTime && !offerTimeIsWithinOneOpeningTime)
		{
			bindingResult.rejectValue("timeSchedules", "timeSchedules.offerTimes.notWithinOneOpeningTime", new String[]{ts.getDayOfWeek().getName()}, null);
		}
		
		return atLeastOneOfferTime;
	}
	
	/**
	 * Check offer time is within one opening time.
	 *
	 * @param bindingResult the binding result
	 * @param ot the OpeningTime
	 * @param ts the TimeSchedule
	 * @param dayHasOfferTime the day has offer time
	 * @param timeFormat the time format
	 * @return true, if successful
	 */
	private boolean checkOfferTimeIsWithinOneOpeningTime(Errors bindingResult, OpeningTime ot, TimeSchedule ts, boolean dayHasOfferTime, DateFormat timeFormat)
	{
		boolean offerTimeIsWithinOneOpeningTime = false;
		long thirtyMinutesInMillis = 1800000;
		
		Calendar openingTime = Calendar.getInstance();
		openingTime.setTime(ot.getOpeningTime());
		
		Calendar closingTime = Calendar.getInstance();
		closingTime.setTime(ot.getClosingTime());
		
		if(openingTime.getTimeInMillis() > closingTime.getTimeInMillis())
			bindingResult.rejectValue("timeSchedules", "timeSchedules.openingTimes.openingTimeAfterClosingTime", new String[]{timeFormat.format(ot.getOpeningTime()), ts.getDayOfWeek().getName(), timeFormat.format(ot.getClosingTime())}, null);
		else if(openingTime.getTimeInMillis() + thirtyMinutesInMillis > closingTime.getTimeInMillis())
			bindingResult.rejectValue("timeSchedules", "timeSchedules.openingTimes.periodTooShort", new String[]{timeFormat.format(ot.getOpeningTime()), timeFormat.format(ot.getClosingTime()), ts.getDayOfWeek().getName()}, null);
		
		if(dayHasOfferTime)
		{
			Calendar offerStartTime = Calendar.getInstance();
			offerStartTime.setTime(ts.getOfferStartTime());
			
			Calendar offerEndTime = Calendar.getInstance();
			offerEndTime.setTime(ts.getOfferEndTime());
			
			if(openingTime.getTimeInMillis() <= offerStartTime.getTimeInMillis() && closingTime.getTimeInMillis() >= offerEndTime.getTimeInMillis())
				offerTimeIsWithinOneOpeningTime = true;
		}
		
		return offerTimeIsWithinOneOpeningTime;
	}
	
	/** The Constant VALID_EMAIL_ADDRESS_REGEX. */
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	/**
	 * Validate email.
	 *
	 * @param emailStr the email str
	 * @return true, if successful
	 */
	private static boolean validateEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
}
