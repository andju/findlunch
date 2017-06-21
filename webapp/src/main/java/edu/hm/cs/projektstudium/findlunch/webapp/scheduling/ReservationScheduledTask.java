package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationStatusRepository;

@Component
public class ReservationScheduledTask {
	
	/** The reservation repository. */
	@Autowired
	private ReservationRepository reservationRepository;
	
	/** The reservationStatus repository. */
	@Autowired
	private ReservationStatusRepository reservationStatusRepository;

	@Scheduled(fixedRate = 200000)
	public void checkReservations() {
		
		Date now = new Date();
		
		List<Reservation> reservations = reservationRepository.findAll();
		
		for(Reservation reservation : reservations){
			
			/** @TODO: Timestamp anpassen*/
			if(now.after(reservation.getTimestampReceived())){
				reservation.setReservationStatus(reservationStatusRepository.findById(3));
			}
			
		}
	}
}
