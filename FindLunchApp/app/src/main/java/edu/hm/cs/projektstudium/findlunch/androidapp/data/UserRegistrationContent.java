package edu.hm.cs.projektstudium.findlunch.androidapp.data;


/**
 * The type User registration content
 * that contains the user and password of a user.
 */
public class UserRegistrationContent {

    /**
     * The User name.
     */
    private final String userName;

    /**
     * The Password.
     */
    private final String password;

    /**
     * The Password repeated.
     */
    private final String passwordRepeated;

    /**
     * Instantiates a new User registration content.
     *
     * @param userName         the user name
     * @param password         the password
     * @param passwordRepeated the password repeated
     */
    public UserRegistrationContent(String userName, String password, String passwordRepeated) {
        this.userName = userName;
        this.password = password;
        this.passwordRepeated = passwordRepeated;
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

    /**
     * Gets password repeated.
     *
     * @return the password repeated
     */
    public String getPasswordRepeated() {
        return passwordRepeated;
    }
}
