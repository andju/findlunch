package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.MinimumProfit;

/**
 * The Interface MinimumProfitRepository. Abstraction for the data access layer
 */
public interface MinimumProfitRepository extends JpaRepository<MinimumProfit, Integer>{

}
