package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CameraActivity extends Dialog {

    public static final int CAMERA_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private final AppCompatActivity activity;

    public CameraActivity(@NonNull AppCompatActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_options);

        // Set up the button listeners
        findViewById(R.id.take_photo_button).setOnClickListener(v -> openCamera());
        findViewById(R.id.share_progress_button).setOnClickListener(v -> shareProgress());
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void sharePhotoToSocialMedia(Bitmap photo) {
        File photoFile = new File(activity.getExternalFilesDir(null), "photo.jpg");
        try (FileOutputStream out = new FileOutputStream(photoFile)) {
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to save photo", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri photoUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", photoFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(shareIntent, "Share photo via"));
    }

    public void shareProgress() {
        String progress = getChildProgress();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, progress);
        activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public String getChildProgress() {
        StringBuilder dataToShare = new StringBuilder("Child's Progress Notes:\n\n");
        SQLConnection c = new SQLConnection("user1", "");

        if (c.isConn()) {
            try {

                LoginManager manager = (LoginManager) activity.getClass().getMethod("getManager").invoke(activity);

                String query = "SELECT date, age, reason FROM ProgressNotes WHERE childID = ?;";
                HashMap<String, String[]> result = c.select(query, new String[]{String.valueOf(manager.childID)}, new char[]{'i'});

                if (result != null && result.get("date") != null) {

                    dataToShare.append(String.format("%-20s %-8s %-20s\n", "Date", "Age", "Reason/Action"));
                    dataToShare.append("-----------------------------------------------------------\n");

                    // Format each row with fixed-width columns
                    for (int i = 0; i < result.get("date").length; i++) {
                        dataToShare.append(String.format("%-15s %-8s %-20s\n",
                                result.get("date")[i],
                                result.get("age")[i],
                                result.get("reason")[i]));
                    }
                } else {
                    dataToShare.append("No progress notes available.");
                }
                c.disconnect();
            } catch (Exception e) {
                Log.e("CameraActivity", "Error accessing getManager(): " + e.getMessage());
                Toast.makeText(activity, "Unable to fetch child progress.", Toast.LENGTH_SHORT).show();
                return "No progress data available.";
            }
        } else {
            dataToShare.append("Failed to connect to the database.");
        }

        return dataToShare.toString().trim();
    }
}
