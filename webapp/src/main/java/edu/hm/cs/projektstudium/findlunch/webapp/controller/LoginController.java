package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.security.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * The class is responsible for handling http calls related to the login page.
 */
@Controller
public class LoginController {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

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
     * Gets the the login page for a request with the path "/login".
     *
     * @param request the HttpServletRequest
     * @return the string for the corresponding HTML page
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLogin(HttpServletRequest request) {
        final String ipAddress = getClientIP();

        if (!authenticationHelper.isBlocked(ipAddress, request.getSession().getId())) {
            LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
            return "login";
        } else {
            LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()) +
                    " IP-address banned: " + ipAddress + " Session-ID banned: " +
                    (request.getSession().getId() == null ? "SESSION IS NULL" : request.getSession().getId()));

            NotificationController.sendMessageToTelegram(LogUtils.getDefaultInfoString(request,
                    Thread.currentThread().getStackTrace()[1].getMethodName()) +
                    " IP-address banned: " + ipAddress + " Session-ID banned: " +
                    (request.getSession().getId() == null ? "SESSION IS NULL" : request.getSession().getId()) +
                    " There is a potential brute-force-attack against the website.");

            throw new LockedException("The IP-address: " + ipAddress + " was blocked." + " Session-ID banned: " +
                    (request.getSession().getId() == null ? "SESSION IS NULL" : request.getSession().getId()));
        }
    }

    /**
     * This method gets the client's IP-address and pays attention for the X-Forwarded-For header which could
     * identify a proxy user. See for example: https://tools.ietf.org/html/rfc7239
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
