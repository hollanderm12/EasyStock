package com.mtlcollege.quickstocks.util;

import android.content.Context;
import android.widget.Toast;

public class WebLookup {

    public static String lookup(String url, Context context) {

        String result;
        HTTPConnection connectionRequest = new HTTPConnection();
        try {
            result = connectionRequest.execute(url).get();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context,
                    "Communication with the remote server was interrupted. Please try again.", Toast.LENGTH_LONG).show();
            return "";
        }

        //A connection error occurred, usually due to a connection timeout or the device being offline
        if(result.equals("$CONNECTION_ERROR$")) {
            Toast.makeText(context,
                    "There was a problem communicating with the remote server. Please ensure your Internet connection is properly functional.", Toast.LENGTH_LONG).show();
            return "";
        }

        if(result.startsWith("$RESPONSE_ERROR$")) {
            /*Handle HTTP server errors
            * 401 - User/Password API keys are invalid or expired (this will happen once the subscription to the API expires)
            * 404 - The endpoint requested does not exist (should never occur in this program)
            * 503 - The Intrinio server is experiencing high server load or the throttle load has been reached */
                Toast.makeText(context, "There was a problem communicating with the remote server. Please try again later.",
                        Toast.LENGTH_LONG).show();
            return "";
        }

        return result;
    }
}
