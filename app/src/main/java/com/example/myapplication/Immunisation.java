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

public class Immunisation extends AppCompatActivity {
    public LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_immunisation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));
        NavBarManager.setNavBarButtons(Immunisation.this, manager);

        Button button = findViewById(R.id.button_immunisation_record);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Immunisation Record");
                Intent intent=new Intent(Immunisation.this, ImmunisationRecord.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtra("guardianID", extras.getInt("guardianID"));
                    intent.putExtra("childID", extras.getInt("childID"));
                }
                startActivity(intent);
            }
        });


        button = findViewById(R.id.button_4_month_immunisation_check);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "changing to Four Month Immunisation Check");
                Intent intent=new Intent(Immunisation.this, FourMonthImmunisationCheck.class);
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
