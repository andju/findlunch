/**
 * 
 */
package edu.hm.cs.projektstudium.findlunch.webapp.model.comparison;

import java.util.Comparator;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;

/**
 * The Class RestaurantDistanceComparator.
 */
public class RestaurantDistanceComparator implements Comparator<Restaurant> {


	/**
	 * Compares the distance between two restaurants
	 * 
	 * @param restaurant1 the first restaurant
	 * @param restaurant2 the second restaurant
	 * @return the distance between the two restaurants
	 */
	@Override
	public int compare(Restaurant restaurant1, Restaurant restaurant2) {
		return Double.compare(restaurant1.getDistance(), restaurant2.getDistance());
	}
}
