package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The type Address component in
 * a response of the Google Geocoding API.
 */
public class AddressComponent {
    /**
     * The Long name is the
     * full text description or name
     * of the address component
     * as returned by the Geocoder.
     */
    @JsonProperty("long_name")
    private String longName;
    /**
     * The Short name
     * is an abbreviated textual name
     * for the address component,
     * if available.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @JsonProperty("short_name")
    private String shortName;
    /**
     * The Types is an array indicating
     * the type of the address component.
     */
    private String[] types;

    /**
     * Gets long name.
     *
     * @return the long name
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Sets long name.
     *
     * @param longName the long name
     */
    @SuppressWarnings("unused")
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Sets short name.
     *
     * @param shortName the short name
     */
    @SuppressWarnings("unused")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Get types string [ ].
     *
     * @return the string [ ]
     */
    public String[] getTypes() {
        return types;
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
}
