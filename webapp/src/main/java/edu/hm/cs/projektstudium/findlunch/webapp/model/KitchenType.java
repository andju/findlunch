package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PushNotificationView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;


/**
 * The Class KitchenType.
 */
@Entity
@Table(name="kitchen_type")
public class KitchenType {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({RestaurantView.RestaurantRest.class, PushNotificationView.PushNotificationRest.class})
	private int id;

	/** The name. */
	@JsonView({RestaurantView.RestaurantRest.class, PushNotificationView.PushNotificationRest.class})
	private String name;
	
	/** The push notifications. */
	//bi-directional many-to-many association to PushNotification
	@ManyToMany(mappedBy="kitchenTypes")
	private List<DailyPushNotificationData> pushNotifications;

	/** The restaurants. */
	//bi-directional many-to-many association to Restaurant
	@ManyToMany(mappedBy="kitchenTypes")
	@JsonIgnore
	private List<Restaurant> restaurants;

	/**
	 * Instantiates a new kitchen type.
	 */
	public KitchenType() {
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
	 * Gets the push notifications.
	 *
	 * @return the push notifications
	 */
	public List<DailyPushNotificationData> getPushNotifications() {
		return this.pushNotifications;
	}

	/**
	 * Sets the push notifications.
	 *
	 * @param pushNotifications the new push notifications
	 */
	public void setPushNotifications(List<DailyPushNotificationData> pushNotifications) {
		this.pushNotifications = pushNotifications;
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

}