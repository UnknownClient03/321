package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginManager;
import com.example.myapplication.MyInfoAndFamHis;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

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
        View layout = inflater.inflate(R.layout.fragment_healthhistory, container, false);
        Button submit = (Button)layout.findViewById(R.id.button_healthHistory);
        if(submit == null) throw new NullPointerException("could not find button: " + R.id.button_healthHistory);
        else submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_7);
                button.performClick();

                String [][] info = new String[3][10];
                LinearLayout container = (LinearLayout)layout.findViewById(R.id.famHealthHis_container);
                int index = -1;
                for(int i = 0; i < container.getChildCount(); i++, index++) {
                    if( container.getChildAt(i) instanceof LinearLayout ) {
                        LinearLayout middleContainer = (LinearLayout)container.getChildAt(i);
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
                int ID = c.getMaxID("familyHealthHistory")-1;
                LoginManager manager = ((MyInfoAndFamHis)getActivity()).manager;
                for(int i = 0; i < index-1; i++, ID++)
                {
                    String query = "INSERT INTO familyHealthHistory VALUES (" + ID + ", "
                                                                              + manager.childID + ", '"
                                                                              + info[1][i] + "', "
                                                                              + info[2][i]+", '"
                                                                              + info[0][i]+"');";
                    c.update(query);
                }
                c.disconnect();
            }
        });
        return layout;
    }
}