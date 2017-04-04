package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;

/**
 * Gets the list of active push notifications for the current user (through the FindLunch REST API).
 *
 * Created by Andreas Juckel on 18.07.2016.
 */
public class PushNotificationOverviewRequest extends AuthenticatedRequest<PushNotification> {

    /**
     * Instantiates a new Favourite Registration request.
     * @param requestReason         the request reason
     * @param userName              the user name of the user
     * @param password              the password of the user
     * @param connectionInformation the connection information
     * @param context               the context
     */
    public PushNotificationOverviewRequest(RequestReason requestReason, String userName, String password, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        super(requestReason, context, ((Context) context).getResources().getString(R.string.text_loading_push_overview), connectionInformation, userName, password);
        requestUrl = "https://" + requestHost + ":" + requestPort + "/api/get_push";
    }


    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context) context)) {
            try {

                //Send get_push request
                ResponseEntity<PushNotification[]> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, PushNotification[].class, getParameters());

                setResponseData(new ArrayList<>(Arrays.asList(responseEntity.getBody())));
                setRequestResult(RequestResult.SUCCESS);
            } catch (RestClientException e) {
                Log.e(getClass().getName(), e.getMessage());
                setRequestResult(RequestResult.FAILED);
                setRequestResultDetail(RequestResultDetail.FAILED_REST_REQUEST_FAILED);
            } catch (Exception e) {
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
        context.onRestPushNotificationOverviewFinished(this);
    }
}