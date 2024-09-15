package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.LoginManager;
import com.example.myapplication.ImmunisationRecord;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

public class Fragment_Immunisation_Write extends Fragment {

    public Fragment_Immunisation_Write() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_immunisation_write, container, false);

        Button viewTableBut = layout.findViewById(R.id.button_PN_toTable);
        viewTableBut.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new Fragment_Immunisation_Read())
                .commit());

        Button submit = layout.findViewById(R.id.button_immunisation_submit);
        submit.setOnClickListener(v -> {
            LoginManager manager = ((ImmunisationRecord)getActivity()).manager;
            ImmunisationRecord activity = ((ImmunisationRecord)getActivity());
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);

            EditText age = layout.findViewById(R.id.input_immunisation_age);
            EditText vaccine = layout.findViewById(R.id.input_immunisation_vaccine);
            EditText D = (EditText)layout.findViewById(R.id.input_immunisation_dateGiven_d);
            EditText M = (EditText)layout.findViewById(R.id.input_immunisation_dateGiven_m);
            EditText Y = (EditText)layout.findViewById(R.id.input_immunisation_dateGiven_y);
            EditText batchNo = layout.findViewById(R.id.input_immunisation_batch_no);

            SQLConnection c = new SQLConnection("user1", "");
            if(c.isConn()) {
                int ID = c.getMaxID("ImmunisationRecord");
                String query = "INSERT INTO ImmunisationRecord (childID, ID, age, vaccine, dateGiven, batchNum) VALUES (?, ?, ?, ?, ?, ?);";
                String[] params = {
                        String.valueOf(manager.childID),
                        String.valueOf(ID),
                        age.getText().toString(),
                        vaccine.getText().toString(),
                        Y.getText() + "-" + M.getText() + "-" + D.getText(),
                        batchNo.getText().toString(),
                };
                char[] paramTypes = { 'i', 'i', 's', 's', 's', 'i' };
                c.update(query, params, paramTypes);
                c.disconnect();
            }
        });
        return layout;
    }
}
