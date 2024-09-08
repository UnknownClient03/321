package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class AccountSettings extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private CheckBox checkBoxPhotoSharing;
    private CheckBox checkBoxCalendar;
    private CheckBox checkBoxDarkMode;
    private Button saveButton;
    private Button changePasswordButton;
    private Button cancelButton;
    private int guardianID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextTextPassword);
        checkBoxPhotoSharing = findViewById(R.id.checkBox_photo);
        checkBoxCalendar = findViewById(R.id.checkBox_calender);
        checkBoxDarkMode = findViewById(R.id.checkBox_dark);
        saveButton = findViewById(R.id.button_save);
        changePasswordButton = findViewById(R.id.button_change_password);
        cancelButton = findViewById(R.id.button_cancel);

        // Fetch guardian ID from the intent
        guardianID = getIntent().getIntExtra("guardianID", 0);

        loadGuardianData();

        // Initially hide password fields and cancel button
        editTextCurrentPassword.setVisibility(View.GONE);
        editTextNewPassword.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        // Show password fields only when 'Change Password' is clicked
        changePasswordButton.setOnClickListener(v -> {
            changePasswordButton.setVisibility(View.GONE);
            editTextCurrentPassword.setVisibility(View.VISIBLE);
            editTextNewPassword.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        });

        cancelButton.setOnClickListener(v -> {
            changePasswordButton.setVisibility(View.VISIBLE);
            editTextCurrentPassword.setVisibility(View.GONE);
            editTextNewPassword.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        });

        // Save settings when the save button is clicked
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveSettings();
                resetViews();
            } else {
                Toast.makeText(AccountSettings.this, "Please fill all fields correctly", Toast.LENGTH_LONG).show();
            }
        });

        NavBarManager.setNavBarButtons(AccountSettings.this);
    }

    // Load guardian data from the database
    private void loadGuardianData() {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        // Fetch email from the Guardian table
        HashMap<String, String[]> guardianResult = sqlConnection.select("SELECT email FROM Guardian WHERE ID = " + guardianID);

        if (guardianResult != null && guardianResult.get("email") != null) {
            String email = guardianResult.get("email")[0];
            editTextEmail.setText(email);
        } else {
            Toast.makeText(this, "Failed to load email.", Toast.LENGTH_SHORT).show();
        }

        sqlConnection.disconnect();
    }

    // Save the settings to the database
    private void saveSettings() {
        String email = editTextEmail.getText().toString();
        String currentPassword = editTextCurrentPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();

        SQLConnection sqlConnection = new SQLConnection("user1", "");

        // Update the Guardian email
        String updateGuardianQuery = "UPDATE Guardian SET email = ? WHERE ID = ?";
        String[] emailParams = {email, String.valueOf(guardianID)};
        char[] emailParamTypes = {'s', 'i'};
        sqlConnection.update(updateGuardianQuery, emailParams, emailParamTypes);

        // If password fields are visible and new password is entered, update the password
        if (editTextCurrentPassword.getVisibility() == View.VISIBLE) {
            if (!checkCurrentPassword(currentPassword)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.isEmpty()) {
                // Generate a new salt and pepper for the new password
                String newSalt = SHA256.randomUTF8(16);
                String newPepper = SHA256.randomUTF8(16);
                String passwordWithSaltAndPepper = newSalt + newPassword + newPepper;
                String hashedNewPassword = SHA256.convert(passwordWithSaltAndPepper);

                String updatePasswordQuery = "UPDATE GuardianAccountDetails SET Hashpassword = ?, salt = ?, pepper = ? WHERE guardianID = ?";
                String[] passwordParams = {hashedNewPassword, newSalt, newPepper, String.valueOf(guardianID)};
                char[] passwordParamTypes = {'s', 's', 's', 'i'};
                sqlConnection.update(updatePasswordQuery, passwordParams, passwordParamTypes);
            }
        }

        sqlConnection.disconnect();

        Toast.makeText(this, "Settings Saved", Toast.LENGTH_LONG).show();
    }

    // Reset views to the initial state
    private void resetViews() {
        editTextCurrentPassword.setVisibility(View.GONE);
        editTextNewPassword.setVisibility(View.GONE);
        changePasswordButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.GONE);
    }

    private boolean validateInputs() {
        String email = editTextEmail.getText().toString().trim();
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if password fields are visible
        if (editTextCurrentPassword.getVisibility() == View.VISIBLE) {
            if (currentPassword.isEmpty()) {
                Toast.makeText(this, "Current password cannot be empty", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (newPassword.length() <= 5) {
                Toast.makeText(this, "Password must be longer than 5 characters", Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean hasUppercase = !newPassword.equals(newPassword.toLowerCase());
            boolean hasSpecial = newPassword.matches(".*[!@#$%^&*()_+=<>?{}\\[\\]~-].*");

            if (!hasUppercase || !hasSpecial) {
                Toast.makeText(this, "Password must contain uppercase letters and special characters", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private boolean checkCurrentPassword(String currentPassword) {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        HashMap<String, String[]> result = sqlConnection.select("SELECT Hashpassword, salt, pepper FROM GuardianAccountDetails WHERE guardianID = " + guardianID);
        if (result != null && result.get("Hashpassword") != null && result.get("salt") != null && result.get("pepper") != null) {
            String storedHash = result.get("Hashpassword")[0];
            String salt = result.get("salt")[0];
            String pepper = result.get("pepper")[0];

            String hashedCurrentPassword = SHA256.convert(salt + currentPassword + pepper);

            sqlConnection.disconnect();
            return storedHash.equals(hashedCurrentPassword);
        } else {
            sqlConnection.disconnect();
            return false;
        }
    }
}
