package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerBase;
import com.amazon.device.messaging.ADMMessageReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.activity.MainActivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.measure.MeasureProcessing;

import static edu.hm.cs.projektstudium.findlunch.androidapp.measure.MeasureProcessing.isMeasureActive;

/**
 *
 * Class for processing and receiving of Amazon SNS/ADM cloud messages.
 * Displays push notification at device.
 *
 * Created by Maxmilian Haag on 24.11.2016.
 */

public class AmazonPushListenerService extends ADMMessageHandlerBase {

    private static final String TAG = "AmazonPushListenerService";

    /**
     * Identification data for sending to Main Activity.
     */
    public static final String ADM_TOKEN_MESSAGE = "Amazon_SNS_Token";
    public static final String ADM_TOKEN_FILTER_EXTRA = "admToken";
    public static final String ADM_TOKEN_MESSAGE_SUCCESS = "admRequestSuccessStatus";

    /**
     * Activated measure or live operation (only adjustable at separated package "Measure").
     */
    private static final boolean MEASURE_ACTIVE = isMeasureActive();

    /**
     * Only required at measure.
     */
    private static MeasureProcessing measure;

    /**
     * Current Amazon SNS token.
     */
    private String snsToken;


    /**
     * Initiated by broadcast receiver, forwarding intents to service for processing.
     */
    public static class ADMReceiver extends ADMMessageReceiver {
        public ADMReceiver() {
            super(AmazonPushListenerService.class);
        }
    }

    /**
     * Creating push listener service.
     */
    public AmazonPushListenerService() {
        super(AmazonPushListenerService.class.getName());
        // Measure singleton
        if(MEASURE_ACTIVE) {
            getMeasureProcessing();
        }
    }

    public void onCreate() {
        Log.i(TAG, "ADM Listening service started");
        super.onCreate();
    }

    @Override
    protected void onMessage(final Intent intent) {
        //Log info
        Log.e(TAG, "ADM MESSAGE RECEIVED!!");

        //Always live-operation, if not changed at package "Measure".
        if (MEASURE_ACTIVE) {
            // Add data to measure
            sendMeasureToServer(intent);

        } else {
            // Obtain the extras that were included in the intent.
            final Bundle data = intent.getExtras();

            try {
                // Check if message contains a data payload.
                if (data != null) {
                    Log.d(TAG, "Message Notification Body: " + data.toString());

                    String notificationTitle = getString(R.string.text_push_subtitle);
                    String pushTitle = data.get("title").toString();

                    Float longitude = Float.parseFloat(data.get("longitude").toString());
                    Float latitude = Float.parseFloat(data.get("latitude").toString());
                    int radius = Integer.parseInt(data.get("radius").toString());
                    int pushId = Integer.parseInt(data.get("pushId").toString());

                    String kitchenTypeIdsString = data.get("kitchenTypeIds").toString();
                    int[] kitchenTypeIds = new int[0];
                    if (!kitchenTypeIdsString.equals("[]")) {
                        String[] kitchenTypeIdsStringArray = kitchenTypeIdsString.substring(1, kitchenTypeIdsString.length() - 1).split(", ");
                        kitchenTypeIds = new int[kitchenTypeIdsStringArray.length];
                        for (int i = 0; i < kitchenTypeIds.length; i++) {
                            kitchenTypeIds[i] = Integer.parseInt(kitchenTypeIdsStringArray[i]);
                        }
                    }
                    showNotification(pushId, notificationTitle, pushTitle, longitude, latitude, radius, kitchenTypeIds);

                }

            } catch (Exception e) {
                Log.w("onMessageReceived", e.getMessage());
            }
        }

    }



    /**
     * Registration process is started by calling startRegister() at AmazonSNSRegHandler.
     * When the registration ID is ready, ADM calls onRegistred().
     * ID is transmitted to webserver, so webserver knows app instance.
     * If device is rotated or changed for any reason, app passes new registration ID to webserver,
     */
    @Override
    protected void onRegistered(final String newRegistrationId) {

        try {
            snsToken = newRegistrationId;
            sendRegistrationToServer(snsToken, true);

        } catch (Exception e) {
            //Log info
            Log.e("SNSRegistration", "Amazon Token not retrieved");
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
        intent.setAction(ADM_TOKEN_MESSAGE);
        intent.putExtra(ADM_TOKEN_MESSAGE_SUCCESS, status);
        intent.putExtra(ADM_TOKEN_FILTER_EXTRA, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Creating measure processing (used in case of measure).
     */
    private static MeasureProcessing getMeasureProcessing() {
        if(measure == null) {
            measure = new MeasureProcessing();
        }
        return measure;
    }

    /**
     * Send measure data to webserver.
     */
    private void sendMeasureToServer(Intent intent) {
        measure.processMeasureData(intent.getExtras());
    }


    /**
     * Create and show a simple notification containing the received ADM message.
     *
     * @param notificationTitle ADM message received.
     */
    private void showNotification(Integer pushId, String notificationTitle, String pushTitle, Float longitude,
                                  Float latitude, int radius, int[] kitchenTypeIds) {

        // Create pending intent (which is used, when user opens push-notification), that calls MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("intent_source", 1);
        intent.putExtra("title", pushTitle);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("radius", radius);
        intent.putExtra("kitchenTypeIds", kitchenTypeIds);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, pushId, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Display Push-Notification in Android Status-Bar.
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_find_lunch_white)
                .setContentTitle(notificationTitle)
                .setContentText(pushTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(pushId, notificationBuilder.build());
    }

    @Override
    protected void onUnregistered(final String registrationId) {
    }

    @Override
    protected void onRegistrationError(final String errorId) {
    }

}


