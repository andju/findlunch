package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
//import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;

/**
 * The Interface AccountRepository. Abstraction for the data access layer
 */
public interface PointsRepository extends JpaRepository<Points, Serializable>{
	
	/**
	 * Find Points by PointId.
	 * @param compositeKey Needed composite key
	 * @return The points
	 */
	public Points findByCompositeKey(PointId compositeKey);
	
	/**
	 * Find List of Points from a user.
	 * @param Id Id of the user
	 * @return List of points
	 */
	public List<Points> findByCompositeKey_User_Id(int Id);

	/**
	 * Find List of Points from a user for a Restaurant.
	 * @param Id userId of the user
	 * @param Id restaurantId of the restaurant
	 * @return List of points
	 */
	public List<Points> findByCompositeKey_User_IdAndCompositeKey_Restaurant_Id(int userId, int restaurantId);

}
