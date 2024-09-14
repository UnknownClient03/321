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
import com.example.myapplication.Immunisation;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

public class Fragment_Immunisation_Read extends Fragment {

    public Fragment_Immunisation_Read() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_immunisation_read, container, false);

        Button addEntryBut = layout.findViewById(R.id.button_IM_addAnotherEntry);
        addEntryBut.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new Fragment_Immunisation_Write())
                    .addToBackStack(null)
                    .commit();
            }
        });

        SQLConnection c = new SQLConnection("user1", "");
        if(c.isConn()) {
            LoginManager manager = ((Immunisation)getActivity()).manager;
            String query = "SELECT age, vaccine, dateGiven, batchNum FROM ImmunisationRecord WHERE childID = ? ORDER BY dateGiven ASC;";
            HashMap<String, String[]> result = c.select(query, new String[]{String.valueOf(manager.childID)}, new char[]{'i'});
            TableLayout table = layout.findViewById(R.id.Table_Immunisation);
            for(int i = 0; i < result.get("age").length; i++) {
                TableRow newRow = new TableRow(this.getContext());
                TableRow.LayoutParams thparams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                thparams.setMargins(0, 0, 0, 0);
                thparams.weight = 1;
                newRow.setLayoutParams(thparams);
                newRow.setBackgroundColor(Color.BLACK);

                FrameLayout newFL0 = createFrameLayout(0);
                FrameLayout newFL1 = createFrameLayout(1);
                FrameLayout newFL2 = createFrameLayout(2);
                FrameLayout newFL3 = createFrameLayout(3);

                TextView newText0 = new TextView(this.getContext());
                newText0.setText(result.get("age")[i]);

                TextView newText1 = new TextView(this.getContext());
                newText1.setText(result.get("vaccine")[i]);

                TextView newText2 = new TextView(this.getContext());
                newText2.setText(result.get("dateGiven")[i]);

                TextView newText3 = new TextView(this.getContext());
                newText3.setText(result.get("batchNum")[i]);

                newFL0.addView(newText0);
                newFL1.addView(newText1);
                newFL2.addView(newText2);
                newFL3.addView(newText3);

                newRow.addView(newFL0);
                newRow.addView(newFL1);
                newRow.addView(newFL2);
                newRow.addView(newFL3);

                table.addView(newRow);
            }
            c.disconnect();
        }
        return layout;
    }

    private FrameLayout createFrameLayout(int column) {
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
