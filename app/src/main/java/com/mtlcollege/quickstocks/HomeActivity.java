package com.mtlcollege.quickstocks;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mtlcollege.quickstocks.util.HTTPConnection;
import com.mtlcollege.quickstocks.util.JSONParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final int CONNECTION_PERMISSION_REQUEST = 1;
    Button btnLookup;
    EditText inpStartDate, inpEndDate;
    Calendar todaysDate = Calendar.getInstance();
    final Calendar startCalendar = Calendar.getInstance();
    final Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLookup = findViewById(R.id.btnLookup);

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
                //result will be empty if user error occurs (e.g. leaving the symbol input blank) or if there is a problem communicating with the Intrinio server
                if(!result.isEmpty()) {
                    int resultCount = JSONParser.getResultCount(result);
                    if(resultCount == 0)
                        Toast.makeText(getApplicationContext(), "Your lookup returned no results.", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
                        intent.putExtra("lookupResult", result);
                        startActivity(intent);
                    }
                }
            }
        });

        inpStartDate = findViewById(R.id.inpStartDate);
        inpEndDate = findViewById(R.id.inpEndDate);
        updateDate(1);

        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, month);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(2);
            }
        };
        inpStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, startDate, startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, month);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(3);
            }
        };
        inpEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, endDate, endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDate(int mode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

        /* 1: Sets the start and end dialog boxes to the current date (called once in the onCreate() method)
         * 2: Sets the start date based on the user's selection
         * 3: Sets the end date based on the user's selection */
        switch(mode) {
            case 1: inpStartDate.setText(sdf.format(todaysDate.getTime()));
                    inpEndDate.setText(sdf.format(todaysDate.getTime()));
                    break;
            case 2: inpStartDate.setText(sdf.format(startCalendar.getTime())); break;
            case 3: inpEndDate.setText(sdf.format(endCalendar.getTime())); break;
        }
    }

    private String lookupStock() {
        EditText inpSymbol = findViewById(R.id.inpSymbol);
        String symbol = inpSymbol.getText().toString().toUpperCase();
        todaysDate = Calendar.getInstance(); //Reload todaysDate in case the date changes between the activity loading and the lookup button being pressed

        //User error conditions. An empty string returned causes the calling method to perform no action.
        if(symbol.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a symbol.", Toast.LENGTH_SHORT).show();
            return "";
        }
        if(startCalendar.getTime().after(endCalendar.getTime())) {
            Toast.makeText(getApplicationContext(), "The start date must be before or equal to the end date.", Toast.LENGTH_SHORT).show();
            return "";
        }
        if(startCalendar.getTime().after(todaysDate.getTime()) || endCalendar.getTime().after(todaysDate.getTime())) {
            Toast.makeText(getApplicationContext(), "The start and end dates cannot be in the future.", Toast.LENGTH_SHORT).show();
            return "";
        }


        //EXAMPLE: https://api.intrinio.com/prices?identifier=PZA:CT

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://api.intrinio.com/prices?identifier=")
                .append(symbol)
                .append(":CT&api_key=OjgyYzhjNGZhYjJjZGVmZTJiOTAyYTRiZGZiNzI3MDI0")
                .append("&start_date=")
                .append(inpStartDate.getText())
                .append("&end_date=")
                .append(inpEndDate.getText());
        if(((RadioButton)findViewById(R.id.radRecent)).isChecked())
            urlBuilder.append("&sort_order=desc");
        if(((RadioButton)findViewById(R.id.radOldest)).isChecked())
            urlBuilder.append("&sort_order=asc");
        if(((RadioButton)findViewById(R.id.radDaily)).isChecked())
            urlBuilder.append("&frequency=daily");
        if(((RadioButton)findViewById(R.id.radWeekly)).isChecked())
            urlBuilder.append("&frequency=weekly");
        if(((RadioButton)findViewById(R.id.radMonthly)).isChecked())
            urlBuilder.append("&frequency=monthly");
        if(((RadioButton)findViewById(R.id.radQuarterly)).isChecked())
            urlBuilder.append("&frequency=quarterly");
        if(((RadioButton)findViewById(R.id.radYearly)).isChecked())
            urlBuilder.append("&frequency=yearly");
        urlBuilder.append("&page_number=1");

        String url = urlBuilder.toString();
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


