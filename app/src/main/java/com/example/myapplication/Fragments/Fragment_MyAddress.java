package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginManager;
import com.example.myapplication.MyInfoAndFamHis;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_MyAddress#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_MyAddress extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_MyAddress() {
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
    public static Fragment_MyAddress newInstance(String param1, String param2) {
        Fragment_MyAddress fragment = new Fragment_MyAddress();
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
        View layout = inflater.inflate(R.layout.fragment_myaddress, container, false);
        Button submit = (Button)layout.findViewById(R.id.button_myAddress);
        if(submit == null) throw new NullPointerException("could not find button: " + R.id.button_myAddress);
        else submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_3);
                button.performClick();
                EditText country = (EditText)layout.findViewById(R.id.input_address_country);
                EditText city = (EditText)layout.findViewById(R.id.input_address_city);
                EditText street = (EditText)layout.findViewById(R.id.input_address_street);
                EditText streetNumber = (EditText)layout.findViewById(R.id.input_address_streetNumber);
                EditText rawUnit = (EditText)layout.findViewById(R.id.input_address_unit);
                EditText postcode = (EditText)layout.findViewById(R.id.input_address_postcode);

                if(streetNumber.getText().length() == 0 || postcode.getText().length() == 0)
                {
                    Log.e("query syntax error", "integers need to be set");
                    return;
                }

                String unit = (rawUnit.getText().length() == 0) ? "null" : "'"+rawUnit.getText()+"'";
                SQLConnection c = new SQLConnection("user1", "");
                LoginManager manager = ((MyInfoAndFamHis)getActivity()).manager;
                String query = "INSERT INTO Address VALUES (" + manager.guardianID + ", '"
                                                              + country.getText() + "', '"
                                                              + city.getText() + "', '"
                                                              + street.getText() + "', "
                                                              + streetNumber.getText() + ", "
                                                              + unit + ", "
                                                              + postcode.getText() + ");";
                c.update(query);
                c.disconnect();
            }
        });
        return layout;
    }
}