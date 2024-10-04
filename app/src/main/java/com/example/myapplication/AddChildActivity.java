package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddChildActivity extends AppCompatActivity {

    private EditText editTextGivenNames;
    private EditText editTextSurname;
    private EditText editTextDOBDay;
    private EditText editTextDOBMonth;
    private EditText editTextDOBYear;
    private Spinner spinnerSex;
    private Button saveChildButton;
    private ImageView backArrow;

    private boolean isButtonClicked = false; // Flag to prevent multiple executions
    private int guardianID; // Store the current guardian ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // Retrieve the guardian ID from the intent
        guardianID = getIntent().getIntExtra("guardianID", 0);

        // Initialize the views
        initializeViews();

        // Configure the gender spinner
        configureGenderSpinner();

        backArrow.setOnClickListener(v -> finish());

        // Set up click listener for save button
        saveChildButton.setOnClickListener(v -> {
            if (isButtonClicked) return; // Prevent further clicks if already clicked
            isButtonClicked = true; // Set the flag to true after the first click

            if (validateInputs()) {
                processChildData();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                isButtonClicked = false; // Reset the flag to allow retry
            }
        });

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(AddChildActivity.this, new LoginManager(guardianID, 0));
    }

    private void initializeViews() {
        editTextGivenNames = findViewById(R.id.fname_input);
        editTextSurname = findViewById(R.id.lname_input);
        editTextDOBDay = findViewById(R.id.dob_day_input);
        editTextDOBMonth = findViewById(R.id.dob_month_input);
        editTextDOBYear = findViewById(R.id.dob_year_input);
        spinnerSex = findViewById(R.id.sex_input);
        saveChildButton = findViewById(R.id.save_child_button);
        backArrow = findViewById(R.id.back_arrow);
    }

    private void configureGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Male", "Female"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapter);
    }

    private void processChildData() {
        String givenNames = editTextGivenNames.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String dobDay = editTextDOBDay.getText().toString().trim();
        String dobMonth = editTextDOBMonth.getText().toString().trim();
        String dobYear = editTextDOBYear.getText().toString().trim();
        String sex = spinnerSex.getSelectedItem().toString().substring(0, 1).toUpperCase();
        String dob = dobYear + "-" + dobMonth + "-" + dobDay;

        if (!isValidDate(dob)) {
            Toast.makeText(this, "Please enter a valid date of birth.", Toast.LENGTH_SHORT).show();
            isButtonClicked = false; // Reset the flag
            return;
        }

        // Insert the child into the database with the current guardian ID
        insertChildToDatabase(givenNames, surname, dob, sex, guardianID);

        Toast.makeText(AddChildActivity.this, "You have added a new child", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInputs() {
        String givenNames = editTextGivenNames.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String dobDay = editTextDOBDay.getText().toString().trim();
        String dobMonth = editTextDOBMonth.getText().toString().trim();
        String dobYear = editTextDOBYear.getText().toString().trim();
        String sex = spinnerSex.getSelectedItem().toString().trim();

        return !givenNames.isEmpty() && !surname.isEmpty() && !dobDay.isEmpty() &&
                !dobMonth.isEmpty() && !dobYear.isEmpty() && !sex.equals("Gender");
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void insertChildToDatabase(String givenNames, String surname, String dob, String sex, int guardianID) {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        // Get the next available ID
        int newChildId = sqlConnection.getMaxID("Child");

        // Formulate the INSERT query with the new ID and guardian ID
        String insertQuery = "INSERT INTO Child (ID, guardianID, fname, lname, DOB, sex) VALUES ("
                + newChildId + ", " + guardianID + ", '"
                + givenNames + "', '" + surname + "', '" + dob + "', '" + sex + "')";

        sqlConnection.update(insertQuery);
        sqlConnection.disconnect();
    }
}
