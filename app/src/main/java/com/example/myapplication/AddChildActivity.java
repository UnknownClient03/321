package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddChildActivity extends AppCompatActivity {

    private EditText editTextGivenNames;
    private EditText editTextSurname;
    private EditText editTextAddress;
    private Spinner spinnerSex;
    private EditText editTextDOB;
    private EditText editTextBirthWeight;
    private Button saveChildButton;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        editTextGivenNames = findViewById(R.id.given_name_input);
        editTextSurname = findViewById(R.id.surname_input);
        editTextAddress = findViewById(R.id.address_input);
        spinnerSex = findViewById(R.id.sex_input);
        editTextDOB = findViewById(R.id.dob_input);
        editTextBirthWeight = findViewById(R.id.birth_weight_input);
        saveChildButton = findViewById(R.id.save_child_button);
        backArrow = findViewById(R.id.back_arrow);

        saveChildButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String givenNames = editTextGivenNames.getText().toString().trim();
                String surname = editTextSurname.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                String sex = spinnerSex.getSelectedItem().toString().trim();
                String dob = editTextDOB.getText().toString().trim();
                String birthWeight = editTextBirthWeight.getText().toString().trim();

                // Create an Intent to pass the data to the front page
                Intent resultIntent = new Intent();
                resultIntent.putExtra("GIVEN_NAMES", givenNames);
                resultIntent.putExtra("SURNAME", surname);
                resultIntent.putExtra("ADDRESS", address);
                resultIntent.putExtra("SEX", sex);
                resultIntent.putExtra("DOB", dob);
                resultIntent.putExtra("BIRTH_WEIGHT", birthWeight);

                setResult(RESULT_OK, resultIntent);

                Toast.makeText(AddChildActivity.this, "You have added a new child", Toast.LENGTH_SHORT).show();

                finish();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        backArrow.setOnClickListener(v -> finish());

        Spinner spinner = findViewById(R.id.sex_input);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_input_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        NavBarManager.setNavBarButtons(AddChildActivity.this);
    }

    private boolean validateInputs() {
        String givenNames = editTextGivenNames.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String sex = spinnerSex.getSelectedItem().toString().trim();
        String dob = editTextDOB.getText().toString().trim();
        String birthWeight = editTextBirthWeight.getText().toString().trim();

        return !givenNames.isEmpty() && !surname.isEmpty() && !address.isEmpty() && !sex.isEmpty() && !dob.isEmpty() && !birthWeight.isEmpty();
    }
}
