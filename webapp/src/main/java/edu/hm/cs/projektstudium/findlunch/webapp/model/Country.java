package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;

/**
 * The Class Country.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown=true)
public class Country {
	
	/** The country code. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="country_code")
	private String countryCode;

	/** The name. */
	@JsonView(RestaurantView.RestaurantRest.class)
	private String name;

	/** The restaurants. */
	//bi-directional many-to-one association to Restaurant
	@JsonIgnore
	@OneToMany(mappedBy="country", fetch=FetchType.LAZY)
	private List<Restaurant> restaurants;

	/**
	 * Instantiates a new country.
	 */
	public Country() {
		
		this.restaurants = new ArrayList<Restaurant>();
		
	}

	/**
	 * Gets the country code.
	 *
	 * @return the country code
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * Sets the country code.
	 *
	 * @param countryCode the new country code
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
		restaurant.setCountry(this);

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
		restaurant.setCountry(null);

		return restaurant;
	}

}