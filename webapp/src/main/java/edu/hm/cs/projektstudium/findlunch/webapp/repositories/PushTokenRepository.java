package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Integer> {

	PushToken findById(int tokenId);
	
	PushToken findByUserId(int userId);
	
	PushToken findByFcmToken(String fcm_token);
	
}
