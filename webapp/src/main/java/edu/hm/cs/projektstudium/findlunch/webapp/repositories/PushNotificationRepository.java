package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;

/**
 * The Interface PushNotificationRepository. Abstraction for the data access layer
 */
@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Integer>{
	
	/**
	 * Find a PushNotification by its id.
	 *
	 * @param pushNotificationId the push notification id
	 * @return the push notification
	 */
	PushNotification findById(int pushNotificationId);

	/**
	 * Find all PushNotification for a user (by user id).
	 *
	 * @param userId the user id
	 * @return the list of PushNotification for the user
	 */
	public List<PushNotification> findByUser_id(int userId);
}
