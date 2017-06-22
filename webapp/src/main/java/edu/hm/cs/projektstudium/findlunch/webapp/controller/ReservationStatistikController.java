package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationList;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatistik;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;

/**
 * The class is responsible for handling http calls related to the process of manage the reservations.
 */
@Controller 
class ReservationStatistikController {

	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationStatistikController.class);
	
	/**
	 * Get the page for showing the reservation.
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/reservationStatistik", method=RequestMethod.GET)
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
				.findByRestaurantIdAndReservationStatusKeyNotAndTimestampReceivedBetweenOrderByTimestampReceivedAsc(authenticatedUser.getAdministratedRestaurant().getId(), ReservationStatus.RESERVATION_KEY_NEW ,startDate, endDate);
			
			ReservationList r = new ReservationList();
			r.setReservations(reservations);
			model.addAttribute("wrapper", r);

			ArrayList<Reservation> reservationListConfirmed = new ArrayList<Reservation>();
			ArrayList<Reservation> reservationListPointsCollected = new ArrayList<Reservation>();
			ArrayList<Reservation> reservationListRejected = new ArrayList<Reservation>();
			ArrayList<Reservation> reservationListUnprocessed = new ArrayList<Reservation>();

			for (Reservation reservation : reservations) {
				switch (reservation.getReservationStatus().getKey()) {
				case ReservationStatus.RESERVATION_KEY_CONFIRMED:
					if(reservation.isPointsCollected()){
						reservationListPointsCollected.add(reservation);
					}else{
						reservationListConfirmed.add(reservation);
					}
					break;
				case ReservationStatus.RESERVATION_KEY_REJECTED:
					reservationListRejected.add(reservation);
					break;
				case ReservationStatus.RESERVATION_KEY_UNPROCESSED:
					reservationListUnprocessed.add(reservation);
					break;
				default:
					break;
				}
			}
			
			ArrayList<ReservationStatistik> statistikList = new ArrayList<ReservationStatistik>();
			
			ReservationStatistik statistikAll = new ReservationStatistik(reservations, "reservationStatistik.label.reservationAll", reservations.size());
			statistikList.add(statistikAll);
			
			ReservationStatistik statistikConfirmed = new ReservationStatistik(reservationListConfirmed, "reservationStatistik.label.reservationConfirmed", reservations.size());
			statistikList.add(statistikConfirmed);
			
			ReservationStatistik statistikPointsCollected = new ReservationStatistik(reservationListPointsCollected, "reservationStatistik.label.reservationPointsCollected", reservations.size());
			statistikList.add(statistikPointsCollected);
			
			ReservationStatistik statistikPointsRejected = new ReservationStatistik(reservationListRejected, "reservationStatistik.label.reservationRejected", reservations.size());
			statistikList.add(statistikPointsRejected);
			
			ReservationStatistik statistikPointsUnprocessed = new ReservationStatistik(reservationListUnprocessed, "reservationStatistik.label.reservationUnprocessed", reservations.size());
			statistikList.add(statistikPointsUnprocessed);
			
			model.addAttribute("statistik", statistikList);
			
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			model.addAttribute("startDate", df.format(startDate));
			model.addAttribute("endDate", df.format(endDate));
			
			return "reservationStatistik";
		}
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
