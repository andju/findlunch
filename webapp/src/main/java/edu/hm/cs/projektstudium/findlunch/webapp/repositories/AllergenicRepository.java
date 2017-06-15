package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Allergenic;

/**
 * 
 * @author Basti Heller
 *
 */
@Repository
public interface AllergenicRepository extends JpaRepository<Allergenic, Integer>{

	/**
	 * 
	 * @param courseType
	 * @return
	 */
	Allergenic findByName(String name);
	
	/**
	 * 
	 * @param courseType
	 * @return
	 */
	Allergenic findByShortKey(String key);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	Allergenic findById(int id);
	
}
