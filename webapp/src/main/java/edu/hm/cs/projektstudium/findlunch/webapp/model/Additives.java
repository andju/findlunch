package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PushNotificationView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;


/**
 * The Class Additives.
 */
@Entity
@Table(name="additives")
public class Additives {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({RestaurantView.RestaurantRest.class, PushNotificationView.PushNotificationRest.class})
	private int id;

	/** The name. */
	@Column(name="name")
	@JsonView({RestaurantView.RestaurantRest.class, PushNotificationView.PushNotificationRest.class})
	private String name;

	/** The description. */
	@Column(name="description")
	@JsonView({RestaurantView.RestaurantRest.class, PushNotificationView.PushNotificationRest.class})
	private String description;

	/** The short Key. */
	@Column(name="short")
	@JsonView({RestaurantView.RestaurantRest.class, PushNotificationView.PushNotificationRest.class})
	private String shortKey;

	/** The offers. */
	//bi-directional many-to-many association to Offer
	@ManyToMany(mappedBy="additives")
	private List<Offer> offers;
	
	/**
	 * Instantiates a new additive.
	 */
	public Additives() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortKey() {
		return shortKey;
	}

	public void setShortKey(String shortKey) {
		this.shortKey = shortKey;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

}