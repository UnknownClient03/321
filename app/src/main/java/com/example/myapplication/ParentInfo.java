package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ParentInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_info);

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(ParentInfo.this, new LoginManager(extras.getInt("guardianID"), extras.getInt("childID")));
    }
}
