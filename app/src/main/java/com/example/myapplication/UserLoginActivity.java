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

import java.util.HashMap;

public class UserLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;
    private Button practitionerLoginButton;
    private Button forgotPasswordButton;

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

        EditText passwordView = findViewById(R.id.editTextTextPassword2);
        EditText emailView = findViewById(R.id.editTextTextEmailAddress2);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int guardianID = validateLogin(passwordView.getText().toString(), emailView.getText().toString());
                if (guardianID != -1) {
                    Log.d("BUTTON", "Changing to select child page");
                    Intent intent = new Intent(UserLoginActivity.this, ChildSelectActivity.class);
                    intent.putExtra("guardianID", guardianID);
                    intent.putExtra("childID", 0);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserLoginActivity.this, "Please enter a valid email or password", Toast.LENGTH_LONG).show();
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

        forgotPasswordButton = findViewById(R.id.button_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to Reset Password page");
                Intent intent = new Intent(UserLoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("guardianID", 0);
                startActivity(intent);
            }
        });
    }

    private int validateLogin(String pass, String email) {
        SQLConnection conn = new SQLConnection("user1", "");
        String query = "SELECT salt, pepper FROM GuardianAccountDetails INNER JOIN Guardian ON guardianID = ID where email = '" + email + "';";
        HashMap<String, String[]> result = conn.select(query);
        if(result.get("salt").length == 0)
        {
            conn.disconnect();
            return -1;
        }
        String salt = result.get("salt")[0];
        String pepper = result.get("pepper")[0];
        String password = salt + pass + pepper;
        String hash = SHA256.convert(password);
        query = "SELECT ID FROM Guardian INNER JOIN GuardianAccountDetails ON ID = guardianID where email = '" + email + "' AND Hashpassword = '" + hash + "';";
        result = conn.select(query);
        int ID = -1;
        if(result.get("ID").length > 0)
        {
            ID = Integer.parseInt(result.get("ID")[0]);
        }
        conn.disconnect();
        return ID;
    }
}
