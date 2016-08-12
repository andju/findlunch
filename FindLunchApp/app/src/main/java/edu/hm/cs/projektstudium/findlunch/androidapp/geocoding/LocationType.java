package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;


/**
 * The enum Location type
 * stores additional data
 * about the specified location
 */
@SuppressWarnings("unused")
public enum LocationType {
    /**
     * Rooftop location type
     * indicates that the returned result is
     * a precise geocode for which we have
     * location information accurate down
     * to street address precision.
     */
    ROOFTOP, /**
     * Range interpolated location type
     * indicates that the returned result
     * reflects an approximation (usually on a road)
     * interpolated between two precise points
     * (such as intersections).
     * Interpolated results are generally returned
     * when rooftop geocodes are unavailable
     * for a street address.
     */
    RANGE_INTERPOLATED, /**
     * Geometric center location type
     * indicates that the returned result is the
     * geometric center of a result such as
     * a polyline (for example, a street)
     * or polygon (region).
     */
    GEOMETRIC_CENTER, /**
     * Approximate location type
     * indicates that the returned
     * result is approximate.
     */
    APPROXIMATE
}
