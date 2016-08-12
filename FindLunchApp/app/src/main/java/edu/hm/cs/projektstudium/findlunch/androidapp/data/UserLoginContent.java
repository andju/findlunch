package edu.hm.cs.projektstudium.findlunch.androidapp.data;


/**
 * The type Login logout content.
 */
public class UserLoginContent {

    /**
     * The User name.
     */
    private final String userName;

    /**
     * The Password.
     */
    private final String password;

    /**
     * Instantiates a new Login logout content.
     *
     * @param userName         the user name
     * @param password         the password
     */
    public UserLoginContent(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
