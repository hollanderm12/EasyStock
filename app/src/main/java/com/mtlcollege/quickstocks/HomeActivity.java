package com.mtlcollege.quickstocks;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mtlcollege.quickstocks.model.StockInfo;
import com.mtlcollege.quickstocks.util.Credentials;
import com.mtlcollege.quickstocks.util.HTTPConnection;
import com.mtlcollege.quickstocks.util.JSONParser;
import com.mtlcollege.quickstocks.util.WebLookup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final int CONNECTION_PERMISSION_REQUEST = 1;
    Button btnLookup;
    EditText inpSymbol, inpStartDate, inpEndDate;
    Calendar todaysDate = Calendar.getInstance();
    final Calendar startCalendar = Calendar.getInstance();
    final Calendar endCalendar = Calendar.getInstance();
    boolean oldestFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inpSymbol = findViewById(R.id.inpSymbol);

        btnLookup = findViewById(R.id.btnLookup);
        btnLookup.setOnClickListener(v -> {
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
                    if(resultCount > 100)
                        Toast.makeText(getApplicationContext(), "The maximum of 100 results has been returned.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
                    intent.putExtra("lookupResult", result);
                    String symbol = inpSymbol.getText().toString().toUpperCase();
                    intent.putExtra("symbol", symbol);
                    intent.putExtra("oldestFirst", oldestFirst);
                    startActivity(intent);
                }
            }
        });

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivityForResult(intent, 1);
        });

        inpStartDate = findViewById(R.id.inpStartDate);
        inpEndDate = findViewById(R.id.inpEndDate);
        updateDate(Mode.CURRENT_DATE);

        final DatePickerDialog.OnDateSetListener startDate = (view, year, month, dayOfMonth) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(Mode.UPDATE_START);
        };
        inpStartDate.setOnClickListener(v -> new DatePickerDialog(HomeActivity.this, startDate, startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)).show());

        final DatePickerDialog.OnDateSetListener endDate = (view, year, month, dayOfMonth) -> {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(Mode.UPDATE_END);
        };
        inpEndDate.setOnClickListener(v -> new DatePickerDialog(HomeActivity.this, endDate, endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //1 is passed if a search result was returned from SearchActivity
        if(resultCode == 1) {
            String symbolReceived = data.getStringExtra("symbol");
            inpSymbol.setText(symbolReceived);
        }
    }

    private enum Mode {
        CURRENT_DATE, UPDATE_START, UPDATE_END
    }

    private void updateDate(Mode mode) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

        /* CURRENT_DATE: Sets the start and end dialog boxes to the current date (called once in the onCreate() method)
         * UPDATE_START: Sets the start date based on the user's selection
         * UPDATE_END: Sets the end date based on the user's selection */
        switch(mode) {
            case CURRENT_DATE: inpStartDate.setText(sdf.format(todaysDate.getTime()));
                    inpEndDate.setText(sdf.format(todaysDate.getTime()));
                    break;
            case UPDATE_START: inpStartDate.setText(sdf.format(startCalendar.getTime())); break;
            case UPDATE_END: inpEndDate.setText(sdf.format(endCalendar.getTime())); break;
        }
    }

    private String lookupStock() {
        String symbol = inpSymbol.getText().toString().trim().toUpperCase();
        symbol = symbol.replace(" ", "%20");
        todaysDate = Calendar.getInstance(); //Reload todaysDate in case the date changes between the activity loading and the lookup button being pressed

        //User error conditions
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

        StringBuilder urlBuilder = new StringBuilder();
           urlBuilder.append("https://api.intrinio.com/prices?identifier=")
                .append(symbol)
                .append(":CT&api_key=")
                .append(Credentials.getApiKey())
                .append("&start_date=")
                .append(inpStartDate.getText())
                .append("&end_date=")
                .append(inpEndDate.getText());
        if(((RadioButton)findViewById(R.id.radRecent)).isChecked())
            urlBuilder.append("&sort_order=desc");
        if(((RadioButton)findViewById(R.id.radOldest)).isChecked()) {
            urlBuilder.append("&sort_order=asc");
            oldestFirst = true;
        }
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
        urlBuilder.append("&page_number=1&page_size=100");

        return WebLookup.lookup(urlBuilder.toString(), getApplicationContext());
    }
}


