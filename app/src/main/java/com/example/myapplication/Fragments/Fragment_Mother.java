package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginManager;
import com.example.myapplication.MyInfoAndFamHis;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Mother#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Mother extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Mother() {
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
    public static Fragment_Mother newInstance(String param1, String param2) {
        Fragment_Mother fragment = new Fragment_Mother();
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
        View layout = inflater.inflate(R.layout.fragment_mother, container, false);
        if(!c.isConn()) return layout;
        Button submit = (Button)layout.findViewById(R.id.button_mother);
        Button update = (Button)layout.findViewById(R.id.update_mother);
        HashMap<String, String[]> result = c.select("SELECT childID, fname, lname, DOB, MRN, isAboriginal, isTorresStraitIslander, career FROM Parent WHERE childID = "+manager.childID+" AND parent = 'Mother';");
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_5);
                button.performClick();
                EditText fname = (EditText)layout.findViewById(R.id.input_mother_fname);
                EditText lname = (EditText)layout.findViewById(R.id.input_mother_lname);
                EditText dobD = (EditText)layout.findViewById(R.id.input_mother_dob_d);
                EditText dobM = (EditText)layout.findViewById(R.id.input_mother_dob_m);
                EditText dobY = (EditText)layout.findViewById(R.id.input_mother_dob_y);
                EditText mrn = (EditText)layout.findViewById(R.id.input_mother_MRN);
                CheckBox isAboRaw = (CheckBox)layout.findViewById(R.id.input_mother_isAboriginal);
                CheckBox isTSIRaw = (CheckBox)layout.findViewById(R.id.input_mother_isTorresStrait);
                EditText career = (EditText)layout.findViewById(R.id.input_mother_career);
                if(dobD.getText().length() == 0 || dobM.getText().length() == 0 || dobY.getText().length() == 0 || mrn.getText().length() == 0)
                {
                    Log.e("query syntax error", "integers need to be set.");
                    return;
                }
                int isAbo = (isAboRaw.isChecked()) ? 1 : 0;
                int isTSI = (isTSIRaw.isChecked()) ? 1 : 0;
                SQLConnection c = new SQLConnection("user1", "");
                String query = (updateFlag) ? "UPDATE Parent SET fname = '" + fname.getText()
                                                           + "', lname = '" + lname.getText()
                                                           + "', DOB = '" + dobY.getText() + "-" + dobM.getText()+"-"+dobD.getText()
                                                           + "', MRN = '" + mrn.getText()
                                                           + "', isAboriginal = " + isAbo
                                                           + ", isTorresStraitIslander = " + isTSI
                                                           + ", career = '" + career.getText()
                                                           + "' WHERE childID = " + manager.childID + " AND parent = 'Mother';"
                                            : "INSERT INTO Parent VALUES (" + manager.childID + ", 'Mother', '"
                                                                            + fname.getText() + "', '"
                                                                            + lname.getText() + "', '"
                                                                            + dobY.getText() + "-" + dobM.getText()+"-"+dobD.getText() + "', "
                                                                            + mrn.getText() + ", "
                                                                            + isAbo + ", "
                                                                            + isTSI + ", '"
                                                                            + career.getText() + "');";
                c.update(query);
                c.disconnect();
            }
        });

        if(result.get("childID").length == 0) update.setVisibility(View.INVISIBLE);
        else
        {
            submit.setVisibility(View.INVISIBLE);

            EditText fname = (EditText)layout.findViewById(R.id.input_mother_fname);
            EditText lname = (EditText)layout.findViewById(R.id.input_mother_lname);
            EditText dobD = (EditText)layout.findViewById(R.id.input_mother_dob_d);
            EditText dobM = (EditText)layout.findViewById(R.id.input_mother_dob_m);
            EditText dobY = (EditText)layout.findViewById(R.id.input_mother_dob_y);
            EditText mrn = (EditText)layout.findViewById(R.id.input_mother_MRN);
            CheckBox isAboRaw = (CheckBox)layout.findViewById(R.id.input_mother_isAboriginal);
            CheckBox isTSIRaw = (CheckBox)layout.findViewById(R.id.input_mother_isTorresStrait);
            EditText career = (EditText)layout.findViewById(R.id.input_mother_career);
            fname.setText(result.get("fname")[0]);
            lname.setText(result.get("lname")[0]);
            dobD.setText(result.get("DOB")[0].substring(8, 10));
            dobM.setText(result.get("DOB")[0].substring(5, 7));
            dobY.setText(result.get("DOB")[0].substring(0, 4));
            mrn.setText(result.get("MRN")[0]);
            if(result.get("isAboriginal")[0].charAt(0) == '1') isAboRaw.setChecked(true);
            if(result.get("isTorresStraitIslander")[0].charAt(0) == '1') isTSIRaw.setChecked(true);
            career.setText("career");
            fname.setEnabled(false);
            lname.setEnabled(false);
            dobD.setEnabled(false);
            dobM.setEnabled(false);
            dobY.setEnabled(false);
            mrn.setEnabled(false);
            isAboRaw.setEnabled(false);
            isTSIRaw.setEnabled(false);
            career.setEnabled(false);

            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    dobD.setEnabled(true);
                    dobM.setEnabled(true);
                    dobY.setEnabled(true);
                    mrn.setEnabled(true);
                    isAboRaw.setEnabled(true);
                    isTSIRaw.setEnabled(true);
                    career.setEnabled(true);

                    submit.setVisibility(View.VISIBLE);
                    updateFlag = true;
                }
            });
        }
        c.disconnect();
        return layout;
    }
}