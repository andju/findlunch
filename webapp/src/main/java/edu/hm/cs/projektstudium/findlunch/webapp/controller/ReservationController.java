package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.EuroPerPoint;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationList;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushNotificationManager;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.EuroPerPointRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * The class is responsible for handling http calls related to the process of manage the reservations.
 */
@Controller
public class ReservationController {

	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/** The offer repository. */
	@Autowired
	private OfferRepository offerRepository;
	
	/** The euroPerPoint repository. */
	@Autowired
	private EuroPerPointRepository euroPerPointRepository;
	
	/** The user repository. */
	@Autowired
	private UserRepository userRepository;
	
	/** The points repository. */
	@Autowired
	private PointsRepository pointsRepository;
	
	/** The token repository. */
	@Autowired
	private PushTokenRepository tokenRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);
	
	/**
	 * Get the page for showing the reservation.
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/reservations", method=RequestMethod.GET)
	public String getReservations(Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " dont have a restaurant. Redirect to /restaurant/add"));
			return "redirect:/restaurant/add";
		}
		else{
			ArrayList<Reservation> reservations = (ArrayList<Reservation>) reservationRepository
					.findByRestaurantIdAndConfirmedFalseAndReservationTimeAfter(authenticatedUser.getAdministratedRestaurant().getId(), getMidnightDateOfToday()); //reservationRepository.findAll(); //reservation form restaurant
				
				ReservationList r = new ReservationList();
				r.setReservations(reservations);
				model.addAttribute("wrapper", r);
				return "reservations";
		}
	}
	
	/**
	 * Confirm selected reservations.
	 * @param reservationList List of reservation
	 * @param principal principal Currently logged in user
	 * @param request request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/reservations", method = RequestMethod.POST, params={"confrim"})
	public String confirmReservationByOwner(@ModelAttribute ReservationList reservationList, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "reservations", reservationList.toString()));
		
		List<Reservation> reservations = reservationList.getReservations();

		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		List<Reservation> confirmedReservations = new ArrayList<>();
	
		//get reservations that are confirmed
		for(Reservation r: reservations){
			if(r.isConfirmed()){
				Reservation reservation = reservationRepository.findOne(r.getId());
				if(!reservation.isConfirmed()){ //maybe scanned before with qr-code scanner
					confirmedReservations.add(r);
				}
			}
		}
		
		if(confirmedReservations.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no reservation selected. Redirect to /reservations."));
			
			return "redirect:/reservations?selectReservation";
		}
		
		
		
		for(Reservation r: confirmedReservations){
			Reservation reservation = reservationRepository.findOne(r.getId());
			reservation.setConfirmed(true);
			reservationRepository.save(reservation);
			increaseConsumerPoints(reservation);
			confirmPush(reservation);
		}
		return "redirect:/reservations?success";
	}
	
	@RequestMapping(path = "/reservations", method = RequestMethod.POST, params={"reject"})
	public String rejectReservationByOwner(@ModelAttribute ReservationList reservationList, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "reservations", reservationList.toString()));
		
		List<Reservation> reservations = reservationList.getReservations();
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		List<Reservation> rejectedReservations = new ArrayList<>();
		
		for(Reservation r: reservations){
			if(r.isRejected()){
				Reservation reservation = reservationRepository.findOne(r.getId());
				if(!reservation.isConfirmed()){ //maybe scanned before with qr-code scanner
					rejectedReservations.add(r);
				}
			}
		}
			
			if(rejectedReservations.isEmpty()){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no reservation selected. Redirect to /reservations."));
				return "redirect:/reservations?selectReservation";
			}
			
			for(Reservation r: rejectedReservations){
				Reservation reservation = reservationRepository.findOne(r.getId());
				reservation.setConfirmed(false);
				reservation.setRejected(true);
				reservationRepository.save(reservation);
				increaseConsumerPoints(reservation);
			}
			return "redirect:/reservations?success";
		
	}
	
	/**
	 * Confirm selected reservations that are costless.
	 * @param reservationList List of reservation
	 * @param principal principal Currently logged in user
	 * @param requestrequest the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/reservations", method = RequestMethod.POST, params={"confrimFreeReservation"})
	public String confirmFreeReservationByOwner(@ModelAttribute ReservationList reservationList, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "reservations", reservationList.toString()));
		
		List<Reservation> reservations = reservationList.getReservations();

		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		List<Reservation> freeReservations = new ArrayList<>();
	
		//get reservations that are free
		for(Reservation r: reservations){
			if(r.isConfirmed()){
				freeReservations.add(r);
			}
		}
		
		if(freeReservations.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no free reservation selected. Redirect to /reservations."));
			return "redirect:/reservations?selectReservation";
		}
		
		for(Reservation r: freeReservations){
			Reservation reservation = reservationRepository.findOne(r.getId());
			reservation.setConfirmed(true);
			reservationRepository.save(reservation);
			//calculateConsumerPoints(reservation);
		}
		return "redirect:/reservations?success";
	}
	
	/**
	 * Calculate the earned points for this reservations and add them to the points of the user.
	 * @param reservation reservation
	 */
	private void increaseConsumerPoints(Reservation reservation) {
		Offer offer = offerRepository.findOne(reservation.getOffer().getId());
		Restaurant restaurant = restaurantRepository.findOne(offer.getRestaurant().getId());
		EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1);
		User consumer = userRepository.findOne(reservation.getUser().getId());
		
		Float amountOfPoints= new Float((reservation.getAmount()*offer.getPrice()) / euroPerPoint.getEuro());
		
		//composite Key
		PointId pointId = new PointId();
		pointId.setUser(consumer);
		pointId.setRestaurant(restaurant);
		
		Points points = pointsRepository.findByCompositeKey(pointId);
		if(points == null){ //user get First time points
			points = new Points();
			points.setCompositeKey(pointId);
			points.setPoints(amountOfPoints.intValue());
		}
		else{//add new points to the old points
			points.setPoints(points.getPoints() +amountOfPoints.intValue());
		}
		pointsRepository.save(points);
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
	 *  
	 * 
	 */
	private void confirmPush(Reservation reservation) {
		
		PushNotificationManager pushManager = new PushNotificationManager();
		PushNotification push = new PushNotification();
		push.generateReservationConfirm(reservation);
		
		User user = reservation.getUser();
		PushToken userToken = tokenRepository.findByUserId(user.getId());
		
		push.setFcmToken(userToken.toString());
		pushManager.sendFcmNotification(push);
		
	}
}
