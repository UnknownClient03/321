package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Change this to Button
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NavBarManager {
    public static void setNavBarButtons(AppCompatActivity x, LoginManager manager) {
        if (x.findViewById(R.id.header) != null) {
            // Sets the Home button to go home
            Button buttonHome = x.findViewById(R.id.home_button); // Changed to normal button
            buttonHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent;

                    // Check if coming from a practitioner-related activity
                    boolean comingFromPractitioner = false;
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null && extras.getBoolean("fromPractitionerHomepage", false)) {
                        comingFromPractitioner = true;
                    }

                    // Decide the target activity based on the origin
                    if (comingFromPractitioner) {
                        intent = new Intent(x, PractitionerHomepage.class); // Go back to PractitionerHomepage
                    } else {
                        intent = new Intent(x, Homepage.class); // Go to Homepage
                    }

                    // Pass extras as needed
                    if (extras != null) {
                        intent.putExtra("guardianID", extras.getInt("guardianID"));
                        intent.putExtra("childID", extras.getInt("childID"));
                    }

                    // Start the appropriate home activity
                    x.startActivity(intent);
                }
            });


            // Sets the Settings button
            ImageButton buttonSettings = x.findViewById(R.id.settings_button); // Keep this as ImageButton
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent;

                    // Check if coming from PractitionerHomepage
                    boolean comingFromPractitioner = false;
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null && extras.getBoolean("fromPractitionerHomepage", false)) {
                        comingFromPractitioner = true;
                    }

                    // Decide the target activity based on the origin
                    if (comingFromPractitioner || x instanceof PractitionerHomepage) {
                        intent = new Intent(x, PractitionerAccountSettings.class);
                    } else if (x instanceof Homepage) {
                        intent = new Intent(x, AccountSettings.class);
                    } else {
                        // Default to a common settings page or log an error
                        intent = new Intent(x, AccountSettings.class);
                    }

                    // Pass extras as needed
                    if (extras != null) {
                        intent.putExtra("guardianID", extras.getInt("guardianID"));
                        intent.putExtra("childID", extras.getInt("childID"));
                    }

                    // Start the appropriate settings activity
                    x.startActivity(intent);
                }
            });

            // Sets the Share button
            ImageButton shareButton = x.findViewById(R.id.share_button); // Keep this as ImageButton
            shareButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Show the dialog with camera options on the same screen
                    CameraActivity dialog = new CameraActivity(x);
                    dialog.show();
                }
            });
        }

        if (x.findViewById(R.id.footer) != null) {
            // Sets the Back button
            ImageButton backButton = x.findViewById(R.id.back_button); // Keep this as ImageButton
            backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    x.getOnBackPressedDispatcher().onBackPressed();
                }
            });

            // Sets the Log out button
            ImageButton logoutButton = x.findViewById(R.id.logout_button); // Keep this as ImageButton
            logoutButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, UserLoginActivity.class);
                    x.startActivity(intent);
                }
            });

            // Sets the Records button
            ImageButton recordsButton = x.findViewById(R.id.records_button); // Keep this as ImageButton
            recordsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, Records.class); // Navigate to normal Records page

                    // Check if coming from a practitioner-related activity
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null && extras.getBoolean("fromPractitionerHomepage", false)) {
                        intent.putExtra("fromPractitionerHomepage", true); // Set a flag for coming from practitioner
                    }

                    // Pass extras as needed
                    if (extras != null) {
                        intent.putExtra("guardianID", manager.guardianID);
                        intent.putExtra("childID", manager.childID);
                    }

                    // Start the Records activity
                    x.startActivity(intent);
                }
            });

            // Set the Appointments button
            ImageButton appointmentsButton = x.findViewById(R.id.appointments_button); // Keep this as ImageButton
            appointmentsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(x, AppointmentsActivity.class); // Navigate to normal Appointments page

                    // Check if coming from a practitioner-related activity
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null && extras.getBoolean("fromPractitionerHomepage", false)) {
                        intent.putExtra("fromPractitionerHomepage", true); // Set a flag for coming from practitioner
                    }

                    // Pass extras as needed
                    if (extras != null) {
                        intent.putExtra("guardianID", manager.guardianID);
                        intent.putExtra("childID", manager.childID);
                    }

                    // Start the Appointments activity
                    x.startActivity(intent);
                }
            });


            // Commented out the Progress button for now
            /*
            ImageButton progressButton = x.findViewById(R.id.progress_button); // Keep this as ImageButton
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
