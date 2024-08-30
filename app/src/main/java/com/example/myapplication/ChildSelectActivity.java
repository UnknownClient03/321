package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ChildSelectActivity extends AppCompatActivity {

    private LoginManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_select);

        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), 0);

        Button manageChildrenButton = findViewById(R.id.buttonManageChildren);
        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton addButton = findViewById(R.id.add_btn);

        manageChildrenButton.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, ManageChildrenActivity.class);
            startActivity(intent);
        });

        imageButton1.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, Homepage.class);
            intent.putExtra("childID", 0);
            intent.putExtra("guardianID", manager.guardianID);
            startActivity(intent);
        });

        imageButton2.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, Homepage.class);
            intent.putExtra("childID", 1);
            intent.putExtra("guardianID", manager.guardianID);
            startActivity(intent);
        });

        addButton.setOnClickListener(v -> {

            Intent intent = new Intent(ChildSelectActivity.this, AddChildActivity.class);
            startActivity(intent);
        });

        NavBarManager.setNavBarButtons(ChildSelectActivity.this);
    }
}
