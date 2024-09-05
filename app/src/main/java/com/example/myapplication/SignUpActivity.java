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
    private ImageView backArrow;
    private EditText editTextPassword;

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
        backArrow = findViewById(R.id.back_arrow);

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
                if (validateInputs() && registeruser()) {
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        backArrow.setOnClickListener(v -> finish());

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
        editTextPassword = findViewById(R.id.password_input);
        String password = editTextPassword.getText().toString().trim();

        if (password.isEmpty() || password.length() <= 5) {
            Toast.makeText(this, "Password must be greater than 5 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.equals(password.toLowerCase())) {
            Toast.makeText(this, "Password must have an uppercase letter.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+=<>?{}\\[\\]~-].*")) {
            Toast.makeText(this, "Password must have a special character.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
//test
    }

    private boolean registeruser()
    {
        SQLConnection conn = new SQLConnection("user1", "");
        if(!conn.isConn()) return false;
        int ID = conn.getMaxID("Guardian");
        String query = "INSERT INTO Guardian VALUES (?, ?, ?, ?, ?, ?)";
        String[] params = { String.valueOf(ID), firstNameInput.getText().toString(), lastNameInput.getText().toString(), phoneNumberInput.getText().toString(), phoneNumberInput.getText().toString(), emailInput.getText().toString()};
        char[] paramTypes = {'i', 's', 's', 'i', 'i', 's' };
        if(!conn.update(query, params, paramTypes))
        {
            Toast.makeText(this, "email Address is already a user.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String salt = SHA256.randomUTF8(16);
        String pepper = SHA256.randomUTF8(16);
        String password = salt + passwordInput.getText() + pepper;
        String hash = SHA256.convert(password);
        query = "INSERT INTO GuardianAccountDetails VALUES (?, ?, ?, ?)";
        String[] params2 = { String.valueOf(ID), hash, salt, pepper };
        char[] paramTypes2 = {'i', 's', 's', 's'};
        conn.update(query, params2, paramTypes2);
        conn.disconnect();
        return true;
    }

}
