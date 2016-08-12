package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;

/**
 * The type Offer request
 * allows to request the
 * offers of a specified
 * restaurant through
 * the FindLunch REST API.
 */
public class OfferRequest extends Request<Offer> {
    /**
     * The constant PARAMETER_RESTAURANT_ID.
     */
    private static final String PARAMETER_RESTAURANT_ID = "restaurant_id";
    /**
     * The id of the restaurant.
     */
    private final int restaurantId;

    /**
     * Instantiates a new Offer request.
     *  @param context               the context
     * @param requestReason         the request reason
     * @param restaurantId          the id of the restaurant
     * @param connectionInformation the connection information
     */
    public OfferRequest(OnHttpRequestFinishedCallback context, RequestReason requestReason, int restaurantId, ConnectionInformation connectionInformation) {
        super(requestReason, context, ((Context) context).getResources().getString(R.string.text_loading_offers), connectionInformation);
        this.parameters.put(PARAMETER_RESTAURANT_ID, restaurantId);
        this.restaurantId = restaurantId;
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/offers?restaurant_id={restaurant_id}";
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            try {
                setResponseData(new ArrayList<>(Arrays.asList(restTemplate.getForObject(requestUrl, Offer[].class, getParameters()))));
                setRequestResult(RequestResult.SUCCESS);
            } catch (RestClientException e) {
                Log.e(getClass().getName(), e.getMessage());
                setRequestResult(RequestResult.FAILED);
                setRequestResultDetail(RequestResultDetail.FAILED_REST_REQUEST_FAILED);
            } catch (RuntimeException e) {
                Log.e(getClass().getName(), e.getMessage());
                setRequestResult(RequestResult.FAILED);
                setRequestResultDetail(RequestResultDetail.FAILED_REQUEST_FAILED);
            }
        } else {
            setRequestResult(RequestResult.FAILED);
            setRequestResultDetail(RequestResultDetail.FAILED_NO_NETWORK_CONNECTION);
        }
    }

    @Override
    public void sendRequestResponse() {
        context.onRestOfferFinished(this);
    }

    /**
     * Gets restaurant id.
     *
     * @return the restaurant id
     */
    public int getRestaurantId() {
        return restaurantId;
    }
}
