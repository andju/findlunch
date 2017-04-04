package edu.hm.cs.projektstudium.findlunch.androidapp.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * The type User login credentials
 * persists and deletes the userName
 * and password of the user and
 * allows to check the login state.
 *
 * Extended by Maximilian Haag on 18.02.2017.
 * Tokens for Amazon SNS and Google Firebase Messaging Services added.
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
     * The constant FCM_TOKEN.
     */
    private static final String PREF_FCM_TOKEN = "fcmToken";

    /**
     * The constant SNS_TOKEN.
     */
    private static final String PREF_SNS_TOKEN = "snsToken";

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

        editor.remove(PREF_FCM_TOKEN);
        editor.remove(PREF_SNS_TOKEN);

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
     * Gets fcm token.
     *
     * @return the fcm token
     */
    public String getFcmToken() {
        return getPreferences().getString(PREF_FCM_TOKEN, null);
    }

    /**
     * Sets fcm token.
     *
     * @param token the token
     */
    public void setFcmToken(String token) {

        SharedPreferences.Editor editor = getPreferencesEditor();
        editor.putString(PREF_FCM_TOKEN, token);
        editor.commit();
    }


    /**
     * Gets sns token.
     *
     * @return the sns token
     */
    public String getSnsToken() {
        return getPreferences().getString(PREF_SNS_TOKEN, null);
    }

    /**
     * Sets sns token.
     *
     * @param token the token
     */
    public void setSnsToken(String token) {

        SharedPreferences.Editor editor = getPreferencesEditor();
        editor.putString(PREF_SNS_TOKEN, token);
        editor.commit();
    }
}
