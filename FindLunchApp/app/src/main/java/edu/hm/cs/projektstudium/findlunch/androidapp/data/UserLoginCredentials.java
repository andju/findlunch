package edu.hm.cs.projektstudium.findlunch.androidapp.data;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * The type User login credentials
 * persists and deletes the userName
 * and password of the user and
 * allows to check the login state.
 */
public class UserLoginCredentials {

    /**
     * The constant instance.
     */
    private static UserLoginCredentials instance;
    /**
     * The Context.
     */
    private final Context context;

    /**
     * The constant PREFS_LOGIN.
     */
    private static final String PREFS_LOGIN = "loginPreferences";
    /**
     * The constant PREF_USERNAME.
     */
    private static final String PREF_USERNAME = "userName";
    /**
     * The constant PREF_PASSWORD.
     */
    private static final String PREF_PASSWORD = "password";
    /**
     * The constant GCM_TOKEN.
     */
    private static final String PREF_GCM_TOKEN = "gcmToken";

    /**
     * Instantiates a new User login credentials.
     *
     * @param context the context
     */
    private UserLoginCredentials(Context context) {
        this.context = context;
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static UserLoginCredentials getInstance(Context context) {
        if(instance == null) {
            instance = new UserLoginCredentials(context);
        }
        return instance;
    }

    /**
     * Gets the preferences that
     * provide access to the key-value-pairs.
     *
     * @return the preferences
     */
    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREFS_LOGIN, 0);
    }

    /**
     * Gets the preferences editor that
     * provides the ability to edit the key-value-pairs.
     *
     * @return the preferences editor
     */
    private SharedPreferences.Editor getPreferencesEditor() {
        return context.getSharedPreferences(PREFS_LOGIN, 0).edit();
    }

    /**
     * Persist the userName and password
     * as key-value-pairs in the preferences.
     *
     * @param userName the user name
     * @param password the password
     */
    public void persistCredentials(String userName, String password) {
        SharedPreferences.Editor editor = getPreferencesEditor();
        editor.putString(PREF_USERNAME, userName);
        editor.putString(PREF_PASSWORD, password);

        // Commit the edits!
        editor.commit();
    }

    /**
     * Deletes the userName and password
     * from the preferences.
     */
    public void deleteCredentials() {
        SharedPreferences.Editor editor = getPreferencesEditor();
        editor.remove(PREF_USERNAME);
        editor.remove(PREF_PASSWORD);
        editor.remove(PREF_GCM_TOKEN);

        // Commit the edits!
        editor.commit();
    }

    /**
     * Returns <code>true</code> if the
     * user is logged in.
     *
     * @return <code>true</code> if the user is logged in.
     */
    public boolean isLoggedIn() {
        return getUserName() != null && getPassword() != null;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return getPreferences().getString(PREF_USERNAME, null);
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return getPreferences().getString(PREF_PASSWORD, null);
    }

    /**
     * Gets gcm token.
     *
     * @return the gcm token
     */
    public String getGcmToken() {
        return getPreferences().getString(PREF_GCM_TOKEN, null);
    }

    /**
     * Sets gcm token.
     *
     * @param token the token
     */
    public void setGcmToken(String token) {

        SharedPreferences.Editor editor = getPreferencesEditor();
        editor.putString(PREF_GCM_TOKEN, token);
        editor.commit();

    }
}
