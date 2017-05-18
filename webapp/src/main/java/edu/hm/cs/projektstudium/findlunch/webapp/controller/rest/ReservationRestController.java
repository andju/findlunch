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
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.EuroPerPoint;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
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
		if(reservation.getAmount() <= 0){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation has no amount"));
			return new ResponseEntity<Integer>(5, HttpStatus.CONFLICT);
		}
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());	
		
		if(reservation.getOffer() == null){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "No offer id"));
			return new ResponseEntity<Integer>(3, HttpStatus.CONFLICT);
			
		}
		Offer offer = offerRepository.findOne(reservation.getOffer().getId());
		if(offer == null){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "It do not exist an offer with the id "+ reservation.getOffer().getId()));
			return new ResponseEntity<Integer>(4, HttpStatus.CONFLICT);
		}
		if(offer.getSold_out()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The offer is currently sold out "+ reservation.getOffer().getId()));
			return new ResponseEntity<Integer>(5, HttpStatus.CONFLICT);
		}

		EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1); //holt den euro pro punkt mit der id
		
		reservation.setReservationNumber(generateReservationNumber());
		reservation.setReservationTime(new Date());
		reservation.setConfirmed(false);
		reservation.setRejected(false);
		reservation.setUser(authenticatedUser);
		reservation.setOffer(offer);
		reservation.setRestaurant(offer.getRestaurant());
		reservation.setTotalPrice(reservation.getAmount() * offer.getPrice());
		reservation.setEuroPerPoint(euroPerPoint);
		
		if(!reservation.isUsedPoints()){
			reservation.setTotalPrice(reservation.getAmount() * offer.getPrice());
		}
		else{
			reservation.setTotalPrice(0f);
		}
		
		reservationRepository.save(reservation);
		
		confirmPush(reservation);
		
		
		if(reservation.isUsedPoints()){
			Restaurant restaurant = restaurantRepository.findById(offer.getRestaurant().getId());
			PointId pointId = new PointId();
			pointId.setUser(authenticatedUser);
			pointId.setRestaurant(restaurant);
			
			Points points = pointsRepository.findByCompositeKey(pointId);
			int usablePoints = points.getPoints();
			int neededPoints = reservation.getAmount() * offer.getNeededPoints();
			if(usablePoints >= neededPoints){
				points.setPoints(usablePoints - neededPoints);
				pointsRepository.save(points);
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
			else {
				//punkte reichen nicht
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Points not enough"));
				return new ResponseEntity<Integer>(6, HttpStatus.CONFLICT); 
			}
			
		}else{
			return new ResponseEntity<Integer>(0, HttpStatus.OK);
		}
	}
	
	
	/**
	 * Confirm a reservation for a given user.
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
		LocalDateTime midnight = LocalDate.now().atStartOfDay();
		Date startOfDay = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
		List<Reservation> reservations = reservationRepository.findByUserIdAndReservationTimeAfterAndConfirmedFalse(authenticatedUser.getId(),startOfDay);
		
		if(r != null){
			List<Offer> offerList = offerRepository.findByRestaurant_id(r.getId());
			if(offerList != null && !offerList.isEmpty()){
				
				for(int i = 0; i < reservations.size(); i++) {
					for(int j = 0; j <offerList.size(); j++) {
						if(reservations.get(i).getOffer().getId() == offerList.get(j).getId()){
							reservations.get(i).setConfirmed(true);
							reservationRepository.save(reservations.get(i));
							Offer offer = offerRepository.findOne(reservations.get(i).getOffer().getId());
							Restaurant restaurant = restaurantRepository.findOne(offer.getRestaurant().getId());
							EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1);

							if(!reservations.get(i).isUsedPoints()){
								Float amountOfPoints= new Float((reservations.get(i).getAmount()*offer.getPrice()) / euroPerPoint.getEuro());
	
								//composite Key
								PointId pointId = new PointId();
								pointId.setUser(authenticatedUser);
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
						}
					}
				}
				return new ResponseEntity<Integer>(0, HttpStatus.OK);
			}
			else{
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "No offer existing"));
				return new ResponseEntity<Integer>(4, HttpStatus.CONFLICT);
			}
		}
		else{
			//restaurant nicht gefunden
			return new ResponseEntity<Integer>(3, HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Sendet eine Push Notification über die neue Bestellung an das Restaurant.
	 * @param reservation
	 */
	private void confirmPush(Reservation reservation) {
		
		PushNotificationManager pushManager = new PushNotificationManager();
		PushNotification push = new PushNotification();
		push.generateReservationConfirm(reservation);
		
		Restaurant restaurant = reservation.getRestaurant();
		
		
		PushToken userToken = tokenRepository.findByUserId(restaurant.getId());
		
		push.setFcmToken(userToken.getFcm_token());
		pushManager.sendFcmNotification(push);
		
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
}
