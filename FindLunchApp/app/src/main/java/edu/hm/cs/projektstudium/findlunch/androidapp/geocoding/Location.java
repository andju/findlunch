package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;


/**
 * The type Location
 * contains the geocoded
 * latitude and longitude value
 * in a response of the Google Geocoding API.
 */
@SuppressWarnings("unused")
public class Location {
    /**
     * The latitude
     * of the geocoded location.
     */
    private float lat;
    /**
     * The longitude
     * of the geocoded location.
     */
    private float lng;

    /**
     * Gets the latitude
     * of the geocoded location.
     *
     * @return the latitude
     */
    public float getLat() {
        return lat;
    }

    /**
     * Sets the latitude
     * of the geocoded location.
     *
     * @param lat the latitude
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

    /**
     * Gets longitude
     * of the geocoded location.
     *
     * @return the longitude
     */
    public float getLng() {
        return lng;
    }

    /**
     * Sets longitude
     * of the geocoded location.
     *
     * @param lng the longitude
     */
    public void setLng(float lng) {
        this.lng = lng;
    }
}
