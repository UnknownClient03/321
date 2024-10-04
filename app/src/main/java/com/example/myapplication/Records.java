package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Records extends AppCompatActivity {

    public LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));
        NavBarManager.setNavBarButtons(Records.this, manager);

        Button button = findViewById(R.id.button_myRecords_1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Progress Notes");
                Intent intent=new Intent(Records.this, ProgressNotes.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", extras.getInt("guardianID"));
                    intent.putExtra("childID", extras.getInt("childID"));
                }
                startActivity(intent);
            }
        });


        button = findViewById(R.id.button_myRecords_2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Illness and Injury Notes");
                Intent intent=new Intent(Records.this, IllnessInjuryNotes.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", extras.getInt("guardianID"));
                    intent.putExtra("childID", extras.getInt("childID"));
                }
                startActivity(intent);
            }
        });

    }
}
