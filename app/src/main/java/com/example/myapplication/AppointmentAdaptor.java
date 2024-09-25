package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.Appointment;

import java.util.List;

public class AppointmentAdaptor extends BaseAdapter {
    private Context context;
    private List<Appointment> appointmentList;

    public AppointmentAdaptor(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        Appointment appointment = appointmentList.get(position);

        TextView title = convertView.findViewById(android.R.id.text1);
        TextView dateTime = convertView.findViewById(android.R.id.text2);

        title.setText(appointment.getTitle());
        dateTime.setText(appointment.getDate() + " " + appointment.getTime());

        return convertView;
    }
}
