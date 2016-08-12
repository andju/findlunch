package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.ArrayList;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.User;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;

/**
 * The type User Registration request
 * allows to request the
 * registration of a user through
 * the FindLunch REST API.
 */
public class UserRegistrationRequest extends Request<UserRegistrationStatus> {

    /**
     * The user to register.
     */
    private final User user;

    /**
     * Instantiates a new User Registration request.
     * @param context               the context
     * @param user                  the user
     * @param connectionInformation the connection information
     */
    public UserRegistrationRequest(OnHttpRequestFinishedCallback context, User user, ConnectionInformation connectionInformation) {
        super(RequestReason.SEARCH, context, ((Context) context).getResources().getString(R.string.text_loading_user_registration), connectionInformation);
        this.user = user;
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/register_user";
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            ArrayList<UserRegistrationStatus> response = new ArrayList<>();
            try {
                // response entity with the result of the post request
                ResponseEntity<UserRegistrationStatus> responseEntity = restTemplate.postForEntity(URI.create(requestUrl), user, UserRegistrationStatus.class);
                response.add(responseEntity.getBody());
                setResponseData(response);
                setRequestResult(RequestResult.SUCCESS);
            } catch (HttpClientErrorException e) {
                Log.e(getClass().getName(), e.getMessage());
                response.add(UserRegistrationStatus.values()[Integer.valueOf(e.getResponseBodyAsString())]);
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
        context.onRestUserRegistrationFinished(this);
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }
}
