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

import java.util.HashMap;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;
import javax.mail.Transport;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private TextView resetPasswordTextView;
    private EditText emailEditText;
    private Button getConfirmationEmailButton;
    private Button signUpButton;
    private Button practitionerLoginButton;
    private ImageView backArrow;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        extras = getIntent().getExtras();

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
                int ID = validateEmail(email);
                if (ID == -1) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter an email address associated with an account", Toast.LENGTH_SHORT).show();
                } else {
                    // Here you would normally call a method to send the reset email
                    // For demonstration purposes, we just show a toast

                    String pass = SHA256.randomUTF8(16);
                    String salt = SHA256.randomUTF8(16);
                    String pepper = SHA256.randomUTF8(16);
                    String password = salt + pass + pepper;
                    String hash = SHA256.convert(password);

                    SQLConnection conn = new SQLConnection("user1", "");
                    String query = (extras.getBoolean("user")) ? "UPDATE GuardianAccountDetails SET Hashpassword = ?, salt = ?, pepper = ? WHERE guardianID = ?" :
                                                                      "UPDATE PractitionerAccountDetails SET Hashpassword = ?, salt = ?, pepper = ? WHERE practitionerID = ?";
                    String[] params2 = { hash, salt, pepper, String.valueOf(ID) };
                    char[] paramTypes2 = {'s', 's', 's', 'i'};
                    conn.update(query, params2, paramTypes2);
                    conn.disconnect();
                    sendEmail(pass, email);


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
                startActivity(intent);
            }
        });

        // Set click listener for Practitioner Login button
        practitionerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON", "Changing to practitioner login");
                Intent intent = new Intent(ResetPasswordActivity.this, PractitionerLoginActivity.class);
                startActivity(intent);
            }
        });

        backArrow.setOnClickListener(v -> finish());
    }

    private int validateEmail(String email) {
        SQLConnection conn = new SQLConnection("user1", "");
        if(!conn.isConn()) return -1;
        String query;
        if(extras.getBoolean("user"))
            query = "SELECT ID FROM Guardian where email = ?;";
        else if (extras.getBoolean("practitioner"))
            query = "SELECT ID FROM Practitioner where email = ?;";
        else return -1;
        HashMap<String, String[]> result = conn.select(query, new String[]{email}, new char[]{'s'});
        return (result.get("ID").length == 0) ? -1 : Integer.valueOf(result.get("ID")[0]);
    }

    private void sendEmail(String password, String recipient) {
        Log.d("msg", recipient);
        String sender = "sender@gmail.com";
        String host = "127.0.0.1";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("This is Subject");
            message.setText("This is a test mail");
            Transport.send(message);
            System.out.println("Mail successfully sent");
        }
        catch (MessagingException mex)
        {
            mex.printStackTrace();
        }
    }

}