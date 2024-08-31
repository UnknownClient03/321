package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsefulContactsDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsefulContactsDisplay extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsefulContactsDisplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsefulContactsDisplay.
     */
    // TODO: Rename and change types and number of parameters
    public static UsefulContactsDisplay newInstance(String param1, String param2) {
        UsefulContactsDisplay fragment = new UsefulContactsDisplay();
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
        View layout = inflater.inflate(R.layout.useful_contacts_display, container, false);
        LoginManager manager = ((UsefulContacts)getActivity()).manager;

        Button button = layout.findViewById(R.id.button_editUsefulContacts);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView3, new UsefulContactsChange())
                        .addToBackStack(null)
                        .commit();
            }
        });

        SQLConnection c = new SQLConnection("user1", "");
        if(c.isConn())
        {
            String query = "SELECT name, phoneNumber, email, Country, City, Street, StreetNumber, unit, postcode FROM UsefulContact WHERE guardianID = ?;";
            HashMap<String, String[]> result = c.select(query, new String[]{String.valueOf(manager.guardianID)}, new char[]{'i'});
            TableLayout table = layout.findViewById(R.id.Table_UsefulContacts);
            for(int i = 0; i < result.get("name").length; i++)
                if(table.getChildAt(i) instanceof TableRow)
                {
                    int tableRowIndex = 0;
                    switch(result.get("name")[i])
                    {
                        case "Child and Family Health Centre": tableRowIndex = 1; break;
                        case "Community Health Centre": tableRowIndex = 2; break;
                        case "Dentist":tableRowIndex = 3; break;
                        case "Family daycare/Childcare centre": tableRowIndex = 4; break;
                        case "Family doctor": tableRowIndex = 5; break;
                        case "High school": tableRowIndex = 6; break;
                        case "Local government/Council": tableRowIndex = 7; break;
                        case "Pre-school/Kindergarten": tableRowIndex = 8; break;
                        case "Primary school": tableRowIndex = 9; break;
                        case "Specialist doctor": tableRowIndex = 10;
                    }
                    TableRow tableRow = (TableRow)table.getChildAt(tableRowIndex);
                    TextView col2 = (TextView)tableRow.getChildAt(2);
                    String txt2 = ((result.get("phoneNumber")[i] == null) ? "" : "PhoneNumber: " + result.get("phoneNumber")[i])
                            + ((result.get("email")[i] == null) ? "" : "\nEmail: " + result.get("email")[i]);
                    col2.setText(txt2);

                    TextView col1 = (TextView)tableRow.getChildAt(1);
                    String txt1 = result.get("Country")[i] + ", "
                            + result.get("City")[i] + ", "
                            + result.get("Street")[i] + ", "
                            + result.get("StreetNumber")[i] + ", "
                            + ((result.get("unit")[i] == null) ? "" : result.get("unit")[i] + ", ")
                            + result.get("postcode")[i];
                    col1.setText(txt1);
                }
            c.disconnect();
        }
        return layout;
    }
}
