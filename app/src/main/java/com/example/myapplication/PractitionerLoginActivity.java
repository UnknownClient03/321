package com.example.myapplication;

import android.content.Intent;
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

public class PractitionerLoginActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private Button loginButton;
    private Button signupButton;
    private Button userLoginButton;
    private Button forgotPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.practitioner_login);
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
                int pracID = validateLogin(passwordView.getText().toString(), emailView.getText().toString());
                if (pracID != -1) {
                    Log.d("BUTTON", "Changing to select child page");
                    Intent intent = new Intent(PractitionerLoginActivity.this, ChildSelectActivity.class);
                    intent.putExtra("pracID", pracID);
                    startActivity(intent);
                } else {
                    Toast.makeText(PractitionerLoginActivity.this, "Please enter a valid password", Toast.LENGTH_LONG).show();
                }
            }
        });

        userLoginButton = findViewById(R.id.user_login_button);
        userLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to user login");
                Intent intent = new Intent(PractitionerLoginActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordButton = findViewById(R.id.button_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to Reset Password page");
                Intent intent = new Intent(PractitionerLoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("practitioner", true);
                startActivity(intent);
            }
        });

        CheckBox showPasswordCheckbox = findViewById(R.id.show_password_checkbox);

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                // Hide password
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move cursor to the end of the text
            editTextPassword.setSelection(editTextPassword.getText().length());
        });
    }

    private int validateLogin(String pass, String email)
    {
        SQLConnection conn = new SQLConnection("user1", "");
        if(!conn.isConn()) return -1;
        String query = "SELECT salt, pepper FROM PractitionerAccountDetails INNER JOIN Practitioner ON practitionerID = ID where email = ?;";
        HashMap<String, String[]> result = conn.select(query, new String[]{email}, new char[]{'s'});
        if(result.get("salt").length == 0)
        {
            conn.disconnect();
            return -1;
        }
        String salt = result.get("salt")[0];
        String pepper = result.get("pepper")[0];
        String password = salt + pass + pepper;
        Log.d("MSG", password);
        String hash = SHA256.convert(password);
        Log.d("MSG", hash);
        int ID = -1;
        query = "SELECT ID FROM Practitioner INNER JOIN PractitionerAccountDetails ON ID = practitionerID where email = ? AND Hashpassword = ?;";
        result = conn.select(query, new String[]{email, hash}, new char[]{'s', 's'});
        if(result.get("ID").length > 0)
        {
            ID = Integer.parseInt(result.get("ID")[0]);
        }
        conn.disconnect();
        return ID;
    }
}
