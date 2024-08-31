package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginManager;
import com.example.myapplication.MyInfoAndFamHis;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_HealthHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_HealthHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_HealthHistory() {
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
    public static Fragment_HealthHistory newInstance(String param1, String param2) {
        Fragment_HealthHistory fragment = new Fragment_HealthHistory();
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
        SQLConnection c = new SQLConnection("user1", "");
        LoginManager manager = ((MyInfoAndFamHis)getActivity()).manager;
        View layout = inflater.inflate(R.layout.fragment_healthhistory, container, false);
        if(!c.isConn()) return layout;
        Button submit = (Button)layout.findViewById(R.id.button_healthHistory);

        String query = "SELECT childID, riskFactor, condition, note FROM familyHealthHistory WHERE childID = ?;";
        HashMap<String, String[]> result = c.select(query, new String[]{String.valueOf(manager.guardianID)}, new char[]{'i'});
        HashMap<String, String[]> resultB = c.select("SELECT ID FROM Child WHERE ID = "+manager.childID+" AND guardianID = "+manager.guardianID+";");
        if(resultB.get("ID").length == 0)
        {
            submit.setText("Complete 'All About Me' section first.");
            submit.setTextSize(10f);
            submit.setWidth(200);
            submit.setMinWidth(0);
            submit.setEnabled(false);
        }
        else if(result.get("childID").length == 0)
        {
            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_7);
                    button.performClick();

                    LinearLayout topContainer = (LinearLayout)layout.findViewById(R.id.famHealthHis_container);
                    String [][] info = new String[3][8];
                    int index = -1;
                    for(int i = 0; i < topContainer.getChildCount(); i++, index++) {
                        if( topContainer.getChildAt(i) instanceof LinearLayout ) {
                            LinearLayout middleContainer = (LinearLayout)topContainer.getChildAt(i);
                            for(int j = 0; j < middleContainer.getChildCount(); j++) {
                                if(middleContainer.getChildAt(j) instanceof EditText)
                                    info[0][index] = ""+((EditText)middleContainer.getChildAt(j)).getText();
                                else if(middleContainer.getChildAt(j) instanceof LinearLayout) {
                                    LinearLayout lowerContainer = (LinearLayout)middleContainer.getChildAt(j);
                                    for(int k = 0; k < lowerContainer.getChildCount(); k++) {
                                        if((lowerContainer.getChildAt(k)) instanceof CheckBox)
                                            info[2][index] = (((CheckBox)lowerContainer.getChildAt(k)).isChecked()) ? "1": "0";
                                        else if((lowerContainer.getChildAt(k)) instanceof TextView)
                                            info[1][index] = ""+((TextView)lowerContainer.getChildAt(k)).getText();
                                    } } } } }

                    SQLConnection c = new SQLConnection("user1", "");
                    int ID = c.getMaxID("familyHealthHistory");
                    for(int i = 0; i < 8; i++, ID++)
                    {
                        String note = (info[0][i].length() == 0) ? "null" : "'"+info[0][i]+"'";
                        String query = "INSERT INTO familyHealthHistory VALUES (?, ?, ?, ?, ?)";
                        String[] params = { String.valueOf(ID), String.valueOf(manager.childID), info[1][i], info[2][i], info[0][i] };
                        char[] paramTypes = { 'i', 'i', 's', 'i', (info[0][i].length() == 0) ? 'n' : 's' };
                        c.update(query, params, paramTypes);
                    }
                    c.disconnect();
                }
            });
        }
        else {
            LinearLayout topContainer = (LinearLayout)layout.findViewById(R.id.famHealthHis_container);
            int length = 8;
            EditText[] txts = new EditText[length];
            CheckBox[] checks = new CheckBox[length];

            int index = -1;
            for(int i = 0; i < topContainer.getChildCount(); i++, index++) {
                if( topContainer.getChildAt(i) instanceof LinearLayout ) {
                    LinearLayout middleContainer = (LinearLayout)topContainer.getChildAt(i);
                    for(int j = 0; j < middleContainer.getChildCount(); j++) {
                        if(middleContainer.getChildAt(j) instanceof EditText)
                            txts[index] = (EditText)middleContainer.getChildAt(j);
                        else if(middleContainer.getChildAt(j) instanceof LinearLayout) {
                            LinearLayout lowerContainer = (LinearLayout)middleContainer.getChildAt(j);
                            for(int k = 0; k < lowerContainer.getChildCount(); k++) {
                                if((lowerContainer.getChildAt(k)) instanceof CheckBox)
                                    checks[index] = (CheckBox)lowerContainer.getChildAt(k);
                            } } } } }

            for(int i = 0; i < length; i++)
            {
                txts[i].setText(result.get("note")[i]);
                if(result.get("condition")[i].charAt(0) == '1') checks[i].setChecked(true);

                txts[i].setEnabled(false);
                checks[i].setEnabled(false);
            }
            submit.setVisibility(View.INVISIBLE);
        }

        c.disconnect();
        return layout;
    }
}