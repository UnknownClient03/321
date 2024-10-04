package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CPRChart extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpr_chart);

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(CPRChart.this, new LoginManager(extras.getInt("guardianID"), extras.getInt("childID")));
    }
}
