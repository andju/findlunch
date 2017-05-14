package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;

/**
 * The Interface DayOfWeekRepository. Abstraction for the data access layer
 */
@Repository
public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Integer>{
	
	/**
	 * Find a DayOfWeek by the name of the day.
	 *
	 * @param dayOfWeekName the name of the day
	 * @return DayOfWeek DayOfWeek
	 */
	DayOfWeek findByName(String dayOfWeekName);
	
	/**
	 * Find a DayOfWeek by its day number.
	 *
	 * @param dayNumber the day number
	 * @return DayOfWeek DayOfWeek
	 */
	DayOfWeek findByDayNumber(int dayNumber);

}
