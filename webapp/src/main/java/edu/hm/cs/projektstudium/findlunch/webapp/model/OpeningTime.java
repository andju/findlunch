package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;


/**
 * The Class OpeningTime.
 */
@Entity
@Table(name="opening_time")
public class OpeningTime {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The closing time. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", locale="de", timezone="Europe/Berlin")
	@DateTimeFormat(pattern="HH:mm")
	@Column(name="closing_time")
	private Date closingTime;

	/** The opening time. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", locale="de", timezone="Europe/Berlin")
	@DateTimeFormat(pattern="HH:mm")
	@Column(name="opening_time")
	private Date openingTime;

	/** The time schedule. */
	//bi-directional many-to-one association to TimeSchedule
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="time_schedule_id")
	private TimeSchedule timeSchedule;

	/**
	 * Instantiates a new opening time.
	 */
	public OpeningTime() {
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
	 * Gets the closing time.
	 *
	 * @return the closing time
	 */
	public Date getClosingTime() {
		return this.closingTime;
	}

	/**
	 * Sets the closing time.
	 *
	 * @param closingTime the new closing time
	 */
	public void setClosingTime(Date closingTime) {
		this.closingTime = closingTime;
	}

	/**
	 * Gets the opening time.
	 *
	 * @return the opening time
	 */
	public Date getOpeningTime() {
		return this.openingTime;
	}

	/**
	 * Sets the opening time.
	 *
	 * @param openingTime the new opening time
	 */
	public void setOpeningTime(Date openingTime) {
		this.openingTime = openingTime;
	}

	/**
	 * Gets the time schedule.
	 *
	 * @return the time schedule
	 */
	public TimeSchedule getTimeSchedule() {
		return this.timeSchedule;
	}

	/**
	 * Sets the time schedule.
	 *
	 * @param timeSchedule the new time schedule
	 */
	public void setTimeSchedule(TimeSchedule timeSchedule) {
		this.timeSchedule = timeSchedule;
	}

}