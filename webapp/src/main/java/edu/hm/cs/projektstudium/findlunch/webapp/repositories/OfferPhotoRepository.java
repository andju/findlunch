package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;

/**
 * The Interface OfferPhotoRepository. Abstraction for the data access layer
 */
public interface OfferPhotoRepository extends JpaRepository<OfferPhoto, Integer>{
	
	/**
	 * Find all OfferPhoto by a given offer id.
	 *
	 * @param offerId the offer id
	 * @return the list of OfferPhoto for the offer id
	 */
	List<OfferPhoto> findByOffer_id(int offerId);
}
