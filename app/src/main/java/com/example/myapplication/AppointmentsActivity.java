package com.example.myapplication;

import static com.example.myapplication.NotificationHelper.CHANNEL_ID;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.Manifest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class AppointmentsActivity extends AppCompatActivity {
    private List<Appointment> appointmentsList = new ArrayList<>();
    private ListView listView;
    private AppointmentAdaptor adapter;
    private SQLConnection sqlConnection;
    private int childID; // Store childID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Retrieve childID from Intent (assuming you pass it from another Activity)
        childID = getIntent().getIntExtra("childID", -1);

        // Check for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        listView = findViewById(R.id.appointmentsListView);
        adapter = new AppointmentAdaptor(this, appointmentsList);
        listView.setAdapter(adapter);

        Button addAppointmentButton = findViewById(R.id.addAppointmentButton);
        addAppointmentButton.setOnClickListener(v -> promptForAppointmentTitle());

        sqlConnection = new SQLConnection("user1", ""); // Use your credentials
        if (sqlConnection.isConn()) {
            fetchAppointments(); // Fetch appointments from SQL Server when activity starts
        } else {
            Toast.makeText(this, "Database connection failed.", Toast.LENGTH_SHORT).show();
        }

        // Ensure the notification channel is created
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createChannels();
        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(AppointmentsActivity.this, new LoginManager(extras.getInt("guardianID"), extras.getInt("childID")));

        // Delete appointments that have occurred in a previous day
        PeriodicWorkRequest deleteExpiredAppointmentsRequest =
                new PeriodicWorkRequest.Builder(DeleteExpiredAppointmentsWorker.class, 1, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueue(deleteExpiredAppointmentsRequest);
    }

    private void promptForAppointmentTitle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Appointment Title");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (!title.isEmpty()) {
                openDateTimePicker(title); // Open date and time picker with the title
            } else {
                Toast.makeText(this, "Please enter a title for the appointment", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openDateTimePicker(String title) {
        Calendar calendar = Calendar.getInstance();

        // Date Picker
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;

            // Time Picker
            TimePickerDialog timePicker = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                addAppointment(title, date, time); // Use the title here
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void addAppointment(String title, String date, String time) {
        if (sqlConnection.isConn()) {
            String query = "INSERT INTO Appointments (childID, title, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement statement = sqlConnection.getConnection().prepareStatement(query);
                statement.setInt(1, childID); // Set the childID
                statement.setString(2, title);

                // Convert date from "day/month/year" to "YYYY-MM-DD"
                String[] dateParts = date.split("/");
                String formattedDate = String.format("%s-%s-%s", dateParts[2], dateParts[1].length() == 1 ? "0" + dateParts[1] : dateParts[1], dateParts[0].length() == 1 ? "0" + dateParts[0] : dateParts[0]);
                statement.setString(3, formattedDate);
                statement.setString(4, time);
                statement.executeUpdate();

                appointmentsList.add(new Appointment(0, title, formattedDate, time));
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Appointment added!", Toast.LENGTH_SHORT).show();

                // Schedule the notification
                scheduleNotification(title, formattedDate, time);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to add appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Database connection failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAppointments() {
        if (sqlConnection.isConn()) {
            String query = "SELECT appointment_id, title, appointment_date, appointment_time FROM Appointments WHERE childID = ? ORDER BY appointment_date ASC, appointment_time ASC";
            try {
                PreparedStatement statement = sqlConnection.getConnection().prepareStatement(query);
                statement.setInt(1, childID); // Set childID for fetching appointments
                ResultSet resultSet = statement.executeQuery();

                appointmentsList.clear();
                while (resultSet.next()) {
                    int id = resultSet.getInt("appointment_id");
                    String title = resultSet.getString("title");
                    String date = resultSet.getString("appointment_date");

                    // Get the time as a string
                    String time = resultSet.getString("appointment_time");

                    // Format the time to "HH:mm"
                    String formattedTime = formatTime(time);

                    appointmentsList.add(new Appointment(id, title, date, formattedTime));
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to fetch appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Database connection failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTime(String time) {
        // Assuming time is in the format "HH:mm:ss" or "HH:mm:ss.SSSSSSSS"
        String[] parts = time.split(":");
        return String.format("%s:%s", parts[0], parts[1]); // return "HH:mm"
    }

    private void scheduleNotification(String title, String date, String time) {
        Calendar calendar = Calendar.getInstance();
        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");

        // Set the calendar time to the appointment time
        calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0); // Reset seconds

        // If the set time is in the past, adjust to the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Subtract 1 hour (3600000 milliseconds) for the notification
        calendar.add(Calendar.HOUR, -1);

        // Format the appointment time for display
        String appointmentTime = String.format("%02d:%02d", Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", "Appointment Reminder");
        intent.putExtra("message", "You have an appointment in an hour at: " + appointmentTime + " - " + title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the SQL connection is closed when the activity is destroyed
        sqlConnection.disconnect(); // Close the connection properly
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
