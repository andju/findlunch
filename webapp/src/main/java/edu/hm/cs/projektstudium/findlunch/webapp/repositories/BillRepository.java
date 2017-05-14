package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Bill;

/**
 * The Interface BillRepository. Abstraction for the data access layer
 */
public interface BillRepository extends JpaRepository<Bill, Integer>{

	/**
	 * Find bills from a Restaurant.
	 * @param restaurantId Id of the restaurant
	 * @return List of bills from a restaurant
	 */
	public List<Bill> findByRestaurantId(int restaurantId);
	
	/**
	 * Find bill with the bill Number.
	 * @param billNumber The bill number
	 * @return The bill
	 */
	public Bill findByBillNumber(String billNumber);
	
	/**
	 * Find all Bills that are paid.
	 * @return List of bills
	 */
	public List<Bill> findByPaidTrue();
	
	/**
	 * Find all Bills that are not paid.
	 * @return List of bills
	 */
	public List<Bill> findByPaidFalse();
}
