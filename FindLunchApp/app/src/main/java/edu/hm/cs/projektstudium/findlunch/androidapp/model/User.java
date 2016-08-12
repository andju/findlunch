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
     * Instantiates a new User.
     *
     * @param username        the username
     * @param password        the password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
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
     * Gets id.
     *
     * @return the password
     */
    public int getId() {
        return id;
    }
}
