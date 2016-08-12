package edu.hm.cs.projektstudium.findlunch.androidapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The type Restaurant.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {

    /**
     * The Id.
     */
    private int id;
    /**
     * The City.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String city;
    /**
     * The Email.
     */
    private String email;
    /**
     * The Location latitude.
     */
    private float locationLatitude;
    /**
     * The Location longitude.
     */
    private float locationLongitude;
    /**
     * The Name.
     */
    private String name;
    /**
     * The Phone.
     */
    private String phone;
    /**
     * The Street.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String street;
    /**
     * The Street number.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String streetNumber;
    /**
     * The Url.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String url;
    /**
     * The Zip.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String zip;
    /**
     * The Offers.
     */
    private List<Offer> offers;
    /**
     * The Country.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Country country;
    /**
     * The Kitchen types.
     */
    private List<KitchenType> kitchenTypes;
    /**
     * The Restaurant type.
     */
    private RestaurantType restaurantType;
    /**
     * The Time schedules.
     */
    private List<TimeSchedule> timeSchedules;
    /**
     * The Distance.
     */
    private int distance;
    /**
     * The restaurant is
     * a Favourite of the user
     */
    private boolean isFavorite;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    @SuppressWarnings("unused")
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    @SuppressWarnings("unused")
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets location latitude.
     *
     * @return the location latitude
     */
    public float getLocationLatitude() {
        return locationLatitude;
    }

    /**
     * Sets location latitude.
     *
     * @param locationLatitude the location latitude
     */
    @SuppressWarnings("unused")
    public void setLocationLatitude(float locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    /**
     * Gets location longitude.
     *
     * @return the location longitude
     */
    public float getLocationLongitude() {
        return locationLongitude;
    }

    /**
     * Sets location longitude.
     *
     * @param locationLongitude the location longitude
     */
    @SuppressWarnings("unused")
    public void setLocationLongitude(float locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    @SuppressWarnings("unused")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Sets street.
     *
     * @param street the street
     */
    @SuppressWarnings("unused")
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Sets street number.
     *
     * @param streetNumber the street number
     */
    @SuppressWarnings("unused")
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Sets zip.
     *
     * @param zip the zip
     */
    @SuppressWarnings("unused")
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    @SuppressWarnings("unused")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets offers.
     *
     * @return the offers
     */
    public List<Offer> getOffers() {
        return offers;
    }

    /**
     * Sets offers.
     *
     * @param offers the offers
     */
    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    @SuppressWarnings("unused")
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Gets kitchen types.
     *
     * @return the kitchen types
     */
    public List<KitchenType> getKitchenTypes() {
        return kitchenTypes;
    }

    /**
     * Sets kitchen types.
     *
     * @param kitchenTypes the kitchen types
     */
    @SuppressWarnings("unused")
    public void setKitchenTypes(List<KitchenType> kitchenTypes) {
        this.kitchenTypes = kitchenTypes;
    }

    /**
     * Gets restaurant type.
     *
     * @return the restaurant type
     */
    public RestaurantType getRestaurantType() {
        return restaurantType;
    }

    /**
     * Sets restaurant type.
     *
     * @param restaurantType the restaurant type
     */
    @SuppressWarnings("unused")
    public void setRestaurantType(RestaurantType restaurantType) {
        this.restaurantType = restaurantType;
    }

    /**
     * Gets time schedules.
     *
     * @return the time schedules
     */
    public List<TimeSchedule> getTimeSchedules() {
        return timeSchedules;
    }

    /**
     * Sets time schedules.
     *
     * @param timeSchedules the time schedules
     */
    @SuppressWarnings("unused")
    public void setTimeSchedules(List<TimeSchedule> timeSchedules) {
        this.timeSchedules = timeSchedules;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Sets distance.
     *
     * @param distance the distance
     */
    @SuppressWarnings("unused")
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * Is favorite boolean.
     *
     * @return the boolean
     */
    public boolean isFavorite() {
        return isFavorite;
    }

    /**
     * Sets is favorite.
     *
     * @param favorite the favorite
     */
    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
