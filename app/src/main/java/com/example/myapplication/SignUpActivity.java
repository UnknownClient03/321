package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SignUpActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private EditText firstNameInput, lastNameInput, emailInput, phoneNumberInput, passwordInput, confirmPasswordInput;
    private Button uploadButton, signUpButton;
    private ImageView backArrow;
    private EditText editTextPassword;
    private CheckBox showPasswordCheckbox, showConfirmPasswordCheckbox;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;

    private CaptureImage imageCapturer;

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
        showPasswordCheckbox = findViewById(R.id.show_password_checkbox);
        showConfirmPasswordCheckbox = findViewById(R.id.show_confirm_password_checkbox);

        // Requests permission for application to use camera
        CaptureImage.checkPermissions(SignUpActivity.this, SignUpActivity.this);
        imageCapturer = new CaptureImage(profilePicture, this);

        // Handle upload button click
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allows the user to take a picture or select an image and saves the thumbnail as the profile picture
                showImageSourceOptions();
            }
        });

        // Handle sign-up button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs() && registerUser()) {
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        backArrow.setOnClickListener(v -> finish());

        CheckBox showPasswordCheckbox = findViewById(R.id.show_password_checkbox);
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                // Hide password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move cursor to the end of the text
            passwordInput.setSelection(passwordInput.getText().length());
        });

        CheckBox showConfirmPasswordCheckbox = findViewById(R.id.show_confirm_password_checkbox);
        showConfirmPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                // Hide password
                confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move cursor to the end of the text
            confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
        });

        NavBarManager.setNavBarButtons(SignUpActivity.this, new LoginManager(0, 0));

    }

    private void showImageSourceOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Camera option
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        break;
                    case 1: // Gallery option
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null && data.getExtras() != null) {
                // Handle the camera result
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    // Set the captured image to the ImageView
                    profilePicture.setImageBitmap(bitmap);
                    imageCapturer.currentBitmap = bitmap;  // Store the bitmap in CaptureImage
                }
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                // Handle the gallery result
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    profilePicture.setImageURI(selectedImage);  // Show the selected image in the ImageView
                    imageCapturer.setImageUri(selectedImage, this);  // Store the image in CaptureImage
                }
            }
        }
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
        String email = emailInput.getText().toString().trim();
        editTextPassword = findViewById(R.id.password_input);
        String password = editTextPassword.getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

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

    }

    private boolean registerUser()
    {
        SQLConnection conn = new SQLConnection("user1", "");
        if(!conn.isConn()) return false;
        int ID = conn.getMaxID("Guardian");
        String query = "INSERT INTO Guardian VALUES (?, ?, ?, ?, ?, ?, ?)";
        String[] params = { String.valueOf(ID), firstNameInput.getText().toString(), lastNameInput.getText().toString(), phoneNumberInput.getText().toString(), phoneNumberInput.getText().toString(), emailInput.getText().toString(), imageCapturer.convertBitmap() };
        char[] paramTypes = {'i', 's', 's', 'i', 'i', 's', (imageCapturer.currentBitmap != null) ? 's' : 'n' };
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