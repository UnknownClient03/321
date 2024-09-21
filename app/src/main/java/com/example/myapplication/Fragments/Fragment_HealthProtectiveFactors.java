package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import java.util.HashMap;
import android.widget.Toast;
import com.example.myapplication.SQLConnection;
import com.example.myapplication.R;

public class Fragment_HealthProtectiveFactors extends Fragment {

    private SQLConnection dbHelper;
    private EditText healthFactorInput;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_protective_factors, container, false);

        dbHelper = new SQLConnection("user1", "");  // Establish connection

        healthFactorInput = view.findViewById(R.id.health_factor_input);
        saveButton = view.findViewById(R.id.save_button);

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String healthFactor = healthFactorInput.getText().toString();

            // Insert data using the update method from SQLConnection
            String[] params = {String.valueOf(1), healthFactor};  // Assuming child_id = 1
            char[] paramTypes = {'i', 's'};
            boolean isInserted = dbHelper.update("INSERT INTO ChildHealthFactors (child_id, factor) VALUES (?, ?)", params, paramTypes);

            if (isInserted) {
                Toast.makeText(getContext(), "Data Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Load existing data for child_id = 1
        loadHealthFactors(1);

        return view;
    }

    private void loadHealthFactors(int childId) {
        String[] params = {String.valueOf(childId)};
        char[] paramTypes = {'i'};
        HashMap<String, String[]> result = dbHelper.select("SELECT * FROM ChildHealthFactors WHERE child_id = ?", params, paramTypes);

        if (result != null && result.size() > 0) {
            healthFactorInput.setText(result.get("factor")[0]);
        }
    }
}
