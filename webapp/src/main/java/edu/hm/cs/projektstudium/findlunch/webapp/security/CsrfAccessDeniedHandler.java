package edu.hm.cs.projektstudium.findlunch.webapp.security;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class handles invalid CSRF-tokens by sending a message to a mobile messenger.
 */
public class CsrfAccessDeniedHandler extends AccessDeniedHandlerImpl
        implements org.springframework.security.web.access.AccessDeniedHandler {

    /**
     * This method handles the exception and should act custom in case a CSRF-token is affected.
     */
    @Override
    public final void handle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        if (accessDeniedException instanceof MissingCsrfTokenException
                || accessDeniedException instanceof InvalidCsrfTokenException) {

            // Either use one of both variables in order to get the CSRF-token.
            /*
            final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class.getName()
                    .concat(".CSRF_TOKEN");
			final CsrfToken csrfToken = (CsrfToken) request.getSession().getAttribute(DEFAULT_CSRF_TOKEN_ATTR_NAME);
			final String csrfToken = sessionToken.getToken();
			*/

            final CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            final String csrfToken = csrf.getToken();

            NotificationController.sendMessageToTelegram("There was an invalid CSRF-token sent."
                    + " The IP-address " + request.getRemoteAddr() + " sent an invalid CSRF-token."
                    + " The corresponding session-ID is: " + request.getSession().getId()
                    + " CSRF-Token: " + csrfToken
                    + " Requested URI: " + request.getRequestURI()
                    // The message should contain more information e.g. the CSRF-token if available.
                    + "\n Message was: " + accessDeniedException.getMessage());


            // When the unit tests are running they are calling the methods with names like testIllegalMethodTypes
            // which are testing for illegal HTTP methods like PUT/DELETE/PATCH
            // (see the term HTTP-verb-tampering for an attack scenario).
            // For example see:
            // https://www.acunetix.com/vulnerabilities/web/http-verb-tampering
            // https://www.owasp.org/index.php/Testing_for_HTTP_Verb_Tampering_(OTG-INPVAL-003)
            // The problem is the tests are expecting the status code 405 Method Not Allowed but the missing
            // CSRF-token is taking precedence over this status code so the status code 403 Forbidden is shown instead.
            // This approach is unclean because of the incorrect handling of the status code
            // when the CSRF-protection is enabled.
            if (request.getRequestURI().startsWith("/api/")) {
                response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Request method '" +
                        request.getMethod() + "' not supported");
            }

            super.handle(request, response, accessDeniedException);

        }
    }
}
