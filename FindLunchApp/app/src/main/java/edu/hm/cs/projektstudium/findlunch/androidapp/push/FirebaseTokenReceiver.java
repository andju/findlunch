package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginCredentials;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.RequestHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.RequestReason;

/**
 *
 * Service class to obtain the FCM-Token and Broadcast to MainActivity (Main Process)
 *
 * Created by Maxmilian Haag on 20.11.2016.
 */

public class FirebaseTokenReceiver extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseTokenReceiver";

    /**
     * Identification data for sending to Main Activity.
     */
    public static final String FCM_TOKEN_MESSAGE = "Google_FCM_Token";
    public static final String FCM_TOKEN_FILTER_EXTRA = "fcmToken";
    public static final String FCM_TOKEN_MESSAGE_SUCCESS = "fcmRequestSuccessStatus";

    /**
     * Current obtainted FCM token.
     */
    private String currentToken = "";

    /**
     * Initialization of token receiver.
     */
    public FirebaseTokenReceiver() {
    }

    /**
     * Asynchronous call, if new token is available for device.
     * Important: Only called when NEW token for device, if old token available its NOT called!
     * (Force, if no token available)
     */
    @Override
    public void onTokenRefresh() {

        //Obtain token from Google Firebase Cloud Messaging service.
        currentToken = FirebaseInstanceId.getInstance().getToken();

        try {
            //Log info
            Log.e(TAG, "Refreshed token: " + currentToken);

            //Broadcast obtained token to MainActivity to pass to webserver.
            sendRegistrationToServer(currentToken, true);
        } catch (Exception e) {
            //Log info
            Log.e(TAG, "FCM Token could not be obtained");
            sendRegistrationToServer("", false);
        }
    }


    /**
     * Sends broadcast to Main for persisting token to webserver.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, boolean status) {

        Intent intent = new Intent();
        intent.setAction(FCM_TOKEN_MESSAGE);
        intent.putExtra(FCM_TOKEN_MESSAGE_SUCCESS, status);
        intent.putExtra(FCM_TOKEN_FILTER_EXTRA, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}