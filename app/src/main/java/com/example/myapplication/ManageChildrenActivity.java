package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ManageChildrenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_children);

        // Initialize UI components
        Button addChildButton = findViewById(R.id.add_child_button);

        // Set up onClick listeners
        addChildButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageChildrenActivity.this, AddChildActivity.class);
            startActivity(intent);
        });

        // Initialize other UI components and set up listeners as needed
    }
}
