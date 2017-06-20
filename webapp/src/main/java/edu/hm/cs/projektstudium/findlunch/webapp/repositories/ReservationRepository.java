package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;

/**
 * The Interface ReservationRepository. Abstraction for the data access layer
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	
	/**
	 * Find all Reservation from a user after a given date.
	 * @param userId Id of the user
	 * @param midnight date
	 * @return List of reservation
	 */
	public List<Reservation> findByUserIdAndTimestampReceivedAfterAndReservationStatusKey(int userId, Date midnight, int statuskey);
	
	/**
	 * Find all reservations that are not confirmed and after the given date.
	 * @param restaurantId Id of restaurant
	 * @param reservationTime date
	 * @return List of reservation
	 */
	public List<Reservation> findByRestaurantIdAndReservationStatusKeyAndTimestampReceivedAfter(int restaurantId, int statuskey, Date reservationTime);
	
	/**
	 * Find all reservations that are confirmed or rejected and between the given dates.
	 * @param restaurantId
	 * @param statuskey
	 * @param reservationTimeStart
	 * @param reservationTimeEnd
	 * @return
	 */
	public List<Reservation> findByRestaurantIdAndReservationStatusKeyNotAndTimestampReceivedBetweenOrderByTimestampReceivedAsc(int restaurantId, int statuskey, Date reservationTimeStart, Date reservationTimeEnd);
	
	/**
	 * Find all confirmed reservations from a restaurant.
	 * @param restaurantId Id of restaurant
	 * @param billId Id of bill
	 * @return List of reservation (order by reservationTime ascending)
	 */
	public List<Reservation> findByRestaurantIdAndReservationStatusKeyAndBillIdOrderByTimestampReceivedAsc(int restaurantId, int statuskey, Integer billId);
	
	/**
	 * Find all reservations which are new, nor rejected neighter confirmed and not paied by points
	 * @param userId Id of the customer
	 * @param midnight date
	 * @return List of corresponding reservations
	 */
	public List<Reservation> findByUserIdAndTimestampReceivedAfterAndUsedPointsFalseAndReservationStatusKey(int userId, Date midnight, int statuskey);

	/**
	 * Find all reservations that are between the given dates.
	 * @param reservationTime date
	 * @param reservationTime date
	 * @return List of reservation
	 */
	public List<Reservation> findByRestaurantIdAndTimestampReceivedBetween(int restaurantId, Date reservationTimeAfter, Date reservationTimeBefore);
	
	/**
	 * Find all reservations from a restaurant for the guven user.
	 * @param restaurantId
	 * @param userId
	 * @param statuskey
	 * @return
	 */
	public List<Reservation> findByUserIdOrderByRestaurantIdAscReservationStatusKeyAsc(int userId);
	
}
