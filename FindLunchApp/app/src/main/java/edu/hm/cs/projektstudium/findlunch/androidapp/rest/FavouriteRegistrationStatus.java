package edu.hm.cs.projektstudium.findlunch.androidapp.rest;


/**
 * The enum User login status
 * that represents the status of
 * a user login request.
 */
public enum FavouriteRegistrationStatus {
    /**
     * Success user login status.
     */
    SUCCESS,
    /**
     * Failed unauthorized user login status.
     */
    FAILED_UNAUTHORIZED,
    /**
     * Failed invalid restaurant id favourite registration status.
     */
    FAILED_INVALID_RESTAURANT_ID
}
