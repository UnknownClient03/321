package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
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
    private Button uploadButton;

    private CaptureImage imageCapturer;

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

        // Requests permission for application to use camera
        CaptureImage.checkPermissions(AddChildActivity.this, AddChildActivity.this);
        imageCapturer = new CaptureImage(childProfilePicture, this);

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
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allows the user to take picture and saves the thumbnail as the profile picture
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageCapturer.activityResultLauncher.launch(intent);
            }
        });

        // Make sure some buttons cannot be pressed yet
        Button buttonHome = findViewById(R.id.home_button);
        buttonHome.setEnabled(false);  // Disable the button
        ImageButton buttonRecords = findViewById(R.id.records_button);
        buttonRecords.setEnabled(false);
        ImageButton buttonProgress = findViewById(R.id.progress_button);
        buttonProgress.setEnabled(false);

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(AddChildActivity.this, new LoginManager(guardianID, 0));
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
        uploadButton = findViewById(R.id.select_image_button);
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
        insertChildToDatabase(givenNames, surname, dob, sex, childProfilePicture, guardianID);

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

    private void insertChildToDatabase(String givenNames, String surname, String dob, String sex, ImageView profilePicture, int guardianID) {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        // Get the next available ID
        int newChildId = sqlConnection.getMaxID("Child");

        String profilePictureValue;

        // Set default profile picture (if nothing was uploaded, set to default)
        if (imageCapturer.convertBitmap().isEmpty()) {
            // Get the drawable resource
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.profile_picture);

            Bitmap bitmap;

            // Check if the drawable is a VectorDrawable
            if (drawable instanceof VectorDrawable) {
                // Convert VectorDrawable to Bitmap
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            } else if (drawable instanceof BitmapDrawable) {
                // If it's already a BitmapDrawable
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                // Handle other drawable types if necessary, or set a default bitmap
                bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888); // Placeholder
            }

            // Convert Bitmap to Base64 string
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            profilePictureValue = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            profilePictureValue = imageCapturer.convertBitmap();
        }

        // Formulate the INSERT query with the new ID and guardian ID
        String insertQuery = "INSERT INTO Child (ID, guardianID, fname, lname, DOB, sex, profilePicture) VALUES ("
                + newChildId + ", " + guardianID + ", '"
                + givenNames + "', '"
                + surname + "', '"
                + dob + "', '"
                + sex + "', '"
                + profilePictureValue + "');";

        // Execute the query
        sqlConnection.update(insertQuery);
    }
}
