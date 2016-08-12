package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;

/**
 * The type Favourite Unregistration request
 * allows to unregister a favourite restaurant
 * for a certain user, through
 * the FindLunch REST API.
 */
public class FavouriteUnregistrationRequest extends FavouriteRequest {

    /**
     * Instantiates a new Favourite Unregistration request.
     * @param context               the context
     * @param userName              the user name of the user
     * @param password              the password of the user
     * @param restaurantId          the id of the restaurant to register as favourite
     * @param connectionInformation the connection information
     */
    public FavouriteUnregistrationRequest(OnHttpRequestFinishedCallback context,
                                          String userName, String password, int restaurantId, ConnectionInformation connectionInformation) {
        super(context, ((Context) context).getResources().getString(R.string.text_loading_favourite_unregistration), connectionInformation, userName, password, restaurantId, false);
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/unregister_favorite/{restaurant_id}";
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            ArrayList<FavouriteRegistrationStatus> response = new ArrayList<>();
            try {
                // Make the HTTP DELETE request to the Basic Auth protected URL
                ResponseEntity<FavouriteRegistrationStatus> responseEntity = restTemplate.exchange(requestUrl,
                        HttpMethod.DELETE, requestEntity, FavouriteRegistrationStatus.class, parameters);
                if(responseEntity.getBody() == FavouriteRegistrationStatus.SUCCESS &&
                        responseEntity.getStatusCode() == HttpStatus.OK) {
                    response.add(FavouriteRegistrationStatus.SUCCESS);
                }
                setResponseData(response);
                setRequestResult(RequestResult.SUCCESS);
            } catch (HttpClientErrorException e) {
                Log.e(getClass().getName(), e.getMessage());
                if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    response.add(FavouriteRegistrationStatus.FAILED_UNAUTHORIZED);
                } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                    if(Integer.valueOf(e.getResponseBodyAsString()) == 3) {
                        response.add(FavouriteRegistrationStatus.FAILED_INVALID_RESTAURANT_ID);
                    }
                }
                setResponseData(response);
                setRequestResult(RequestResult.FAILED);
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
        context.onRestFavouriteRegistrationFinished(this);
    }

}
