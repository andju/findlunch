package edu.hm.cs.projektstudium.findlunch.androidapp.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * The type Permission helper
 * provides methods that
 * allow to check and request permissions.
 */
public class PermissionHelper {
    /**
     * The Activity.
     */
    private final Activity activity;

    /**
     * The constant MAIN_PERMISSION_REQUEST.
     */
    public static final int MAIN_PERMISSION_REQUEST = 1;

    /**
     * Instantiates a new Permission helper.
     *
     * @param activity the activity
     */
    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks whether a specific permission has already been granted or not.
     *
     * @param permission the permission to check
     * @return <code>true</code> if the permission has already been granted
     */
    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity,
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request permission.
     *
     * @param permission the permission
     */
    public void requestPermission(@SuppressWarnings("SameParameterValue") String permission) {
        // Here, thisActivity is the current activity
        if (!checkPermission(permission)) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        MAIN_PERMISSION_REQUEST);

                // MAIN_PERMISSION_REQUEST is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
