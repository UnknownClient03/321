package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NavBarManager {
    public static void setNavBarButtons(AppCompatActivity x)
    {
        if(x.findViewById(R.id.header) != null)
        {
            //Sets the Home button to go home
            Button button = x.findViewById(R.id.home_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent=new Intent(x, Homepage.class);
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null) {
                        intent.putExtra("guardianID", extras.getInt("guardianID"));
                        intent.putExtra("childID", extras.getInt("childID"));
                    }
                    x.startActivity(intent);
                }
            });
            ImageButton buttonSettings = x.findViewById(R.id.settings_button);
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent=new Intent(x, AccountSettings.class);
                    Bundle extras = x.getIntent().getExtras();
                    if (extras != null) {
                        intent.putExtra("guardianID", extras.getInt("guardianID"));
                        intent.putExtra("childID", extras.getInt("childID"));
                    }
                    x.startActivity(intent);
                }
            });
        }
        if(x.findViewById(R.id.footer) != null)
        {
            //Sets the back button by removing form the stack of activities from the cache
            Button button = x.findViewById(R.id.back_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    x.getOnBackPressedDispatcher().onBackPressed();
                }
            });
            //Sets the log out button
            button = x.findViewById(R.id.logout_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent=new Intent(x, UserLoginActivity.class);
                    x.startActivity(intent);
                }
            });
        }
    }
}

/*example

navBarManager.setNavBarButtons(Homepage.this);

 */
