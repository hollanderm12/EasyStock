package com.mtlcollege.quickstocks;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mtlcollege.quickstocks.model.Results;
import com.mtlcollege.quickstocks.model.StockInfo;
import com.mtlcollege.quickstocks.util.Credentials;
import com.mtlcollege.quickstocks.util.JSONParser;
import com.mtlcollege.quickstocks.util.WebLookup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity {
    TextView txtSymbol, txtCompanyName;
    Button btnPrevious, btnGoTo, btnNext;
    int maxIndex, currentIndex;
    ArrayList<Results> resultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        txtSymbol = findViewById(R.id.txtSymbol);
        txtCompanyName = findViewById(R.id.txtCompanyName);
        Bundle extras = getIntent().getExtras();
        String result = extras.getString("lookupResult");
        Boolean oldestFirst = extras.getBoolean("oldestFirst");
        String symbol = extras.getString("symbol");
        symbol = symbol.replace(" ", "%20");

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://api.intrinio.com/securities?identifier=")
                .append(symbol)
                .append(":CT&exch_symbol=XTSE&api_key=")
                .append(Credentials.getApiKey());
        StockInfo si = JSONParser.getTopInformation(WebLookup.lookup(urlBuilder.toString(), getApplicationContext()));
        txtSymbol.setText(symbol + " (" + si.getCurrencyType() + ")");
        txtCompanyName.setText(si.getSecurityName());

        //resultList cannot be empty as an empty list would have been detected in HomeActivity prior to this being called
        resultList = JSONParser.getLookupResults(result);
        maxIndex = resultList.size() - 1;
        currentIndex = 0;

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnGoTo = findViewById(R.id.btnGoTo);

        writeText(currentIndex, oldestFirst);
        updateButtons(currentIndex);

        btnPrevious.setOnClickListener(v -> {
            currentIndex--;
            writeText(currentIndex, oldestFirst);
            updateButtons(currentIndex);
        });

        btnNext.setOnClickListener(v -> {
            currentIndex++;
            writeText(currentIndex, oldestFirst);
            updateButtons(currentIndex);
        });

        final PopupMenu popupMenu = new PopupMenu(DisplayActivity.this, btnGoTo);
        btnGoTo.setOnClickListener(v -> {
            popupMenu.getMenu().clear();
            int i = 0;
            for(Results r : resultList) {
                SpannableString s = new SpannableString(formatDate(r.getDate()));
                s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
                popupMenu.getMenu().add(0, i++, Menu.NONE, s);
            }
            popupMenu.show();
        });

        popupMenu.setOnMenuItemClickListener(item -> {
            currentIndex = item.getItemId();
            writeText(currentIndex, oldestFirst);
            updateButtons(currentIndex);
            return true;
        });
    }

    private void writeText(int index, boolean oldestFirst) {
        TextView txtDate = findViewById(R.id.txtDate);
        String dateText = "Results for " + formatDate(resultList.get(index).getDate());
        txtDate.setText(dateText);

        final String[] labels = {"Open: ", "High: ", "Low: ", "Close: ", "Volume: ",
                "Nonsplit Adjusted Dividend: ", "Split Ratio: ", "Adjust Factor: ",
                "Open: ", "High: ", "Low: ", "Close: ", "Volume: "};
        final String[] methods = {"getOpen", "getHigh", "getLow", "getClose", "getVolume",
                "getExDividend", "getSplitRatio", "getAdjFactor", "getAdjOpen", "getAdjHigh",
                "getAdjLow", "getAdjClose", "getAdjVolume"};
        final TextView[] textBoxes = {findViewById(R.id.txtOpen), findViewById(R.id.txtHigh),
                findViewById(R.id.txtLow), findViewById(R.id.txtClose), findViewById(R.id.txtVolume),
                findViewById(R.id.txtExDividend), findViewById(R.id.txtSplitRatio), findViewById(R.id.txtAdjFactor),
                findViewById(R.id.txtAdjOpen), findViewById(R.id.txtAdjHigh), findViewById(R.id.txtAdjLow),
                findViewById(R.id.txtAdjClose), findViewById(R.id.txtAdjVolume)};

        for(int i = 0 ; i < labels.length ; i++) {
            try {
                textBoxes[i].setVisibility(View.VISIBLE);
                StringBuilder resultBuilder = new StringBuilder();
                resultBuilder.append(labels[i]);
                Results r = resultList.get(index);
                Method m = r.getClass().getMethod(methods[i]);
                Object value = m.invoke(r);
                if(value instanceof Double) {
                    double d = (Double) value;
                    DecimalFormat df = new DecimalFormat("##0.00");
                    resultBuilder.append(df.format(d));
                    if(d > 0) {
                        if (((index != maxIndex && !oldestFirst) || (index != 0 && oldestFirst))
                                && !methods[i].equals("getExDividend") && !methods[i].equals("getSplitRatio")
                                && !methods[i].equals("getAdjFactor")) {
                            if (!oldestFirst)
                                r = resultList.get(index + 1);
                            else
                                r = resultList.get(index - 1);
                            m = r.getClass().getMethod(methods[i]);
                            Object valueNext = m.invoke(r);
                            double dDiff = d - (Double) valueNext;
                            if (dDiff != d) {
                                double diffPercentage = (dDiff / d) * 100;
                                if (dDiff > 0) {
                                    resultBuilder.append(" (+").append(df.format(diffPercentage)).append("%)");
                                    textBoxes[i].setTextColor(Color.GREEN);
                                } else if (dDiff == 0) {
                                    resultBuilder.append(" (No change)");
                                    textBoxes[i].setTextColor(Color.WHITE);
                                } else {
                                    resultBuilder.append(" (").append(df.format(diffPercentage)).append("%)");
                                    textBoxes[i].setTextColor(Color.RED);
                                }
                            }
                        }
                    }
                    else
                        textBoxes[i].setVisibility(View.GONE);
                }
                if(value instanceof Long) {
                    long l = (Long) value;
                    if(l > 0)
                        resultBuilder.append(l);
                    else
                        textBoxes[i].setVisibility(View.GONE);
                }
                textBoxes[i].setText(resultBuilder.toString());

                if ((index == maxIndex && !oldestFirst) || (index == 0 && oldestFirst))
                    textBoxes[i].setTextColor(Color.WHITE);
            }
            catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        }
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
        SimpleDateFormat sdfParser = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
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

