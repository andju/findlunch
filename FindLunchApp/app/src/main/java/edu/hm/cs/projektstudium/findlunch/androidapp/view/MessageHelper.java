package edu.hm.cs.projektstudium.findlunch.androidapp.view;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The type Message helper
 * provides methods that
 * allow to print messages
 * on the user interface.
 */
public class MessageHelper {

    /**
     * The Context.
     */
    private final Context context;

    /**
     * Instantiates a new Message helper.
     *
     * @param context the context
     */
    public MessageHelper(Context context) {
        this.context = context;
    }

    /**
     * Method that prints
     * a snackbar message.
     *
     * @param view     the view
     * @param message  the message
     * @param length   the length
     * @param text     the text
     * @param listener the listener
     */
    private void printSnackbarMessage(View view, String message, int length,
                                      @SuppressWarnings("SameParameterValue") String text,
                                      @SuppressWarnings("SameParameterValue") View.OnClickListener listener,
                                      boolean multiLines) {
        // snackbar that shows the message text
        Snackbar result = Snackbar.make(view, message, length);

        if(multiLines) {
            View snackbarView = result.getView();
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(5);
        }

        if(text != null && listener != null) {
            result.setAction(text, listener);
        }
        result.show();
    }

    /**
     * Method that prints
     * a snackbar message.
     *
     * @param view    the view
     * @param message the message
     */
    public void printSnackbarMessage(View view, String message) {
        // custom duration to show the message in milliseconds
        int duration = 5000;
        printSnackbarMessage(view, message, duration, null, null, false);
    }

    /**
     * Method that prints
     * a multiline snackbar message.
     *
     * @param view    the view
     * @param message the message
     */
    public void printMultilineSnackbarMessage(View view, String message) {
        // custom duration to show the message in milliseconds
        int duration = 5000;
        printSnackbarMessage(view, message, duration, null, null, true);
    }

    /**
     * Method that prints
     * a toast message.
     *
     * @param message the message
     */
    public void printToastMessage(String message) {
        // duration to show the message
        int duration = Toast.LENGTH_SHORT;
        // toast message that shows the message text
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    /**
     * Method that prints
     * a log message.
     *
     * @param tag     the tag
     * @param message the message
     */
    public void printLogMessage(String tag, String message) {
        Log.v(tag, message);
    }
}
