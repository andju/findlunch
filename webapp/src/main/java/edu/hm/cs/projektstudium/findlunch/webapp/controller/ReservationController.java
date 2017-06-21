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

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.EuroPerPoint;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationList;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationOffers;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushNotificationManager;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.EuroPerPointRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationStatusRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * The class is responsible for handling http calls related to the process of manage the reservations.
 */
@Controller 
class ReservationController {

	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The reservationStatus repository. */
	@Autowired
	private ReservationStatusRepository reservationStatusRepository;
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
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
					.findByRestaurantIdAndReservationStatusKeyAndTimestampReceivedAfter(authenticatedUser.getAdministratedRestaurant().getId(), ReservationStatus.RESERVATION_KEY_NEW, getMidnightDateOfToday()); //reservationRepository.findAll(); //reservation form restaurant
				
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
			reservation.setReservationStatus(reservationStatusRepository.findById(1));
			reservationRepository.save(reservation);
			increaseConsumerPoints(reservation);
			confirmPush(reservation);
			
		}
		return "redirect:/reservations?success";
	}
	
	/**
	 * Reject the selected reservations
	 * @param reservationList List of reservation
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return redirect to the webpage
	 */
	@RequestMapping(path = "/reservations", method = RequestMethod.POST, params={"reject"})
	public String rejectReservationByOwner(@ModelAttribute ReservationList reservationList, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "reservations", reservationList.toString()));
		
		List<Reservation> reservations = reservationList.getReservations();
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		List<Reservation> rejectedReservations = new ArrayList<>();
		
		for(Reservation r: reservations){
			// Because a checked checkbox always transmitts confirmed for checked reservations
			if(r.isConfirmed()){
				Reservation reservation = reservationRepository.findOne(r.getId());
				if(!reservation.isConfirmed()){ //maybe scanned before with qr-code scanner
					rejectedReservations.add(r);
				}
			}
		}
			
			if(rejectedReservations.isEmpty()){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no reservation selected. Redirect to /reservations."));
				return "redirect:/reservations?selectReservationReject";
			}
			
			for(Reservation r: rejectedReservations){
				Reservation reservation = reservationRepository.findOne(r.getId());
				reservation.setReservationStatus(reservationStatusRepository.findById(2));
				reservationRepository.save(reservation);
				confirmPush(reservation);
			}
			return "redirect:/reservations?successReject";
		
	}
	
	/**
	 * Reject the submitted reservation
	 * @param reservationId
	 * @param reasonId
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return redirect to the webpage
	 */
	@RequestMapping(path = "/reservations/saveReservationStatusReject/{reservationId}/{reasonId}", method = RequestMethod.GET)
	public String rejectReservationByOwnerAJAX(@PathVariable("reservationId") String reservationId, @PathVariable("reasonId") String reasonId, ModelMap model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		int res_id = Integer.parseInt(reservationId);
		int reason_id = Integer.parseInt(reasonId);
		
		Reservation reservation = reservationRepository.findOne(res_id);
		reservation.setReservationStatus(reservationStatusRepository.findOne(reason_id));
		reservation.setTimestampResponded(new Date());
		reservationRepository.save(reservation);
		confirmPush(reservation);
		
		return "redirect:/reservations?successReject";
		
	}
	
	
	/**
	 * Confirm the submitted reservation
	 * @param reservationId
	 * @param waittimeRestaurant
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return redirect to the webpage
	 */
	@RequestMapping(path = "/reservations/saveReservationStatusConfirm/{reservationId}", method = RequestMethod.GET)
	public String confirmReservationByOwnerAJAX(@PathVariable("reservationId") String reservationId,ModelMap model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		int res_id = Integer.parseInt(reservationId);
		
		Reservation reservation = reservationRepository.findOne(res_id);
		reservation.setReservationStatus(reservationStatusRepository.findById(1));
		
		reservationRepository.save(reservation);
		confirmPush(reservation);
		
		return "redirect:/reservations?successReject";
	}
	
	/**
	 * Confirm selected reservations that are costless.
	 * @param reservationList List of reservation
	 * @param principal principal Currently logged in user
	 * @param requestrequest the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/reservations", method = RequestMethod.POST, params={"confirmFreeReservation"})
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
			reservation.setReservationStatus(reservationStatusRepository.findById(1));
			reservationRepository.save(reservation);
			confirmPush(reservation);
			//calculateConsumerPoints(reservation);
		}
		return "redirect:/reservations?success";
	}
	
	/**
	 * Calculate the earned points for this reservations and add them to the points of the user.
	 * @param reservation reservation
	 */
	private void increaseConsumerPoints(Reservation reservation) {
		
		Restaurant restaurant = restaurantRepository.findOne(reservation.getRestaurant().getId());
		User consumer = userRepository.findOne(reservation.getUser().getId());
		int reservationPoints = getReservationPoints(reservation.getReservation_offers());
		
		//composite Key
		PointId pointId = new PointId();
		pointId.setUser(consumer);
		pointId.setRestaurant(restaurant);
		
		Points points = pointsRepository.findByCompositeKey(pointId);
		if(points == null){ //user get First time points
			points = new Points();
			points.setCompositeKey(pointId);
			points.setPoints(reservationPoints);
		}
		else{//add new points to the old points
			points.setPoints(points.getPoints() +reservationPoints);
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
	 * Sends a confirmation of the reservation via firebase push to the customer
	 * @param reservation the reservation
	 * @param confirm true, if confirmed, false if rejected
	 * @return
	 */
	private Boolean confirmPush(Reservation reservation) {
		
		PushNotificationManager pushManager = new PushNotificationManager();
		
		User user = reservation.getUser();
		PushToken userToken = tokenRepository.findByUserId(user.getId());
		
		if(reservation.isConfirmed() && userToken != null){
			JSONObject notification = pushManager.generateReservationConfirm(reservation, userToken.getFcm_token());
			pushManager.sendFcmNotification(notification);
			return true;
		}

		if(reservation.isRejected() && userToken != null){
			JSONObject notification = pushManager.generateReservationReject(reservation, userToken.getFcm_token());
			pushManager.sendFcmNotification(notification);
			return true;
		} 
		
		return false;
	}
	
	/**
	 * Gets the pints for the reservation
	 * @param reservation_Offers the list of offers within the reservation
	 * @return the points for the reservation
	 */
	private int getReservationPoints(List<ReservationOffers> reservation_Offers){
		
		int addPoints = 0;
		EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1);
		
		for(ReservationOffers reOffers : reservation_Offers){
			addPoints += reOffers.getAmount() * reOffers.getOffer().getPrice() / euroPerPoint.getEuro();
		}
		
		return addPoints;
	}
	
	/**
	 * Gets the details of a given reservation
	 * @param reservationId the reservation
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return the reservation detials into the corresponding html fragment
	 */
	@RequestMapping(path="/reservations/details/{reservationId}", method=RequestMethod.GET)
	public String getReservationDetails(@PathVariable("reservationId") String reservationId, ModelMap model, Principal principal, HttpServletRequest request){
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
	 * Gets data for the reservation Reject Modal
	 * @param reservationId the reservation
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return the reservation detials into the corresponding html fragment
	 */
	@RequestMapping(path="/reservations/rejectModal/{reservationId}", method=RequestMethod.GET)
	public String getReservationDeclineModal(@PathVariable("reservationId") String reservationId, ModelMap model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), " reservationId ", reservationId.toString()));

		return setStatusModalData(reservationId, model, principal, request, ReservationStatus.RESERVATION_KEY_REJECTED);
	}
	
	/**
	 * Gets data for the reservation Confirm Modal
	 * @param reservationId the reservation
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return the reservation detials into the corresponding html fragment
	 */
	@RequestMapping(path="/reservations/confirmModal/{reservationId}", method=RequestMethod.GET)
	public String getReservationConfirmModal(@PathVariable("reservationId") String reservationId, ModelMap model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), " reservationId ", reservationId.toString()));
		
		return setStatusModalData(reservationId, model, principal, request, ReservationStatus.RESERVATION_KEY_CONFIRMED);
	}

	private String setStatusModalData(String reservationId, ModelMap model, Principal principal,
			HttpServletRequest request, int reservationStatusKey) {
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
		
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		model.addAttribute("collectTime", df.format(reservation.getCollectTime()));
		model.addAttribute("reservationId", reservationId);
		model.addAttribute("offers", reservationOffers);

		List<ReservationStatus> listRs = reservationStatusRepository.findByKey(reservationStatusKey);
		model.addAttribute("statusList", listRs);
		
		if(reservationStatusKey == ReservationStatus.RESERVATION_KEY_REJECTED){
			return "reservations :: reservationStatusReject";
		}else if(reservationStatusKey == ReservationStatus.RESERVATION_KEY_CONFIRMED){
			return "reservations :: reservationStatusConfirmed";
		}else{
			return null;
		}
	}
}
