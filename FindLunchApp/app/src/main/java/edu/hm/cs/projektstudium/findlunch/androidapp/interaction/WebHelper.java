package edu.hm.cs.projektstudium.findlunch.androidapp.interaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * The type Web helper.
 */
public class WebHelper {

    /**
     * The Context.
     */
    private final Context context;

    /**
     * Instantiates a new Web helper.
     *
     * @param context the context
     */
    public WebHelper(Context context) {
        this.context = context;
    }

    /**
     * Opens web page in the default browser.
     *
     * @param url the url
     */
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * Constructs and url.
     *
     * @param host  the host
     * @param port  the port
     * @param path  the path
     * @param https https is used
     * @return the url
     */
    public String constructUrl(String host, int port, String path,
                               @SuppressWarnings("SameParameterValue") boolean https) {
        StringBuilder result = new StringBuilder();
        if(https) {
            result.append("https://");
            result.append(host);
            if(port != 443) {
                result.append(":");
                result.append(port);
            }
        } else {
            result.append("http://");
            result.append(host);
            if(port != 80) {
                result.append(":");
                result.append(port);
            }
        }
        result.append(path);

        return result.toString();
    }
}
