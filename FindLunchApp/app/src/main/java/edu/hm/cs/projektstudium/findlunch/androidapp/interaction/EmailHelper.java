package edu.hm.cs.projektstudium.findlunch.androidapp.interaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * The type Email helper
 * provides a method
 * that opens a new email
 * in the default email agent.
 */
public class EmailHelper {

    /**
     * The Context of the activity.
     */
    private final Context context;

    /**
     * Instantiates a new Email helper.
     *
     * @param context the context
     */
    public EmailHelper(Context context) {
        this.context = context;
    }

    /**
     * Method that opens a new email
     * in the default email agent.
     *
     * @param addresses the addresses to send the email to
     * @param subject   the subject of the email
     * @param text      the text of the email
     */
    public void composeEmail(String[] addresses,
                             @SuppressWarnings("SameParameterValue") String subject,
                             @SuppressWarnings("SameParameterValue") String text) {
        // create a new intent
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
