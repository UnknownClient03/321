package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CaptureImage {

    public Bitmap currentBitmap;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    private int width = 250;

    public CaptureImage(ImageView view, AppCompatActivity appCompat) {
        activityResultLauncher = appCompat.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
                                // Camera image
                                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                                currentBitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);
                                view.setImageBitmap(currentBitmap);
                            } else if (data != null && data.getData() != null) {
                                // Gallery image
                                try {
                                    Uri imageUri = data.getData();
                                    InputStream imageStream = appCompat.getContentResolver().openInputStream(imageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                                    currentBitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);
                                    view.setImageBitmap(currentBitmap);
                                } catch (Exception e) {
                                    Log.e("Image Error", "Failed to load image from gallery: " + e.getMessage());
                                }
                            }
                        }
                    }
                });
    }

    public String convertBitmap() {
        if (currentBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            currentBitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
            byte[] arr = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(arr, Base64.DEFAULT);
            if (imageEncoded.length() > 100000) Log.e("decoding error", "encoded image is too large");
            return imageEncoded;
        } else return "";
    }

    public static Bitmap convertString(String imageEncoded) {
        byte[] decodedByte = Base64.decode(imageEncoded, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        if (bitmap == null) {
            Log.e("decoding error", "could not decode image");
        }
        return bitmap;
    }

    public static void checkPermissions(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    android.Manifest.permission.CAMERA
            }, 100);
        }
    }

    public void setImageUri(Uri imageUri, Context context) {
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            currentBitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);
        } catch (Exception e) {
            Log.e("Image Error", "Failed to convert image from URI: " + e.getMessage());
        }
    }


}
