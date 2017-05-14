package edu.hm.cs.projektstudium.findlunch.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * This class is responsible for getting users with type "Kunde" for authentication purposes (REST).
 */
@Service("consumerUserDetailsService")
public class ConsumerUserDetailsService implements UserDetailsService {

	/** The user repository. */
	@Autowired
	private UserRepository userRepository;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {

		return userRepository.findByUsernameAndUserType_name(username,"Kunde");
	}
	
}
