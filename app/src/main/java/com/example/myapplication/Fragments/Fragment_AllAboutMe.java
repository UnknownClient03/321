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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_allaboutme, container, false);
        Button submit = (Button)layout.findViewById(R.id.button_AllAboutMe);
        if(submit == null) throw new NullPointerException("could not find button: " + R.id.button_AllAboutMe);
        else submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_2);
                button.performClick();
                EditText fname = (EditText)layout.findViewById(R.id.input_child_fname);
                EditText lname = (EditText)layout.findViewById(R.id.input_child_lname);
                EditText dobD = (EditText)layout.findViewById(R.id.input_child_dob_d);
                EditText dobM = (EditText)layout.findViewById(R.id.input_child_dob_m);
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
                LoginManager manager = ((MyInfoAndFamHis)getActivity()).manager;
                String query = "INSERT INTO Child VALUES (" + manager.childID + ", "
                                                            + manager.guardianID + ", '"
                                                            + fname.getText() + "','"
                                                            + lname.getText() + "','"
                                                            + dobY.getText() + "-" + dobM.getText()+"-"+dobD.getText() + "','"
                                                            + sex +"');";
                c.update(query);
                c.disconnect();
            }
        });
        return layout;
    }
}