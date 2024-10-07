package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Child;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildSelectActivity extends AppCompatActivity {

    private LinearLayout childContainer;
    private Button manageChildrenButton;
    private List<Child> childrenList = new ArrayList<>();
    private int guardianID; // Store the current guardian ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_select);

        // Retrieve the guardian ID from the intent
        guardianID = getIntent().getIntExtra("guardianID", 0);

        // Initialize UI components
        childContainer = findViewById(R.id.child_container);
        manageChildrenButton = findViewById(R.id.buttonManageChildren);

        manageChildrenButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChildSelectActivity.this, ManageChildrenActivity.class);
            intent.putExtra("guardianID", guardianID); // Pass the guardian ID
            startActivity(intent);
        });

        // Make sure some buttons cannot be pressed yet
        Button buttonHome = findViewById(R.id.home_button);
        buttonHome.setEnabled(false);  // Disable the button
        ImageButton buttonSettings = findViewById(R.id.settings_button);
        buttonSettings.setEnabled(false);
        ImageButton buttonRecords = findViewById(R.id.records_button);
        buttonRecords.setEnabled(false);
        ImageButton buttonProgress = findViewById(R.id.progress_button);
        buttonProgress.setEnabled(false);

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(ChildSelectActivity.this, new LoginManager(extras.getInt("guardianID"), 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Updates the UI to display the children
        loadChildrenFromDatabase();
    }

    private void loadChildrenFromDatabase() {
        Log.d("ChildSelectActivity", "Fetching children for guardian ID: " + guardianID);

        SQLConnection sqlConnection = new SQLConnection("user1", "");

        HashMap<String, String[]> result = sqlConnection.select("SELECT ID, fname, lname, DOB, sex FROM Child WHERE guardianID = " + guardianID);
        sqlConnection.disconnect();

        if (result != null && result.get("ID") != null) {
            String[] ids = result.get("ID");
            String[] fnames = result.get("fname");
            String[] lnames = result.get("lname");
            String[] dobs = result.get("DOB");
            String[] sexes = result.get("sex");

            childrenList.clear();
            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                String fname = fnames[i];
                String lname = lnames[i];
                String dob = dobs[i];
                String sex = sexes[i];
                childrenList.add(new Child(id, fname, lname, dob, sex));
                Log.d("ChildSelectActivity", "Added child: ID=" + id + ", Name=" + fname + " " + lname + ", DOB=" + dob + ", Sex=" + sex);
            }
        } else {
            Log.d("ChildSelectActivity", "No children found for this guardian.");
        }

        childContainer.removeAllViews();

        // Iterates through childrenList and creates a new child view for each child using a predefined layout
        for (Child child : childrenList) {
            View childView = LayoutInflater.from(this).inflate(R.layout.child_item_layout, childContainer, false);
            TextView childName = childView.findViewById(R.id.child_name);
            TextView childAge = childView.findViewById(R.id.child_age);
            ImageButton childButton = childView.findViewById(R.id.child_image_button);

            childName.setText(child.getName());
            childAge.setText("Age: " + child.getAge());

            childButton.setOnClickListener(v -> {
                Intent intent = new Intent(ChildSelectActivity.this, Homepage.class);
                intent.putExtra("guardianID", guardianID);
                intent.putExtra("childID", child.getId());
                startActivity(intent);
            });

            childContainer.addView(childView);
            Log.d("ChildSelectActivity", "Child view added: " + child.getName());
        }

        // Add "Add Child" button
        View addChildView = LayoutInflater.from(this).inflate(R.layout.child_item_layout, childContainer, false);
        ImageButton addButton = addChildView.findViewById(R.id.child_image_button);
        addButton.setImageResource(R.drawable.add_person);
        addButton.setBackgroundResource(R.drawable.white_button);
        addButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        TextView addChildText = addChildView.findViewById(R.id.child_name);
        addChildText.setText("Add Child");
        TextView addChildAge = addChildView.findViewById(R.id.child_age);
        addChildAge.setVisibility(View.GONE);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChildSelectActivity.this, AddChildActivity.class);
            intent.putExtra("guardianID", guardianID); // Pass the guardian ID
            startActivity(intent);
        });

        childContainer.addView(addChildView);

        if (childrenList.isEmpty()) {
            Log.d("ChildSelectActivity", "No children available to display.");
        }
    }
}
