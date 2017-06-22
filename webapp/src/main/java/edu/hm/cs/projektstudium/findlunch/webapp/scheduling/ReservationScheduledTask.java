package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushNotificationManager;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationStatusRepository;

@Component
public class ReservationScheduledTask {
	
	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(PushNotificationScheduledTask.class);
	
	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The reservationStatus repository. */
	@Autowired
	private ReservationStatusRepository reservationStatusRepository;
	
	/** The token repository. */
	@Autowired
	private PushTokenRepository tokenRepository;

	@Scheduled(fixedRate = 200000)
	public void checkReservations() {
		//Log info
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Starting check for unprocessed reservations."));
		
		Date now = new Date();
		
		List<Reservation> reservations = reservationRepository.findAll();
		
		for(Reservation reservation : reservations){
			
			reservation.getReservationStatus();
			if(now.after(reservation.getCollectTime())&&reservation.getReservationStatus().getKey()==ReservationStatus.RESERVATION_KEY_NEW){
				reservation.setReservationStatus(reservationStatusRepository.findById(9));
				reservationRepository.save(reservation);
				sendPush(reservation);
			}
			
		}
		//Console log info
				LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), "Check for unprocessed reservations finished."));
	}
	
	private void sendPush(Reservation reservation) {
		
		PushNotificationManager pushManager = new PushNotificationManager();
		
		User user = reservation.getUser();
		PushToken userToken = tokenRepository.findByUserId(user.getId());
		if(userToken!=null) {
		JSONObject notification = pushManager.generateReservationNotProcessed(reservation, userToken.getFcm_token());
		pushManager.sendFcmNotification(notification);
		}
		else
			LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), "No FMC Token for user "+user.getUsername()+" found. Could not send a Notification."));
	}
}
