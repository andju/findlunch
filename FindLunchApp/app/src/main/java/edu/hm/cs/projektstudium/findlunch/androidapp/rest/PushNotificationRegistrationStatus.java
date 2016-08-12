package edu.hm.cs.projektstudium.findlunch.androidapp.rest;


/**
 * The enum User login status
 * that represents the status of
 * a user login request.
 */
public enum PushNotificationRegistrationStatus {
    /**
     * Success user login status.
     */
    SUCCESS,

    FAILED_UNKNOWN_USER,
    /**
     * Failed unauthorized user login status.
     */
    FAILED_UNAUTHORIZED
}
