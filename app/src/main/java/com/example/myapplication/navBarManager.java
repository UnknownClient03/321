package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class navBarManager {
    public static void setNavBarButtons(AppCompatActivity x)
    {
        if(x.findViewById(R.id.header) != null)
        {
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
        }
        if(x.findViewById(R.id.footer) != null)
        {
            Button button = x.findViewById(R.id.back_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    x.getOnBackPressedDispatcher().onBackPressed();
                }
            });
            button = x.findViewById(R.id.logout_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent=new Intent(x, MainActivity.class);
                    x.startActivity(intent);
                }
            });
        }
    }
}
