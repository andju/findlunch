package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;

/**
 * The Class TimeSchedule.
 */
@Entity
@Table(name = "time_schedule")
public class TimeSchedule {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	/** The offer end time. */
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de", timezone = "Europe/Berlin")
	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "offer_end_time")
	@JsonView(RestaurantView.RestaurantRest.class)
	private Date offerEndTime;

	/** The offer start time. */
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de", timezone = "Europe/Berlin")
	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "offer_start_time")
	@JsonView(RestaurantView.RestaurantRest.class)
	private Date offerStartTime;

	/** The opening times. */
	// bi-directional many-to-one association to OpeningTime
	@JsonView(RestaurantView.RestaurantRest.class)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "timeSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OpeningTime> openingTimes;

	/** The day of week. */
	// bi-directional many-to-one association to DayOfWeek
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "day_of_week_id")
	@JsonView(RestaurantView.RestaurantRest.class)
	private DayOfWeek dayOfWeek;

	/** The restaurant. */
	// bi-directional many-to-one association to Restaurant
	@ManyToOne(fetch = FetchType.EAGER)
	private Restaurant restaurant;

	/**
	 * Instantiates a new time schedule.
	 */
	public TimeSchedule() {
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
	 * Gets the offer end time.
	 *
	 * @return the offer end time
	 */
	public Date getOfferEndTime() {
		return this.offerEndTime;
	}

	/**
	 * Sets the offer end time.
	 *
	 * @param offerEndTime the new offer end time
	 */
	public void setOfferEndTime(Date offerEndTime) {
		this.offerEndTime = offerEndTime;
	}

	/**
	 * Gets the offer start time.
	 *
	 * @return the offer start time
	 */
	public Date getOfferStartTime() {
		return this.offerStartTime;
	}

	/**
	 * Sets the offer start time.
	 *
	 * @param offerStartTime the new offer start time
	 */
	public void setOfferStartTime(Date offerStartTime) {
		this.offerStartTime = offerStartTime;
	}

	/**
	 * Gets the opening times.
	 *
	 * @return the opening times
	 */
	public List<OpeningTime> getOpeningTimes() {
		return this.openingTimes;
	}

	/**
	 * Sets the opening times.
	 *
	 * @param openingTimes the new opening times
	 */
	public void setOpeningTimes(List<OpeningTime> openingTimes) {
		if(this.openingTimes == null)
			this.openingTimes = openingTimes;
		else
		{
			this.openingTimes.clear();
			if(openingTimes != null)
				this.openingTimes.addAll(openingTimes);
		}
	}

	/**
	 * Adds the opening time.
	 *
	 * @param openingTime the opening time
	 * @return the opening time
	 */
	public OpeningTime addOpeningTime(OpeningTime openingTime) {
		getOpeningTimes().add(openingTime);
		openingTime.setTimeSchedule(this);

		return openingTime;
	}

	/**
	 * Removes the opening time.
	 *
	 * @param openingTime the opening time
	 * @return the opening time
	 */
	public OpeningTime removeOpeningTime(OpeningTime openingTime) {
		getOpeningTimes().remove(openingTime);
		openingTime.setTimeSchedule(null);

		return openingTime;
	}

	/**
	 * Gets the day of week.
	 *
	 * @return the day of week
	 */
	public DayOfWeek getDayOfWeek() {
		return this.dayOfWeek;
	}

	/**
	 * Sets the day of week.
	 *
	 * @param dayOfWeek the new day of week
	 */
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * Gets the restaurant.
	 *
	 * @return the restaurant
	 */
	public Restaurant getRestaurant() {
		return this.restaurant;
	}

	/**
	 * Sets the restaurant.
	 *
	 * @param restaurant the new restaurant
	 */
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

}