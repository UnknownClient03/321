package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.Toast;
import com.example.myapplication.SQLConnection;
import com.example.myapplication.SignatureView;
import com.example.myapplication.R;

public class SignatureFragment extends Fragment {

    private SQLConnection dbHelper;
    private SignatureView signatureView;
    private Button clearButton, saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signature, container, false);

        dbHelper = new SQLConnection("user1", "");  // Establish connection

        signatureView = view.findViewById(R.id.signature_view);
        clearButton = view.findViewById(R.id.clear_button);
        saveButton = view.findViewById(R.id.save_button);

        // Clear signature
        clearButton.setOnClickListener(v -> signatureView.clearSignature());

        // Save signature logic
        saveButton.setOnClickListener(v -> {
            // Convert signature to base64 or any other appropriate format
            String signatureData = signatureView.getSignatureData();

            // Insert signature data into the database
            String[] params = {String.valueOf(1), signatureData};  // Assuming child_id = 1
            char[] paramTypes = {'i', 's'};
            boolean isInserted = dbHelper.update("INSERT INTO Signatures (child_id, signature) VALUES (?, ?)", params, paramTypes);

            if (isInserted) {
                Toast.makeText(getContext(), "Signature Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Save Failed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}