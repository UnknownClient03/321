package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.Fragments.ParentQuestionsFragment;
import com.example.myapplication.Fragments.ChecksMenuFragment;

import java.util.HashMap;

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

        /* Don't think we need a welcome text in this section specifically
        // Set welcome text
        TextView textView = findViewById(R.id.textView3);
        SQLConnection conn = new SQLConnection("user1", ""); // Replace with actual password
        if (conn.isConn()) {
            String query = "SELECT fname, lname FROM Guardian WHERE ID = ?;";
            HashMap<String, String[]> result = conn.select(query, new String[]{String.valueOf(manager.guardianID)}, new char[]{'i'});
            conn.disconnect();
            if (result.get("fname") != null && result.get("fname").length > 0) {
                String fname = result.get("fname")[0];
                textView.setText(fname + "!");
            } else {
                textView.setText("...");
            }
        } else {
            textView.setText("...");
        }
         */

        // Load ChecksMenuFragment initially
        if (savedInstanceState == null) {
            loadFragment(new ChecksMenuFragment(), false);
        }

        NavBarManager.setNavBarButtons(EncapsulatingActivity.this);

    }

    /**
     * Method to load fragments into the fragment container
     */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_encapsulating, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    /**
     * Callback from ChecksMenuFragment when a check is selected
     */
    @Override
    public void onCheckSelected(String checkType) {
        // Display the ParentQuestionsFragment first
        ParentQuestionsFragment parentQuestionsFragment = ParentQuestionsFragment.newInstance(manager.childID, checkType);
        loadFragment(parentQuestionsFragment, true); // Assuming loadFragment is a method in your activity to load fragments
    }
}
