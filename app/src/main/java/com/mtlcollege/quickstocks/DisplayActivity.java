package com.mtlcollege.quickstocks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mtlcollege.quickstocks.model.Results;
import com.mtlcollege.quickstocks.util.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity {
    TextView txtSymbol, txtCompanyName, txtTest;
    Button btnPrevious, btnGoTo, btnNext;
    int maxIndex, currentIndex;
    ArrayList<Results> resultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        txtSymbol = findViewById(R.id.txtSymbol);
        txtCompanyName = findViewById(R.id.txtCompanyName);
        txtTest = findViewById(R.id.txtTest);
        Bundle extras = getIntent().getExtras();
        String result = extras.getString("lookupResult");
        resultList = JSONParser.getLookupResults(result);
        maxIndex = resultList.size() - 1;
        currentIndex = 0;

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnGoTo = findViewById(R.id.btnGoTo);

        //resultList cannot be empty as an empty list would have been detected in HomeActivity prior to this being called
        writeText(currentIndex);
        updateButtons(currentIndex);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex--;
                writeText(currentIndex);
                updateButtons(currentIndex);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex++;
                writeText(currentIndex);
                updateButtons(currentIndex);
            }
        });

        final PopupMenu popupMenu = new PopupMenu(DisplayActivity.this, btnGoTo);
        btnGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.getMenu().clear();
                int i = 0;
                for(Results r : resultList) {
                    popupMenu.getMenu().add(0, i++, Menu.NONE, formatDate(r.getDate()));
                }
                popupMenu.show();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                currentIndex = item.getItemId();
                writeText(currentIndex);
                updateButtons(currentIndex);
                return true;
            }
        });
    }

    private void writeText(int index) {
        TextView txtDate = findViewById(R.id.txtDate);
        txtDate.setText("Results for " + formatDate(resultList.get(index).getDate()));

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("Open: ").append(resultList.get(index).getOpen())
                .append("\nHigh: ").append(resultList.get(index).getHigh())
                .append("\nLow: ").append(resultList.get(index).getLow())
                .append("\nClose: ").append(resultList.get(index).getClose());
        txtTest.setText(resultBuilder.toString());
    }

    private void updateButtons(int index) {
        if(maxIndex == 0) {
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            btnGoTo.setEnabled(false);
        }
        else if(maxIndex == index) {
            btnPrevious.setEnabled(true);
            btnNext.setEnabled(false);
        }
        else if(index == 0) {
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(true);
        }
        else {
            btnPrevious.setEnabled(true);
            btnNext.setEnabled(true);
        }
    }

    public String formatDate(String toFormat) {
        SimpleDateFormat sdfParser = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdfParser.parse(toFormat);
            SimpleDateFormat sdfFormatter = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.CANADA);
            return sdfFormatter.format(date);
        }
        catch(ParseException e) {
            e.printStackTrace();
            return toFormat;
        }
    }
}

