package edu.hm.cs.projektstudium.findlunch.androidapp.geocoding;

/**
 * Possible values of the attribute type
 * in a response of the Google Geocoding API.
 * The values can change from time to time,
 * so the enumeration might be incomplete
 * and should not be used for deserialisation
 * of the values of type.
 */
@SuppressWarnings("unused")
public enum Type {
    /**
     * Street number type.
     */
    street_number, /**
     * Street address type
     * indicates a precise street address.
     */
    street_address, /**
     * Route type
     * indicates a named route
     * (such as "US 101").
     */
    route, /**
     * Intersection type
     * indicates a major intersection,
     * usually of two major roads.
     */
    intersection, /**
     * Political type
     * indicates a political entity.
     * Usually, this type indicates
     * a polygon of some civil administration.
     */
    political, /**
     * Country type
     * indicates the national political entity,
     * and is typically the highest order
     * type returned by the Geocoder.
     */
    country, /**
     * Administrative area level 1 type
     * indicates a first-order civil entity
     * below the country level. Within the
     * United States, these administrative
     * levels are states. Not all nations
     * exhibit these administrative levels.
     */
    administrative_area_level_1, /**
     * Administrative area level 2 type
     * indicates a second-order civil entity
     * below the country level. Within the
     * United States, these administrative
     * levels are counties. Not all nations
     * exhibit these administrative levels.
     */
    administrative_area_level_2, /**
     * Administrative area level 3 type
     * indicates a third-order civil entity
     * below the country level. This type
     * indicates a minor civil division.
     * Not all nations exhibit these
     * administrative levels.
     */
    administrative_area_level_3, /**
     * Administrative area level 4 type
     * indicates a fourth-order civil entity
     * below the country level. This type
     * indicates a minor civil division.
     * Not all nations exhibit these
     * administrative levels.
     */
    administrative_area_level_4, /**
     * Administrative area level 5 type
     * indicates a fifth-order civil entity
     * below the country level. This type
     * indicates a minor civil division.
     * Not all nations exhibit these
     * administrative levels.
     */
    administrative_area_level_5, /**
     * Colloquial area type
     * indicates a commonly-used
     * alternative name for the entity.
     */
    colloquial_area, /**
     * Locality type indicates
     * an incorporated city or
     * town political entity.
     */
    locality, /**
     * Ward type indicates a specific type
     * of Japanese locality, to facilitate
     * distinction between multiple locality
     * components within a Japanese address.
     */
    ward, /**
     * Sublocality type
     * indicates a first-order civil entity
     * below a locality. For some locations
     * may receive one of the additional types:
     * sublocality_level_1 to sublocality_level_5.
     * Each sublocality level is a civil entity.
     * Larger numbers indicate a smaller geographic area.
     */
    sublocality, /**
     * Sublocality level 1 type.
     */
    sublocality_level_1, /**
     * Sublocality level 2 type.
     */
    sublocality_level_2, /**
     * Sublocality level 3 type.
     */
    sublocality_level_3, /**
     * Sublocality level 4 type.
     */
    sublocality_level_4, /**
     * Sublocality level 5 type.
     */
    sublocality_level_5, /**
     * Neighborhooh type
     * indicates a
     * named neighborhood
     */
    neighborhooh, /**
     * Premise type
     * indicates a named location,
     * usually a building or collection
     * of buildings with a common name.
     */
    premise, /**
     * Subpremise type
     * indicates a first-order entity
     * below a named location, usually
     * a singular building within a
     * collection of buildings with
     * a common name.
     */
    subpremise, /**
     * Postal code type
     * indicates a postal code as
     * used to address postal mail
     * within the country.
     */
    postal_code, /**
     * Natural feature type
     * indicates a prominent
     * natural feature.
     */
    natural_feature, /**
     * Airport type
     * indicates an airport.
     */
    airport, /**
     * Park type
     * indicates a named park.
     */
    park, /**
     * Point of interest type
     * indicates a named
     * point of interest.
     */
    point_of_interest
}
