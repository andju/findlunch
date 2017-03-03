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
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;

/**
 * Allows to request confirm a reservation trough the FindLunch REST API.
 */
public class ReservationConfirmRequest extends AuthenticatedRequest<ReservationConfirmStatus> {

    /**
     * The uuid of the restaurant.
     */
    private static final String PARAMETER_RESTAURANT_UUID = "restaurantUuid";

    /**
     * Instantiates a new reservation confirm request.
     * @param context the context
     * @param restaurant the restaurant
     * @param connectionInformation the connection information
     * @param userName the username
     * @param password the password
     */
    public ReservationConfirmRequest(OnHttpRequestFinishedCallback context, Restaurant restaurant, ConnectionInformation connectionInformation, String userName, String password) {
        super(RequestReason.SEARCH, context, ((Context) context).getResources().getString(R.string.text_loading_reservation_confirm), connectionInformation, userName, password);
        this.parameters.put(PARAMETER_RESTAURANT_UUID, restaurant.getRestaurantUuid());

        requestUrl = "http://" + requestHost + ":" + requestPort + "api/confirm_reservation/{restaurantUuid}";
    }


    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context) context)) {
            ArrayList<ReservationConfirmStatus> response = new ArrayList<>();
            try {
                ResponseEntity<ReservationConfirmStatus> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.PUT, requestEntity, ReservationConfirmStatus.class, parameters);
                if (responseEntity.getBody() == ReservationConfirmStatus.SUCCESS && responseEntity.getStatusCode() == HttpStatus.OK) {
                    response.add(responseEntity.getBody());
                    setResponseData(response);
                    setRequestResult(RequestResult.SUCCESS);
                }
            } catch (HttpClientErrorException e) {
                Log.e(getClass().getName(), e.getMessage());
                //response.add(ReservationConfirmStatus.values()[Integer.valueOf(e.getResponseBodyAsString())]);
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
        }
    }

    @Override
    public void sendRequestResponse() {
        context.onRestReservationConfirmFinished(this);
    }
}
