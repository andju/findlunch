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
 * The type User Login request
 * allows to check the validity of
 * the user credentials through
 * the FindLunch REST API.
 */
public class UserLoginRequest extends AuthenticatedRequest<UserLoginStatus> {

    /**
     * Instantiates a new User Login request.
     * @param context               the context
     * @param userName              the user name of the user
     * @param password              the password of the user
     * @param connectionInformation the connection information
     */
    public UserLoginRequest(OnHttpRequestFinishedCallback context,
                            String userName, String password, ConnectionInformation connectionInformation) {
        super(RequestReason.SEARCH, context, ((Context) context).getResources().getString(R.string.text_loading_user_login), connectionInformation, userName, password);
        requestUrl = "https://" +  requestHost + ":" + requestPort + "/api/login_user";
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            ArrayList<UserLoginStatus> response = new ArrayList<>();
            try {
                // Make the HTTP GET request to the Basic Auth protected URL
                ResponseEntity<UserLoginStatus> responseEntity = restTemplate.exchange(requestUrl,
                        HttpMethod.GET, requestEntity, UserLoginStatus.class);
                if(responseEntity.getBody() == UserLoginStatus.SUCCESS &&
                        responseEntity.getStatusCode() == HttpStatus.OK) {
                    response.add(UserLoginStatus.SUCCESS);
                }
                setResponseData(response);
                setRequestResult(RequestResult.SUCCESS);
            } catch (HttpClientErrorException e) {
                Log.e(getClass().getName(), e.getMessage());
                if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    response.add(UserLoginStatus.FAILED_UNAUTHORIZED);
                } else {
                    response.add(UserLoginStatus.FAILED);
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
        context.onRestUserLoginFinished(this);
    }

}
