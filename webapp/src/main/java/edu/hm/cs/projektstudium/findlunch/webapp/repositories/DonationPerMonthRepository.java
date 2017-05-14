package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.DonationPerMonth;

/**
 * The Interface DonationPerMonthRepository. Abstraction for the data access layer
 */
public interface DonationPerMonthRepository extends JpaRepository<DonationPerMonth, Integer> {
	
	/**
	 * Find latest donation from a restaurant.
	 * @param restaurantId Id of the restaurant
	 * @return latest donation (ordered by date descending)
	 */
	DonationPerMonth findFirstByRestaurantIdOrderByDateDesc(int restaurantId);
	
	/**
	 * Find List of donationPerMonth by RestaurantId and billId.
	 * @param restaurantId Restaurant of Id
	 * @param billId Id of bill
	 * @return List of donationPerMonth (ordered by date descending)
	 */
	List<DonationPerMonth> findByRestaurantIdAndBillIdOrderByDateDesc(int restaurantId, Integer billId);
	
}
