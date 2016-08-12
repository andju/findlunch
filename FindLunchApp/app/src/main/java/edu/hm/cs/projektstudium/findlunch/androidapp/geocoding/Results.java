package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The type Results
 * represents the results in
 * a response of the Google Geocoding API.
 */
public class Results {
    /**
     * The Formatted address is a string
     * that contains a human-readable address of the location,
     * often equal to the "postal address".
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    @JsonProperty("formatted_address")
    private String formattedAddress;
    /**
     * The Geometry.
     */
    private Geometry geometry;
    /**
     * The Types array indicates the type
     * of the result returned. It contains a set
     * of zero or more tags identifying the type
     * of feature returned in the result.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private String[] types;
    /**
     * The Address components
     * contains the separate address components.
     */
    @JsonProperty("address_components")
    private AddressComponent[] addressComponents;
    /**
     * The Partial match indicates that the geocoder
     * did not return an exact match for the original request,
     * though it was able to match part of the requested address.
     * You may wish to examine the original request
     * for misspellings and/or an incomplete address.
     */
    @JsonProperty("partial_match")
    private boolean partialMatch;
    /**
     * The Place id is a unique identifier
     * that can be used with other Google APIs,
     * p. ex. the Google Places API.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    @JsonProperty("place_id")
    private String placeId;


    /**
     * Sets formatted address.
     *
     * @param formattedAddress the formatted address
     */
    @SuppressWarnings("unused")
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    /**
     * Gets geometry.
     *
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets geometry.
     *
     * @param geometry the geometry
     */
    @SuppressWarnings("unused")
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Sets types.
     *
     * @param types the types
     */
    @SuppressWarnings("unused")
    public void setTypes(String[] types) {
        this.types = types;
    }

    /**
     * Get address components address components [ ].
     *
     * @return the address components [ ]
     */
    public AddressComponent[] getAddressComponents() {
        return addressComponents;
    }

    /**
     * Sets address components.
     *
     * @param addressComponents the address components
     */
    @SuppressWarnings("unused")
    public void setAddressComponents(AddressComponent[] addressComponents) {
        this.addressComponents = addressComponents;
    }

    /**
     * Is partial match boolean.
     *
     * @return the boolean
     */
    public boolean isPartialMatch() {
        return partialMatch;
    }

    /**
     * Sets partial match.
     *
     * @param partialMatch the partial match
     */
    @SuppressWarnings("unused")
    public void setPartialMatch(boolean partialMatch) {
        this.partialMatch = partialMatch;
    }

    /**
     * Sets place id.
     *
     * @param placeId the place id
     */
    @SuppressWarnings("unused")
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
