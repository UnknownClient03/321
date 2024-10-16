package com.example.myapplication.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.myapplication.LoginManager;
import com.example.myapplication.MyInfoAndFamHis;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_AllAboutMe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_AllAboutMe extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_AllAboutMe() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_AllAboutMe.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_AllAboutMe newInstance(String param1, String param2) {
        Fragment_AllAboutMe fragment = new Fragment_AllAboutMe();
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

    //this flag denotes wether the
    private boolean updateFlag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SQLConnection c = new SQLConnection("user1", "");
        LoginManager manager = ((MyInfoAndFamHis)getActivity()).manager;
        View layout = inflater.inflate(R.layout.fragment_allaboutme, container, false);
        if(!c.isConn()) return layout;
        Button submit = (Button)layout.findViewById(R.id.button_AllAboutMe);
        Button update = (Button)layout.findViewById(R.id.update_AllAboutMe);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_2);
                button.performClick();
                EditText fname = (EditText)layout.findViewById(R.id.input_child_fname);
                EditText lname = (EditText)layout.findViewById(R.id.input_child_lname);
                EditText dobM = (EditText)layout.findViewById(R.id.input_child_dob_m);
                EditText dobD = (EditText)layout.findViewById(R.id.input_child_dob_d);
                EditText dobY = (EditText)layout.findViewById(R.id.input_child_dob_y);
                RadioButton sexM = (RadioButton)layout.findViewById(R.id.input_child_M);
                RadioButton sexF = (RadioButton)layout.findViewById(R.id.input_child_F);
                if(dobD.getText().length() == 0 || dobM.getText().length() == 0 || dobY.getText().length() == 0)
                {
                    Log.e("query syntax error", "integers need to be set");
                    return;
                }
                SQLConnection c = new SQLConnection("user1", "");
                char sex = (sexM.isChecked()) ? 'M':(sexF.isChecked()) ? 'F' : ' ';
                String query = (updateFlag) ? "UPDATE Child SET fname = ?, lname = ?, DOB = ?, sex = ? WHERE ID = ? AND guardianID = ?"
                                            : "INSERT INTO Child VALUES (?, ?, ?, ?, ?, ?);";
                String[] params = (updateFlag) ? new String[]{ fname.getText().toString(), lname.getText().toString(), dobY.getText() + "-" + dobM.getText()+"-"+dobD.getText(), String.valueOf(sex), String.valueOf(manager.childID), String.valueOf(manager.guardianID) }
                                               : new String[]{ String.valueOf(manager.childID), String.valueOf(manager.guardianID), fname.getText().toString(), lname.getText().toString(), dobY.getText() + "-" + dobM.getText()+"-"+dobD.getText(), String.valueOf(sex) };
                char[] paramTypes = (updateFlag) ? new char[]{ 's', 's', 's', 's', 'i', 'i' } : new char[]{ 'i', 'i', 's', 's', 's', 's' };
                c.update(query, params, paramTypes);
                c.disconnect();
            }
        });

        String query = "SELECT ID, guardianID, fname, lname, DOB, sex FROM Child WHERE ID = ? AND guardianID = ?;";
        String[] params = { String.valueOf(manager.childID), String.valueOf(manager.guardianID) };
        char[] paramTypes = { 'i', 'i' };
        HashMap<String, String[]> result = c.select(query, params, paramTypes);
        if(result.get("ID").length == 0)
            update.setVisibility(View.INVISIBLE);
        else
        {
            submit.setVisibility(View.INVISIBLE);

            EditText fname = (EditText)layout.findViewById(R.id.input_child_fname);
            EditText lname = (EditText)layout.findViewById(R.id.input_child_lname);
            EditText dobD = (EditText)layout.findViewById(R.id.input_child_dob_d);
            EditText dobM = (EditText)layout.findViewById(R.id.input_child_dob_m);
            EditText dobY = (EditText)layout.findViewById(R.id.input_child_dob_y);
            RadioButton sexM = (RadioButton)layout.findViewById(R.id.input_child_M);
            RadioButton sexF = (RadioButton)layout.findViewById(R.id.input_child_F);
            fname.setText(result.get("fname")[0]);
            lname.setText(result.get("lname")[0]);
            dobD.setText(result.get("DOB")[0].substring(8, 10));
            dobM.setText(result.get("DOB")[0].substring(5, 7));
            dobY.setText(result.get("DOB")[0].substring(0, 4));
            if(result.get("sex")[0].charAt(0) == 'M') sexM.setChecked(true);
            else if(result.get("sex")[0].charAt(0) == 'F') sexF.setChecked(true);
            fname.setEnabled(false);
            lname.setEnabled(false);
            dobD.setEnabled(false);
            dobM.setEnabled(false);
            dobY.setEnabled(false);
            sexM.setEnabled(false);
            sexF.setEnabled(false);
            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    dobD.setEnabled(true);
                    dobM.setEnabled(true);
                    dobY.setEnabled(true);
                    sexM.setEnabled(true);
                    sexF.setEnabled(true);

                    submit.setVisibility(View.VISIBLE);
                    updateFlag = true;
                }
            });
        }

        c.disconnect();
        return layout;
    }
}