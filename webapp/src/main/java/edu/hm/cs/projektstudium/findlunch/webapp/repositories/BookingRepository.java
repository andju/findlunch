package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Booking;

/**
 * The Interface BookingRepository. Abstraction for the data access layer
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

	/**
	 * Find List of bookings between start and end date.
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return List of bookings
	 */
	public List<Booking> findByBookingTimeBetween(Date startDate, Date endDate);
	
	/**
	 * Calculate sum of bookings from an account.
	 * @param accountId Id of the account
	 * @return result as a float
	 */
	@Query(value = "SELECT SUM(b.amount) FROM Booking b WHERE b.account.id=:accountId")
	public Float findCurrentAmountOfAccount(@Param("accountId") int accountId);
}
