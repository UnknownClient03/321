package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ChildSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_select);

        Button manageChildrenButton = findViewById(R.id.button3);
        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton addButton = findViewById(R.id.add_btn);

        manageChildrenButton.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, ManageChildrenActivity.class);
            startActivity(intent);
        });

        imageButton1.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, Homepage.class);
            intent.putExtra("child_id", 1);
            startActivity(intent);
        });

        imageButton2.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, Homepage.class);
            intent.putExtra("child_id", 2);
            startActivity(intent);
        });

        addButton.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, AddChildActivity.class);
            startActivity(intent);
        });
    }
}
