package com.mtlcollege.quickstocks;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

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

            //Keys keys = new Keys();
            //sp = getSharedPreferences(keys.PREFS, Context.MODE_PRIVATE);
            //String symbol = "";
            //String companyName = "";

        ArrayList<String> resultsFound = extras.getStringArrayList("lookupResult");
        //resultsFound cannot be empty as an empty list would have been detected in HomeActivity prior to this being called
        for(String item : resultsFound)
            txtTest.append(item + "\n");
    }
}

