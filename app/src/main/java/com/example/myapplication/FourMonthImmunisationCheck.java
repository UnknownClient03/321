package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class FourMonthImmunisationCheck extends AppCompatActivity {

    private RadioGroup[] radioGroups;
    private RadioButton[][] radioButtons;
    private Button submitButton;
    public LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_four_month_immunisation_check);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavBarManager.setNavBarButtons(FourMonthImmunisationCheck.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));

        // Initialize arrays
        radioGroups = new RadioGroup[18];
        radioButtons = new RadioButton[18][2];

        // Find RadioGroups and RadioButtons
        for (int i = 0; i < 18; i++) {
            int groupId = getResources().getIdentifier("radioGroup" + (i + 1), "id", getPackageName());
            int yesId = getResources().getIdentifier("yesRadioButton" + (i + 1), "id", getPackageName());
            int noId = getResources().getIdentifier("noRadioButton" + (i + 1), "id", getPackageName());

            radioGroups[i] = findViewById(groupId);
            radioButtons[i][0] = findViewById(yesId);
            radioButtons[i][1] = findViewById(noId);
        }

        submitButton = findViewById(R.id.button_4_month_immunisation_check_submit);

        loadExistingAnswers();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswers();
            }
        });
    }

    private void loadExistingAnswers() {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        if (sqlConnection.isConn()) {
            String query = "SELECT question, answer FROM FourMonthImmunisation WHERE childID = ?";
            String[] params = { String.valueOf(manager.childID) };
            char[] paramTypes = { 'i' }; // 'i' for integer

            HashMap<String, String[]> results = sqlConnection.select(query, params, paramTypes);

            // Set answers based on query results
            for (int i = 0; i < 18; i++) {
                // Ensure radio buttons are unselected initially
                radioButtons[i][0].setChecked(false);
                radioButtons[i][1].setChecked(false);
            }

            for (int i = 0; i < results.get("question").length; i++) {
                String question = results.get("question")[i];
                String answerStr = results.get("answer")[i];
                boolean answer = answerStr.equals("1");

                // Assuming question format is "Question X" where X is a number
                int questionIndex = extractQuestionIndex(question);

                if (questionIndex >= 0 && questionIndex < 18) {
                    if (answer) radioButtons[questionIndex][0].setChecked(true);
                    else radioButtons[questionIndex][1].setChecked(true);
                }
            }

            sqlConnection.disconnect();
        }
    }

    private int extractQuestionIndex(String question) {
        try {
            // Extract the number after "Question " in the format "Question X"
            return Integer.parseInt(question.replaceAll("Question ", "")) - 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1; // Return -1 if the format is incorrect
        }
    }

    private void submitAnswers() {
        // Check if all questions are answered
        boolean allAnswered = true;
        for (int i = 0; i < 18; i++) {
            if (radioGroups[i].getCheckedRadioButtonId() == -1) {
                allAnswered = false;
                break;
            }
        }

        if (!allAnswered) {
            // Show a Toast message if not all questions are answered
            Toast.makeText(this, "Please answer all questions before submitting.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with submitting answers if all questions are answered
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        if (sqlConnection.isConn()) {
            // Define the SQL queries for upserts using MERGE
            String query = "MERGE INTO FourMonthImmunisation AS target " +
                    "USING (SELECT ? AS childID, ? AS question, ? AS answer) AS source " +
                    "ON (target.childID = source.childID AND target.question = source.question) " +
                    "WHEN MATCHED THEN " +
                    "    UPDATE SET answer = source.answer " +
                    "WHEN NOT MATCHED THEN " +
                    "    INSERT (childID, question, answer) VALUES (source.childID, source.question, source.answer);";

            // Execute the updates
            for (int i = 0; i < 18; i++) {
                String question = "Question " + (i + 1); // Replace with actual question text if needed
                boolean answer = radioButtons[i][0].isChecked();

                String[] params = {String.valueOf(manager.childID), question, String.valueOf(answer)};
                char[] paramTypes = {'i', 's', 'b'}; // 'b' for boolean

                sqlConnection.update(query, params, paramTypes);
            }

            sqlConnection.disconnect();

            // Show a Toast message and go back
            Toast.makeText(this, "Answers submitted", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the previous page
        }
    }
}