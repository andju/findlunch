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
 * Deletes a push notification (through the FindLunch REST API).
 *
 * Created by Andreas Juckel on 20.07.2016.
 */
public class PushNotificationDeleteRequest  extends AuthenticatedRequest<PushNotificationDeleteStatus> {

    private ResponseEntity<PushNotificationDeleteStatus> responseEntity;

    /**
     * Instantiates a new Push Notification Delete request.
     * @param requestReason         the request reason
     * @param userName              the user name of the user
     * @param password              the password of the user
     * @param pushNotificationId    the id of the push notification to delete
     * @param connectionInformation the connection information
     * @param context               the context
     */
    public PushNotificationDeleteRequest(RequestReason requestReason, String userName, String password, int pushNotificationId, ConnectionInformation connectionInformation, OnHttpRequestFinishedCallback context) {
        super(requestReason, context, ((Context) context).getResources().getString(R.string.text_loading_push_delete), connectionInformation, userName, password);
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/unregister_push/" + pushNotificationId;
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            ArrayList<PushNotificationDeleteStatus> response = new ArrayList<>();
            try {
                responseEntity = restTemplate.exchange(requestUrl,
                        HttpMethod.DELETE, requestEntity, PushNotificationDeleteStatus.class, parameters);
                if(responseEntity.getBody() == PushNotificationDeleteStatus.SUCCESS &&
                        responseEntity.getStatusCode() == HttpStatus.OK) {
                    response.add(PushNotificationDeleteStatus.SUCCESS);
                }
                setResponseData(response);
                setRequestResult(RequestResult.SUCCESS);
            } catch (HttpClientErrorException e) {
                Log.e(getClass().getName(), e.getMessage());
                if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    response.add(PushNotificationDeleteStatus.FAILED_UNAUTHORIZED);
                } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                    if(Integer.valueOf(e.getResponseBodyAsString()) < PushNotificationDeleteStatus.values().length) {
                        response.add(PushNotificationDeleteStatus.values()[Integer.valueOf(e.getResponseBodyAsString())]);
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
        context.onRestPushNotificationDeleteFinished(this);
    }
}