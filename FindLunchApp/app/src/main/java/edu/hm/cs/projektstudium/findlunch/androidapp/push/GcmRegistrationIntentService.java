package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;

/**
 * Service to obtain the GCM-Token (used by Google to address push messages to the device)
 * from Google.
 *
 * The Code is based on the Class RegistrationIntentService.java from Cloud Messaging Quickstart
 * (https://github.com/googlesamples/google-services/tree/master/android/gcm).
 *
 * Created by Andreas Juckel on 13.07.2016.
 */
public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";
    private static final String GCM_TOKEN_MESSAGE = "gcmTokenMessage";
    private static final String GCM_TOKEN_MESSAGE_TOKEN = "gcmToken";
    private static final String GCM_TOKEN_MESSAGE_SUCCESS = "gcmRequestSuccessStatus";

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            // Retrieve the GCM-Token
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // Broadcasts the GCM-Token as a Message (so it can be used by other classes).
            sendGcmTokenMessage(true, token);

        } catch (Exception e) {
            Log.w("GcmRegistration", "GCM Token could not be obtained");
            sendGcmTokenMessage(false, "");
        }
    }

    /**
     * Sends a broadcast message with the Intent gcmTokenMessage. Contains the Success-Status of
     * the Registration and (if successful) the GCM-Token
     *
     * @param successStatus token registration successful?
     * @param token gcm-token
     */
    private void sendGcmTokenMessage(Boolean successStatus, String token) {
        Intent intent = new Intent (GCM_TOKEN_MESSAGE);
        intent.putExtra(GCM_TOKEN_MESSAGE_SUCCESS, successStatus);
        intent.putExtra(GCM_TOKEN_MESSAGE_TOKEN, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}