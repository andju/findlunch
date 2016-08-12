package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;


/**
 * The enum Status
 * represents the status of the request in
 * a response of the Google Geocoding API.
 */
@SuppressWarnings("unused")
public enum Status {
    /**
     * Ok status indicates that
     * no errors occurred; the
     * address was successfully
     * parsed and at least one
     * geocode was returned.
     */
    OK, /**
     * Zero results status
     * indicates that the geocode
     * was successful but returned
     * no results. This may occur
     * if the geocoder was passed
     * a non-existent address.
     */
    ZERO_RESULTS, /**
     * Over query limit status
     * indicates that you
     * are over your quota.
     */
    OVER_QUERY_LIMIT, /**
     * Request denied status
     * indicates that your
     * request was denied.
     */
    REQUEST_DENIED, /**
     * Invalid request status
     * generally indicates that
     * the query
     * (address, components or latlng)
     * is missing.
     */
    INVALID_REQUEST, /**
     * Unknown error status
     * indicates that the request
     * could not be processed due
     * to a server error.
     * The request may succeed
     * if you try again.
     */
    UNKNOWN_ERROR
}
