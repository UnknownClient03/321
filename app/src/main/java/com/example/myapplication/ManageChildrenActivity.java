package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ManageChildrenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_children);

        // Initialize UI components
        Button addChildButton = findViewById(R.id.add_child_button);
        ImageView backArrow = findViewById(R.id.back_arrow);
        // Set up onClick listeners
        addChildButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageChildrenActivity.this, AddChildActivity.class);
            startActivity(intent);
        });

        backArrow.setOnClickListener(v -> {
            finish(); // This will close the current activity and return to the previous one
        });

    }
}
