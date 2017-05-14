package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;;


/**
 * The Interface KitchenTypeRepository. Abstraction for the data access layer
 */
@Repository
public interface KitchenTypeRepository extends JpaRepository<KitchenType, Integer>{

	/**
	 * Find all KitchenType and order them by name ascending.
	 *
	 * @return the list of KitchenType (ordered by name ascending)
	 */
	List<KitchenTypeRepository> findAllByOrderByNameAsc();
	
	/**
	 * Find KitchenTypes by their name.
	 *
	 * @param name of the KitchenType
	 * @return the list of KitchenType
	 */
	List<KitchenType> findByName(String name);
	
	/**
	 * Find KitchenTypes by their id.
	 *
	 * @param id of the KitchenType
	 * @return the list of KitchenType
	 */
	KitchenType findById(int id);
	
	
	/**
	 * Find KitchenTypes for a given restaurant.
	 *
	 * @param restaurantId the id of the restaurant
	 * @return the list of KitchenType
	 */
	List<KitchenType> findByRestaurants_id(int restaurantId);
	
	
}
