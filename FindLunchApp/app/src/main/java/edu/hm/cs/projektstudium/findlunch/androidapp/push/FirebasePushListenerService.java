package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.activity.MainActivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.measure.MeasureProcessing;

import static edu.hm.cs.projektstudium.findlunch.androidapp.measure.MeasureProcessing.isMeasureActive;


/**
 *
 * Listener class that handles incoming Push-Notification from FCM.
 * Displays push notification at device.
 * For measure only 2 data from message required.
 *
 * Created by Maxmilian Haag on 21.11.2016.
 */
public class FirebasePushListenerService extends FirebaseMessagingService {

    private static final String TAG = "FirebasePushListenerService";

    /**
     * Activated measure or live operation (only adjustable at separated package "Measure").
     */
    private static final boolean MEASURE_ACTIVE = isMeasureActive();
    private static MeasureProcessing measure;


    /**
     * Creating push listener service.
     */
    public FirebasePushListenerService() {
        // Measure singleton
        if(MEASURE_ACTIVE) {
            getMeasureProcessing();
        }
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log info
        Log.e(TAG, "FCM MESSAGE RECEIVED!!");

        //Always live-operation, if not changed at package "Measure".
        if (MEASURE_ACTIVE) {
            //Add data to measure
            recordMeasure(remoteMessage);

        } else {
            try {
                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                    Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                }

                // Check if message contains a data payload.
                if (remoteMessage.getData() != null) {
                    Log.d(TAG, "Message Notification Body: " + remoteMessage.getData());

                    //data part of FCM message
                    Map<String, String> data = remoteMessage.getData();

                    String notificationTitle = getString(R.string.text_push_subtitle);
                    String pushTitle = data.get("title").toString();
                    Float longitude = Float.parseFloat(data.get("longitude"));
                    Float latitude = Float.parseFloat(data.get("latitude"));
                    int radius = Integer.parseInt(data.get("radius"));
                    int pushId = Integer.parseInt(data.get("pushId"));

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
     * Create and show a simple notification containing the received FCM message.
     *
     * @param notificationTitle FCM message received.
     */
    private void showNotification(Integer pushId, String notificationTitle, String pushTitle, Float longitude,
                                  Float latitude, int radius, int[] kitchenTypeIds) {

        // Create pending intent (which is used, when user opens push-notification), that calls MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("intent_source",1);
        intent.putExtra("title",pushTitle);
        intent.putExtra("longitude",longitude);
        intent.putExtra("latitude",latitude);
        intent.putExtra("radius",radius);
        intent.putExtra("kitchenTypeIds",kitchenTypeIds);

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
    private void recordMeasure(RemoteMessage remoteMessage) {
        measure.storeMeasureData(remoteMessage);
    }

}

