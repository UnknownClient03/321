package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragments.Fragment_BirthDetails;
import com.example.myapplication.Fragments.Fragment_NewbornExamination;

public class BirthDetailsAndNewbornExamination extends AppCompatActivity {

    private LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birth_details_and_newborn_examination);

        // Initialize LoginManager
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            manager = new LoginManager(extras.getInt("guardianID", -1), extras.getInt("childID", -1));
        } else {
            Log.e("BirthDetailsActivity", "No extras provided in intent");
            return;
        }

        // Log childID and guardianID to ensure they are passed correctly
        if (manager.childID != -1 && manager.guardianID != -1) {
            Log.d("BirthDetailsActivity", "Received childID: " + manager.childID);
            Log.d("BirthDetailsActivity", "Received guardianID: " + manager.guardianID);
        } else {
            Log.e("BirthDetailsActivity", "Invalid childID or guardianID received");
            return;
        }

        Button buttonBirthDetails = findViewById(R.id.button_birth_details);
        Button buttonNewbornExamination = findViewById(R.id.button_newborn_examination);

        buttonBirthDetails.setOnClickListener(v -> {
            Fragment_BirthDetails fragment = new Fragment_BirthDetails();
            Bundle bundle = new Bundle();
            bundle.putInt("childID", manager.childID);
            bundle.putInt("guardianID", manager.guardianID);
            fragment.setArguments(bundle);

            loadFragment(fragment);
        });

        buttonNewbornExamination.setOnClickListener(v -> {
            Fragment_NewbornExamination fragment = new Fragment_NewbornExamination();
            Bundle bundle = new Bundle();
            bundle.putInt("childID", manager.childID);
            bundle.putInt("guardianID", manager.guardianID);
            fragment.setArguments(bundle);

            loadFragment(fragment);
        });

        // Load default fragment (Birth Details) and pass arguments
        if (savedInstanceState == null) {
            Fragment_BirthDetails fragment = new Fragment_BirthDetails();
            Bundle bundle = new Bundle();
            bundle.putInt("childID", manager.childID);
            bundle.putInt("guardianID", manager.guardianID);
            fragment.setArguments(bundle);

            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        // Replace the fragment in the fragment_container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public LoginManager getManager() {
        return manager;
    }
}
