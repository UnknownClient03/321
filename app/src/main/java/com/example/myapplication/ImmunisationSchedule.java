package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ImmunisationSchedule extends AppCompatActivity {

    private ListView listViewAppointments;
    private ListView listViewImmunisations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunisation_schedule);

        listViewAppointments = findViewById(R.id.listViewAppointments);
        listViewImmunisations = findViewById(R.id.listViewImmunisations);

        List<Appointment> appointments = new ArrayList<>();
        // Add sample data
        appointments.add(new Appointment("2024-08-15", "10:00 AM", "Routine Check-up"));
        appointments.add(new Appointment("2024-08-30", "02:00 PM", "Dental Visit"));

        List<Immunisation> immunisations = new ArrayList<>();
        // Add sample data
        immunisations.add(new Immunisation("MMR Vaccine", "2024-09-15"));
        immunisations.add(new Immunisation("Flu Vaccine", "2024-10-01"));

        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this, appointments);
        listViewAppointments.setAdapter(appointmentAdapter);

        ImmunisationAdapter immunisationAdapter = new ImmunisationAdapter(this, immunisations);
        listViewImmunisations.setAdapter(immunisationAdapter);
    }

}
