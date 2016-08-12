package edu.hm.cs.projektstudium.findlunch.androidapp.interaction;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * The type KeyboardHelper
 * provides a method
 * that closes the
 * software keyboard.
 */
public class KeyboardHelper {
    /**
     * Instantiates a new KeyboardHelper.
     */
    public KeyboardHelper() {
    }

    /**
     * Method that closes the
     * software keyboard.
     *
     * @param activity the activity
     */
    public void closeKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            // create an input method mananger
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}