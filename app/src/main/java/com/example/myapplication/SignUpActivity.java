package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private EditText firstNameInput, lastNameInput, emailInput, phoneNumberInput, passwordInput, confirmPasswordInput;
    private Button uploadButton, signUpButton;
    private ImageView backArrow;
    private EditText editTextPassword;
    private CheckBox showPasswordCheckbox, showConfirmPasswordCheckbox;
    private Bitmap selectedImageBitmap; // Store the selected image bitmap

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    // Declare ActivityResultLaunchers
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

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

        // Initialize ActivityResultLaunchers
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            profilePicture.setImageBitmap(selectedImageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageBitmap = (Bitmap) result.getData().getExtras().get("data");
                        profilePicture.setImageBitmap(selectedImageBitmap);
                    }
                });

        // Handle upload button click
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
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

    // Show options to either select image from gallery or take a new picture
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(new CharSequence[]{"Select from Gallery", "Take a Picture"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Select from Gallery
                        selectImageFromGallery();
                        break;
                    case 1: // Take a Picture
                        captureImageFromCamera();
                        break;
                }
            }
        });
        builder.show();
    }

    // Select image from gallery
    private void selectImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            selectImageLauncher.launch(intent);
        }
    }

    // Capture image from camera
    private void captureImageFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureImageLauncher.launch(intent);
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImageFromCamera();
        } else if (requestCode == SELECT_FILE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImageFromGallery();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
    }

    private boolean registeruser() {
        SQLConnection conn = new SQLConnection("user1", "strongPwd123");
        if (!conn.isConn()) return false;

        String email = emailInput.getText().toString();
        // Check if the email already exists
        String checkEmailQuery = "SELECT COUNT(*) FROM Guardian WHERE email = ?";
        String[] checkParams = { email };
        char[] checkParamTypes = { 's' };

        // Assuming conn.query returns a result set where you can get the count
        int count = conn.queryCount(checkEmailQuery, checkParams, checkParamTypes);
        if (count > 0) {
            Toast.makeText(this, "Email Address is already a user.", Toast.LENGTH_SHORT).show();
            conn.disconnect();
            return false;
        }

        int ID = conn.getMaxID("Guardian");

        // Convert Bitmap to byte array
        byte[] imageBytes = null;
        if (selectedImageBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageBytes = stream.toByteArray();
        }

        // Add imageBytes to the parameters
        String query = "INSERT INTO Guardian (ID, fname, lname, phoneNumber, altPhoneNumber, email, profilePicture) VALUES (?, ?, ?, ?, ?, ?, CONVERT(varbinary(max), ?))";
        String[] params = {
                String.valueOf(ID),
                firstNameInput.getText().toString(),
                lastNameInput.getText().toString(),
                phoneNumberInput.getText().toString(),
                phoneNumberInput.getText().toString(),
                email,
                imageBytes != null ? Base64.encodeToString(imageBytes, Base64.DEFAULT) : null
        };
        char[] paramTypes = {'i', 's', 's', 'i', 'i', 's', 's'};

        if (!conn.update(query, params, paramTypes)) {
            Toast.makeText(this, "Error during sign-up.", Toast.LENGTH_SHORT).show();
            conn.disconnect();
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
