package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.io.Serializable;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;

/**
 * The Class Restaurant.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView({RestaurantView.RestaurantRest.class, ReservationView.ReservationRest.class})
	private int id;

	/** The city. */
	@JsonView({RestaurantView.RestaurantRest.class, ReservationView.ReservationRest.class})
	@NotBlank(message = "{restaurant.city.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.city.sizeError}")
	@Pattern(regexp = "[\\p{L} ]*", message="{restaurant.city.patternMismatch}")
	private String city;

	/** The email. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotBlank(message = "{restaurant.email.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.email.sizeError}")
	private String email;

	/** The location latitude. */
	@Column(name = "location_latitude")
	@JsonView(RestaurantView.RestaurantRest.class)
	private float locationLatitude;

	/** The location longitude. */
	@Column(name = "location_longitude")
	@JsonView(RestaurantView.RestaurantRest.class)
	private float locationLongitude;

	/** The name. */
	@JsonView({RestaurantView.RestaurantRest.class, ReservationView.ReservationRest.class})
	@NotBlank(message = "{restaurant.name.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.name.sizeError}")
	@Pattern(regexp = "[\\p{L}0-9-&´`'\"(). ]*", message="{restaurant.name.patternMismatch}")
	private String name;

	/** The phone. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotBlank(message = "{restaurant.phone.notBlank}")
	@Size(min=3, max=60, message= "{restaurant.phone.sizeError}")
	@Pattern(regexp = "[0-9+/()\\- ]{1,}", message="{restaurant.phone.patternMismatch}")
	private String phone;

	/** The street. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotBlank(message = "{restaurant.street.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.street.sizeError}")
	private String street;

	/** The street number. */
	@Column(name = "street_number")
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotBlank(message = "{restaurant.streetNumber.notBlank}")
	@Pattern(regexp = "[1-9]{1}[0-9]{0,3}[a-zA-Z]?(-[1-9]{1}[0-9]{0,3}[a-zA-Z]?)?", message="{restaurant.streetNumber.patternMismatch}")
	@Size(min=1, max=11, message= "{restaurant.streetNumber.sizeError}")
	private String streetNumber;

	/** The url. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@URL(message = "{restaurant.urlInvalid}")
	@Size(max=60, message= "{restaurant.url.sizeError}")
	private String url;

	/** The zip. */
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotBlank(message = "{restaurant.zip.notBlank}")
	@NumberFormat(style = Style.NUMBER, pattern = "#####")
	@Size(min=5, max=5, message = "{restaurant.zip.size}")
	@Pattern(regexp = "^([0]{1}[1-9]{1}|[1-9]{1}[0-9]{1})[0-9]{3}$", message="{restaurant.zip.patternMismatch}")
	private String zip;

	/** The offers. */
	// bi-directional many-to-one association to Offer
	@OneToMany(mappedBy = "restaurant")
	private List<Offer> offers;

	/** The country. */
	// bi-directional many-to-one association to Country
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "country_code")
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotNull(message = "{restaurant.country.notNull}")
	private Country country;

	/** The kitchen types. */
	// bi-directional many-to-many association to KitchenType
	@ManyToMany
	@JoinTable(name = "restaurant_has_kitchen_type", joinColumns = {
	@JoinColumn(name = "restaurant_id") }, inverseJoinColumns = { @JoinColumn(name = "kitchen_type_id") })
	@JsonView(RestaurantView.RestaurantRest.class)
	@NotEmpty(message = "{restaurant.kitchenType.notEmpty}")
	private List<KitchenType> kitchenTypes;

	/** The restaurant type. */
	// bi-directional many-to-one association to RestaurantType
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "restaurant_type_id")
	@JsonView(RestaurantView.RestaurantRest.class)
	private RestaurantType restaurantType;

	/** The time schedules. */
	// bi-directional many-to-one association to TimeSchedule
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView(RestaurantView.RestaurantRest.class)
	private List<TimeSchedule> timeSchedules;

	/** The admins. */
	// bi-directional many-to-one association to User
	@JsonIgnore
	@OneToMany(mappedBy = "restaurant",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<User> admins;
	
	
	/** The fav users. */
	@ManyToMany
	@JoinTable(name = "favorites", joinColumns = {
	@JoinColumn(name = "restaurant_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<User> favUsers;
	
	/** The default Logo. */
	@Transient
	@JsonView(RestaurantView.RestaurantRest.class)
	private RestaurantLogo defaultLogo;
	
	/** The distance. */
	@Transient
	@JsonView(RestaurantView.RestaurantRest.class)
	private int distance;
	
	/** Is favorite restaurant.*/
	@Transient
	@JsonView(RestaurantView.RestaurantRest.class)
	private boolean isFavorite;

	/** The points of restaurant.*/
	@OneToMany(mappedBy="compositeKey.restaurant", cascade= CascadeType.ALL)
	private List<Points>  restaurantPoints;
	
	/** The actual point.*/
	@Transient
	@JsonView(RestaurantView.RestaurantRest.class)
	private int actualPoints;
	
	/** The reservations.*/
	@OneToMany(mappedBy="restaurant")
	List<Reservation> reservation;
	
	/** The uuid of the restaurant.*/
	private String restaurantUuid;

	/** The qr-code in bytes.*/
	@Lob
	private byte[] qrUuid;
	
	/** The qr-code in base64.*/
	@Transient
	private String base64Encoded;
	
	/** The customer id.*/
	private int customerId;
	
	/** The donations of the restaurant.*/
	@OneToMany(mappedBy="restaurant", fetch = FetchType.EAGER)
	private List<DonationPerMonth> donations;
	
	/** The bills of the restaurant.*/
	@OneToMany(mappedBy="restaurant")
	private List<Bill> bills;

	/** The offer photos. */
	//bi-directional many-to-one association to OfferPhoto
	@OneToMany(mappedBy="restaurant", cascade=CascadeType.ALL, orphanRemoval=true )
	private List<RestaurantLogo> restaurantLogos;
	
	@JsonView(RestaurantView.RestaurantRest.class)
	@Transient
	private boolean currentlyOpen;
	
	/**
	 * Instantiates a new restaurant.
	 */
	public Restaurant() {
		this.admins = new ArrayList<User>();

	}
	
	public RestaurantLogo getDefaultLogo() {
		if(this.restaurantLogos != null && this.restaurantLogos.size() > 0)
			defaultLogo = this.restaurantLogos.get(0);
		
		return defaultLogo;
	}

	public void setDefaultLogo(RestaurantLogo defaultLogo) {
		this.defaultLogo = defaultLogo;
	}

	public List<RestaurantLogo> getRestaurantLogos() {
		return restaurantLogos;
	}

	public void setRestaurantLogos(List<RestaurantLogo> restaurantLogos) {
		this.restaurantLogos = restaurantLogos;
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
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the location latitude.
	 *
	 * @return the location latitude
	 */
	public float getLocationLatitude() {
		return this.locationLatitude;
	}

	/**
	 * Sets the location latitude.
	 *
	 * @param locationLatitude the new location latitude
	 */
	public void setLocationLatitude(float locationLatitude) {
		this.locationLatitude = locationLatitude;
	}

	/**
	 * Gets the location longitude.
	 *
	 * @return the location longitude
	 */
	public float getLocationLongitude() {
		return this.locationLongitude;
	}

	/**
	 * Sets the location longitude.
	 *
	 * @param locationLongitude the new location longitude
	 */
	public void setLocationLongitude(float locationLongitude) {
		this.locationLongitude = locationLongitude;
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
	 * Gets the phone.
	 *
	 * @return the phone
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * Sets the phone.
	 *
	 * @param phone the new phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the street.
	 *
	 * @return the street
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * Sets the street.
	 *
	 * @param street the new street
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Gets the street number.
	 *
	 * @return the street number
	 */
	public String getStreetNumber() {
		return this.streetNumber;
	}

	/**
	 * Sets the street number.
	 *
	 * @param streetNumber the new street number
	 */
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the zip.
	 *
	 * @return the zip
	 */
	public String getZip() {
		return this.zip;
	}

	/**
	 * Sets the zip.
	 *
	 * @param zip the new zip
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * Gets the offers.
	 *
	 * @return the offers
	 */
	public List<Offer> getOffers() {
		return this.offers;
	}

	/**
	 * Sets the offers.
	 *
	 * @param offers the new offers
	 */
	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	/**
	 * Adds the offer.
	 *
	 * @param offer the offer
	 * @return the offer
	 */
	public Offer addOffer(Offer offer) {
		getOffers().add(offer);
		offer.setRestaurant(this);

		return offer;
	}

	/**
	 * Removes the offer.
	 *
	 * @param offer the offer
	 * @return the offer
	 */
	public Offer removeOffer(Offer offer) {
		getOffers().remove(offer);
		offer.setRestaurant(null);

		return offer;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public Country getCountry() {
		return this.country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(Country country) {
		this.country = country;
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

	/**
	 * Gets the restaurant type.
	 *
	 * @return the restaurant type
	 */
	public RestaurantType getRestaurantType() {
		return this.restaurantType;
	}

	/**
	 * Sets the restaurant type.
	 *
	 * @param restaurantType the new restaurant type
	 */
	public void setRestaurantType(RestaurantType restaurantType) {
		this.restaurantType = restaurantType;
	}

	/**
	 * Gets the time schedules.
	 *
	 * @return the time schedules
	 */
	public List<TimeSchedule> getTimeSchedules() {
		return this.timeSchedules;
	}

	/**
	 * Sets the time schedules.
	 *
	 * @param timeSchedules the new time schedules
	 */
	public void setTimeSchedules(List<TimeSchedule> timeSchedules) {
		if(this.timeSchedules == null)
			this.timeSchedules = timeSchedules;
		else
		{
			this.timeSchedules.clear();
			if(timeSchedules != null)
				this.timeSchedules.addAll(timeSchedules);
		}
	}

	/**
	 * Adds the time schedule.
	 *
	 * @param timeSchedule the time schedule
	 * @return the time schedule
	 */
	public TimeSchedule addTimeSchedule(TimeSchedule timeSchedule) {
		getTimeSchedules().add(timeSchedule);
		timeSchedule.setRestaurant(this);

		return timeSchedule;
	}

	/**
	 * Removes the time schedule.
	 *
	 * @param timeSchedule the time schedule
	 * @return the time schedule
	 */
	public TimeSchedule removeTimeSchedule(TimeSchedule timeSchedule) {
		getTimeSchedules().remove(timeSchedule);
		timeSchedule.setRestaurant(null);

		return timeSchedule;
	}

	/**
	 * Gets the admins.
	 *
	 * @return the admins
	 */
	public List<User> getAdmins() {
		return this.admins;
	}

	/**
	 * Sets the admins.
	 *
	 * @param admins the new admins
	 */
	public void setAdmins(List<User> admins) {
		this.admins = admins;
	}

	/**
	 * Adds an admin.
	 *
	 * @param admin the admin
	 * @return the user
	 */
	public User addAdmin(User admin) {
		getAdmins().add(admin);
		admin.setAdministratedRestaurant(this);

		return admin;
	}

	/**
	 * Removes an admin.
	 *
	 * @param admin the admin
	 * @return the user
	 */
	public User removeAdmin(User admin) {
		getAdmins().remove(admin);
		admin.setAdministratedRestaurant(null);

		return admin;
	}

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Sets the distance.
	 *
	 * @param distance the new distance
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * Gets the fav users.
	 *
	 * @return the fav users
	 */
	public List<User> getFavUsers() {
		return this.favUsers;
	}

	/**
	 * Sets the fav user.
	 *
	 * @param users the new fav user
	 */
	public void setFavUser(List<User> users) {
		this.favUsers = users;
	}

	/**
	 * Checks if the restaurant is a favorite of the user.
	 *
	 * @return true, if is favorite
	 */
	public boolean isFavorite() {
		return isFavorite;
	}

	/**
	 * Sets the restaurant as favorite for a user (transient).
	 *
	 * @param isFavorite the new favorite
	 */
	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
	
	/**
	 * Gets points of restaurant.
	 * @return List of points
	 */
	public List<Points> getRestaurantPoints(){
		return restaurantPoints;
	}
	
	/**
	 * Sets the points of restaurant.
	 * @param restaurantPoints List of points to set
	 */
	public void setRestaurantPoints(List<Points> restaurantPoints){
		this.restaurantPoints = restaurantPoints;
	}
	
	/**
	 * Gets the actual point (transient).
	 * @return current points
	 */
	public int getActuallPoints() {
		return actualPoints;
	}

	/**
	 * Sets the acutalPoints.
	 * @param actuallPoints actualPoints to set
	 */
	public void setActuallPoints(int actuallPoints) {
		this.actualPoints = actuallPoints;
	}
	
	/**
	 * Gets the qr-code uuid as byte array.
	 * @return the qr-code as a byte array
	 */
	public byte[] getQrUuid() {
		return qrUuid;
	}

	/**
	 * Sets the qr-code
	 * @param qrUuid bytes to set
	 */
	public void setQrUuid(byte[] qrUuid) {
		this.qrUuid = qrUuid;
	}
	
	/**
	 * Gets the uuid from restaurant.
	 * @return Uuid form restaurant
	 */
	public String getRestaurantUuid() {
		return restaurantUuid;
	}

	/**
	 * Sets the uuid from restaurant
	 * @param restaurantUuid Uuid to set
	 */
	public void setRestaurantUuid(String restaurantUuid) {
		this.restaurantUuid = restaurantUuid;
	}
	
	/**
	 * Gets the base64 encoded qr-code
	 * @return encoded qr-code
	 */
	public String getBase64Encoded() {
		return base64Encoded;
	}
	
	/**
	 * Sets the base64 encoded qr-code.
	 * @param base64Encoded the base64 encoded code
	 */
	public void setBase64Encoded(String base64Encoded) {
		this.base64Encoded = base64Encoded;
	}
	
	/**
	 * Gets the donations from a restaurant.
	 * @return the donations
	 */
	public List<DonationPerMonth> getDonations() {
		return donations;
	}
	
	/**
	 * Sets the donations from a restaurant.
	 * @param donations List of donations to set
	 */
	public void setDonations(List<DonationPerMonth> donations) {
		this.donations = donations;
	}
	
	/**
	 * Add a new donationPerMonth.
	 * @param donationPerMonth donation to add
	 * @return added donation
	 */
	public DonationPerMonth addDonationPerMonth(DonationPerMonth donationPerMonth) {
		getDonations().add(donationPerMonth);
		donationPerMonth.setRestaurant(this);

		return donationPerMonth;
	}

	/**
	 * Gets the bills from a restaurant.
	 * @return bills of the restaurant
	 */
	public List<Bill> getBills() {
		return bills;
	}

	/**
	 * Sets the bills from a restaurant.
	 * @param bills Bills to set
	 */
	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

	/**
	 * Gets the customer id from a restaurant.
	 * @return cusomerId from restaurant
	 */
	public int getCustomerId() {
		return customerId;
	}

	/**
	 * Sets the customerId from a restaurant.
	 * @param customerId CustomerId to set
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	/**
	 * Gets the reservations.
	 * @return the reservations
	 */
	public List<Reservation> getReservation() {
		return reservation;
	}

	/**
	 * Sets the reservations.
	 * @param reservation the reservations
	 */
	public void setReservation(List<Reservation> reservation) {
		this.reservation = reservation;
	}
	
	/**
	 * Adds the restaurant logo.
	 *
	 * @param RestaurantLogo the restuarant logo
	 * @return the restaurant logo
	 */
	public RestaurantLogo addRestaurantLogo(RestaurantLogo restaurantLogo) {
		getRestaurantLogos().add(restaurantLogo);
		restaurantLogo.setRestaurant(this);

		return restaurantLogo;
	}

	/**
	 * Removes the restaurant logo.
	 *
	 * @param RestaurantLogo the restaurant logo
	 * @return the restaurant logo
	 */
	public RestaurantLogo removeRestaurantLogo(RestaurantLogo restaurantLogo) {
		getRestaurantLogos().remove(restaurantLogo);
		restaurantLogo.setRestaurant(null);

		return restaurantLogo;
	}

	/**
	 * @return the isOpen
	 */
	public boolean getCurrentlyOpen() {
		
		int openHour = 0;
		int openMin = 0;
		int closeHour = 0;
		int closeMin = 0;
		
		for (TimeSchedule timeSchedule : this.timeSchedules) {
			Date actuallyDate = new Date();
			if(actuallyDate.getDay()+1==timeSchedule.getDayOfWeek().getDayNumber()){
				
				// check if there is a openingtime for the day
				List<OpeningTime> openingTimes = timeSchedule.getOpeningTimes();
				if(!openingTimes.isEmpty()){
					for(OpeningTime opening : openingTimes) {
						if(opening.getTimeSchedule().getId()==timeSchedule.getId()){
							openHour = opening.getOpeningTime().getHours();
							openMin = opening.getOpeningTime().getMinutes();
							closeHour = opening.getClosingTime().getHours();
							closeHour = opening.getClosingTime().getMinutes();
						}
						else {
							openHour = timeSchedule.getOfferStartTime().getHours();
							openMin = timeSchedule.getOfferStartTime().getMinutes();
							closeHour = timeSchedule.getOfferEndTime().getHours();
							closeMin = timeSchedule.getOfferEndTime().getMinutes();
						}
					}
				}
				else {
					openHour = timeSchedule.getOfferStartTime().getHours();
					openMin = timeSchedule.getOfferStartTime().getMinutes();
					closeHour = timeSchedule.getOfferEndTime().getHours();
					closeMin = timeSchedule.getOfferEndTime().getMinutes();
				}
				
				int nowHour = actuallyDate.getHours();
				int nowMin = actuallyDate.getMinutes();
				
				if(openHour > nowHour || (openHour == nowHour && openMin > nowMin )){
					return false;
				}
				
				if(closeHour < nowHour || (closeHour == nowHour && closeMin < nowMin )){
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * @param isOpen the isOpen to set
	 */
	public void setcurrentlyOpen(boolean currentlyOpen) {
		this.currentlyOpen = currentlyOpen;
	}
}