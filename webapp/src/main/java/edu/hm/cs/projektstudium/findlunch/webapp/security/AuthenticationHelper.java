package edu.hm.cs.projektstudium.findlunch.webapp.security;

import com.google.appengine.repackaged.com.google.common.cache.CacheBuilder;
import com.google.appengine.repackaged.com.google.common.cache.CacheLoader;
import com.google.appengine.repackaged.com.google.common.cache.LoadingCache;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class is used as a helper class for valid and invalid login attempts.
 * This class is based on the idea of: http://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 */
@Service
public final class AuthenticationHelper {

    /**
     * The logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHelper.class);

    /**
     * The maximum attempts of a failed login.
     */
    private static final int MAX_ATTEMPT = 6;

    /**
     * Two caches which keep the failed attempts. A time limit when the caches should be cleared can be assigned.
     * See: https://google.github.io/guava/releases/18.0/api/docs/com/google/common/cache/CacheBuilder.html
     */
    private static LoadingCache<String, Integer> attemptsCacheIpAddress;
    private static LoadingCache<String, Integer> attemptsCacheSessionId;

    /**
     * The constructor which mainly sets the caches.
     */
    private AuthenticationHelper() {
        super();

        attemptsCacheIpAddress = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });

        attemptsCacheSessionId = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    /**
     * Handle a failed login attempt where the IP-address and the session-ID is known.
     */
    public void loginFailedIpAddressAndSessionId(final String ipAddress, final String sessionId) {
        int attemptsIpAddress;
        int attemptsSessionId = 0;
        try {
            attemptsIpAddress = attemptsCacheIpAddress.get(ipAddress);

            if (sessionId != null) {
                attemptsSessionId = attemptsCacheSessionId.get(sessionId);
            }

        } catch (ExecutionException e) {
            LOGGER.error("ExecutionException", "There was an unknown execution error.");
            attemptsIpAddress = 0;
            attemptsSessionId = 0;
        }

        attemptsIpAddress++;
        attemptsSessionId++;
        attemptsCacheIpAddress.put(ipAddress, attemptsIpAddress);
        attemptsCacheSessionId.put(sessionId, attemptsSessionId);
    }

    /**
     * Handle a failed login attempt where the IP-address is known.
     */
    public void loginFailedIpAddress(final String ipAddress) {
        int attemptsIpAddress = 0;
        try {
            if (ipAddress != null) {
                attemptsIpAddress = attemptsCacheIpAddress.get(ipAddress);
            }
        } catch (ExecutionException e) {
            LOGGER.error("ExecutionException", "There was an unknown execution error.");
            attemptsIpAddress = 0;
        }
        attemptsIpAddress++;
        attemptsCacheIpAddress.put(ipAddress, attemptsIpAddress);
    }

    /**
     * This method checks if an IP-address or session-ID is blocked
     *
     * @param ipAddress the IP-address
     * @param sessionId the session-ID
     *
     * @return whether the given data is blocked or not
     */
    public boolean isBlocked(final String ipAddress, final String sessionId) {
        try {

            if (attemptsCacheIpAddress.get(ipAddress) >= MAX_ATTEMPT) {
                NotificationController.sendMessageToTelegram("The IP-address: " + ipAddress
                        + " is banned but still tries to login."
                        + " It was attempted: " + attemptsCacheIpAddress.get(ipAddress) + " times from this IP-address."
                        + " The corresponding session-ID is: " + sessionId);
            }

            if (attemptsCacheSessionId.get(sessionId) >= MAX_ATTEMPT) {
                NotificationController.sendMessageToTelegram("The session-ID: " + sessionId
                        + " is banned but still tries to login."
                        + " It was attempted: " + attemptsCacheSessionId.get(sessionId) + " times from this session-ID."
                        + " The corresponding IP-address is: " + ipAddress);
            }

            return attemptsCacheIpAddress.get(ipAddress) >= MAX_ATTEMPT ||
                    attemptsCacheSessionId.get(sessionId) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }

    /**
     * Handle a failed login attempt where an IP-address is blocked.
     *
     * @param ipAddress the IP-address
     */
    public boolean isBlocked(final String ipAddress) {
        try {

            if (attemptsCacheIpAddress.get(ipAddress) >= MAX_ATTEMPT) {
                NotificationController.sendMessageToTelegram("The " + ipAddress + " is banned but still tries to login."
                + " It was attempted: " + attemptsCacheIpAddress.get(ipAddress) + " times from this IP-address.");
            }

            return attemptsCacheIpAddress.get(ipAddress) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }

}
