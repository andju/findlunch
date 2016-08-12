package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Country;

/**
 * The Interface CountryRepository. Abstraction for the data access layer
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Serializable>{

}
