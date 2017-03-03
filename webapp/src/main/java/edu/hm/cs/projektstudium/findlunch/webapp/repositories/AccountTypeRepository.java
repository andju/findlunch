package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.AccountType;

/**
 * The Interface AccountTypeRepository. Abstraction for the data access layer
 */
@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Integer> {

	/**
	 * Find a AccountType by its name.
	 * @param name the name of the type
	 * @return the accountType
	 */
	AccountType findByName(String name);
}
