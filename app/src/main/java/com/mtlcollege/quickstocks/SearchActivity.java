package com.mtlcollege.quickstocks;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mtlcollege.quickstocks.model.StockInfo;
import com.mtlcollege.quickstocks.util.HTTPConnection;
import com.mtlcollege.quickstocks.util.JSONParser;
import com.mtlcollege.quickstocks.util.WebLookup;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final int CONNECTION_PERMISSION_REQUEST = 1;
    Button btnReturnSearch;
    ArrayList<StockInfo> si = new ArrayList<>();
    int rbChecked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button btnSearchThis = findViewById(R.id.btnSearchThis);
        btnReturnSearch = findViewById(R.id.btnReturnSearch);
        final RadioGroup rg = new RadioGroup(SearchActivity.this);

        btnSearchThis.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(SearchActivity.this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SearchActivity.this, Manifest.permission.INTERNET)) {
                    ActivityCompat.requestPermissions(SearchActivity.this,
                            new String[]{Manifest.permission.INTERNET}, CONNECTION_PERMISSION_REQUEST);
                }
                else {
                    ActivityCompat.requestPermissions(SearchActivity.this,
                            new String[]{Manifest.permission.INTERNET}, CONNECTION_PERMISSION_REQUEST);
                }
            }

            //Hide the soft keyboard after a search
            View kbView = this.getCurrentFocus();
            if(kbView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(kbView.getWindowToken(), 0);
            }

            btnReturnSearch.setEnabled(false);
            rg.removeAllViews();

            rbChecked = -1;
            String result = searchSymbols();
            if(!result.isEmpty()) {
                int resultCount = JSONParser.getResultCount(result);
                if (resultCount == 0)
                    Toast.makeText(getApplicationContext(), "Your lookup returned no results.", Toast.LENGTH_SHORT).show();
                else {
                    if (resultCount > 100)
                        Toast.makeText(getApplicationContext(), "Your search returned over 100 results. Please refine your search to see all the search results.", Toast.LENGTH_SHORT).show();
                    si = JSONParser.getSearchResults(result);
                    LinearLayout layoutSearch = findViewById(R.id.layoutSearch);

                    rg.setOrientation(RadioGroup.VERTICAL);
                    RadioGroup.LayoutParams rgParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    rgParams.setMargins(5, 5, 5, 5);
                    rg.setLayoutParams(rgParams);

                    int rbId = 0;
                    int lastResult = resultCount;
                    for(StockInfo s : si) {
                        RadioButton rb = new RadioButton(SearchActivity.this);

                        StringBuilder thisResult = new StringBuilder();
                        thisResult.append("Symbol: ")
                                .append(s.getSymbol())
                                .append("\nCompany: ")
                                .append(s.getSecurityName())
                                .append("\nSecurity Type: ")
                                .append(s.getSecurityType())
                                .append("\nCurrency: ")
                                .append(s.getCurrencyType());
                        rb.setText(thisResult.toString());
                        rb.setId(rbId++);
                        rg.addView(rb);

                        if(rbId != lastResult) {
                            LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
                            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.search_divider, null, false);
                            rg.addView(layout);
                        }
                    }
                    layoutSearch.removeView(rg);
                    layoutSearch.addView(rg);
                }
            }
        });

        rg.setOnCheckedChangeListener((group, checkedId) -> {
              RadioButton rb = findViewById(checkedId);
              rbChecked = rb.getId();
              btnReturnSearch.setEnabled(true);
        });

        btnReturnSearch.setOnClickListener(v -> {
            String symbol = si.get(rbChecked).getSymbol();
            Intent intent = new Intent();
            intent.putExtra("symbol", symbol);
            setResult(1, intent);
            finish();
        });
    }

    private String searchSymbols() {
        EditText inpSearch = findViewById(R.id.inpSearch);
        String search = inpSearch.getText().toString().toUpperCase();
        search = search.replace(" ", "%20");

        if(search.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a company name.", Toast.LENGTH_SHORT).show();
            return "";
        }

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://api.intrinio.com/securities?query=")
                .append(search)
                .append("&exch_symbol=XTSE&api_key=OjgyYzhjNGZhYjJjZGVmZTJiOTAyYTRiZGZiNzI3MDI0")
                .append("&page_number=1&page_size=100");

        return WebLookup.lookup(urlBuilder.toString(), getApplicationContext());
    }
}
