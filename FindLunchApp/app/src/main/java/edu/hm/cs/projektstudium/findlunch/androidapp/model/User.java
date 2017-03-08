package edu.hm.cs.projektstudium.findlunch.androidapp.model;

/**
 * The type User.
 */
public class User {

    /**
     * The Id of the User.
     */
    @SuppressWarnings("unused")
    private int id;

    /**
     * The Username.
     */
    private String username;

    /**
     * The Password.
     */
    private String password;

    /**
     * The Captcha object
     */
    private Captcha captcha;

    /**
     * Instantiates a new User.
     *
     * @param username        the username
     * @param password        the password
     * @param captcha         the Captcha
     */
    public User(String username, String password, final Captcha captcha) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    /**
     * Empty Constructor a new User.
     */
    @SuppressWarnings("unused")
    public User() {
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
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
     * Gets the Captcha.
     *
     * @return the Captcha object
     */
    public Captcha getCaptcha() {
        return captcha;
    }


    /**
     * Gets id.
     *
     * @return the password
     */
    public int getId() {
        return id;
    }
}
