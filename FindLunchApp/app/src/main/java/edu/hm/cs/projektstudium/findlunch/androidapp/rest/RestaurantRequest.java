package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;

/**
 * The type RestaurantRequest
 * allows to request the
 * restaurants of a specified
 * location through
 * the FindLunch REST API.
 */
public class RestaurantRequest extends AuthenticatedRequest<Restaurant> {
    /**
     * The constant PARAMETER_LATITUDE.
     */
    private static final String PARAMETER_LATITUDE = "latitude";
    /**
     * The constant PARAMETER_LONGITUDE.
     */
    private static final String PARAMETER_LONGITUDE = "longitude";
    /**
     * The constant PARAMETER_RADIUS.
     */
    private static final String PARAMETER_RADIUS = "radius";

    /**
     * Instantiates a new Restaurant request.
     *
     * @param context               the context
     * @param requestReason         the request reason
     * @param longitude             the longitude
     * @param latitude              the latitude
     * @param radius                the radius
     * @param connectionInformation the connection information
     */
    public RestaurantRequest(OnHttpRequestFinishedCallback context, RequestReason requestReason, float longitude, float latitude, int radius, ConnectionInformation connectionInformation, String userName, String password) {
        super(requestReason, context, ((Context) context).getResources().getString(R.string.text_loading_restaurants), connectionInformation, userName, password);
        this.parameters.put(PARAMETER_LATITUDE, latitude);
        this.parameters.put(PARAMETER_LONGITUDE, longitude);
        this.parameters.put(PARAMETER_RADIUS, radius);
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/restaurants?latitude={latitude}&longitude={longitude}&radius={radius}";
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
                    try {
                        if (userName != null && password != null) {
                            ResponseEntity<Restaurant[]> responseEntity = restTemplate.exchange(requestUrl,
                                    HttpMethod.GET, requestEntity, Restaurant[].class, getParameters());
                            setResponseData(new ArrayList<>(Arrays.asList(responseEntity.getBody())));
                        } else {
                            setResponseData(new ArrayList<>(Arrays.asList(restTemplate.getForObject(requestUrl, Restaurant[].class, getParameters()))));
                        }

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
        context.onRestRestaurantFinished(this);
    }
}
