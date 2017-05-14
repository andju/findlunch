package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;


/**
 * The Class RestaurantType.
 */
@Entity
@Table(name="restaurant_type")
public class RestaurantType  {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView(RestaurantView.RestaurantRest.class)
	private int id;

	/** The name. */
	@JsonView(RestaurantView.RestaurantRest.class)
	private String name;

	/** The restaurants. */
	//bi-directional many-to-one association to Restaurant
	@OneToMany(mappedBy="restaurantType")
	private List<Restaurant> restaurants;

	/**
	 * Instantiates a new restaurant type.
	 */
	public RestaurantType() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the restaurants.
	 *
	 * @return the restaurants
	 */
	public List<Restaurant> getRestaurants() {
		return this.restaurants;
	}

	/**
	 * Sets the restaurants.
	 *
	 * @param restaurants the new restaurants
	 */
	public void setRestaurants(List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	/**
	 * Adds the restaurant.
	 *
	 * @param restaurant the restaurant
	 * @return the restaurant
	 */
	public Restaurant addRestaurant(Restaurant restaurant) {
		getRestaurants().add(restaurant);
		restaurant.setRestaurantType(this);

		return restaurant;
	}

	/**
	 * Removes the restaurant.
	 *
	 * @param restaurant the restaurant
	 * @return the restaurant
	 */
	public Restaurant removeRestaurant(Restaurant restaurant) {
		getRestaurants().remove(restaurant);
		restaurant.setRestaurantType(null);

		return restaurant;
	}

}

