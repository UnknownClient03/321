package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.res.Resources;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;
import java.util.ArrayList;

public class Fragment_HearingPreScreening extends Fragment {

    private SQLConnection dbHelper;
    private int childID;
    private int guardianID;

    // List of questions
    private ArrayList<String> questionsList;

    // List of RadioGroups corresponding to each question
    private ArrayList<RadioGroup> radioGroupsList;

    // Save Button
    private Button saveButton;

    // Colors for enabled and disabled text
    private int enabledTextColor;
    private int disabledTextColor;

    public Fragment_HearingPreScreening() {
        // Required empty public constructor
    }

    public static Fragment_HearingPreScreening newInstance() {
        return new Fragment_HearingPreScreening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_hearing_prescreening, container, false);

        // Initialize SQL connection
        dbHelper = new SQLConnection(); // Ensure secure initialization
        Log.d("Fragment_HearingPreScreening", "SQLConnection initialized");

        // Initialize colors
        Resources res = getResources();
        enabledTextColor = res.getColor(R.color.enabled_text);
        disabledTextColor = res.getColor(R.color.disabled_text);

        // Get childID and guardianID from bundle
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1);
            guardianID = args.getInt("guardianID", -1);
            Log.d("Fragment_HearingPreScreening", "Received childID: " + childID);
            Log.d("Fragment_HearingPreScreening", "Received guardianID: " + guardianID);
        } else {
            Log.e("Fragment_HearingPreScreening", "No arguments passed to the fragment.");
            Toast.makeText(getActivity(), "Error: No child information provided.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize questions
        initializeQuestions();

        // Initialize RadioGroups
        initializeRadioGroups(view);

        // Ensure NewBornHearing record exists
        ensureNewBornHearingRecord(childID);

        // Load existing answers
        retrieveHearingPreScreening(childID);

        // Set up save button
        saveButton = view.findViewById(R.id.button_saveHearingPreScreening);
        saveButton.setOnClickListener(v -> saveHearingPreScreening());

        return view;
    }

    private void initializeQuestions() {
        questionsList = new ArrayList<>();
        questionsList.add("I have completed the health risk factor questions");
        questionsList.add("My baby had severe breathing problems at birth");
        questionsList.add("My baby had meningitis");
        questionsList.add("My baby had jaundice, requiring an exchange transfusion");
        questionsList.add("My baby was in intensive care for more than 5 days after birth");
        questionsList.add("I have noticed something unusual about my baby’s head or neck, such as an unusually shaped face, or skin tags");
        questionsList.add("My baby has Down Syndrome (Trisomy 21) or another condition associated with hearing loss");
    }

    private void initializeRadioGroups(View view) {
        radioGroupsList = new ArrayList<>();

        // Map each question to its RadioGroup
        for (int i = 1; i <= questionsList.size(); i++) {
            String radioGroupID = "radioGroup_question" + i;
            int resID = getResources().getIdentifier(radioGroupID, "id", getActivity().getPackageName());
            RadioGroup radioGroup = view.findViewById(resID);
            if (radioGroup != null) {
                radioGroupsList.add(radioGroup);
            } else {
                Log.e("Fragment_HearingPreScreening", "RadioGroup not found: " + radioGroupID);
            }
        }
    }

    private void ensureNewBornHearingRecord(int childID) {
        String checkQuery = "SELECT COUNT(*) AS count FROM NewBornHearing WHERE childID = ?";
        String[] checkParams = {String.valueOf(childID)};
        char[] checkParamTypes = {'i'};

        try {
            if (!dbHelper.isConn()) {
                Log.e("Fragment_HearingPreScreening", "Error: Unable to connect to the database.");
                Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;
            if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(checkResult.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("Fragment_HearingPreScreening", "Invalid count value: " + checkResult.get("count")[0]);
                }
            }

            if (recordCount == 0) {
                // Insert a default record into NewBornHearing
                String insertHearingQuery = "INSERT INTO NewBornHearing (childID, preScreeningVal, fname, lname, DOB, requiresrepeatScreen, hearingRiskFactorIdentified, coordinatorTelephone) " +
                        "VALUES (?, 'Normal', '', '', '1970-01-01', 0, 0, NULL)";
                String[] insertParams = {String.valueOf(childID)};
                char[] insertParamTypes = {'i'};

                boolean isInserted = dbHelper.update(insertHearingQuery, insertParams, insertParamTypes);
                if (isInserted) {
                    Log.d("Fragment_HearingPreScreening", "Inserted default NewBornHearing record for childID: " + childID);
                } else {
                    Log.e("Fragment_HearingPreScreening", "Failed to insert default NewBornHearing record for childID: " + childID);
                    Toast.makeText(getActivity(), "Error initializing hearing records.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("Fragment_HearingPreScreening", "NewBornHearing record already exists for childID: " + childID);
            }

        } catch (Exception e) {
            Log.e("Fragment_HearingPreScreening", "Error ensuring NewBornHearing record: " + e.getMessage());
        }
    }

    private void retrieveHearingPreScreening(int childID) {
        String query = "SELECT question, condition FROM HearingPreScreening WHERE childID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            if (!dbHelper.isConn()) {
                Log.e("Fragment_HearingPreScreening", "Error: Unable to connect to the database.");
                return;
            }

            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result == null || result.get("question") == null || result.get("question").length == 0) {
                Log.d("Fragment_HearingPreScreening", "No existing hearing pre-screening records found.");
                return;
            }

            String[] questions = result.get("question");
            String[] answers = result.get("condition");

            boolean hasData = false;

            for (int i = 0; i < questions.length; i++) {
                int questionIndex = questionsList.indexOf(questions[i]);
                if (questionIndex != -1 && questionIndex < radioGroupsList.size()) {
                    RadioGroup rg = radioGroupsList.get(questionIndex);
                    if (answers[i].equals("1")) {
                        // Select 'Yes'
                        selectRadioButton(rg, true);
                    } else {
                        // Select 'No'
                        selectRadioButton(rg, false);
                    }
                    hasData = true;
                }
            }

            if (hasData) {
                setFieldsEditable(false); // Make fields uneditable if data exists
            }

        } catch (Exception e) {
            Log.e("Fragment_HearingPreScreening", "Error retrieving hearing pre-screening details: " + e.getMessage());
        }
    }

    private void selectRadioButton(RadioGroup rg, boolean isYes) {
        if (isYes) {
            // Find and check the 'Yes' RadioButton
            for (int i = 0; i < rg.getChildCount(); i++) {
                View child = rg.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (rb.getText().toString().equalsIgnoreCase("Yes")) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        } else {
            // Find and check the 'No' RadioButton
            for (int i = 0; i < rg.getChildCount(); i++) {
                View child = rg.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (rb.getText().toString().equalsIgnoreCase("No")) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        }
    }

    private void saveHearingPreScreening() {
        try {
            if (!dbHelper.isConn()) {
                Log.e("Fragment_HearingPreScreening", "Error: Unable to connect to the database.");
                Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean allAnswered = true;
            ArrayList<Boolean> answersList = new ArrayList<>();

            // Gather answers
            for (RadioGroup rg : radioGroupsList) {
                int selectedId = rg.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = rg.findViewById(selectedId);
                if (selectedRadioButton == null) {
                    allAnswered = false;
                    break;
                }
                String answerText = selectedRadioButton.getText().toString().trim();
                if (answerText.equalsIgnoreCase("Yes")) {
                    answersList.add(true);
                } else if (answerText.equalsIgnoreCase("No")) {
                    answersList.add(false);
                } else {
                    answersList.add(false); // Default to 'No' if unexpected value
                }
            }

            if (!allAnswered) {
                Toast.makeText(getActivity(), "Please answer all questions before saving.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Iterate through questions and answers to insert/update
            for (int i = 0; i < questionsList.size(); i++) {
                String question = questionsList.get(i);
                boolean answer = answersList.get(i) ? true : false;

                // Check if an entry already exists for this question and childID
                String checkQuery = "SELECT COUNT(*) AS count FROM HearingPreScreening WHERE childID = ? AND question = ?";
                String[] checkParams = {String.valueOf(childID), question};
                char[] checkParamTypes = {'i', 's'};

                HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);
                int recordCount = 0;
                if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                    try {
                        recordCount = Integer.parseInt(checkResult.get("count")[0]);
                    } catch (NumberFormatException e) {
                        Log.e("Fragment_HearingPreScreening", "Invalid count value: " + checkResult.get("count")[0]);
                    }
                }

                if (recordCount > 0) {
                    // Update existing record
                    String updateQuery = "UPDATE HearingPreScreening SET condition = ? WHERE childID = ? AND question = ?";
                    String[] updateParams = {answer ? "1" : "0", String.valueOf(childID), question};
                    char[] updateParamTypes = {'i', 'i', 's'};

                    boolean isUpdated = dbHelper.update(updateQuery, updateParams, updateParamTypes);
                    if (!isUpdated) {
                        Log.e("Fragment_HearingPreScreening", "Failed to update answer for question: " + question);
                        Toast.makeText(getActivity(), "Failed to update some answers.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Insert new record
                    String insertQuery = "INSERT INTO HearingPreScreening (childID, question, condition) VALUES (?, ?, ?)";
                    String[] insertParams = {String.valueOf(childID), question, answer ? "1" : "0"};
                    char[] insertParamTypes = {'i', 's', 'i'};

                    boolean isInserted = dbHelper.update(insertQuery, insertParams, insertParamTypes);
                    if (!isInserted) {
                        Log.e("Fragment_HearingPreScreening", "Failed to insert answer for question: " + question);
                        Toast.makeText(getActivity(), "Failed to save some answers.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            Toast.makeText(getActivity(), "Hearing PreScreening saved successfully", Toast.LENGTH_SHORT).show();
            setFieldsEditable(false); // Make fields uneditable after saving

        } catch (Exception e) {
            Log.e("Fragment_HearingPreScreening", "Error saving hearing pre-screening: " + e.getMessage());
            Toast.makeText(getActivity(), "Error saving Hearing PreScreening", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the RadioGroups and Save button as editable or non-editable.
     * Also updates the visual indicators (text color) based on the state.
     * @param editable true to make fields editable, false to make them non-editable
     */
    private void setFieldsEditable(boolean editable) {
        for (RadioGroup rg : radioGroupsList) {
            rg.setEnabled(editable);
            // Additionally, disable all child RadioButtons and set text color
            for (int i = 0; i < rg.getChildCount(); i++) {
                View child = rg.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    rb.setEnabled(editable);
                    // Set text color based on editable state
                    if (editable) {
                        rb.setTextColor(enabledTextColor);
                    } else {
                        rb.setTextColor(disabledTextColor);
                    }
                }
            }
        }

        if (saveButton != null) {
            saveButton.setEnabled(editable);
            // Optionally, hide the save button when not editable
            // Uncomment the following line if you prefer hiding the button
            // saveButton.setVisibility(editable ? View.VISIBLE : View.GONE);

            // Alternatively, keep the button visible but disabled
            saveButton.setAlpha(editable ? 1.0f : 0.5f); // Dim the button when disabled
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.disconnect();
        }
    }
}