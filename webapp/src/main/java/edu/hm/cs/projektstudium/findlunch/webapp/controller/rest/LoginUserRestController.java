package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.security.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * The Class LoginUserRestController. The class is responsible for handling rest
 * calls related to login users
 */
@RestController
public class LoginUserRestController {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(LoginUserRestController.class);

    /**
     * The helper class used for handling login attempts.
     */
    @Autowired
    private AuthenticationHelper authenticationHelper;

    /**
     * The HTTP servlet request.
     */
    @Autowired
    private HttpServletRequest request;

    /**
     * Login user.
     *
     * @param request the HttpServletRequest
     * @return the response entity representing a status code
     */
    @CrossOrigin
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/api/login_user", method = RequestMethod.GET)
    public ResponseEntity<Integer> loginUser(HttpServletRequest request) {
        final String ipAddress = getClientIP();

        // As there is no session-ID with the RESTful-Webservice we just can use the retrieved IP-address.
        if (!authenticationHelper.isBlocked(ipAddress)) {
            LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
            
            return new ResponseEntity<>(0, HttpStatus.OK);
        } else {
            LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()) +
                    " IP-address banned: " + ipAddress);

            NotificationController.sendMessageToTelegram(LogUtils.getDefaultInfoString(request,
                    Thread.currentThread().getStackTrace()[1].getMethodName()) +
                    " IP-address banned: " + ipAddress + " There is a potential brute-force-attack against the" +
                    " RESTful-webservice. The potential attacker used the correct password but is banned." +
                    "Better check if it was an valid attempt or a successful attack.");
            throw new LockedException("The IP-address: " + ipAddress + " was blocked.");
        }

    }

    /**
     * This method gets the client's IP-address and pays attention for the X-Forwarded-For header which could
     * identify a proxy user. See for example: https://tools.ietf.org/html/rfc7239
     * When a potential attacker uses a proxy during a brute-force-attack the XFF-header could reveal his
     * real IP-address.
     *
     * @return the client's IP-address
     */
    private String getClientIP() {
        final String xffHeader = request.getHeader("X-Forwarded-For");
        if (xffHeader == null) {
            return request.getRemoteAddr();
        }
        return xffHeader.split(",")[0];
    }
}
