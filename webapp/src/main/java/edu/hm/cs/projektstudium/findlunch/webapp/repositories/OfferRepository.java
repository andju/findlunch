package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;

/**
 * The Interface OfferRepository. Abstraction for the data access layer
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Serializable>{

	/**
	 * Find all Offers for a specific restaurant id.
	 *
	 * @param restaurantId the restaurant id
	 * @return the list of Offers for this restaurant
	 */
	List<Offer> findByRestaurant_id(int restaurantId);
	
	/**
	 * Find a Offer by its id and the restaurant id.
	 *
	 * @param id the id of the offer
	 * @param restaurantId the restaurant id
	 * @return the offer
	 */
	Offer findByIdAndRestaurant_id(int id, int restaurantId);
	
}
