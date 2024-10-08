package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.Fragments.ParentQuestionsFragment;
import com.example.myapplication.Fragments.ChecksMenuFragment;
import com.example.myapplication.Fragments.Fragment_ChecksInfo;

public class EncapsulatingActivity extends AppCompatActivity implements ChecksMenuFragment.OnCheckSelectedListener {

    private LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encapsulating); // Ensure your layout includes the fragment container

        // Handle window insets for edge-to-edge display
        View mainView = findViewById(R.id.main_encapsulating);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));
        }

        // Load ChecksMenuFragment initially
        if (savedInstanceState == null) {
            loadFragment(new ChecksMenuFragment(), false);
        }

        NavBarManager.setNavBarButtons(EncapsulatingActivity.this, manager);
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_encapsulating, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    // Method to handle check selection
    @Override
    public void onCheckSelected(String checkType) {
        // Display the InitialInformationFragment first
        Fragment_ChecksInfo initialInfoFragment = Fragment_ChecksInfo.newInstance(manager.childID, checkType);
        loadFragment(initialInfoFragment, true);
    }
}