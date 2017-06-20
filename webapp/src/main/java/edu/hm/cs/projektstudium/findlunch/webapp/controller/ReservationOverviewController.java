package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationList;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationOffers;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;

/**
 * The class is responsible for handling http calls related to the process of manage the reservations.
 */
@Controller 
class ReservationOverviewController {

	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationOverviewController.class);
	
	/**
	 * Get the page for showing the reservation.
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/reservationOverview", method=RequestMethod.GET)
	public String getReservations(@RequestParam(value = "startDate", required = false) String startDateString, @RequestParam(value = "endDate", required = false) String endDateString, Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " dont have a restaurant. Redirect to /restaurant/add"));
			return "redirect:/restaurant/add";
		}else{
			Date startDate;
			Date endDate;
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
			if (null == startDateString || startDateString.length() == 0  || startDateString.equals("")){
				startDate = getMidnightDateOfToday();
			}else{
				startDate = formatter.parseDateTime(startDateString).toDate();
			}
			if (null == endDateString || endDateString.length() == 0  || endDateString.equals("")){
				endDate = getEndDateOfToday();
			}else{
				endDate = formatter.parseDateTime(endDateString).toDate();
			}
			
			ArrayList<Reservation> reservations = (ArrayList<Reservation>) reservationRepository
				.findByRestaurantIdAndReservationStatusKeyNotAndTimestampReceivedBetweenOrderByTimestampReceivedAsc(authenticatedUser.getAdministratedRestaurant().getId(), ReservationStatus.RESERVATION_KEY_NEW, startDate, endDate);
			
			ReservationList r = new ReservationList();
			r.setReservations(reservations);
			model.addAttribute("wrapper", r);

			DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			model.addAttribute("startDate", df.format(startDate));
			model.addAttribute("endDate", df.format(endDate));
			
			return "reservationOverview";
		}
	}
	
	/**
	 * Gets the details of a given reservation
	 * @param reservationId the reservation
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return the reservation detials into the corresponding html fragment
	 */
	@RequestMapping(path="/reservationOverview/details/{reservationId}", method=RequestMethod.GET)
	public String getReservationOverviewDetails(@PathVariable("reservationId") String reservationId, ModelMap model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), " reservationId ", reservationId.toString()));

		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return null;
		}
		
		Reservation reservation = reservationRepository.findOne(Integer.parseInt(reservationId));
		if(reservation == null){
			return null;
		}
		List<ReservationOffers> reservationOffers = reservation.getReservation_offers();
		if(reservationOffers == null){
			return null;
		}
		model.addAttribute("offers", reservationOffers);

		
		return "reservations :: reservationOfferTable";
	}

	
	/**
	 * Get the Midnight of current Day.
	 * @return Midnight of today
	 */
	private Date getMidnightDateOfToday(){
		LocalDateTime midnight = LocalDateTime.now().toLocalDate().atStartOfDay();
		return Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Get the End of current Day.
	 * @return End of today
	 */
	private Date getEndDateOfToday(){
		LocalDateTime midnight = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1).minusMinutes(1);
		return Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
	}

}
