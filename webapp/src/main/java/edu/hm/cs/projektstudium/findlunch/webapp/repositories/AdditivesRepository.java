package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Additives;

/**
 * 
 * @author Basti Heller
 *
 */
@Repository
public interface AdditivesRepository extends JpaRepository<Additives, Integer>{

	/**
	 * 
	 * @param courseType
	 * @return
	 */
	Additives findByName(String name);
	
	/**
	 * 
	 * @param courseType
	 * @return
	 */
	Additives findByShortKey(String key);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	Additives findById(int id);
	
}
