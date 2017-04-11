package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import static android.content.ContentValues.TAG;


/**
 *
 * Token request class for ensuring, that token is always up-to-date for current device.
 * If no token is available, asynchronous call at token receiver has to be forced to obtain token.
 *
 * Created by Maxmilian Haag on 20.12.2016.
 */

public class FirebaseTokenRequest extends IntentService {


    /**
     * Initialize token request.
     */
    public FirebaseTokenRequest() {
        super("FirebaseTokenRequest");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            // Check for current token (not required yet/token always changing, so always new ontainment!).
            String originalToken = getTokenFromPrefs();

            // Resets Instance ID and revokes all tokens for further token force.
            FirebaseInstanceId.getInstance().deleteInstanceId();

            // Clear current saved token to shared preferences.
            saveTokenToPrefs("");

            // Check for success of empty token (not required yet/check manually).
            String tokenCheck = getTokenFromPrefs();

            // Manually async call onTokenRefresh() at FirebaseTokenReceiver.
            // Log info, new token force.
            Log.d(TAG, "Getting new token");
            FirebaseInstanceId.getInstance().getToken();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Save and keep obtained token at Shared Preferences.
     */
    private void saveTokenToPrefs(String _token) {
        // Access Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        // Save to SharedPreferences
        editor.putString("registration_id", _token);
        editor.apply();
    }

    /**
     * Read current token from Shared Preferences.
     */
    private String getTokenFromPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("registration_id", null);
    }
}
