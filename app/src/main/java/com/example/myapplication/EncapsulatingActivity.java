package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.myapplication.Fragments.Fragment_HealthAssessment;
import com.example.myapplication.Fragments.Fragment_HealthProtectiveFactors;
import com.example.myapplication.Fragments.SignatureFragment;

public class EncapsulatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encapsulating);

        // Load initial fragment or handle fragment transactions
        loadFragment(new Fragment_HealthAssessment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
