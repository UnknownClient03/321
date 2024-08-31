package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsefulContactsChange#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsefulContactsChange extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsefulContactsChange() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsefulContactsChange.
     */
    // TODO: Rename and change types and number of parameters
    public static UsefulContactsChange newInstance(String param1, String param2) {
        UsefulContactsChange fragment = new UsefulContactsChange();
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
        View layout = inflater.inflate(R.layout.useful_contacts_change, container, false);

        Button submit = (Button) layout.findViewById(R.id.button_submit_contact);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager manager = ((UsefulContacts)getActivity()).manager;
                UsefulContacts activity = ((UsefulContacts)getActivity());
                activity.getOnBackPressedDispatcher().onBackPressed();
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);


                EditText rawphone = (EditText)layout.findViewById(R.id.input_usefulContact_phone);
                EditText rawemail = (EditText)layout.findViewById(R.id.input_usefulContact_email);
                EditText country = (EditText)layout.findViewById(R.id.input_usefulContact_country);
                EditText city = (EditText)layout.findViewById(R.id.input_usefulContact_city);
                EditText street = (EditText)layout.findViewById(R.id.input_usefulContact_street);
                EditText streetNumber = (EditText)layout.findViewById(R.id.input_usefulContact_streetNumber);
                EditText rawUnit = (EditText)layout.findViewById(R.id.input_usefulContact_unit);
                EditText postcode = (EditText)layout.findViewById(R.id.input_usefulContact_postcode);

                RadioGroup group = (RadioGroup)layout.findViewById(R.id.radiogroup_usefulContacts);
                int i;
                for(i = 0; i < group.getChildCount(); i++)
                    if(group.getChildAt(i) instanceof RadioButton)
                    {
                        RadioButton radio = (RadioButton)group.getChildAt(i);
                        if(radio.isChecked()) break;
                    }

                String name = "";
                switch(i)
                {
                    case 0: name = "Child and Family Health Centre"; break;
                    case 1: name = "Community Health Centre"; break;
                    case 2: name = "Dentist"; break;
                    case 3: name = "Family daycare/Childcare centre"; break;
                    case 4: name = "Family doctor"; break;
                    case 5: name = "High school"; break;
                    case 6: name = "Local government/Council"; break;
                    case 7: name = "Pre-school/Kindergarten"; break;
                    case 8: name = "Primary school"; break;
                    case 9: name = "Specialist doctor";
                }

                String phone = (rawphone.getText().length() == 0) ? "NULL" : ""+rawphone.getText();
                String email = (rawemail.getText().length() == 0) ? "NULL" : "'" + rawemail.getText() + "'";
                String unit = (rawUnit.getText().length() == 0) ? "null" : "'"+rawUnit.getText()+"'";
                if(streetNumber.getText().length() == 0 || postcode.getText().length() == 0)
                {
                    Log.e("query syntax error", "integers need to be set");
                    return;
                }

                SQLConnection c = new SQLConnection("user1", "");
                if(c.isConn())
                {
                    String query = "SELECT name FROM UsefulContact WHERE guardianID = " + manager.guardianID + " AND name = '" + name + "';";
                    HashMap<String, String[]> result = c.select(query);
                    query = (result.get("name").length == 0) ? "INSERT INTO UsefulContact VALUES(" + manager.guardianID + ", '"
                            + name + "', "
                            + phone + ", "
                            + email + ", '"
                            + country.getText() + "', '"
                            + city.getText() + "', '"
                            + street.getText() + "', "
                            + streetNumber.getText() + ", "
                            + unit + ", "
                            + postcode.getText() + ");" :
                            "UPDATE UsefulContact SET phoneNumber = " + phone
                                    + ", email = " + email
                                    + ", Country = '" + country.getText()
                                    + "', City = '" + city.getText()
                                    + "', Street = '" + street.getText()
                                    + "', StreetNumber = '" + streetNumber.getText()
                                    + "', unit = " + unit + ", postcode = "
                                    + postcode.getText() + " WHERE guardianID = "
                                    + manager.guardianID + " AND name = '"
                                    + name + "';";
                    c.update(query);
                    c.disconnect();
                }
            }
        });


        return layout;
    }
}