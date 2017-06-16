package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;

/**
 * The repository for the firebase tokens
 * @author Niklas Klotz
 *
 */
@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Integer> {

	/**
	 * Gets a specific token
	 * @param tokenId the ID
	 * @return the token
	 */
	PushToken findById(int tokenId);
	
	/**
	 * Gets a token for a user
	 * @param userId the user 
	 * @return the token
	 */
	PushToken findByUserId(int userId);
	
	/**
	 * Gets a token by its firebase token
	 * @param fcm_token the firebase token
	 * @return the token
	 */
	PushToken findByFcmToken(String fcm_token);
	
}
