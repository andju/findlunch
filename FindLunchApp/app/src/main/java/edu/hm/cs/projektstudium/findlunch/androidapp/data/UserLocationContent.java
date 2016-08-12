package edu.hm.cs.projektstudium.findlunch.androidapp.data;

/**
 * Representation of the location based
 * search information provided by the user.
 */
public class UserLocationContent {
    /**
     * The street to search at
     */
    private final String street;
    /**
     * The street number to search at
     */
    private final String streetNumber;
    /**
     * The zip to search at
     */
    private final String zip;
    /**
     * The distance to search
     */
    private final String distance;

    /**
     * The constant separationSign.
     */
    private static final String separationSign = "+";

    /**
     * Gets street.
     *
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Gets street number.
     *
     * @return the street number
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Gets zip.
     *
     * @return the zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Representation of the location based
     * search information provided by the user.
     *
     * @param street       The street of the location.
     * @param streetNumber The street number of the location.
     * @param zip          The zip of the location.
     * @param distance     The distance to search around the location.
     */
    public UserLocationContent(String street, String streetNumber, String zip, String distance) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.zip = zip;
        this.distance = distance;
    }

    /**
     * Gets location name.
     *
     * @return the location name
     */
    public String getLocationName() {
        // create string builder for the location name
        return getStreet() +
                separationSign +
                getStreetNumber() +
                "," +
                separationSign +
                getZip() +
                separationSign;
    }
}
