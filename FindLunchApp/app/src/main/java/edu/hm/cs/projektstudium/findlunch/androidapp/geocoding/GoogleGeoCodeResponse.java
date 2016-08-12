package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The type GoogleGeoCodeResponse represents
 * a response of the Google Geocoding API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleGeoCodeResponse {
    /**
     * The status of the request,
     * that may contain debugging information.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Status status;
    /**
     * The Results. Even if the geocoder returns
     * no result (such as if the address doesn't exist)
     * it still returns an empty result array.
     */
    private Results[] results;

    /**
     * Sets status.
     *
     * @param status the status
     */
    @SuppressWarnings("unused")
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get results results [ ].
     *
     * @return the results [ ]
     */
    public Results[] getResults() {
        return results;
    }

    /**
     * Sets results.
     *
     * @param results the results
     */
    @SuppressWarnings("unused")
    public void setResults(Results[] results) {
        this.results = results;
    }
}
