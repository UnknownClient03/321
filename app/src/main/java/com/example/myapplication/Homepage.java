package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Homepage extends AppCompatActivity {
    LoginManager manager;
    private CameraActivity cameraDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cameraDialog = new CameraActivity(this);
        
        NavBarManager.setNavBarButtons(Homepage.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));

        TextView textView = (TextView)findViewById(R.id.textView_homepage_name);
        SQLConnection conn = new SQLConnection("user1", "");
        if(conn.isConn())
        {
            String query = "SELECT fname, lname from Guardian WHERE ID = ?;";
            HashMap<String, String[]> result = conn.select(query, new String[]{String.valueOf(manager.guardianID)}, new char[]{'i'});
            conn.disconnect();
            String fname = result.get("fname")[0];
            textView.setText(fname + "!");
        }

        ImageButton shareButton = findViewById(R.id.share_button);
        shareButton.setVisibility(View.VISIBLE); // Make it visible only on the homepage
        shareButton.setOnClickListener(v -> {
            CameraActivity cameraDialog = new CameraActivity(this);
            cameraDialog.show();
        });

        // Setting up the Immunisation button
        Button buttonImmunisation = findViewById(R.id.button_immunisation);
        buttonImmunisation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Immunisation page");
                Intent intent = new Intent(Homepage.this, Immunisation.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Setting up the Appointments button
        Button buttonAppointments = findViewById(R.id.button_appointments);
        buttonAppointments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Appointments page");
                Intent intent = new Intent(Homepage.this, AppointmentsActivity.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Setting up the CPR Chart button
        Button buttonCPRChart = findViewById(R.id.button_cpr_chart);
        buttonCPRChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON", "changing to CPR Chart");
                Intent intent = new Intent(Homepage.this, CPRChart.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Setting up the My Information and Family History button
        Button buttonMyInfoAndFamHis = findViewById(R.id.button_my_information_and_family_history);
        buttonMyInfoAndFamHis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to My Information and Family History");
                Intent intent = new Intent(Homepage.this, MyInfoAndFamHis.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Setting up the Birth Details and Newborn Examination button
        Button buttonBirthDetailsAndNewbornExamination = findViewById(R.id.button_birth_details_and_newborn_examination);
        buttonBirthDetailsAndNewbornExamination.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Birth Details and Newborn Examination");
                Intent intent = new Intent(Homepage.this, BirthDetailsAndNewbornExamination.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Setting up the Info for Parents button
        Button buttonInfoForParents = findViewById(R.id.button_info_for_parents);
        buttonInfoForParents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Parent Info");
                Intent intent = new Intent(Homepage.this, ParentInfo.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Setting up the Useful Contacts button
        Button buttonUsefulContacts = findViewById(R.id.button_useful_contacts_and_websites);
        buttonUsefulContacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Useful Contacts");
                Intent intent = new Intent(Homepage.this, UsefulContacts.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        Button buttonRecords = findViewById(R.id.button_my_records);
        buttonRecords.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to records");
                Intent intent=new Intent(Homepage.this, Records.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

        // Primary and Secondary School button
        Button buttonPrimarySecondarySchool = findViewById(R.id.button_primary_secondary_school);
        buttonPrimarySecondarySchool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Primary & Secondary School page");
                Intent intent = new Intent(Homepage.this, PrimarySecondarySchool.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", manager.guardianID);
                    intent.putExtra("childID", manager.childID);
                }
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraActivity.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                sharePhotoToSocialMedia(photo);
            }
        } else {
            Toast.makeText(this, "Failed to take photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePhotoToSocialMedia(Bitmap photo) {
        File photoFile = new File(getExternalFilesDir(null), "photo.jpg");
        try (FileOutputStream out = new FileOutputStream(photoFile)) {
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);

        // Create an intent to share the photo
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share photo via"));
    }

    public LoginManager getManager() {
        return manager;
    }
    
}
