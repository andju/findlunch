package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;


/**
 *
 * Registers a new push notification (through the FindLunch REST API).
 *
 * Using initially push message for database / device update.
 * Display other message.
 *
 * Extended by Maxmilian Haag on 07.02.2017
 */


public class PushNotificationRegistrationRequest extends AuthenticatedRequest<PushNotificationRegistrationStatus> {

    private ResponseEntity<PushNotificationRegistrationStatus> responseEntity;

    /**
     * Instantiates a new Favourite Registration request.
     * @param requestReason         the request reason
     * @param userName              the user name of the user
     * @param password              the password of the user
     * @param pushNotification      the push notification to register
     * @param connectionInformation the connection information
     * @param context               the context
     */
    public PushNotificationRegistrationRequest(RequestReason requestReason, String userName, String password, PushNotification pushNotification, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        // 07.02.2017
        // Message for push registration changed if performing initial database update push.
        super(requestReason, context, (pushNotification.getTitle().equals("INIT_PUSH"))?("Updating Database..."):(((Context) context).getResources().getString(R.string.text_loading_push_registration)), connectionInformation, userName, password);
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/register_push";

        // include push notification to register in requestEntity
        requestEntity = new HttpEntity<Object>(pushNotification, requestHeaders);
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            ArrayList<PushNotificationRegistrationStatus> response = new ArrayList<>();
            try {

                //send request
                responseEntity = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, PushNotificationRegistrationStatus.class, parameters);

                if(responseEntity.getBody() == PushNotificationRegistrationStatus.SUCCESS && responseEntity.getStatusCode() == HttpStatus.OK) {
                    response.add(PushNotificationRegistrationStatus.SUCCESS);
                }
                setResponseData(response);
                setRequestResult(RequestResult.SUCCESS);
            } catch (HttpClientErrorException e) {
                Log.e(getClass().getName(), e.getMessage());
                if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    response.add(PushNotificationRegistrationStatus.FAILED_UNAUTHORIZED);
                } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                    if(Integer.valueOf(e.getResponseBodyAsString()) < PushNotificationRegistrationStatus.values().length) {
                        response.add(PushNotificationRegistrationStatus.values()[Integer.valueOf(e.getResponseBodyAsString())]);
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
        context.onRestPushNotificationRegistrationFinished(this);
    }
}
