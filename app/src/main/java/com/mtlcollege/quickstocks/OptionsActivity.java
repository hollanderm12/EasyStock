package com.mtlcollege.quickstocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.mtlcollege.quickstocks.util.Keys;


public class OptionsActivity extends AppCompatActivity {

    Button btnCheckAll, btnClearAll;
    LinearLayout optionsLayout;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        btnCheckAll = findViewById(R.id.btnSelectAll);
        btnClearAll = findViewById(R.id.btnClearAll);
        optionsLayout = findViewById(R.id.optionsLayout);

        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = optionsLayout.getChildCount();
                for(int i = 0 ; i < count ; i++) {
                    v = optionsLayout.getChildAt(i);
                    if(v instanceof CheckBox)
                        ((CheckBox) v).setChecked(true);
                }
            }
        });

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = optionsLayout.getChildCount();
                for(int i = 0 ; i < count ; i++) {
                    v = optionsLayout.getChildAt(i);
                    if(v instanceof CheckBox)
                        ((CheckBox) v).setChecked(false);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        Keys keys = new Keys();

        sp = getSharedPreferences(keys.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();

        if(((CheckBox)findViewById(R.id.chkLow)).isChecked())
            editor.putBoolean(keys.Q_LOW, true);
        if(((CheckBox)findViewById(R.id.chkHigh)).isChecked())
            editor.putBoolean(keys.Q_HIGH, true);
        if(((CheckBox)findViewById(R.id.chkLatestPrice)).isChecked())
            editor.putBoolean(keys.Q_LATEST_PRICE, true);

        editor.apply();
    }
}
