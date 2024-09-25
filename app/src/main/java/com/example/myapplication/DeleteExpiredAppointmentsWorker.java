package com.example.myapplication;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeleteExpiredAppointmentsWorker extends Worker {

    public DeleteExpiredAppointmentsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Create a SQLConnection instance
        SQLConnection sqlConnection = new SQLConnection("user1", "strongPwd123");

        // Update the SQL DELETE statement to use GETDATE() for SQL Server
        String deleteSQL = "DELETE FROM Appointments WHERE appointment_date < GETDATE()";

        try {
            // Call the update method to execute the delete query
            if (sqlConnection.update(deleteSQL)) {
                // Successful deletion
                return Result.success();
            } else {
                // Deletion failed
                return Result.failure();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        } finally {
            sqlConnection.disconnect(); // Always disconnect after operations
        }
    }
}
