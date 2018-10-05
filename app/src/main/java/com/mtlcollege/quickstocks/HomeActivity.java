package com.mtlcollege.quickstocks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mtlcollege.quickstocks.util.HTTPConnection;

public class HomeActivity extends AppCompatActivity {

    private static final int CONNECTION_PERMISSION_REQUEST = 1;
    Button btnLookup, btnOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLookup = findViewById(R.id.btnLookup);
        btnOptions = findViewById(R.id.btnOptions);

        btnLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.INTERNET)) {
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{Manifest.permission.INTERNET}, CONNECTION_PERMISSION_REQUEST);
                    }
                    else {
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{Manifest.permission.INTERNET}, CONNECTION_PERMISSION_REQUEST);
                    }
                }

                String result = lookupStock();
                if(!result.isEmpty()) {
                    Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
                    intent.putExtra("lookupResult", result);
                    startActivity(intent);
                }
            }
        });

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });


        //TODO: Search for symbols based on company name (found at https://api.iextrading.com/1.0/ref-data/symbols )
        //TODO: Display parameters
    }

    private String lookupStock() {
        EditText inpSymbol = findViewById(R.id.inpSymbol);
        String symbol = inpSymbol.getText().toString().toUpperCase();
        if(symbol.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a symbol.", Toast.LENGTH_SHORT).show();
            return "";
        }

        String url = "https://api.iextrading.com/1.0/stock/" + symbol + "/batch?types=quote";
        String result;
        HTTPConnection connectionRequest = new HTTPConnection();
        try {
            result = connectionRequest.execute(url).get();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Communication with the remote server was interrupted. Please try again.", Toast.LENGTH_LONG).show();
            return "";
        }

        //A connection error occurred, more than likely due to the device being offline
        if(result.equals("$CONNECTION_ERROR$")) {
            Toast.makeText(getApplicationContext(),
                    "There was a problem communicating with the remote server. Please ensure you are connected to the Internet.", Toast.LENGTH_LONG).show();
            return "";
        }

        if(result.startsWith("$RESPONSE_ERROR$")) {
            String[] splitString = result.split("#");

            //If an invalid symbol is searched, HTTP 404 will be returned
            if(splitString[1].equals("404"))
                Toast.makeText(getApplicationContext(), "The symbol entered could not be found.", Toast.LENGTH_SHORT).show();

            //Handle all other responses
            else {
                Log.w(String.valueOf(HomeActivity.this), "Error communicating with the remote server. Response code: " + splitString[1]);
                Toast.makeText(getApplicationContext(), "There was a problem communicating with the remote server. Please try again later.",
                        Toast.LENGTH_LONG).show();
            }
            return "";
        }

        return result;
    }
}


