package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.LoginManager;
import com.example.myapplication.ProgressNotes;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_ProgressNotes_Write#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_ProgressNotes_Write extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_ProgressNotes_Write() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_ProgressNotes_Write.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_ProgressNotes_Write newInstance(String param1, String param2) {
        Fragment_ProgressNotes_Write fragment = new Fragment_ProgressNotes_Write();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment__progress_notes__write, container, false);

        Button readEntryBut = (Button)layout.findViewById(R.id.button_PN_toTable);
        readEntryBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new Fragment_ProgressNotes_Read())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button submit = (Button)layout.findViewById(R.id.button_progressNotes_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager manager = ((ProgressNotes)getActivity()).manager;
                getActivity().getOnBackPressedDispatcher().onBackPressed();

                EditText M = (EditText)layout.findViewById(R.id.input_progressNotes_year_m);
                EditText D = (EditText)layout.findViewById(R.id.input_progressNotes_year_d);
                EditText Y = (EditText)layout.findViewById(R.id.input_progressNotes_year_y);
                EditText reason = (EditText)layout.findViewById(R.id.input_progressNotes_reason);

                SQLConnection c = new SQLConnection("user1", "");
                int ID = c.getMaxID("ProgressNotes");
                String query = "SELECT DOB FROM child WHERE ID = " + manager.childID + ";";
                HashMap<String, String[]> result = c.select(query);
                String DOB = result.get("DOB")[0];


                query = "INSERT INTO ProgressNotes VALUES (" + manager.childID + ", "
                                                                    + ID + ", '"
                                                                    + Y.getText() + "-" + M.getText() + "-" + D.getText() + "', "
                                                                    + "(SELECT DATEDIFF(year, '" + DOB + "', '" + Y.getText() + "-" + M.getText() + "-" + D.getText() + "')), '"
                                                                    + reason.getText() + "');";
                c.update(query);
                c.disconnect();
            }
        });


        return layout;
    }
}
