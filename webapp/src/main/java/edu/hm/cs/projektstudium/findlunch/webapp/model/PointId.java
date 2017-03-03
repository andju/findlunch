package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PointsView;

/**
 * The Class PointId.
 */
@Embeddable
public class PointId implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The user.*/
	@ManyToOne(cascade=CascadeType.ALL)
	private User user;
	
	/** The restaurant.*/
	@ManyToOne(cascade=CascadeType.ALL)
	@JsonView(PointsView.PointsRest.class)
	private Restaurant restaurant;
	
	/**
	 * Gets the user.
	 * @return The user
	 */
	public User getUser(){
		return user;
	}
	
	/**
	 * Sets the new user.
	 * @param user The user to set
	 */
	public void setUser(User user){
		this.user = user;
	}
	
	/**
	 * Gets the restaurant.
	 * @return The restaurant
	 */
	public Restaurant getRestaurant(){
		return restaurant;
	}
	
	/**
	 * Sets the new restaurant.
	 * @param restaurant The restaurant to set
	 */
	public void setRestaurant(Restaurant restaurant){
		this.restaurant = restaurant;
	}
}