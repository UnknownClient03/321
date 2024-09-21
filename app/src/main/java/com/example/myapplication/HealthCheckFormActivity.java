package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class HealthCheckFormActivity extends AppCompatActivity {

    private EditText weightInput, lengthInput, headCircInput, doctorInput, commentsInput, venueInput, signatureInput;
    private Spinner spinnerChildStatus, spinnerOutcome, checkTypeSpinner;
    private CheckBox checkboxHealthInfoDiscussed;
    private Button submitButton;
    private SQLConnection dbHelper;
    private int childID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_check_form);

        dbHelper = new SQLConnection("user1", "password");

        // Retrieve values from intent
        childID = getIntent().getIntExtra("childID", -1);
        String childDOB = getIntent().getStringExtra("childDOB");
        String childSex = getIntent().getStringExtra("childSex");
        String fname = getIntent().getStringExtra("fname");
        String lname = getIntent().getStringExtra("lname");

        if (childID == -1 || childDOB == null || childSex == null || fname == null || lname == null) {
            Toast.makeText(this, "Invalid child data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize inputs
        weightInput = findViewById(R.id.weightInput);
        lengthInput = findViewById(R.id.lengthInput);
        headCircInput = findViewById(R.id.headCircInput);
        doctorInput = findViewById(R.id.doctorInput);
        commentsInput = findViewById(R.id.commentsInput);
        venueInput = findViewById(R.id.venueInput);
        signatureInput = findViewById(R.id.signatureInput);
        spinnerChildStatus = findViewById(R.id.spinnerChildStatus);
        checkTypeSpinner = findViewById(R.id.checkTypeSpinner);
        spinnerOutcome = findViewById(R.id.spinnerOutcome);
        checkboxHealthInfoDiscussed = findViewById(R.id.checkboxHealthInfoDiscussed);
        submitButton = findViewById(R.id.submitButton);

        // Set the click listener for the submit button
        submitButton.setOnClickListener(v -> submitForm(childDOB, childSex, fname, lname));
    }

    // Method to handle form submission
    private void submitForm(String childDOB, String childSex, String fname, String lname) {
        String weight = weightInput.getText().toString().trim();
        String length = lengthInput.getText().toString().trim();
        String headCirc = headCircInput.getText().toString().trim();
        String doctor = doctorInput.getText().toString().trim();
        String comments = commentsInput.getText().toString().trim();
        String venue = venueInput.getText().toString().trim();
        String signature = signatureInput.getText().toString().trim();
        String childStatus = spinnerChildStatus.getSelectedItem().toString();
        String outcome = spinnerOutcome.getSelectedItem().toString();
        boolean healthInfoDiscussed = checkboxHealthInfoDiscussed.isChecked();
        String checkType = checkTypeSpinner.getSelectedItem().toString().trim();

        // Ensure that required fields are filled in
        if (weight.isEmpty() || length.isEmpty() || headCirc.isEmpty() || doctor.isEmpty() || comments.isEmpty() || venue.isEmpty() || signature.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the form data to the database
        boolean isSuccess = saveToDatabase(
                childID, weight, length, headCirc, doctor, comments, checkType,
                childDOB, childSex, fname, lname, childStatus, outcome,
                healthInfoDiscussed, venue, signature
        );

        // Provide feedback to the user
        if (isSuccess) {
            Toast.makeText(this, "Form Submitted Successfully", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(this, "Failed to submit the form", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle saving data to the database using SQLConnection
    private boolean saveToDatabase(
            int childID, String weight, String length, String headCirc, String doctor,
            String comments, String checkType, String childDOB, String childSex,
            String fname, String lname, String childStatus, String outcome,
            boolean healthInfoDiscussed, String venue, String signature
    ) {
        String insertQuery =
                "INSERT INTO ChildCheck (childID, checkType, DOB, age, sex, fname, lname, childStatus, outcome, " +
                        "weight, length, headCirc, nameOfDoctor, comments, healthInfoDiscussed, venue, signature, date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        String[] params = {
                String.valueOf(childID), checkType, childDOB, String.valueOf(calculateAge(childDOB)),
                childSex, fname, lname, childStatus, outcome, weight, length, headCirc, doctor, comments,
                String.valueOf(healthInfoDiscussed ? 1 : 0), venue, signature
        };
        char[] paramTypes = {'i', 's', 's', 'i', 's', 's', 's', 's', 's', 'i', 'i', 'i', 's', 's', 'i', 's', 's'};

        try {
            return dbHelper.update(insertQuery, params, paramTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dbHelper.disconnect();
        }
    }

    // Method to calculate age from DOB
    private int calculateAge(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dob, formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    // Method to clear the form inputs after submission
    private void clearForm() {
        weightInput.setText("");
        lengthInput.setText("");
        headCircInput.setText("");
        doctorInput.setText("");
        commentsInput.setText("");
        venueInput.setText("");
        signatureInput.setText("");
        spinnerChildStatus.setSelection(0);
        spinnerOutcome.setSelection(0);
        checkboxHealthInfoDiscussed.setChecked(false);
    }
}
