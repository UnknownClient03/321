package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ImmunisationAdapter extends ArrayAdapter<Immunisation> {

    public ImmunisationAdapter(Context context, List<Immunisation> immunisations) {
        super(context, 0, immunisations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.immunisation_item, parent, false);
        }

        Immunisation immunisation = getItem(position);

        TextView textViewVaccineName = convertView.findViewById(R.id.textViewVaccineName);
        TextView textViewDateDue = convertView.findViewById(R.id.textViewDateDue);

        textViewVaccineName.setText(immunisation.getVaccineName());
        textViewDateDue.setText(immunisation.getDateDue());

        return convertView;
    }

}
