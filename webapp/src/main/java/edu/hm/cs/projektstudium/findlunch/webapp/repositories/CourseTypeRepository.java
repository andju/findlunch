package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;

/**
 * 
 * @author Niklas Klotz
 *
 */
@Repository
public interface CourseTypeRepository extends JpaRepository<CourseTypes, Integer>{

	/**
	 * 
	 * @param courseType
	 * @return
	 */
	CourseTypes findByName(String name);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	CourseTypes findById(int id);


}
