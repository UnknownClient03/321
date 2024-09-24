package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.Toast;
import java.util.HashMap;
import com.example.myapplication.SQLConnection;
import com.example.myapplication.R;

public class Fragment_HealthAssessment extends Fragment {

    private SQLConnection dbHelper;
    private EditText weightInput, heightInput, headCircumferenceInput;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_assessment, container, false);

        dbHelper = new SQLConnection("user1", "");  // Establish connection

        weightInput = view.findViewById(R.id.weight_input);
        heightInput = view.findViewById(R.id.height_input);
        headCircumferenceInput = view.findViewById(R.id.head_circumference_input);
        saveButton = view.findViewById(R.id.save_button);

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String weight = weightInput.getText().toString();
            String height = heightInput.getText().toString();
            String headCircumference = headCircumferenceInput.getText().toString();

            // Insert data using the update method from SQLConnection
            String[] params = {String.valueOf(1), weight, height, headCircumference};  // Assuming child_id = 1
            char[] paramTypes = {'i', 's', 's', 's'};
            boolean isInserted = dbHelper.update("INSERT INTO ChildCheckAssessment (child_id, weight, height, head_circumference) VALUES (?, ?, ?, ?)", params, paramTypes);

            if (isInserted) {
                Toast.makeText(getContext(), "Data Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Load existing data for child_id = 1
        loadHealthAssessment(1);

        return view;
    }

    private void loadHealthAssessment(int childId) {
        String[] params = {String.valueOf(childId)};
        char[] paramTypes = {'i'};
        HashMap<String, String[]> result = dbHelper.select("SELECT * FROM ChildCheckAssessment WHERE child_id = ?", params, paramTypes);

        if (result != null && result.size() > 0) {
            weightInput.setText(result.get("weight")[0]);
            heightInput.setText(result.get("height")[0]);
            headCircumferenceInput.setText(result.get("head_circumference")[0]);
        }
    }
}