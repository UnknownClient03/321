package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountSettings extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private CheckBox checkBoxPhotoSharing;
    private CheckBox checkBoxCalendar;
    private CheckBox checkBoxDarkMode;
    private Button saveButton;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        editTextUsername = findViewById(R.id.editTextTextUsername);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        checkBoxPhotoSharing = findViewById(R.id.checkBox_photo);
        checkBoxCalendar = findViewById(R.id.checkBox_calender);
        checkBoxDarkMode = findViewById(R.id.checkBox_dark);
        saveButton = findViewById(R.id.button_save);
        editButton = findViewById(R.id.button_edit_info);

        setEditable(false);

        editButton.setOnClickListener(v -> {
            setEditable(true);
            saveButton.setVisibility(View.VISIBLE);
        });

        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveSettings();
                setEditable(false);
                saveButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(AccountSettings.this, "Please fill all fields correctly", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveSettings() {
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        boolean isPhotoSharingEnabled = checkBoxPhotoSharing.isChecked();
        boolean isCalendarEnabled = checkBoxCalendar.isChecked();
        boolean isDarkModeEnabled = checkBoxDarkMode.isChecked();

        Toast.makeText(this, "Settings Saved", Toast.LENGTH_LONG).show();

        // Add logic to save these settings
    }

    private void setEditable(boolean isEditable) {
        editTextUsername.setEnabled(isEditable);
        editTextEmail.setEnabled(isEditable);
        editTextPassword.setEnabled(isEditable);

    }

    private boolean validateInputs() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        return !username.isEmpty() && !email.isEmpty() && !password.isEmpty();
    }
}
