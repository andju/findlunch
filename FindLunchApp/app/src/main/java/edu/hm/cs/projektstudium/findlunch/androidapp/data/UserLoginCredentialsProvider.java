package edu.hm.cs.projektstudium.findlunch.androidapp.data;

/**
 * An activity implementing this interface
 * provides the login credentials of the user.
 */
public interface UserLoginCredentialsProvider {
    /**
     * Gets user login credentials.
     *
     * @return the user login credentials
     */
    UserLoginCredentials getUserLoginCredentials();
}
