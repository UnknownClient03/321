package com.example.myapplication.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapplication.LoginManager;
import com.example.myapplication.ProgressNotes;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_ProgressNotes_Read#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_ProgressNotes_Read extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_ProgressNotes_Read() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_ProgressNotes_Read.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_ProgressNotes_Read newInstance(String param1, String param2) {
        Fragment_ProgressNotes_Read fragment = new Fragment_ProgressNotes_Read();
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
        View layout = inflater.inflate(R.layout.fragment__progress_notes__read, container, false);

        Button writeEntryBut = (Button)layout.findViewById(R.id.button_PN_addAnotherEntry);
        writeEntryBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new Fragment_ProgressNotes_Write())
                        .addToBackStack(null)
                        .commit();
            }
        });

        SQLConnection c = new SQLConnection("user1", "");
        if(c.isConn())
        {
            LoginManager manager = ((ProgressNotes)getActivity()).manager;
            String query = "SELECT date, age, reason FROM ProgressNotes WHERE childID = ?;";
            HashMap<String, String[]> result = c.select(query, new String[]{String.valueOf(manager.childID)}, new char[]{'i'});
            TableLayout table = (TableLayout)layout.findViewById(R.id.Table_ProgressNotes);
            for(int i = 0; i < result.get("date").length; i++)
            {
                TableRow newRow = new TableRow(this.getContext());
                TableRow.LayoutParams thparams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                thparams.setMargins(0, 0, 0, 0);
                thparams.weight = 1;
                newRow.setLayoutParams(thparams);
                newRow.setBackgroundColor(Color.BLACK);

                FrameLayout newFL0 = createframeLayout(0);
                FrameLayout newFL1 = createframeLayout(1);
                FrameLayout newFL2 = createframeLayout(2);

                TextView newText0 = new TextView(this.getContext());
                newText0.setText(result.get("date")[i]);


                TextView newText1 = new TextView(this.getContext());
                newText1.setText(result.get("age")[i]);

                TextView newText2 = new TextView(this.getContext());
                newText2.setText(result.get("reason")[i]);

                newFL0.addView(newText0);
                newFL1.addView(newText1);
                newFL2.addView(newText2);
                newRow.addView(newFL0);
                newRow.addView(newFL1);
                newRow.addView(newFL2);
                table.addView(newRow);
            }
            c.disconnect();
        }
        return layout;
    }

    private FrameLayout createframeLayout(int column)
    {
        FrameLayout newFL = new FrameLayout(this.requireContext());
        TableRow.LayoutParams FLparams = new TableRow.LayoutParams(column);
        FLparams.width = 0;
        FLparams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        FLparams.setMargins(3, 3, 3, 3);
        newFL.setLayoutParams(FLparams);
        newFL.setBackgroundColor(Color.WHITE);

        return newFL;
    }
}