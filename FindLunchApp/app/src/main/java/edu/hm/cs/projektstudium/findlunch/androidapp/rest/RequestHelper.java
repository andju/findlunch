package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.AddressComponent;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.Geometry;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.GoogleGeoCodeResponse;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.Results;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.Type;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.User;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;

/**
 * The type Request helper
 * provides methods that
 * create {@link Request}
 * for restaurants, offers
 * and geocoding and executes
 * them through {@link HttpRequestTask}.
 */
public class RequestHelper {
    /**
     * Instantiates a new Request helper.
     */
    public RequestHelper() {
    }

    /**
     * Method that creates and executes
     * a {@link RestaurantRequest}.
     *
     * @param requestReason         the request reason
     * @param longitude             the longitude
     * @param latitude              the latitude
     * @param radius                the radius
     * @param connectionInformation the connection information
     * @param context               the context
     */
    public void requestRestaurants(RequestReason requestReason,
                                   float longitude, float latitude, int radius,
                                   String userName, String password,
                                   ConnectionInformation connectionInformation,
                                   OnHttpRequestFinishedCallback context) {
        // variable for the request
        Request request;
        // variable for the request task
        HttpRequestTask requestTask;

        if (longitude != 0 && latitude != 0 && radius != 0) {
            // create a request
            request = new RestaurantRequest(context, requestReason, longitude, latitude, radius, connectionInformation, userName, password);
            // create a request task
            requestTask = new HttpRequestTask((Context) context);
            // execute the request
            requestTask.execute(request);
        }
    }

    /**
     * Method that creates and executes
     * a {@link OfferRequest}.
     * @param requestReason         the request reason
     * @param restaurantId          the restaurant id
     * @param connectionInformation the connection information
     * @param context               the context
     */
    public void requestOffers(RequestReason requestReason, int restaurantId, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        // variable for the request
        Request request;
        // variable for the request task
        HttpRequestTask requestTask;

        if (restaurantId >= 0) {
            // create a request
            request = new OfferRequest(context, requestReason, restaurantId, connectionInformation);
            // create a request task
            requestTask = new HttpRequestTask((Context) context);
            // execute the request
            requestTask.execute(request);
        }
    }

    /**
     * Method that creates and executes
     * a {@link AddressRequest}.
     * @param address       the address
     * @param context       the context
     */
    public void requestAddresses(String address, OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new AddressRequest(
                context, address, "country:DE",
                new ConnectionInformation("maps.googleapis.com", 443));
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }


    /**
     * Method that creates and executes
     * a {@link UserRegistrationRequest}.
     * @param user          the user to register
     * @param context       the context
     */
    public void requestUserRegistration(User user, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new UserRegistrationRequest(
                context, user, connectionInformation);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that creates and executes
     * a {@link PushNotificationRegistrationRequest}.
     * @param requestReason     the request reason
     * @param pushNotification  the push-notification to register
     * @param context           the context
     */
    public void requestPushRegistration(RequestReason requestReason, String userName, String password, PushNotification pushNotification, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {

        // create a request
        Request request = new PushNotificationRegistrationRequest(requestReason, userName, password, pushNotification, connectionInformation, context);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that creates and executes
     * a {@link PushNotificationOverviewRequest}.
     * @param requestReason     the request reason
     * @param context           the context
     */
    public void requestPushOverview(RequestReason requestReason, String userName, String password, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new PushNotificationOverviewRequest(requestReason, userName, password, connectionInformation, context);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that creates and executes
     * a {@link PushNotificationDeleteRequest}.
     * @param requestReason     the request reason
     * @param context           the context
     */
    public void requestPushDelete(RequestReason requestReason, String userName, String password, int pushRegistrationId, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new PushNotificationDeleteRequest(requestReason, userName, password, pushRegistrationId, connectionInformation, context);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that creates and executes
     * a {@link UserLoginRequest}.
     * @param userName      the user name
     * @param password      the password
     * @param context       the context
     */
    public void requestUserLogin(String userName, String password,
                                 ConnectionInformation connectionInformation,
                                 OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new UserLoginRequest(
                context, userName, password, connectionInformation);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that creates and executes
     * a {@link FavouriteRegistrationRequest}.
     * @param userName      the user name
     * @param password      the password
     * @param restaurantId  the id of the restaurant
     *                      to register as favourite
     * @param context       the context
     */
    public void requestFavouriteRegistration(String userName, String password, int restaurantId,
                                             ConnectionInformation connectionInformation,
                                             OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new FavouriteRegistrationRequest(
                context, userName, password,
                restaurantId, connectionInformation);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that creates and executes
     * a {@link FavouriteUnregistrationRequest}.
     * @param userName      the user name
     * @param password      the password
     * @param restaurantId  the id of the restaurant
     *                      to register as favourite
     * @param context       the context
     */
    public void requestFavouriteUnregistration(String userName, String password, int restaurantId,
                                               ConnectionInformation connectionInformation,
                                               OnHttpRequestFinishedCallback context) {
        // create a request
        Request request = new FavouriteUnregistrationRequest(
                context, userName, password,
                restaurantId, connectionInformation);
        // create a request task
        HttpRequestTask requestTask = new HttpRequestTask((Context) context);
        // execute the request
        requestTask.execute(request);
    }

    /**
     * Method that checks the response of a
     * request to the Google Geocoding API
     * and updates the customer search location
     * if the address is valid. It returns <code>true</code>
     * if the address got updated.
     *
     * @param requestResponse the request response
     * @param searchLocation  the search location
     * @return <code>true</code>
     * if the address got updated.
     */
    public boolean checkAndUpdateCustomerSearchLocation(
            Request<GoogleGeoCodeResponse> requestResponse, UserContent searchLocation) {
        // response of the Google Geocoding API
        GoogleGeoCodeResponse googleGeoCodeResponse;
        // the result
        Results[] results;
        // result that matches the criteria
        Results matchingResult;
        // address components of the matching result
        AddressComponent[] addressComponents;
        // geometry of the matching result
        Geometry geometry;
        // street of the matching result
        String street = "";
        // street number of the matching result
        String streetNumber = "";
        // zip of the matching result
        String zip = "";
        // longitude of the matching result
        float longitude;
        // latitude of the matching result
        float latitude;
        // true if the response matches
        // the criteria only partially
        boolean partialMatch;

        if (requestResponse != null && requestResponse.getResponseData() != null
                && requestResponse.getResponseData().size() > 0) {
            googleGeoCodeResponse = requestResponse.getResponseData().get(0);
            results = googleGeoCodeResponse.getResults();

            if (results != null) {
                matchingResult = getMatchingResult(results);

                if (matchingResult != null) {
                    partialMatch = matchingResult.isPartialMatch();
                    addressComponents = matchingResult.getAddressComponents();
                    geometry = matchingResult.getGeometry();
                    latitude = geometry.getLocation().getLat();
                    longitude = geometry.getLocation().getLng();

                    for(AddressComponent currentAddress: addressComponents) {
                        for(String addressType: currentAddress.getTypes()) {
                            if(addressType.equals(Type.street_number.toString())) {
                                streetNumber = currentAddress.getLongName();
                            } else if(addressType.equals(Type.route.toString())) {
                                street = currentAddress.getLongName();
                            } else if(addressType.equals(Type.postal_code.toString())) {
                                zip = currentAddress.getLongName();
                            }
                        }
                    }
                    if (!partialMatch) {
                        searchLocation.setInformation(street, streetNumber, zip,
                                Integer.valueOf(
                                        searchLocation.getSearchLocationEntered().getDistance()));
                        searchLocation.setLatitude(latitude);
                        searchLocation.setLongitude(longitude);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns the first result from the given
     * results that has an address component of
     * the type street number.
     * @param results the results
     * @return first result with an address componentof the type street number.
     */
    @Nullable
    private Results getMatchingResult(Results[] results) {
        Results matchingResult = null;
        Results currentResult;
        for (int i = 0; matchingResult == null && i < results.length; i++) {
            currentResult = results[i];
            if (currentResult.getAddressComponents() != null) {
                for(int j = 0; matchingResult == null && j < currentResult.getAddressComponents().length; j++) {
                    if (currentResult.getAddressComponents()[j] != null) {
                        for(int k = 0; matchingResult == null && k < currentResult.getAddressComponents()[j].getTypes().length; k++) {
                            if(currentResult.getAddressComponents()[j].getTypes()[k].equals(Type.street_number.toString())) {
                                matchingResult = currentResult;
                            }
                        }
                    }
                }
            }
        }
        return matchingResult;
    }
}