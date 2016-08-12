package edu.hm.cs.projektstudium.findlunch.androidapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * The type Connectivity
 * provides a method that allows
 * to check whether the device has an active
 * network connection.
 */
public class Connectivity {
    /**
     * Checks whether the device has an active
     * network connection.
     *
     * @param context the context of the activity
     * @return <code>true</code> if the device has an active network connection
     */
    public static boolean isConnected(Context context) {
        // get the connectivity manager
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // get the active network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
