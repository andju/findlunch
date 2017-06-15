package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.ResetPassword;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Integer>{

	ResetPassword findByToken(String token);
	
	ResetPassword findByUser(User user);
}
