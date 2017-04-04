package edu.hm.cs.projektstudium.findlunch.androidapp.rest;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;

import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;

/**
 * The type Authenticated request.
 *
 * @param <T> the type parameter
 */
public abstract class AuthenticatedRequest<T> extends Request<T> {
    /**
     * The Request headers.
     */
    protected final HttpHeaders requestHeaders;
    /**
     * The Request entity.
     */
    protected HttpEntity<?> requestEntity;
    /**
     * The user name of the user.
     */
    protected final String userName;
    /**
     * The password of the user.
     */
    protected final String password;
    /**
     * The Auth header.
     */
    private final HttpAuthentication authHeader;

    /**
     * Instantiates a new Authenticated request.
     *
     * @param requestReason         the request reason
     * @param context               the context
     * @param loadingMessage        the loading message
     * @param connectionInformation the connection information
     * @param userName              the user name
     * @param password              the password
     */
    public AuthenticatedRequest(RequestReason requestReason, OnHttpRequestFinishedCallback context, String loadingMessage, ConnectionInformation connectionInformation, String userName, String password) {
        super(requestReason, context, loadingMessage, connectionInformation);
        this.userName = userName;
        this.password = password;

        // Set the username and password for creating a Basic Auth request
        authHeader = new HttpBasicAuthentication(userName, password);
        requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestEntity = new HttpEntity<Object>(requestHeaders);

        // Add the String message converter
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }


    /**
     * Gets restaurant position.
     *
     * @return the restaurant position
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets restaurant id.
     *
     * @return the restaurant id
     */
    public String getPassword() {
        return password;
    }
}
