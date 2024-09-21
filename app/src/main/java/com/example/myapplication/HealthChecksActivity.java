package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.util.Log;
import java.util.HashMap;
import android.widget.Toast;

public class HealthChecksActivity extends AppCompatActivity {

    private SQLConnection dbHelper;
    private int childID;
    int guardianID;
    String dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_checks);

        dbHelper = new SQLConnection("user1", "");  // Establish connection

        // Retrieve guardianID and childID from the intent
        int guardianID = getIntent().getIntExtra("guardianID", -1);  // Default to -1 if not found
        int childID = getIntent().getIntExtra("childID", -1);        // Default to -1 if not found

        if (guardianID == -1 || childID == -1) {
            Log.e("HealthChecksActivity", "Invalid guardianID or childID. Cannot proceed.");
            return;
        }

        // Use guardianID and childID in your logic here
        Log.d("HealthChecksActivity", "Received guardianID: " + guardianID + ", childID: " + childID);

        // Retrieve child DOB
        retrieveChildDetails(guardianID);

    }

    // Method to retrieve all necessary child details from the database
    private void retrieveChildDetails(int guardianID) {
        // Adjust the SQL query to retrieve all necessary details
        String query = "SELECT DOB, sex, fname, lname FROM Child WHERE guardianID = ?";
        String[] params = {String.valueOf(guardianID)};
        char[] paramTypes = {'i'};  // Specify that guardianID is an integer

        Log.d("HealthChecksActivity", "Attempting to retrieve child's details with guardianID: " + params[0]);

        // Execute the select query using SQLConnection
        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);
            if (result != null && result.get("DOB") != null && result.get("sex") != null && result.get("fname") != null && result.get("lname") != null) {
                String dob = result.get("DOB")[0];      // Retrieve DOB
                String sex = result.get("sex")[0];      // Retrieve sex
                String fname = result.get("fname")[0];  // Retrieve first name
                String lname = result.get("lname")[0];  // Retrieve last name
                Log.d("HealthChecksActivity", "Child's details retrieved: DOB = " + dob + ", Sex = " + sex + ", FName = " + fname + ", LName = " + lname);

                // Set up the buttons after retrieving all necessary details
                setupButtons(guardianID, dob, sex, fname, lname);
            } else {
                Log.e("HealthChecksActivity", "No child found for the specified guardianID.");
            }
        } catch (Exception e) {
            Log.e("HealthChecksActivity", "Error retrieving child's details: " + e.getMessage());
        } finally {
            dbHelper.disconnect();  // Close the database connection
        }
    }


    // Method to set up all the buttons for health checks
    private void setupButtons(int guardianID, String dob, String sex, String fname, String lname) {
        // Set up buttons for each health check, passing all necessary details
        setupButton(R.id.check1Button, 1, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check2Button, 2, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check3Button, 3, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check4Button, 4, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check5Button, 5, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check6Button, 6, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check7Button, 7, guardianID, dob, sex, fname, lname);
        setupButton(R.id.check8Button, 8, guardianID, dob, sex, fname, lname);
    }


    private void setupButton(int buttonId, int checkId, int guardianID, String childDOB, String childSex, String fname, String lname) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            if (childID != -1) {
                Log.d("HealthChecksActivity", "Starting HealthCheckFormActivity with childID: " + childID + ", checkId: " + checkId + ", guardianID: " + guardianID + ", DOB: " + childDOB + ", Sex: " + childSex + ", FName: " + fname + ", LName: " + lname);

                // Create an Intent to start HealthCheckFormActivity
                Intent intent = new Intent(HealthChecksActivity.this, HealthCheckFormActivity.class);
                intent.putExtra("check_id", checkId);        // Pass the health check ID (1-8)
                intent.putExtra("childID", childID);         // Pass the childID
                intent.putExtra("guardianID", guardianID);   // Pass the guardianID
                intent.putExtra("childDOB", childDOB);       // Pass the child's DOB
                intent.putExtra("childSex", childSex);       // Pass the child's sex
                intent.putExtra("fname", fname);             // Pass the child's first name
                intent.putExtra("lname", lname);             // Pass the child's last name

                startActivity(intent);
            } else {
                // Handle the case where childID is invalid
                Log.e("HealthChecksActivity", "Invalid childID. Cannot proceed.");
                Toast.makeText(HealthChecksActivity.this, "Invalid ChildID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
