package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.mail.MailService;
import edu.hm.cs.projektstudium.findlunch.webapp.model.EuroPerPoint;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationOffers;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.EuroPerPointRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationStatusRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
/**
 * The Class ReservationRestController. The class is responsible for handling
 * rest calls related to registering users
 *
 */
@RestController
public class ReservationRestController {

	/** The restaurant repository. */
	@Autowired
	private UserRepository userRepository;
	
	/** The offer repository. */
	@Autowired
	private OfferRepository offerRepository;
	
	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The reservationStatus repository. */
	@Autowired
	private ReservationStatusRepository reservationStatusRepository;
	
	/** The euroPerPoint repository. */
	@Autowired
	private EuroPerPointRepository euroPerPointRepository;
	
	/** The points repository. */
	@Autowired
	private PointsRepository pointsRepository;
	
	/** The restaurant repository. */
	@Autowired 
	private RestaurantRepository restaurantRepository;
	
	/** The token repository. */
	@Autowired
	private PushTokenRepository tokenRepository;
	
	@Autowired
	private MailService mailService;
	
	private static final String HTTP = "http://";
	
	private static final String HTTPS= "https://";
	
	/** The Logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationRestController.class);
	
	/**
	 * Register a reservation.
	 * @param reservation Reservation to register
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path= "api/register_reservation", method = RequestMethod.POST)
	public ResponseEntity<Integer> registerReservation(@RequestBody Reservation reservation, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		float calculatedPrice = 0;
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());	
		
		
		
		List<ReservationOffers> reservation_Offers = reservation.getReservation_offers();
		
		Restaurant restaurant = null;
		
		// Bestellung entält keine Angebote
		if(reservation_Offers.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation does not contain any offer"));
			return new ResponseEntity<Integer>(1, HttpStatus.CONFLICT);
		}
		
		for(ReservationOffers reservation_offer : reservation_Offers) {
			
			// Bestellte Menge des Angebots ist 0 oder kleiner
			if(reservation_offer.getAmount() <= 0){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation has no amount for offer "+reservation_offer.getOffer().getId()));
				return new ResponseEntity<Integer>(2, HttpStatus.CONFLICT);
			}

			Offer offer = offerRepository.getOne(reservation_offer.getOffer().getId());
			
			// Angebots ID ist nicht in der DB
			if(offer==null){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "No Offer for ID "+reservation_offer.toString()));
				return new ResponseEntity<Integer>(4, HttpStatus.CONFLICT);
			}
			
			// Angebot ist ausverkauft
			if(offer.getSold_out()){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Das Offer "+reservation_offer.getOffer().getId()+" is sold out"));
				return new ResponseEntity<Integer>(5, HttpStatus.CONFLICT);
			}
			
			if(restaurant!=null){
				if(restaurant.getId() != offer.getRestaurant().getId()){
					LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "An offer of the reservation is from another restaurant"));
					return new ResponseEntity<Integer>(7, HttpStatus.CONFLICT);
				}
			}
			restaurant = offer.getRestaurant();
			
			reservation_offer.setReservation(reservation);
			
			calculatedPrice += reservation_offer.getAmount() * offer.getPrice();
			
		}
		
		
		// Der Gesamtpreis, welcher in der Customer App berechnet wurde stimmt nicht
		if(calculatedPrice+reservation.getDonation()!=reservation.getTotalPrice()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation price is incorrect"));
			return new ResponseEntity<Integer>(6, HttpStatus.CONFLICT);
		}

		EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1); //holt den euro pro punkt mit der id
		
		reservation.setReservationNumber(generateReservationNumber());
		reservation.setReservationStatus(reservationStatusRepository.findById(0));
		reservation.setTimestampReceived(new Date());
		reservation.setUser(authenticatedUser);
		reservation.setReservation_offers(reservation_Offers);
		reservation.setRestaurant(restaurant);
		reservation.setEuroPerPoint(euroPerPoint);
		reservation.setPointsCollected(false);
		
		if(!reservation.isUsedPoints()){
			reservation.setTotalPrice(calculatedPrice);
		}
		else{
			reservation.setTotalPrice(0f);
		}
		
		reservationRepository.save(reservation);
		
		String url = getProtocol(request.isSecure()) + request.getServerName()+":"+request.getServerPort() + "/reservations";
		
		mailService.sendNewReservationMail(authenticatedUser, reservation, url);
		
		if(reservation.isUsedPoints()){
			//Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurant().getId());
			PointId pointId = new PointId();
			pointId.setUser(authenticatedUser);
			pointId.setRestaurant(restaurant);
			
			Points points = pointsRepository.findByCompositeKey(pointId);
			int usablePoints = points.getPoints();
			int neededPoints = getNeededPoints(reservation_Offers);
			if(usablePoints >= neededPoints){
				points.setPoints(usablePoints - neededPoints);
				pointsRepository.save(points);
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
			else {
				//punkte reichen nicht
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Points not enough"));
				return new ResponseEntity<Integer>(8, HttpStatus.CONFLICT); 
			}
			
		}else{
			return new ResponseEntity<Integer>(0, HttpStatus.OK);
		}
	}
	
	
	/**
	 * Collects the points for a given user.
	 * @param restaurantUuid UUID from Restaurant
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path = "api/confirm_reservation/{restaurantUuid}", method = RequestMethod.PUT)
	public ResponseEntity<Integer> confirmReservation(@PathVariable("restaurantUuid") String restaurantUuid, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());
		
		Restaurant r = restaurantRepository.findByRestaurantUuid(restaurantUuid);
		if(r==null){
			//restaurant nicht gefunden
			return new ResponseEntity<Integer>(3, HttpStatus.CONFLICT);
		}
		
		LocalDateTime midnight = LocalDate.now().atStartOfDay();
		Date startOfDay = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
		List<Reservation> reservations = reservationRepository.findByUserIdAndTimestampReceivedAfterAndReservationStatusKeyAndPointsCollectedFalse(authenticatedUser.getId(), startOfDay, ReservationStatus.RESERVATION_KEY_NEW);
		
		if(!reservations.isEmpty()){
			for(Reservation reservation : reservations){
				
				if(reservation.getReservationStatus().getKey() == ReservationStatus.RESERVATION_KEY_CONFIRMED){
					EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1);
					
					Float amountOfPoints= new Float(reservation.getTotalPrice()*euroPerPoint.getEuro());
					PointId pointId = new PointId();
					pointId.setUser(authenticatedUser);
					pointId.setRestaurant(reservation.getRestaurant());
						
					Points points = pointsRepository.findByCompositeKey(pointId);
					if(points == null){ //user get First time points
						points = new Points();
						points.setCompositeKey(pointId);
						points.setPoints(amountOfPoints.intValue());
					}
					else{//add new points to the old points
						points.setPoints(points.getPoints() +amountOfPoints.intValue());
					}
					reservationRepository.save(reservation);
					pointsRepository.save(points);
				}
				else {
					//keine Reservierung
					return new ResponseEntity<Integer>(4, HttpStatus.CONFLICT);
				}
			}	
			return new ResponseEntity<Integer>(0, HttpStatus.OK);
		}
		else{
			//keine Reservierung
			return new ResponseEntity<Integer>(4, HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Get reservations for the logged in user.
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@JsonView(ReservationView.ReservationRest.class)
	@RequestMapping(path = "api/getCustomerReservations", method = RequestMethod.GET)
	public ResponseEntity<List<Reservation>> getUserCustomerReservations(Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());
	
		List<Reservation> reservations = reservationRepository.findByUserIdOrderByRestaurantIdAscTimestampReceivedAsc(authenticatedUser.getId());
		
		return new ResponseEntity<List<Reservation>>(reservations, HttpStatus.OK);
	}
	
    /**
     * Generate an unique reservation number for the reservation.
     * @return reservation number
     */
	//See Stackoverflow: http://stackoverflow.com/questions/12659572/how-to-generate-a-random-9-digit-number-in-java
	private int generateReservationNumber(){
        long timeSeed = System.nanoTime();
        double randSeed = Math.random() * 1000;
        long midSeed = (long) (timeSeed * randSeed);
        String rN = Long.toString(midSeed).substring(0, 9);
        return Integer.parseInt(rN);
    }
	
	private int getNeededPoints(List<ReservationOffers> reservation_Offers){
		
		int neededPoints = 0;
		
		for(ReservationOffers reOffers : reservation_Offers){
			Offer offer = reOffers.getOffer();
			neededPoints += reOffers.getAmount() * offer.getNeededPoints();
		}
		
		return neededPoints;
	}
	
	//get Protocol for the email
	private String getProtocol(boolean https){
		return https ? HTTPS : HTTP;
	}
}
