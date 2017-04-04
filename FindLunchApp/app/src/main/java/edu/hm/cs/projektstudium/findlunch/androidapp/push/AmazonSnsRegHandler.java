package edu.hm.cs.projektstudium.findlunch.androidapp.push;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.amazon.device.messaging.ADM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static edu.hm.cs.projektstudium.findlunch.androidapp.push.AmazonPushListenerService.ADM_TOKEN_FILTER_EXTRA;
import static edu.hm.cs.projektstudium.findlunch.androidapp.push.AmazonPushListenerService.ADM_TOKEN_MESSAGE;
import static edu.hm.cs.projektstudium.findlunch.androidapp.push.AmazonPushListenerService.ADM_TOKEN_MESSAGE_SUCCESS;

/**
 *
 * Class for handling token registration from Amazon Simple Notification Service.
 *
 * Created by Maxmilian Haag on 22.11.2016.
 */

public class AmazonSnsRegHandler {

    /**
     * Current obtainted SNS/ADM token.
     */
    private String currentToken = null;

    /**
     * Start registration at creation.
     */
    public AmazonSnsRegHandler (Context mainContext) {
        register(mainContext);
    }

    /**
     * Start registration at creation.
     */
    private void register(Context mainContext) {
        try {
            final ADM adm = new ADM(mainContext);

            if (adm.isSupported()) {
                //Register at Amazon SNS Cloud Messaging service
                currentToken = adm.getRegistrationId();

                //Get token from Amazon, pass to main, send to webserver.
                if (currentToken == null) {
                    //Log info
                    Log.i("AmazonSnsRegHandler", "RegID null, obtaining new token");

                    // startRegister() is asynchronous
                    // Callback of onRegistred() if registration ID / token is available for device.
                    adm.startRegister();
                } else {
                    // If already available, pass to webserver.
                    sendRegistrationToServer(currentToken, true, mainContext);

                }
            }
        } catch (Exception e) {
            //Log error info.
            Log.e("Exception", "Token obtaining errror.");
        }
    }

    /**
     * Pass token to Main and pass to webserver.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, boolean status, Context context) {
        Intent intent = new Intent ();
        intent.setAction(ADM_TOKEN_MESSAGE);
        intent.putExtra(ADM_TOKEN_MESSAGE_SUCCESS, status);
        intent.putExtra(ADM_TOKEN_FILTER_EXTRA, token);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
