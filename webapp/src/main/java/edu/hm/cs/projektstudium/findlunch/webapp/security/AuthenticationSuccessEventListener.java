package edu.hm.cs.projektstudium.findlunch.webapp.security;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * This class handles successful login attempts.
 * This class is based on the idea of: http://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 */
@Component
public final class AuthenticationSuccessEventListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    /**
     * The helper class used for handling login attempts.
     */
    @Autowired
    private AuthenticationHelper authenticationHelper;

    /**
     * This method is called upon a successful authentication.
     *
     * @param e an AuthenticationSuccessEvent
     */
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        final WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails)
                e.getAuthentication().getDetails();

        final String userPassword = e.getAuthentication().getCredentials().toString();

        // This is just a conceptual idea and may not be an appropriate approach for a productive system.
        if (!CustomUserValidator.checkPasswordRules(userPassword)) {
            if (authenticationDetails.getSessionId() != null) {
                authenticationHelper.loginFailedIpAddressAndSessionId(authenticationDetails.getRemoteAddress(),
                        authenticationDetails.getSessionId());
            }
            else {
                authenticationHelper.loginFailedIpAddress(authenticationDetails.getRemoteAddress());
            }
            NotificationController.sendMessageToTelegram("Change the password of the user: " +
                    e.getAuthentication().getName() + " as it doesn't match the password policy.");

            throw new BadCredentialsException("Bad credentials. The password didn't match the password policy");
        }
    }
}
