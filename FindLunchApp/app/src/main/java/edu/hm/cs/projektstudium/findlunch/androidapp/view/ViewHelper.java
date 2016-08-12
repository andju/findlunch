package edu.hm.cs.projektstudium.findlunch.androidapp.view;

import android.view.View;
import android.widget.TextView;


/**
 * The type View helper
 * provides methods that
 * allow to update textViews
 * and change their visibility.
 */
public class ViewHelper {
    /**
     * Method that updates
     * a textView with a given text
     * and makes it visible.
     *
     * @param textView the text view
     * @param text     the text
     */
    public static void makeVisibleOnUpdate(TextView textView, String text) {
        if (textView != null) {
            if(text != null && !(text.length() == 0)) {
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }
}
