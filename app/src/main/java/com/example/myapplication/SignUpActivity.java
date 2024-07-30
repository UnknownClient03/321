package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private EditText firstNameInput, lastNameInput, emailInput, phoneNumberInput, passwordInput, confirmPasswordInput;
    private Button uploadButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize all the views
        profilePicture = findViewById(R.id.profile_picture);
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        uploadButton = findViewById(R.id.upload_button);
        signUpButton = findViewById(R.id.signup_button);

        // Handle upload button click
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, you would typically invoke an intent to pick an image from the gallery
                Toast.makeText(SignUpActivity.this, "Upload Button Clicked", Toast.LENGTH_SHORT).show();
                // For demonstration, assume an image is selected and set a drawable as profile picture
                profilePicture.setImageResource(R.drawable.profile_picture); // Use an actual image resource here
            }
        });

        // Handle sign-up button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Register the user
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    // You can then direct the user to another activity or perform other actions
                }
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        NavBarManager.setNavBarButtons(SignUpActivity.this);
    }

    private boolean validateInputs() {
        // Basic validation logic here. You can expand this with more specific checks.
        if (firstNameInput.getText().toString().trim().isEmpty() ||
                lastNameInput.getText().toString().trim().isEmpty() ||
                emailInput.getText().toString().trim().isEmpty() ||
                phoneNumberInput.getText().toString().trim().isEmpty() ||
                passwordInput.getText().toString().trim().isEmpty() ||
                confirmPasswordInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Add more validation as needed (e.g., email format, phone format, password strength)
        return true;
    }
}
