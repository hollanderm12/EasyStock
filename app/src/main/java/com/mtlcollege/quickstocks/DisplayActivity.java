package com.mtlcollege.quickstocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import com.mtlcollege.quickstocks.util.Keys;

public class DisplayActivity extends AppCompatActivity {
    TextView txtSymbol, txtCompanyName, txtTest;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        txtSymbol = findViewById(R.id.txtSymbol);
        txtCompanyName = findViewById(R.id.txtCompanyName);
        txtTest = findViewById(R.id.txtTest);
        Bundle extras = getIntent().getExtras();

        try {
            Keys keys = new Keys();
            sp = getSharedPreferences(keys.PREFS, Context.MODE_PRIVATE);
            String symbol = "";
            String companyName = "";
            ArrayList<String> list = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(extras.getString("lookupResult"));
            Iterator<String> jsonKeys = jsonObject.keys();

            if(jsonKeys.hasNext()) {
                String currentKey = jsonKeys.next();
                switch(currentKey) {
                    case("quote"):
                        symbol = jsonObject.getJSONObject(currentKey).getString("symbol");
                        companyName = jsonObject.getJSONObject(currentKey).getString("companyName");
                        if (sp.getBoolean(keys.Q_HIGH, false) == true)
                            list.add("High: " + jsonObject.getJSONObject(currentKey).get("high").toString());
                        if (sp.getBoolean(keys.Q_LOW, false) == true)
                            list.add("Low: " + jsonObject.getJSONObject(currentKey).get("low").toString());
                        if (sp.getBoolean(keys.Q_LATEST_PRICE, false) == true)
                            list.add("Latest Price: " + jsonObject.getJSONObject(currentKey).get("latestPrice").toString());
                        break;
                    case("chart"):
                        //TODO: Chart
                        break;
                    case("news"):
                        //TODO: News
                        break;
                }

            }

            /*JSONArray jsonArray = jsonObject.getJSONArray("quote"); //"chart", "news", and "quote" are the three arrays
            for (int i = 0; i < jsonArray.length(); i++) {
                if(sp.contains(keys.LOW))
                    list.add(jsonArray.getJSONObject(i).get("low").toString());
                if(sp.contains(keys.HIGH))
                    list.add(jsonArray.getJSONObject(i).get("low").toString());
                if(sp.contains(keys.LATEST_PRICE))
                    list.add(jsonArray.getJSONObject(i).get("latestPrice").toString());

            }*/

            txtSymbol.setText(symbol);
            txtCompanyName.setText(companyName);
            if(!list.isEmpty()) {
                for(String item : list)
                    txtTest.append(item + "\n");
            }
            else
                txtTest.setText("No options selected!");
        }
        catch (JSONException ex) {
            Log.w(String.valueOf(DisplayActivity.class), "onCreate: ", ex);
            txtTest.setText("JSONException: " + ex.getMessage());
        }
    }
}
