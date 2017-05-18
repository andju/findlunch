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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;


/**
 * The Class Offer.
 */
@Entity
public class Offer {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView(OfferView.OfferRest.class)
	private int id;

	/** The description. */
	@Lob
	@JsonView(OfferView.OfferRest.class)
	@NotBlank(message="{offer.description.notBlank}")
	@Size(min=2, max=500, message= "{offer.description.lengthInvalid}")
	private String description;

	/** The end date. */
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd.MM.yyy")
	@Column(name="end_date")
	@NotNull(message="{offer.endDate.notNull}")
	private Date endDate;

	/** The preparation time. */
	@Column(name="preparation_time")
	@JsonView(OfferView.OfferRest.class)
	@Min(value=1, message="{offer.preparationTime.invalidMinValue}")
	private int preparationTime;

	/** The price. */
	@JsonView(OfferView.OfferRest.class)
	@NumberFormat(style=Style.DEFAULT)
	@DecimalMin(value="0.5", message="{offer.price.invalidMinValue}")
	private float price;

	/** The start date. */
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd.MM.yyy")
	@Column(name="start_date")
	@NotNull(message="{offer.startDate.notNull}")
	private Date startDate;

	/** The title. */
	@JsonView(OfferView.OfferRest.class)
	@NotBlank(message="{offer.title.notBlank}")
	@Size(min=2, max=60, message= "{offer.title.lengthInvalid}")
	private String title;

	/** The default photo. */
	@Transient
	@JsonView(OfferView.OfferRest.class)
	private OfferPhoto defaultPhoto;
	
	/** The reservations.*/
	@OneToMany(mappedBy="offer", cascade=CascadeType.ALL)
	List<Reservation> reservation;
	
	/** The needed point*/
	@JsonView(OfferView.OfferRest.class)
	@Min(value=1, message="{offer.neededPoints.invalidMinValue}")
	private int neededPoints;
	
	/**
	 * Gets the default photo.
	 *
	 * @return the default photo
	 */
	public OfferPhoto getDefaultPhoto() {
		
		if(this.offerPhotos != null && this.offerPhotos.size() > 0)
			defaultPhoto = this.offerPhotos.get(0);
		
		return defaultPhoto;
	}

	/**
	 * Sets the default photo.
	 *
	 * @param defaultPhoto the new default photo
	 */
	public void setDefaultPhoto(OfferPhoto defaultPhoto) {
		this.defaultPhoto = defaultPhoto;
	}

	/** The day of weeks. */
	//bi-directional many-to-many association to DayOfWeek
	@ManyToMany
	@JoinTable(
		name="offer_has_day_of_week"
		, joinColumns={
			@JoinColumn(name="offer_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="day_of_week_id")
			}
		)
	private List<DayOfWeek> dayOfWeeks;

	/** The restaurant. */
	//bi-directional many-to-one association to Restaurant
	@ManyToOne(fetch=FetchType.EAGER)
	private Restaurant restaurant;
	
	/** NIKLAS KLOTZ */ 
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "course_id")
	@JsonView(OfferView.OfferRest.class)
	private CourseTypes courseType;

	/** The offer photos. */
	//bi-directional many-to-one association to OfferPhoto
	@OneToMany(mappedBy="offer", cascade=CascadeType.ALL, orphanRemoval=true )
	private List<OfferPhoto> offerPhotos;

	/** Is sold out */
	@Column(name="sold_out")
	@JsonView(OfferView.OfferRest.class)
	private boolean sold_out;

	/**
	 * Instantiates a new offer.
	 */
	public Offer() {
		
		this.offerPhotos = new ArrayList<OfferPhoto>();
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
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the preparation time.
	 *
	 * @return the preparation time
	 */
	public int getPreparationTime() {
		return this.preparationTime;
	}

	/**
	 * Sets the preparation time.
	 *
	 * @param preparationTime the new preparation time
	 */
	public void setPreparationTime(int preparationTime) {
		this.preparationTime = preparationTime;
	}

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public float getPrice() {
		return this.price;
	}

	/**
	 * Sets the price.
	 *
	 * @param price the new price
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	/**
	 * Gets the offer photos.
	 *
	 * @return the offer photos
	 */
	public List<OfferPhoto> getOfferPhotos() {
		return this.offerPhotos;
	}

	/**
	 * Sets the offer photos.
	 *
	 * @param offerPhotos the new offer photos
	 */
	public void setOfferPhotos(List<OfferPhoto> offerPhotos) {
		this.offerPhotos = offerPhotos;
	}

	/**
	 * Adds the offer photo.
	 *
	 * @param offerPhoto the offer photo
	 * @return the offer photo
	 */
	public OfferPhoto addOfferPhoto(OfferPhoto offerPhoto) {
		getOfferPhotos().add(offerPhoto);
		offerPhoto.setOffer(this);

		return offerPhoto;
	}

	/**
	 * Removes the offer photo.
	 *
	 * @param offerPhoto the offer photo
	 * @return the offer photo
	 */
	public OfferPhoto removeOfferPhoto(OfferPhoto offerPhoto) {
		getOfferPhotos().remove(offerPhoto);
		offerPhoto.setOffer(null);

		return offerPhoto;
	}
	
	/**
	 * Gets the neededPoints for an offer.
	 * @return The needed points
	 */
	public int getNeededPoints() {
		return neededPoints;
	}
	
	/**
	 * Sets the new needed points for an offer.
	 * @param neededPoints The needed points to set
	 */
	public void setNeededPoints(int neededPoints) {
		this.neededPoints = neededPoints;
	}
	
	public CourseTypes getCourseTypes() {
		return courseType;
	}

	public void setCourseTypes(CourseTypes courseType) {
		this.courseType = courseType;
	}
	public CourseTypes getCourseType() {
		return courseType;
	}

	public void setCourseType(CourseTypes courseType) {
		this.courseType = courseType;
	}

	public boolean getSold_out() {
		return sold_out;
	}

	public void setSold_out(boolean sold_out) {
		this.sold_out = sold_out;
	}

}