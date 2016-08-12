package edu.hm.cs.projektstudium.findlunch.androidapp.interaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


/**
 * The type Image helper
 * provides a method
 * that decodes a
 * base 64 encoded bitmap.
 */
public class ImageHelper {
    /**
     * Method that decodes a
     * base 64 encoded bitmap.
     *
     * @param input the base 64 encoded string
     * @return the bitmap
     */
    public static Bitmap decodeBase64(String input) {
        // the decoded bitmap as string
        byte[] decodedString = Base64.decode(input.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
