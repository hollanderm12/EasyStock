package com.mtlcollege.quickstocks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mtlcollege.quickstocks.util.HTTPConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    private static final int CONNECTION_PERMISSION_REQUEST = 1;
    TextView txtTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        txtTest = findViewById(R.id.txtTest);

        if (ContextCompat.checkSelfPermission(DisplayActivity.this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DisplayActivity.this, Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(DisplayActivity.this,
                        new String[]{Manifest.permission.INTERNET}, CONNECTION_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(DisplayActivity.this,
                        new String[]{Manifest.permission.INTERNET}, CONNECTION_PERMISSION_REQUEST);
            }
        }
        else {
            String url = "https://api.iextrading.com/1.0/stock/MCD/batch?types=quote,news,chart&range=1m&last=1";
            HTTPConnection connectionRequest = new HTTPConnection();

            try {
                String result = connectionRequest.execute(url).get();
                if(result.startsWith("$RESPONSE_ERROR$")) {
                    String[] splitString = result.split("#");
                    String responseCode = splitString[1];
                    txtTest.setText("There was a problem contacting the remote server. Response code: " + responseCode);
                    return;
                }
                if(result.startsWith("$JAVA_ERROR$")) {
                    txtTest.setText("The app encountered a problem connecting to the remote server. Please check the logs for more information.");
                    return;
                }

                //TODO: Handler for an unknown symbol (result will be "Unknown Symbol")

                //Print all the values for JSON object "high" for the last month of trading for stock MCD (McDonald's)
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("chart"); //"news" and "quote" are the other two arrays
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        list.add(jsonArray.getJSONObject(i).get("high").toString());
                    }
                    txtTest.setText(list.toString());
                }
                catch (JSONException ex) {
                    Log.w(String.valueOf(DisplayActivity.class), "onCreate: ", ex);
                    txtTest.setText("JSONException: " + ex.getMessage());
                }
            }
            catch(Exception ex) {
                Log.w(String.valueOf(DisplayActivity.class), "onCreate: ", ex);
                txtTest.setText("Exception: " + ex.getMessage());
            }
        }
    }
}
