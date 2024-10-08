package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PractitionerChildSelect extends AppCompatActivity {

    private LoginManager manager;

    private int practitionerID;
    LinearLayout childContainer;
    private List<Child> childrenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practitioner_child_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            manager = new LoginManager(extras.getInt("guardianID", -1), extras.getInt("childID", -1));
        } else {
            Log.e("PractitionerChildSelect", "No extras provided in intent");
            return;
        }

        practitionerID = extras.getInt("practitionerID");

        childContainer = findViewById(R.id.child_container);

        // Make sure some buttons cannot be pressed yet
        Button buttonHome = findViewById(R.id.home_button);
        buttonHome.setEnabled(false);  // Disable the button
        ImageButton buttonRecords = findViewById(R.id.records_button);
        buttonRecords.setEnabled(false);
        ImageButton buttonProgress = findViewById(R.id.appointments_button);
        buttonProgress.setEnabled(false);
        ImageButton buttonSettings = findViewById(R.id.settings_button);
        buttonSettings.setEnabled(false);
        NavBarManager.setNavBarButtons(PractitionerChildSelect.this, manager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Updates the UI to display the children
        loadChildrenFromDatabase();
    }

    private void loadChildrenFromDatabase() {
        Log.d("ChildSelectActivity", "Fetching children for practitioner ID: " + practitionerID);

        SQLConnection sqlConnection = new SQLConnection("user1", "");
        String query = "SELECT guardianID, ID, fname, lname, DOB, sex, profilePicture " +
                           "FROM Child WHERE EXISTS (" +
                           "SELECT * FROM Guardian " +
                               "LEFT JOIN PractitionerGuardianID ON guardianID = Guardian.ID " +
                               "WHERE Child.guardianID = Guardian.ID AND practitionerID = " + practitionerID +
                ")";
        HashMap<String, String[]> result = sqlConnection.select(query);

        sqlConnection.disconnect();

        if (result != null && result.get("ID") != null) {
            String[] ids = result.get("ID");
            String[] guardianIDs = result.get("guardianID");
            String[] fnames = result.get("fname");
            String[] lnames = result.get("lname");
            String[] dobs = result.get("DOB");
            String[] sexes = result.get("sex");
            String[] profilePictures = result.get("profilePicture");

            childrenList.clear();
            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                int guardianID = Integer.parseInt(guardianIDs[i]);
                String fname = fnames[i];
                String lname = lnames[i];
                String dob = dobs[i];
                String sex = sexes[i];
                String profilePicture = profilePictures[i];
                childrenList.add(new Child(id, guardianID, fname, lname, dob, sex, profilePicture));
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
            CircleImageView childButton = childView.findViewById(R.id.child_image_button);

            byte[] decodedString = Base64.decode(child.getProfilePicture(), Base64.DEFAULT);
            childButton.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            childName.setText(child.getName());
            childAge.setText("Age: " + child.getAge());

            childButton.setOnClickListener(v -> {
                Intent intent = new Intent(PractitionerChildSelect.this, Homepage.class);
                intent.putExtra("guardianID", child.getGuardianId()); //this can't be helped
                intent.putExtra("childID", child.getId());
                startActivity(intent);
            });

            childContainer.addView(childView);
            Log.d("ChildSelectActivity", "Child view added: " + child.getName());
        }

        if (childrenList.isEmpty()) {
            Log.d("ChildSelectActivity", "No children available to display.");
        }
    }
}