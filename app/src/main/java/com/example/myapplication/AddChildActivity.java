package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddChildActivity extends AppCompatActivity {

    private EditText editTextGivenNames;
    private EditText editTextSurname;
    private EditText editTextDOBDay;
    private EditText editTextDOBMonth;
    private EditText editTextDOBYear;
    private Spinner spinnerSex;
    private Button saveChildButton;
    private ImageView backArrow;
    private ImageView childProfilePicture; // ImageView for child's profile picture
    private Bitmap selectedImageBitmap; // Store the selected image bitmap
    private Button selectImageButton;

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    // Declare ActivityResultLaunchers
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    private boolean isButtonClicked = false; // Flag to prevent multiple executions
    private int guardianID; // Store the current guardian ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // Retrieve the guardian ID from the intent
        guardianID = getIntent().getIntExtra("guardianID", 0);

        // Initialize the views
        initializeViews();

        // Configure the gender spinner
        configureGenderSpinner();

        // Initialize ActivityResultLaunchers
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            childProfilePicture.setImageBitmap(selectedImageBitmap);
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
                        childProfilePicture.setImageBitmap(selectedImageBitmap);
                    }
                });

        backArrow.setOnClickListener(v -> finish());

        // Set up click listener for save button
        saveChildButton.setOnClickListener(v -> {
            if (isButtonClicked) return; // Prevent further clicks if already clicked
            isButtonClicked = true; // Set the flag to true after the first click

            if (validateInputs()) {
                processChildData();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                isButtonClicked = false; // Reset the flag to allow retry
            }
        });

        // Set up click listener for child's profile picture
        selectImageButton.setOnClickListener(v -> showImagePickerDialog());

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(AddChildActivity.this, new LoginManager(guardianID, 0));
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddChildActivity.this);
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

    private void initializeViews() {
        editTextGivenNames = findViewById(R.id.fname_input);
        editTextSurname = findViewById(R.id.lname_input);
        editTextDOBDay = findViewById(R.id.dob_day_input);
        editTextDOBMonth = findViewById(R.id.dob_month_input);
        editTextDOBYear = findViewById(R.id.dob_year_input);
        spinnerSex = findViewById(R.id.sex_input);
        saveChildButton = findViewById(R.id.save_child_button);
        backArrow = findViewById(R.id.back_arrow);
        childProfilePicture = findViewById(R.id.profile_picture); // Initialize the ImageView for child's profile picture
        selectImageButton = findViewById(R.id.select_image_button);
    }

    private void configureGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Male", "Female"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapter);
    }

    private void processChildData() {
        String givenNames = editTextGivenNames.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String dobDay = editTextDOBDay.getText().toString().trim();
        String dobMonth = editTextDOBMonth.getText().toString().trim();
        String dobYear = editTextDOBYear.getText().toString().trim();
        String sex = spinnerSex.getSelectedItem().toString().substring(0, 1).toUpperCase();
        String dob = dobYear + "-" + dobMonth + "-" + dobDay;

        if (!isValidDate(dob)) {
            Toast.makeText(this, "Please enter a valid date of birth.", Toast.LENGTH_SHORT).show();
            isButtonClicked = false; // Reset the flag
            return;
        }

        // Insert the child into the database with the current guardian ID
        insertChildToDatabase(givenNames, surname, dob, sex, guardianID);

        Toast.makeText(AddChildActivity.this, "You have added a new child", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInputs() {
        String givenNames = editTextGivenNames.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String dobDay = editTextDOBDay.getText().toString().trim();
        String dobMonth = editTextDOBMonth.getText().toString().trim();
        String dobYear = editTextDOBYear.getText().toString().trim();
        String sex = spinnerSex.getSelectedItem().toString().trim();

        return !givenNames.isEmpty() && !surname.isEmpty() && !dobDay.isEmpty() &&
                !dobMonth.isEmpty() && !dobYear.isEmpty() && !sex.equals("Gender");
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void insertChildToDatabase(String givenNames, String surname, String dob, String sex, int guardianID) {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        // Get the next available ID
        int newChildId = sqlConnection.getMaxID("Child");

        // Formulate the INSERT query with the new ID and guardian ID
        String insertQuery = "INSERT INTO Child (ID, guardianID, fname, lname, DOB, sex) VALUES ("
                + newChildId + ", " + guardianID + ", '"
                + givenNames + "', '"
                + surname + "', '"
                + dob + "', '"
                + sex + "');";

        // Execute the query
        sqlConnection.update(insertQuery);
    }
}
