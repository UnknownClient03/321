package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragments.Fragment_BirthDetails;
import com.example.myapplication.Fragments.Fragment_NewbornExamination;

public class BirthDetailsAndNewbornExamination extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birth_details_and_newborn_examination);

        Button buttonBirthDetails = findViewById(R.id.button_birth_details);
        Button buttonNewbornExamination = findViewById(R.id.button_newborn_examination);

        buttonBirthDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Fragment_BirthDetails());
            }
        });

        buttonNewbornExamination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Fragment_NewbornExamination());
            }
        });

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new Fragment_BirthDetails());
        }

        NavBarManager.setNavBarButtons(BirthDetailsAndNewbornExamination.this);
    }

    private void loadFragment(Fragment fragment) {
        // Replace the fragment in the fragment_container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
