package com.mtlcollege.quickstocks.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPConnection extends AsyncTask<String, Void, String> {

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection conn;
        final String username = "ad4aa788b1a8504fce53b6b9bd99a55a";
        final String password = "bbed478ac526629a1f7692bd5f33ef4f";

        try {
            String inputLine;
            URL obj = new URL(urls[0]);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod(REQUEST_METHOD);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestProperty("Authentication", "Basic " + username + ":" + password);
            conn.connect();
            if(conn.getResponseCode() != 200) {
                stringBuilder.append("$RESPONSE_ERROR$#" + conn.getResponseCode());
            }
            else {
                InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
            }
        }
        catch(Exception ex) {
            Log.w(String.valueOf(HTTPConnection.class), "doInBackground: " + ex.getMessage(), ex);
            stringBuilder.setLength(0);
            stringBuilder.append("$CONNECTION_ERROR$");
        }

        return stringBuilder.toString();
    }
}
