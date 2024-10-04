package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProgressNotes extends AppCompatActivity {

    public LoginManager manager;
    private CameraActivity cameraDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_progress_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cameraDialog = new CameraActivity(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));
        NavBarManager.setNavBarButtons(ProgressNotes.this, manager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraActivity.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the photo as a bitmap
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                // Share the photo immediately
                sharePhotoToSocialMedia(photo);
            }
        } else {
            Toast.makeText(this, "Failed to take photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePhotoToSocialMedia(Bitmap photo) {
        // Save the bitmap to a file
        File photoFile = new File(getExternalFilesDir(null), "photo.jpg");
        try (FileOutputStream out = new FileOutputStream(photoFile)) {
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the URI for the photo file using FileProvider
        Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);

        // Create an intent to share the photo
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share photo via"));
    }
}
