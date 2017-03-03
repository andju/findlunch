package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.BookingReason;

/**
 * The Interface BookingReason. Abstraction for the data access layer
 */
@Repository
public interface BookingReasonRepository extends JpaRepository<BookingReason, Integer> {

}
