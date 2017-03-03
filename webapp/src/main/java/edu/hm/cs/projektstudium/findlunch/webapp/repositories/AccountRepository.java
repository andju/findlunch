package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Account;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;

/**
 * The Interface AccountRepository. Abstraction for the data access layer
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	
	/**
	 * Find a Account by its admins.
	 * @param users List of user
	 * @return the account
	 */
	public Account findByUsers(List<User> users);
	
	/**
	 * Find a Account by its id.
	 * @param accountTypeId the account id.
	 * @return the account
	 */
	public Account findByAccountTypeId(int accountTypeId);

}
