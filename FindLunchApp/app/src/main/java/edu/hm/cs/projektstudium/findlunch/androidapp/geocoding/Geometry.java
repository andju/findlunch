package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The type Geometry
 * represents the details of the location in
 * a response of the Google Geocoding API.
 */
public class Geometry {
    /**
     * The Bounds stores the
     * bounding box which can
     * fully contain the returned result.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Bounds bounds;
    /**
     * The Location type
     * stores additional data
     * about the specified location.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @JsonProperty("location_type")
    private LocationType locationType;
    /**
     * The Location
     * contains the geocoded
     * latitude and longitude value.
     */
    private Location location;
    /**
     * The Viewport contains the recommended
     * viewport for displaying the returned result,
     * specified as two latitude,longitude values
     * defining the southwest and northeast corner
     * of the viewport bounding box.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Bounds viewport;

    /**
     * Sets bounds.
     *
     * @param bounds the bounds
     */
    @SuppressWarnings("unused")
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    /**
     * Sets location type.
     *
     * @param locationType the location type
     */
    @SuppressWarnings("unused")
    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    @SuppressWarnings("unused")
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sets viewport.
     *
     * @param viewport the viewport
     */
    @SuppressWarnings("unused")
    public void setViewport(Bounds viewport) {
        this.viewport = viewport;
    }
}
