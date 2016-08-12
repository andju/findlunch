package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;

/**
 * The type Favourite request
 * is the base of {@link FavouriteRegistrationRequest}
 * and {@link FavouriteUnregistrationRequest} that
 * allow to register and unregister a favourite restaurant
 * for a certain user, through
 * the FindLunch REST API.
 */
public abstract class FavouriteRequest extends AuthenticatedRequest<FavouriteRegistrationStatus> {
    /**
     * The constant PARAMETER_RESTAURANT_ID.
     */
    private static final String PARAMETER_RESTAURANT_ID = "restaurant_id";
    /**
     * The id of the restaurant.
     */
    private final int restaurantId;

    /**
     * The Is registration.
     */
    private final boolean isRegistration;

    /**
     * Instantiates a new Favourite request.
     * @param context               the context
     * @param loadingMessage        the loading message
     * @param connectionInformation the connection information
     * @param userName              the user name
     * @param password              the password
     * @param restaurantId          the restaurant id
     * @param isRegistration        the is registration
     */
    public FavouriteRequest(OnHttpRequestFinishedCallback context, String loadingMessage, ConnectionInformation connectionInformation, String userName, String password, int restaurantId, boolean isRegistration) {
        super(RequestReason.SEARCH, context, loadingMessage, connectionInformation, userName, password);
        this.parameters.put(PARAMETER_RESTAURANT_ID, restaurantId);
        this.restaurantId = restaurantId;
        this.isRegistration = isRegistration;
    }

    /**
     * Gets restaurant id.
     *
     * @return the restaurant id
     */
    public int getRestaurantId() {
        return restaurantId;
    }

    /**
     * Is registration boolean.
     *
     * @return the boolean
     */
    public boolean isRegistration() {
        return isRegistration;
    }
}
