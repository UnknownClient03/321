package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        EditText passwordView = findViewById(R.id.editTextTextPassword);
        EditText emailView = findViewById(R.id.editTextTextEmailAddress);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Load saved login data if "Remember Me" was checked
        loadSavedCredentials(emailView, passwordView);

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                int guardianID = validateLogin(password, email);
                if (guardianID != -1) {
                    // Save credentials if "Remember Me" is checked
                    if (rememberMeCheckBox.isChecked()) {
                        editor.putString("emailUser", email);
                        editor.putString("passwordUser", password);
                        editor.putBoolean("rememberMeUser", true);
                        editor.apply();
                    } else {
                        editor.remove("emailUser");
                        editor.remove("passwordUser");
                        editor.remove("rememberMeUser");
                        editor.apply();
                    }
                    Log.d("BUTTON", "Changing to select child page");
                    Intent intent = new Intent(UserLoginActivity.this, ChildSelectActivity.class);
                    intent.putExtra("guardianID", guardianID);
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
                startActivity(intent);
            }
        });

        practitionerLoginButton = findViewById(R.id.practitioner_login_button);
        practitionerLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to practitioner login");
                Intent intent = new Intent(UserLoginActivity.this, PractitionerLoginActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordButton = findViewById(R.id.button_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to Reset Password page");
                Intent intent = new Intent(UserLoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("user", true);
                startActivity(intent);
            }
        });

        CheckBox showPasswordCheckbox = findViewById(R.id.show_password_checkbox);
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordView.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordView.setSelection(passwordView.getText().length());
        });
    }

    private void loadSavedCredentials(EditText emailView, EditText passwordView) {
        boolean rememberMe = sharedPreferences.getBoolean("rememberMeUser", false);
        if (rememberMe) {
            String savedEmail = sharedPreferences.getString("emailUser", "");
            String savedPassword = sharedPreferences.getString("passwordUser", "");
            emailView.setText(savedEmail);
            passwordView.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }

    private int validateLogin(String pass, String email) {
        SQLConnection conn = new SQLConnection("user1", "");
        if(!conn.isConn()) return -1;
        String query = "SELECT salt, pepper FROM GuardianAccountDetails INNER JOIN Guardian ON guardianID = ID where email = ?;";
        HashMap<String, String[]> result = conn.select(query, new String[]{email}, new char[]{'s'});
        if(result.get("salt").length == 0)
        {
            conn.disconnect();
            return -1;
        }
        String salt = result.get("salt")[0];
        String pepper = result.get("pepper")[0];
        String password = salt + pass + pepper;
        String hash = SHA256.convert(password);
        int ID = -1;
        query = "SELECT ID FROM Guardian INNER JOIN GuardianAccountDetails ON ID = guardianID where email = ? AND Hashpassword = ?;";
        result = conn.select(query, new String[]{email, hash}, new char[]{'s', 's'});
        if(result.get("ID").length > 0)
        {
            ID = Integer.parseInt(result.get("ID")[0]);
        }
        conn.disconnect();
        return ID;
    }
}
