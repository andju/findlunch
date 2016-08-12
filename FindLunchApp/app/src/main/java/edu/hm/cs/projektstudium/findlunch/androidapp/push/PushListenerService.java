package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.concurrent.atomic.AtomicInteger;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.activity.MainActivity;

/**
 * Listener that handles incoming Push-Notification from GCM.
 *
 * The Code is based on the Class RegistrationIntentService.java from Cloud Messaging Quickstart
 * (https://github.com/googlesamples/google-services/tree/master/android/gcm).
 *
 * Created by Andreas Juckel on 13.07.2016.
 */
public class PushListenerService extends GcmListenerService {

    private static final String TAG = "PushListenerService";

    //Generator for unique message ids.
    private final static AtomicInteger messageIdGenerator = new AtomicInteger(0);

    @Override
    public void onMessageReceived(String from, Bundle data) {

        try {

            String notificationTitle = getString(R.string.text_push_subtitle);
            String pushTitle = data.getString("title");
            Float longitude = Float.parseFloat(data.getString("longitude"));
            Float latitude = Float.parseFloat(data.getString("latitude"));
            int radius = Integer.parseInt(data.getString("radius"));
            int pushId = Integer.parseInt(data.getString("pushId"));

            String kitchenTypeIdsString = data.getString("kitchenTypeIds");
            int[] kitchenTypeIds = new int[0];
            if(!kitchenTypeIdsString.equals("[]")) {
                String[] kitchenTypeIdsStringArray = kitchenTypeIdsString.substring(1,kitchenTypeIdsString.length()-1).split(", ");
                kitchenTypeIds = new int[kitchenTypeIdsStringArray.length];
                for (int i = 0; i < kitchenTypeIds.length; i++) {
                    kitchenTypeIds[i] = Integer.parseInt(kitchenTypeIdsStringArray[i]);
                }
            }

            showNotification(pushId, notificationTitle, pushTitle, longitude, latitude, radius, kitchenTypeIds);

        } catch (Exception e) {
            Log.w("onMessageReceived", e.getMessage());
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param notificationTitle GCM message received.
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
}