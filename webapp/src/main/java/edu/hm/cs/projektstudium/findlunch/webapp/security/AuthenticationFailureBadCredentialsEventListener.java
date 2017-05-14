package edu.hm.cs.projektstudium.findlunch.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * This class handles failed login attempts.
 * This class is based on the idea of: http://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 */
@Component
public class AuthenticationFailureBadCredentialsEventListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    /**
     * The helper class used for handling login attempts.
     */
    @Autowired
    private AuthenticationHelper authenticationHelper;

    /**
     * This method is called upon a failed authentication.
     *
     * @param e an AuthenticationFailureBadCredentialsEvent
     */
    public final void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent e) {
        final WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails)
                e.getAuthentication().getDetails();

        // When this event gets fired we delegate the handling to the helper class.
        // Either use the session and IP-address of an user or just the IP-address if the session is not existent.
        if (authenticationDetails.getSessionId() != null) {
            authenticationHelper.loginFailedIpAddressAndSessionId(authenticationDetails.getRemoteAddress(),
                    authenticationDetails.getSessionId());
        } else {
            authenticationHelper.loginFailedIpAddress(authenticationDetails.getRemoteAddress());
        }
    }
}

