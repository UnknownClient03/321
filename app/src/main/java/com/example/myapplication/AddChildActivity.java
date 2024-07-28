package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        EditText childNameInput = findViewById(R.id.child_name_input);
        EditText childAgeInput = findViewById(R.id.child_age_input);
        Button saveChildButton = findViewById(R.id.save_child_button);

        saveChildButton.setOnClickListener(v -> {

            String childName = childNameInput.getText().toString();
            String childAge = childAgeInput.getText().toString();

            // Save the new child data (e.g., to a database or shared preferences)
            // For example:
            // saveChildToDatabase(childName, childAge);

            // Navigate back to the previous screen or show a success message
            finish();
        });
    }
}
