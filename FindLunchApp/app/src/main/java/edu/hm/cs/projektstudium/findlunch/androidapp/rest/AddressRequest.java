package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.content.Context;
import android.util.Log;

import org.springframework.web.client.RestClientException;

import java.util.ArrayList;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.GoogleGeoCodeResponse;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.Connectivity;


/**
 * The type Address request
 * allows to lookup the
 * longitude and latitude of
 * a specified address through
 * the Google Geocoding API.
 */
public class AddressRequest extends Request<GoogleGeoCodeResponse> {

    /**
     * The constant PARAMETER_ADDRESS.
     */
    private static final String PARAMETER_ADDRESS = "address";
    /**
     * The constant PARAMETER_COMPONENTS.
     */
    private static final String PARAMETER_COMPONENTS = "components";
    /**
     * The constant PARAMETER_KEY.
     */
    private static final String PARAMETER_KEY = "key";

    /**
     * Instantiates a new Address request.
     * @param context               the context
     * @param address               the address
     * @param components            the components
     * @param connectionInformation the connection information
     */
    public AddressRequest(OnHttpRequestFinishedCallback context,
                          String address,
                          @SuppressWarnings("SameParameterValue") String components,
                          ConnectionInformation connectionInformation) {
        super(RequestReason.SEARCH, context, ((Context) context).getResources().getString(R.string.text_loading_addresses), connectionInformation);
        this.parameters.put(PARAMETER_ADDRESS, address);
        this.parameters.put(PARAMETER_COMPONENTS, components);
        this.parameters.put(PARAMETER_KEY, "AIzaSyAvO9bl1Yi2hn7mkTSniv5lXaPRii1JxjI");
        requestUrl = "https://" + requestHost + ":" + requestPort + "/maps/api/geocode/json?address={address}&components={components}&key={key}";
    }

    @Override
    public void performRequest() {
        if (Connectivity.isConnected((Context)context)) {
            ArrayList<GoogleGeoCodeResponse> response = new ArrayList<>();
            try {
                response.add(restTemplate.getForObject(requestUrl, GoogleGeoCodeResponse.class, getParameters()));
                setResponseData(response);
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
        context.onRestAddressFinished(this);
    }
}
