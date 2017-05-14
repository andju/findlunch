package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.UserType;

/**
 * The Interface UserTypeRepository. Abstraction for the data access layer
 */
@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer>{

	/**
	 * Find a UserType by its name.
	 *
	 * @param userTypeName the name of the UserType
	 * @return the UserType
	 */
	UserType findByName(String userTypeName);
	
}
