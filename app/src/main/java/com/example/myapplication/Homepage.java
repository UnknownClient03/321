package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavBarManager.setNavBarButtons(Homepage.this);

        // Setting up the Immunisation button
        Button buttonImmunisation = findViewById(R.id.button_immunisation);
        buttonImmunisation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Immunisation Schedule page");
                Intent intent = new Intent(Homepage.this, ImmunisationSchedule.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", extras.getInt("guardianID"));
                    intent.putExtra("childID", extras.getInt("childID"));
                }
                startActivity(intent);
            }
        });

        // Setting up the CPR Chart button
        Button buttonCPRChart = findViewById(R.id.button_cpr_chart);
        buttonCPRChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON", "changing to CPR Chart");
                Intent intent = new Intent(Homepage.this, CPRChart.class);
                startActivity(intent);
            }
        });

        // Setting up the My Information and Family History button
        Button buttonMyInfoAndFamHis = findViewById(R.id.button_my_information_and_family_history);
        buttonMyInfoAndFamHis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to My Information and Family History");
                Intent intent = new Intent(Homepage.this, MyInfoAndFamHis.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", extras.getInt("guardianID"));
                    intent.putExtra("childID", extras.getInt("childID"));
                }
                startActivity(intent);
            }
        });

        // Setting up the Birth Details and Newborn Examination button
        Button buttonBirthDetailsAndNewbornExamination = findViewById(R.id.button_birth_details_and_newborn_examination);
        buttonBirthDetailsAndNewbornExamination.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Birth Details and Newborn Examination");
                Intent intent = new Intent(Homepage.this, BirthDetailsAndNewbornExamination.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", extras.getInt("guardianID"));
                    intent.putExtra("childID", extras.getInt("childID"));
                }
                startActivity(intent);
            }
        });

        // Setting up the Info for Parents button
        Button buttonInfoForParents = findViewById(R.id.button_info_for_parents);
        buttonInfoForParents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Parent Info");
                Intent intent = new Intent(Homepage.this, ParentInfo.class);
                startActivity(intent);
            }
        });

        // Setting up the Useful Contacts button
        Button buttonUsefulContacts = findViewById(R.id.button_useful_contacts_and_websites);
        buttonUsefulContacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Useful Contacts");
                Intent intent = new Intent(Homepage.this, UsefulContacts.class);
                startActivity(intent);
            }
        });
    }
}
