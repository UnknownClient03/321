// ResetPasswordActivity.java
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private TextView resetPasswordTextView;
    private EditText emailEditText;
    private Button getConfirmationEmailButton;
    private Button signUpButton;
    private Button practitionerLoginButton;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize views
        logoImageView = findViewById(R.id.imageView2);
        resetPasswordTextView = findViewById(R.id.textView2);
        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        getConfirmationEmailButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_button);
        practitionerLoginButton = findViewById(R.id.practitioner_login_button);
        backArrow = findViewById(R.id.back_arrow);

        // Set click listener for Get Confirmation Email button
        getConfirmationEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                } else {
                    // Here you would normally call a method to send the reset email
                    // For demonstration purposes, we just show a toast
                    Toast.makeText(ResetPasswordActivity.this, "Confirmation email sent to " + email, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ResetPasswordActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Set click listener for Sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to sign up");
                Intent intent=new Intent(ResetPasswordActivity.this, SignUpActivity.class);
                intent.putExtra("guardianID", 0);
                intent.putExtra("childID", 0);
                startActivity(intent);
            }
        });

        // Set click listener for Practitioner Login button
        practitionerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to practitioner login");
                Intent intent = new Intent(ResetPasswordActivity.this, PractitionerLoginActivity.class);
                intent.putExtra("guardianID", 0);
                intent.putExtra("childID", 0);
                startActivity(intent);
            }
        });

        backArrow.setOnClickListener(v -> finish());
    }
}
