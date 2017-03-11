package edu.hm.cs.projektstudium.findlunch.androidapp.data;


import edu.hm.cs.projektstudium.findlunch.androidapp.model.Captcha;

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
     * The Captcha object
     */
    private Captcha captcha;

    /**
     * Instantiates a new User registration content.
     *
     * @param userName         the user name
     * @param password         the password
     * @param passwordRepeated the password repeated
     * @param captchaParam         the Captcha
     */
    public UserRegistrationContent(String userName, String password, String passwordRepeated,
                                   final Captcha captchaParam) {
        this.userName = userName;
        this.password = password;
        this.passwordRepeated = passwordRepeated;
        this.captcha = captchaParam;
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

    /**
     * Gets the Captcha.
     *
     * @return the Captcha object
     */
    public Captcha getCaptcha() {
        return captcha;
    }

}
