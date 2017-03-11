package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.ArrayList;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;

/**
 * Allows to request the registration of a reservation through the FindLunch REST API.
 */
public class ReservationRegistrationRequest extends AuthenticatedRequest<ReservationRegistrationStatus>{

    /**
     * The Reservation.
     */
    private final Reservation reservation;

    /**
     * The Response Entity.
     */
    private ResponseEntity<ReservationRegistrationStatus> responseEntity;


    /**
     * Instantiates a new reservation registration request.
     * @param context the context
     * @param reservation the reservation
     * @param connectionInformation the conection information
     * @param userName the username
     * @param password the password
     */
    public ReservationRegistrationRequest(OnHttpRequestFinishedCallback context, Reservation reservation, ConnectionInformation connectionInformation, String userName, String password){
        super(RequestReason.SEARCH, context, ((Context) context).getResources().getString(R.string.text_loading_reservation_registration), connectionInformation,userName,password);
        this.reservation = reservation;
        requestUrl = "https://" + requestHost + ":" + requestPort + "/api/register_reservation";

        requestEntity = new HttpEntity<Object>(reservation, requestHeaders);
    }


    @Override
    public void performRequest() {
        if(Connectivity.isConnected((Context)context)){
            ArrayList<ReservationRegistrationStatus> response = new ArrayList<>();
            try{
                //ResponseEntity<ReservationRegistrationStatus> responseEntity = restTemplate.postForEntity(URI.create(requestUrl), reservation,ReservationRegistrationStatus.class);
                responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, ReservationRegistrationStatus.class,parameters);
                response.add(responseEntity.getBody());
                setResponseData(response);
                setRequestResult(RequestResult.SUCCESS);
            } catch (HttpClientErrorException e){
                Log.e(getClass().getName(), e.getMessage());
                int x = Integer.valueOf(e.getResponseBodyAsString());
                response.add(ReservationRegistrationStatus.values()[Integer.valueOf(e.getResponseBodyAsString())]);
                setResponseData(response);
                setRequestResult(RequestResult.FAILED);
            } catch (RestClientException e){
                Log.e(getClass().getName(), e.getMessage());
                setRequestResult(RequestResult.FAILED);
                setRequestResultDetail(RequestResultDetail.FAILED_REST_REQUEST_FAILED);
            } catch (RuntimeException e) {
                Log.e(getClass().getName(), e.getMessage());
                setRequestResult(RequestResult.FAILED);
                setRequestResultDetail(RequestResultDetail.FAILED_REQUEST_FAILED);
            }
        }
        else{
           setRequestResult(RequestResult.FAILED);
           setRequestResultDetail(RequestResultDetail.FAILED_NO_NETWORK_CONNECTION);
        }
    }

    @Override
    public void sendRequestResponse() {
       context.onRestReservationRegistrationFinished(this);
    }
}
