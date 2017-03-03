package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;

/**
 * The Interface RestaurantRepository. Abstraction for the data access layer
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Serializable>{
	
	/**
	 * Find a Restaurant by its id.
	 *
	 * @param restaurantId the restaurant id
	 * @return the restaurant
	 */
	Restaurant findById(int restaurantId);
	
	/**
	 * Find all Restaurants which are favorites of a user (by username).
	 *
	 * @param username the username
	 * @return the list of Restaurants which are favorites of the user
	 */
	List<Restaurant> findByFavUsers_username(String username);
	
	/**
	 * Find all Restaurants which have one of the given KitchenTypes (by KitchenType id)
	 *
	 * @param kitchenTypes the ids of the KitchenTypes
	 * @return the list of Restaurants which have one of the given KitchenTypes
	 */
	List<Restaurant> findByKitchenTypes_idIn(List<Integer> kitchenTypes);
	
	/**
	 * Find a Restaurant by its Restaurant uuid.
	 * @param restaurantUuid Uuid of the Restaurant
	 * @return The restaurant
	 */
	Restaurant findByRestaurantUuid(String restaurantUuid);
}
