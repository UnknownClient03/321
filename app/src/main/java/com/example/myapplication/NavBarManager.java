package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NavBarManager {
    public static void setNavBarButtons(AppCompatActivity x) {
        if (x.findViewById(R.id.header) != null) {
            //sets the Home button to go home
            ImageButton buttonHome = x.findViewById(R.id.home_button); //updated to ImageButton
            buttonHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, Homepage.class);
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null) {
                        intent.putExtra("guardianID", extras.getInt("guardianID"));
                        intent.putExtra("childID", extras.getInt("childID"));
                    }
                    x.startActivity(intent);
                }
            });

            //sets the Settings button
            ImageButton buttonSettings = x.findViewById(R.id.settings_button); //updated to ImageButton
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, AccountSettings.class);
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null) {
                        intent.putExtra("guardianID", extras.getInt("guardianID"));
                        intent.putExtra("childID", extras.getInt("childID"));
                    }
                    x.startActivity(intent);
                }
            });

            //sets the Share button
            ImageButton shareButton = x.findViewById(R.id.share_button); // Updated to ImageButton
            shareButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //show the dialog with camera options on the same screen
                    CameraActivity dialog = new CameraActivity(x);
                    dialog.show();
                }
            });
        }

        if (x.findViewById(R.id.footer) != null) {
            // Sets the Back button
            ImageButton backButton = x.findViewById(R.id.back_button); // Updated to ImageButton
            backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    x.getOnBackPressedDispatcher().onBackPressed();
                }
            });

            //sets the Log out button
            ImageButton logoutButton = x.findViewById(R.id.logout_button); //updated to ImageButton
            logoutButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, UserLoginActivity.class);
                    x.startActivity(intent);
                }
            });

            //sets the Records button
            ImageButton recordsButton = x.findViewById(R.id.records_button); //updated to ImageButton
            recordsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, Records.class);
                    x.startActivity(intent);
                }
            });

            //commented out the Progress button for now
            /*
            ImageButton progressButton = x.findViewById(R.id.progress_button); // Updated to ImageButton
            progressButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, ProgressNotes.class); // Change to your actual activity class name
                    x.startActivity(intent);
                }
            });
            */
        }
    }
}
