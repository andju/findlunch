package edu.hm.cs.projektstudium.findlunch.androidapp.interaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * The type Phone helper
 * provides a method
 * that opens the dialer
 * with a given phone number.
 */
public class PhoneHelper {

    /**
     * The Context.
     */
    private final Context context;

    /**
     * Instantiates a new Phone helper.
     *
     * @param context the context
     */
    public PhoneHelper(Context context) {
        this.context = context;
    }

    /**
     * Method that opens the dialer
     * with a given phone number.
     *
     * @param phoneNumber the phone number
     */
    public void dialPhoneNumber(String phoneNumber) {
        // create a new intent
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
