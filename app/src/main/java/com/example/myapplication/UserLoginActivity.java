package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserLoginActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private Button loginButton;
    private Button signupButton;
    private Button practitionerLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextPassword = findViewById(R.id.editTextTextPassword2);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validatePassword()) {
                    Log.d("BUTTON", "Changing to select child page");
                    Intent intent = new Intent(UserLoginActivity.this, ChildSelectActivity.class);
                    intent.putExtra("guardianID", 0);
                    intent.putExtra("childID", 0);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserLoginActivity.this, "Please enter a valid password", Toast.LENGTH_LONG).show();
                }
            }
        });

        signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to sign up");
                Intent intent=new Intent(UserLoginActivity.this, SignUpActivity.class);
                intent.putExtra("guardianID", 0);
                intent.putExtra("childID", 0);
                startActivity(intent);
            }
        });

        practitionerLoginButton = findViewById(R.id.practitioner_login_button);
        practitionerLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to practitioner login");
                Intent intent = new Intent(UserLoginActivity.this, PractitionerLoginActivity.class);
                intent.putExtra("guardianID", 0);
                intent.putExtra("childID", 0);
                startActivity(intent);
            }
        });
    }

    private boolean validatePassword() {
        String password = editTextPassword.getText().toString().trim();

        if (password.isEmpty() || password.length() <= 5) {
            return false;
        }

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=<>?{}\\[\\]~-].*");

        return hasUppercase && hasSpecial;
    }
}
