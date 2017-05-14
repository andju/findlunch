package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PointsView;

/**
 * The Class Points.
 */
@Entity
@AssociationOverrides({
	@AssociationOverride(name="compositeKey.user", joinColumns=@JoinColumn(name="user_id")),
	@AssociationOverride(name="compositeKey.restaurant", joinColumns=@JoinColumn(name="restaurant_id"))})
public class Points {

	/**
	 * The point id (composite key).
	 */
	//composite-id key
	@EmbeddedId
	@JsonView(PointsView.PointsRest.class)
	private PointId compositeKey;//= new PointId()
	
	/** The points.*/
	@JsonView(PointsView.PointsRest.class)
	private int points;
	
	/**
	 * Default Constructor.
	 */
	public Points(){}
	
	/** 
	 * Gets the composite key.
	 * @return The composite key
	 */
	public PointId getCompositeKey(){
		return compositeKey;
	}
	
	/**
	 * Sets the new composite key to Points.
	 * @param compositeKey The new composite key to set
	 */
	public void setCompositeKey(PointId compositeKey){
		this.compositeKey = compositeKey;
	}
	
	/**
	 * Gets the User.
	 * @return The user
	 */
	@Transient
	public User getUser(){
		return compositeKey.getUser();
	}
	
	/**
	 * Sets the new User to Points.
	 * @param user The user
	 */
	public void setUser(User user){
		compositeKey.setUser(user);
	}
	
	/**
	 * Gets the restaurant from points.
	 * @return The restaurant
	 */
	@Transient
	public Restaurant getRestaurant(){
		return compositeKey.getRestaurant();
	}
	
	/**
	 * Sets the new restaurant.
	 * @param restaurant The restaurant
	 */
	public void setRestaurant(Restaurant restaurant){
		compositeKey.setRestaurant(restaurant);
	}
	
	/**
	 * Gets the points.
	 * @return The points
	 */
	public int getPoints(){
		return points;
	}
	
	/**
	 * Sets the points.
	 * @param points The points
	 */
	public void setPoints(int points){
		this.points = points;
	}
	
}
