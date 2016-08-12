package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PushNotificationView;

/**
 * The Class PushNotification.
 */
@Entity
@Table(name="push_notification")
public class PushNotification implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue 
	@JsonView(PushNotificationView.PushNotificationRest.class)
	private int id;
	
	/** The title. */
	@JsonView(PushNotificationView.PushNotificationRest.class)
	private String title;

	/** The gcm token. */
	@Lob
	@Column(name="gcm_token")
	private String gcmToken;

	/** The latitude. */
	private float latitude;

	/** The longitude. */
	private float longitude;


	/** The radius. */
	private int radius;

	/** The user. */
	//bi-directional many-to-one association to User
	@ManyToOne
	private User user;
	
	/** The day of weeks. */
	//bi-directional many-to-many association to DayOfWeek
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="push_notification_has_day_of_week"
		, joinColumns={
			@JoinColumn(name="push_notification_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="day_of_week_id")
			}
		)
	@JsonView(PushNotificationView.PushNotificationRest.class)
	private List<DayOfWeek> dayOfWeeks;

	/** The kitchen types. */
	//bi-directional many-to-many association to KitchenType
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="push_notification_has_kitchen_type"
		, joinColumns={
			@JoinColumn(name="push_notification_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="kitchen_type_id")
			}
		)
	@JsonView(PushNotificationView.PushNotificationRest.class)
	private List<KitchenType> kitchenTypes;

	/**
	 * Instantiates a new push notification.
	 */
	public PushNotification() {
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
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the gcm token.
	 *
	 * @return the gcm token
	 */
	public String getGcmToken() {
		return this.gcmToken;
	}

	/**
	 * Sets the gcm token.
	 *
	 * @param gcmToken the new gcm token
	 */
	public void setGcmToken(String gcmToken) {
		this.gcmToken = gcmToken;
	}

	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public float getLatitude() {
		return this.latitude;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public float getLongitude() {
		return this.longitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * Sets the radius.
	 *
	 * @param radius the new radius
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Gets the day of weeks.
	 *
	 * @return the day of weeks
	 */
	public List<DayOfWeek> getDayOfWeeks() {
		return this.dayOfWeeks;
	}

	/**
	 * Sets the day of weeks.
	 *
	 * @param dayOfWeeks the new day of weeks
	 */
	public void setDayOfWeeks(List<DayOfWeek> dayOfWeeks) {
		this.dayOfWeeks = dayOfWeeks;
	}

	/**
	 * Gets the kitchen types.
	 *
	 * @return the kitchen types
	 */
	public List<KitchenType> getKitchenTypes() {
		return this.kitchenTypes;
	}

	/**
	 * Sets the kitchen types.
	 *
	 * @param kitchenTypes the new kitchen types
	 */
	public void setKitchenTypes(List<KitchenType> kitchenTypes) {
		this.kitchenTypes = kitchenTypes;
	}

}