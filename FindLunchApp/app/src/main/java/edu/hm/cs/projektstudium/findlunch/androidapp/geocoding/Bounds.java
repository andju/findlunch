package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;


/**
 * The type Bounds in
 * a response of the Google Geocoding API,
 * that stores the bounding box which can
 * fully contain the returned result.
 */
@SuppressWarnings("unused")
public class Bounds {
    /**
     * The Northeast location.
     */
    private Location northeast;
    /**
     * The Southwest location.
     */
    private Location southwest;

    /**
     * Gets northeast.
     *
     * @return the northeast
     */
    public Location getNortheast() {
        return northeast;
    }

    /**
     * Sets northeast.
     *
     * @param northeast the northeast
     */
    public void setNortheast(Location northeast) {
        this.northeast = northeast;
    }

    /**
     * Gets southwest.
     *
     * @return the southwest
     */
    public Location getSouthwest() {
        return southwest;
    }

    /**
     * Sets southwest.
     *
     * @param southwest the southwest
     */
    public void setSouthwest(Location southwest) {
        this.southwest = southwest;
    }
}
