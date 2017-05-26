package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

<<<<<<< HEAD
import java.util.ArrayList;
=======
import java.util.List;
>>>>>>> refs/heads/ftr_course_types

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
	 * @param id
	 * @return
	 */
	CourseTypes findById(int id);
<<<<<<< HEAD


=======
	
	/**
	 * 
	 * @param restaurant_id
	 * @return
	 */
	List<CourseTypes> findByRestaurantIdOrderBySortByAsc(int restaurant_id);
	
	/**
	 * 
	 * @param id
	 * @param restaurant_id
	 * @return
	 */
	CourseTypes findByIdAndRestaurantId(int id, int restaurant_id);
>>>>>>> refs/heads/ftr_course_types
}
