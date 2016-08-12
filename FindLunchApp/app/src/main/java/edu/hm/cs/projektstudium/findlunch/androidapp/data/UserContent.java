package edu.hm.cs.projektstudium.findlunch.androidapp.data;

import android.content.Context;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;


/**
 * Representation of the
 * information provided by the user.
 */
public class UserContent {

    /**
     * The Street.
     */
    private String street;
    /**
     * The Street number.
     */
    private String streetNumber;
    /**
     * The Zip.
     */
    private String zip;
    /**
     * The Distance.
     */
    private int distance;
    /**
     * The Longitude.
     */
    private float longitude;
    /**
     * The Latitude.
     */
    private float latitude;

    /**
     * The Context of the activity.
     */
    private final Context context;
    /**
     * The Search location entered.
     */
    private UserLocationContent searchLocationEntered;
    /**
     * The constant instance.
     */
    private static UserContent instance;

    /**
     * Gets instance.
     *
     * @param context the Context of the activity.
     * @return the instance
     */
    public static UserContent getInstance(Context context) {
        if (instance == null) {
            instance = new UserContent(context);
        }
        return instance;
    }

    /**
     * Instantiates a new User content.
     *
     * @param context the context
     */
    private UserContent(Context context) {
        this.context = context;
    }

    /**
     * Sets the information provided by the user.
     *
     * @param street       the street
     * @param streetNumber the street number
     * @param zip          the zip
     * @param distance     the distance
     */
    public void setInformation(String street, String streetNumber, String zip, int distance) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.zip = zip;
        this.distance = distance;
    }

    /**
     * Gets a concatenated header based on
     * the location information provided of the user.
     *
     * @return the header
     */
    public String getHeader() {
        return this.getStreet() + " "
                + this.getStreetNumber() + ", "
                + this.getZip() + ". " + context.getResources().getString(R.string.text_raduis)  + ": "
                + this.getDistance() + " " + context.getResources().getString(R.string.text_meters);
    }

    /**
     * Gets street.
     *
     * @return the street
     */
    private String getStreet() {
        return street;
    }

    /**
     * Gets street number.
     *
     * @return the street number
     */
    private String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Gets zip.
     *
     * @return the zip
     */
    private String getZip() {
        return zip;
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
     * Gets longitude.
     *
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets search location entered.
     *
     * @return the search location entered
     */
    public UserLocationContent getSearchLocationEntered() {
        return searchLocationEntered;
    }

    /**
     * Sets search location entered.
     *
     * @param searchLocationEntered the search location entered
     */
    public void setSearchLocationEntered(UserLocationContent searchLocationEntered) {
        this.searchLocationEntered = searchLocationEntered;
    }
}
