package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.EuroPerPoint;

/**
 * The Interface EuroPerPointRepository. Abstraction for the data access layer
 */
public interface EuroPerPointRepository extends JpaRepository<EuroPerPoint, Integer>{

}
