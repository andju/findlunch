package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import android.support.annotation.NonNull;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;


/**
 * The type Request.
 *
 * @param <T> the type parameter
 */
public abstract class Request<T> {

    /**
     * The constant TIMEOUT.
     */
    public static final int TIMEOUT = 10000;
    /**
     * The Rest template.
     */
    protected final RestTemplate restTemplate;
    /**
     * The Context of the activity.
     */
    protected final OnHttpRequestFinishedCallback context;
    /**
     * The Parameters and arguments
     * of the REST request.
     */
    protected final Map<String, Object> parameters;
    /**
     * The reason for the REST request.
     */
    private final RequestReason requestReason;
    /**
     * The results of the REST request.
     */
    private RequestResult requestResult;
    /**
     * The detailed result of the REST request.
     */
    private RequestResultDetail requestResultDetail;
    /**
     * The Response data of the REST request.
     */
    private List<T> responseData;
    /**
     * The Loading message that is
     * shown on the user interface
     * while the request is processed.
     */
    private final String loadingMessage;
    /**
     * The url of the REST API.
     */
    protected String requestUrl;
    /**
     * The host of the REST API.
     */
    protected final String requestHost;
    /**
     * The port of the REST API.
     */
    protected final int requestPort;


    /**
     * Instantiates a new Request.
     *
     * @param requestReason         the reason for the REST request.
     * @param context               the context
     * @param loadingMessage        the loading message that is
     *                              shown on the user interface
     *                              while the request is processed.
     * @param connectionInformation the connection information
     */
    public Request(RequestReason requestReason, OnHttpRequestFinishedCallback context, String loadingMessage, ConnectionInformation connectionInformation) {
        this.parameters = new HashMap<>();
        this.requestReason = requestReason;
        this.context = context;
        this.loadingMessage = loadingMessage;
        this.requestHost = connectionInformation.getHost();
        this.requestPort = connectionInformation.getPort();
        restTemplate = new RestTemplate(getSimpleClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }



    /**
     * Gets simple client http request factory.
     *
     * @return the simple client http request factory
     */
    @NonNull
    private SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        // instantiate a simple client http request factory
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT);
        requestFactory.setReadTimeout(TIMEOUT * 2);
        return requestFactory;
    }

    /**
     * Perform request.
     */
    public abstract void performRequest();

    /**
     * Send request response.
     */
    public abstract void sendRequestResponse();

    /**
     * Gets parameters.
     *
     * @return the parameters
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Gets request reason.
     *
     * @return the request reason
     */
    public RequestReason getRequestReason() {
        return requestReason;
    }

    /**
     * Gets request result.
     *
     * @return the request result
     */
    public RequestResult getRequestResult() {
        return requestResult;
    }

    /**
     * Sets request result.
     *
     * @param requestResult the request result
     */
    public void setRequestResult(RequestResult requestResult) {
        this.requestResult = requestResult;
    }

    /**
     * Gets request result detail.
     *
     * @return the request result detail
     */
    public RequestResultDetail getRequestResultDetail() {
        return requestResultDetail;
    }

    /**
     * Sets request result detail.
     *
     * @param requestResultDetail the request result detail
     */
    public void setRequestResultDetail(RequestResultDetail requestResultDetail) {
        this.requestResultDetail = requestResultDetail;
    }

    /**
     * Gets response data.
     *
     * @return the response data
     */
    public List<T> getResponseData() {
        return responseData;
    }

    /**
     * Sets response data.
     *
     * @param responseData the response data
     */
    public void setResponseData(List<T> responseData) {
        this.responseData = responseData;
    }

    /**
     * Gets loading message.
     *
     * @return the loading message
     */
    public String getLoadingMessage() {
        return loadingMessage;
    }

}
