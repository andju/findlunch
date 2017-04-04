package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.GoogleGeoCodeResponse;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;


/**
 * The activity that uses an instance of {@link HttpRequestTask} must
 * implement this interface in order to receive event callbacks.
 */
public interface OnHttpRequestFinishedCallback {
    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link RestaurantRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestRestaurantFinished(Request<Restaurant> requestResponse);

    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link OfferRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestOfferFinished(Request<Offer> requestResponse);

    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link AddressRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestAddressFinished(Request<GoogleGeoCodeResponse> requestResponse);

    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link UserRegistrationRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestUserRegistrationFinished(Request<UserRegistrationStatus> requestResponse);

    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link UserLoginRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestUserLoginFinished(Request<UserLoginStatus> requestResponse);
    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link FavouriteRegistrationRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestFavouriteRegistrationFinished(Request<FavouriteRegistrationStatus> requestResponse);
    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link PushNotificationRegistrationRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestPushNotificationRegistrationFinished(Request<PushNotificationRegistrationStatus> requestResponse);
    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link PushNotificationOverviewRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestPushNotificationOverviewFinished(Request<PushNotification> requestResponse);
    /**
     * Method that is invoked
     * in {@link HttpRequestTask},
     * when a {@link PushNotificationDeleteRequest} returns.
     *
     * @param requestResponse the request response
     */
    void onRestPushNotificationDeleteFinished(Request<PushNotificationDeleteStatus> requestResponse);

}
