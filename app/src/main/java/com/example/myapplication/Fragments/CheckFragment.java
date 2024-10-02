package com.example.myapplication.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Question;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckFragment extends Fragment {

    private static final String ARG_CHILD_ID = "childID";
    private static final String ARG_CHECK_TYPE = "checkType";

    private int childID;
    private String checkType;

    private SQLConnection dbHelper;

    // UI Components
    private EditText weightInput, lengthInput, headCircInput;
    private Spinner outcomeSpinner;
    private CheckBox healthInfoDiscussedCheckbox;
    private EditText commentsInput, actionTakenInput, doctorNameInput, signatureInput, venueInput, dateInput;
    private Button submitButton;
    private LinearLayout checksContainer;  // Container for dynamically added checks

    private List<Question> questionList;
    private HashMap<String, Spinner> spinnersMap;  // To hold the spinners for each check

    public CheckFragment() {
        // Required empty public constructor
    }

    public static CheckFragment newInstance(int childID, String checkType) {
        CheckFragment fragment = new CheckFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHILD_ID, childID);
        args.putString(ARG_CHECK_TYPE, checkType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize SQL connection
        dbHelper = new SQLConnection("user1", ""); // Replace with your actual password

        if (getArguments() != null) {
            childID = getArguments().getInt(ARG_CHILD_ID, 0); // Default to 0 if not set
            checkType = getArguments().getString(ARG_CHECK_TYPE, "").trim();
        }

        spinnersMap = new HashMap<>();  // Initialize map for spinners

        Log.d("CheckFragment", "Fragment created with childID: " + childID + ", checkType: " + checkType);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize UI components
        weightInput = view.findViewById(R.id.weight_input);
        lengthInput = view.findViewById(R.id.length_input);
        headCircInput = view.findViewById(R.id.head_circ_input);
        outcomeSpinner = view.findViewById(R.id.outcome_spinner);
        healthInfoDiscussedCheckbox = view.findViewById(R.id.health_info_discussed_checkbox);
        commentsInput = view.findViewById(R.id.comments_input);
        actionTakenInput = view.findViewById(R.id.action_taken_input);
        doctorNameInput = view.findViewById(R.id.doctor_name_input);
        signatureInput = view.findViewById(R.id.signature_input);
        venueInput = view.findViewById(R.id.venue_input);
        dateInput = view.findViewById(R.id.date_input);
        submitButton = view.findViewById(R.id.submit_button);
        checksContainer = view.findViewById(R.id.checks_container);  // This is the LinearLayout to hold specific checks

        // Load specific checks and auto-fill data if it exists
        if (childID >= 0 && !checkType.isEmpty()) { // Allow childID=0 as valid
            new LoadSpecificChecksTask().execute();
        } else {
            Log.e("CheckFragment", "Invalid childID or checkType. childID: " + childID + ", checkType: " + checkType);
            Toast.makeText(getContext(), "Invalid child information. Please try again.", Toast.LENGTH_LONG).show();
        }

        // Set up submit button listener
        submitButton.setOnClickListener(v -> {
            if (childID >= 0 && !checkType.isEmpty()) { // Allow childID=0 as valid
                new SubmitCheckTask().execute();
            } else {
                Toast.makeText(getContext(), "Invalid child information. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class LoadSpecificChecksTask extends AsyncTask<Void, Void, Boolean> {

        private HashMap<String, String> assessmentData;

        @Override
        protected Boolean doInBackground(Void... voids) {
            assessmentData = new HashMap<>();
            questionList = new ArrayList<>(); // Ensure questionList is initialized

            // Query to fetch assessment data from ChildCheckAssessment
            String query = "SELECT weight, length, headCirc, outcome, healthInfoDiscussed " +
                    "FROM ChildCheckAssessment WHERE childID = ? AND checkType = ?";
            String[] params = {String.valueOf(childID), checkType};
            char[] paramTypes = {'i', 's'};
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result != null && result.get("weight") != null && result.get("weight").length > 0) {
                assessmentData.put("weight", result.get("weight")[0]);
                assessmentData.put("length", result.get("length")[0]);
                assessmentData.put("headCirc", result.get("headCirc")[0]);
                assessmentData.put("outcome", result.get("outcome")[0]);
                assessmentData.put("healthInfoDiscussed", result.get("healthInfoDiscussed")[0]);
            }

            // Query to fetch signage data from ChildCheckSignage
            String signageQuery = "SELECT comments, actionTaken, nameOfDoctor, signature, venue, date " +
                    "FROM ChildCheckSignage WHERE childID = ? AND checkType = ?";
            HashMap<String, String[]> signageResult = dbHelper.select(signageQuery, params, paramTypes);

            if (signageResult != null && signageResult.get("comments") != null && signageResult.get("comments").length > 0) {
                assessmentData.put("comments", signageResult.get("comments")[0]);
                assessmentData.put("actionTaken", signageResult.get("actionTaken")[0]);
                assessmentData.put("nameOfDoctor", signageResult.get("nameOfDoctor")[0]);
                assessmentData.put("signature", signageResult.get("signature")[0]);
                assessmentData.put("venue", signageResult.get("venue")[0]);
                assessmentData.put("date", signageResult.get("date")[0]);
            }

            // Query to fetch specific checks from ChildCheckAssessmentVariables
            String checksQuery = "SELECT item, status FROM ChildCheckAssessmentVariables WHERE childID = ? AND checkType = ?";
            HashMap<String, String[]> checksResult = dbHelper.select(checksQuery, params, paramTypes);

            if (checksResult != null && checksResult.get("item") != null) {
                String[] items = checksResult.get("item");
                String[] statuses = checksResult.get("status");

                for (int i = 0; i < items.length; i++) {
                    String status = statuses[i].toLowerCase();
                    questionList.add(new Question(items[i], false, status)); // Add checks to the list
                }
            }

            // If no specific questions were found in the database, load the predefined checks
            if (questionList.size() == 0) {
                questionList = getPredefinedChecksForCheckType(checkType);  // Load predefined checks
            }

            return !assessmentData.isEmpty() || !questionList.isEmpty(); // Return true if either data or checks exist
        }

        @Override
        protected void onPostExecute(Boolean dataExists) {
            if (dataExists) {
                // Autofill the form with data
                weightInput.setText(assessmentData.get("weight"));
                lengthInput.setText(assessmentData.get("length"));
                headCircInput.setText(assessmentData.get("headCirc"));
                commentsInput.setText(assessmentData.get("comments"));
                actionTakenInput.setText(assessmentData.get("actionTaken"));
                doctorNameInput.setText(assessmentData.get("nameOfDoctor"));
                signatureInput.setText(assessmentData.get("signature"));
                venueInput.setText(assessmentData.get("venue"));
                dateInput.setText(assessmentData.get("date"));

                // Update outcome spinner and checkbox
                String outcome = assessmentData.get("outcome");
                if (outcome != null) {
                    setSpinnerSelection(outcomeSpinner, outcome);
                }

                // Set checkbox state, handle potential null values
                healthInfoDiscussedCheckbox.setChecked("1".equals(assessmentData.get("healthInfoDiscussed")));

                // Dynamically add the specific checks to the LinearLayout
                checksContainer.removeAllViews();  // Clear any existing views
                if (questionList != null && !questionList.isEmpty()) {
                    for (Question question : questionList) {
                        addCheckWithSpinnerToLayout(question.getQuestionText(), question.getStatus());
                    }
                }

            } else {
                Toast.makeText(getContext(), "No previous data found.", Toast.LENGTH_SHORT).show();
            }
        }

        private void addCheckWithSpinnerToLayout(String questionText, String currentStatus) {
            // Create a new TextView for the question
            TextView questionView = new TextView(getContext());
            questionView.setText(questionText);  // Set the question text
            checksContainer.addView(questionView);  // Add the TextView to the LinearLayout

            // Create a Spinner for the status selection (Normal, Review, Refer)
            Spinner statusSpinner = new Spinner(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"Normal", "Review", "Refer"});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(adapter);

            // Set the initial selection to the current status
            setSpinnerSelection(statusSpinner, currentStatus);
            checksContainer.addView(statusSpinner);  // Add the Spinner to the LinearLayout

            // Add Spinner to the map to capture its value later
            spinnersMap.put(questionText, statusSpinner);
        }

        private void setSpinnerSelection(Spinner spinner, String value) {
            if (value == null) return;  // Avoid null values causing issues
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private class SubmitCheckTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // Validate required fields
            if (TextUtils.isEmpty(weightInput.getText()) ||
                    TextUtils.isEmpty(lengthInput.getText()) ||
                    TextUtils.isEmpty(headCircInput.getText()) ||
                    TextUtils.isEmpty(doctorNameInput.getText()) ||
                    TextUtils.isEmpty(signatureInput.getText()) ||
                    TextUtils.isEmpty(venueInput.getText()) ||
                    TextUtils.isEmpty(dateInput.getText())) {
                Log.e("CheckFragment", "Validation failed: Required fields are empty.");
                return false;
            }

            // Gather input data
            String weight = weightInput.getText().toString().trim();
            String length = lengthInput.getText().toString().trim();
            String headCirc = headCircInput.getText().toString().trim();
            String outcome = outcomeSpinner.getSelectedItem().toString().trim().toLowerCase(); // "normal", "review", "refer"
            boolean healthInfoDiscussed = healthInfoDiscussedCheckbox.isChecked();
            String comments = commentsInput.getText().toString().trim();
            String actionTaken = actionTakenInput.getText().toString().trim();
            String doctorName = doctorNameInput.getText().toString().trim();
            String signature = signatureInput.getText().toString().trim();
            String venue = venueInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();

            Log.d("CheckFragment", "Submitting check with childID: " + childID + ", checkType: " + checkType);

            // Ensure ChildCheck entry exists before inserting into ChildCheckAssessment
            if (!ensureChildCheckExists()) {
                Log.e("CheckFragment", "Failed to ensure ChildCheck exists.");
                return false;
            }

            // Insert or update ChildCheckAssessment
            boolean assessmentSuccess = insertOrUpdateChildCheckAssessment(weight, length, headCirc, outcome, healthInfoDiscussed);
            if (!assessmentSuccess) {
                return false;
            }

            // Insert or update specific checks (using spinnersMap)
            for (String question : spinnersMap.keySet()) {
                Spinner spinner = spinnersMap.get(question);
                String selectedStatus = spinner.getSelectedItem().toString().toLowerCase(); // Capture spinner value
                boolean variableSuccess = insertOrUpdateChildCheckAssessmentVariables(question, selectedStatus);
                if (!variableSuccess) {
                    return false;
                }
            }

            // Insert or update ChildCheckSignage
            boolean signageSuccess = insertOrUpdateChildCheckSignage(comments, actionTaken, doctorName, signature, venue, date);
            return signageSuccess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Check submitted successfully!", Toast.LENGTH_SHORT).show();
                // Navigate two fragments back to the Checks Menu Fragment
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getContext(), "Failed to submit check. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean insertOrUpdateChildCheckAssessmentVariables(String question, String status) {
            // Check if the record exists
            String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheckAssessmentVariables WHERE childID = ? AND checkType = ? AND item = ?";
            String[] checkParams = {String.valueOf(childID), checkType, question};
            char[] checkParamTypes = {'i', 's', 's'};

            HashMap<String, String[]> result = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;

            if (result != null && result.get("count") != null && result.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(result.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("CheckFragment", "Error parsing count from ChildCheckAssessmentVariables query.", e);
                    return false;
                }
            }

            if (recordCount > 0) {
                // Update existing record
                String updateQuery = "UPDATE ChildCheckAssessmentVariables SET status = ? WHERE childID = ? AND checkType = ? AND item = ?";
                String[] updateParams = {status, String.valueOf(childID), checkType, question};
                char[] updateParamTypes = {'s', 'i', 's', 's'};

                return dbHelper.update(updateQuery, updateParams, updateParamTypes);
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO ChildCheckAssessmentVariables (childID, checkType, item, status) VALUES (?, ?, ?, ?)";
                String[] insertParams = {String.valueOf(childID), checkType, question, status};
                char[] insertParamTypes = {'i', 's', 's', 's'};

                return dbHelper.update(insertQuery, insertParams, insertParamTypes);
            }
        }

        private boolean insertOrUpdateChildCheckAssessment(String weight, String length, String headCirc, String outcome, boolean healthInfoDiscussed) {
            // Check if ChildCheckAssessment exists
            String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheckAssessment WHERE childID = ? AND checkType = ?";
            String[] checkParams = {String.valueOf(childID), checkType};
            char[] checkParamTypes = {'i', 's'};

            HashMap<String, String[]> result = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;

            if (result != null && result.get("count") != null && result.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(result.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("CheckFragment", "Error parsing count from ChildCheckAssessment query.", e);
                    return false;
                }
            }

            if (recordCount > 0) {
                // Update existing record
                String updateQuery = "UPDATE ChildCheckAssessment SET weight = ?, length = ?, headCirc = ?, outcome = ?, healthInfoDiscussed = ? WHERE childID = ? AND checkType = ?";
                String[] updateParams = {
                        weight,
                        length,
                        headCirc,
                        outcome,
                        healthInfoDiscussed ? "1" : "0",
                        String.valueOf(childID),
                        checkType
                };
                char[] updateParamTypes = {'s', 's', 's', 's', 's', 'i', 's'};

                return dbHelper.update(updateQuery, updateParams, updateParamTypes);
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO ChildCheckAssessment (childID, checkType, childQuestionStatus, weight, length, headCirc, outcome, healthInfoDiscussed) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                String[] insertParams = {
                        String.valueOf(childID),
                        checkType,
                        "normal", // Assuming default childQuestionStatus as 'normal'
                        weight,
                        length,
                        headCirc,
                        outcome,
                        healthInfoDiscussed ? "1" : "0"
                };
                char[] insertParamTypes = {'i', 's', 's', 's', 's', 's', 's', 's'};

                return dbHelper.update(insertQuery, insertParams, insertParamTypes);
            }
        }

        private boolean insertOrUpdateChildCheckSignage(String comments, String actionTaken, String doctorName, String signature, String venue, String date) {
            // Check if ChildCheckSignage exists
            String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheckSignage WHERE childID = ? AND checkType = ?";
            String[] checkParams = {String.valueOf(childID), checkType};
            char[] checkParamTypes = {'i', 's'};

            HashMap<String, String[]> result = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;

            if (result != null && result.get("count") != null && result.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(result.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("CheckFragment", "Error parsing count from ChildCheckSignage query.", e);
                    return false;
                }
            }

            if (recordCount > 0) {
                // Update existing record
                String updateQuery = "UPDATE ChildCheckSignage SET comments = ?, actionTaken = ?, nameOfDoctor = ?, signature = ?, venue = ?, date = ? WHERE childID = ? AND checkType = ?";
                String[] updateParams = {
                        comments,
                        actionTaken,
                        doctorName,
                        signature,
                        venue,
                        date,
                        String.valueOf(childID),
                        checkType
                };
                char[] updateParamTypes = {'s', 's', 's', 's', 's', 's', 'i', 's'};

                return dbHelper.update(updateQuery, updateParams, updateParamTypes);
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO ChildCheckSignage (childID, checkType, comments, actionTaken, nameOfDoctor, signature, venue, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                String[] insertParams = {
                        String.valueOf(childID),
                        checkType,
                        comments,
                        actionTaken,
                        doctorName,
                        signature,
                        venue,
                        date
                };
                char[] insertParamTypes = {'i', 's', 's', 's', 's', 's', 's', 's'};

                return dbHelper.update(insertQuery, insertParams, insertParamTypes);
            }
        }
    }

    private boolean ensureChildCheckExists() {
        Log.d("CheckFragment", "Ensuring ChildCheck exists for childID: " + childID + ", checkType: " + checkType);

        // Check if an entry exists in the ChildCheck table for the current childID and checkType
        String checkChildQuery = "SELECT COUNT(*) AS count FROM ChildCheck WHERE childID = ? AND checkType = ?";
        String[] checkChildParams = {String.valueOf(childID), checkType};
        char[] checkChildParamTypes = {'i', 's'};

        HashMap<String, String[]> result = dbHelper.select(checkChildQuery, checkChildParams, checkChildParamTypes);
        int recordCount = 0;

        if (result != null && result.get("count") != null && result.get("count").length > 0) {
            try {
                recordCount = Integer.parseInt(result.get("count")[0]);
            } catch (NumberFormatException e) {
                Log.e("CheckFragment", "Error parsing count from ChildCheck query.", e);
                return false;
            }
        }

        if (recordCount > 0) {
            // Entry exists, no need to insert
            Log.d("CheckFragment", "ChildCheck entry exists.");
            return true;
        } else {
            // Fetch actual child details from Child table
            String fetchChildQuery = "SELECT DOB, sex, fname, lname FROM Child WHERE ID = ?";
            String[] fetchChildParams = {String.valueOf(childID)};
            char[] fetchChildParamTypes = {'i'};

            HashMap<String, String[]> childResult = dbHelper.select(fetchChildQuery, fetchChildParams, fetchChildParamTypes);
            if (childResult != null && childResult.get("DOB") != null && childResult.get("sex") != null
                    && childResult.get("fname") != null && childResult.get("lname") != null) {

                String dob = childResult.get("DOB")[0];
                String sex = childResult.get("sex")[0];
                String fname = childResult.get("fname")[0];
                String lname = childResult.get("lname")[0];

                String insertChildCheckQuery = "INSERT INTO ChildCheck (childID, checkType, DOB, sex, fname, lname) VALUES (?, ?, ?, ?, ?, ?)";
                String[] insertChildCheckParams = {
                        String.valueOf(childID),
                        checkType,
                        dob,
                        sex,
                        fname,
                        lname
                };
                char[] insertChildCheckParamTypes = {'i', 's', 's', 's', 's', 's'};

                return dbHelper.update(insertChildCheckQuery, insertChildCheckParams, insertChildCheckParamTypes);
            } else {
                Log.e("CheckFragment", "Failed to fetch child details from Child table.");
                return false;
            }
        }
    }

    private List<Question> getPredefinedChecksForCheckType(String checkType) {
        List<Question> predefinedChecks = new ArrayList<>();

        switch (checkType.toLowerCase()) {
            case "1-4 weeks":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("head circumference", true, "normal"));
                predefinedChecks.add(new Question("frontanelles", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - white pupil", true, "normal"));
                predefinedChecks.add(new Question("cardiovascular", true, "normal"));
                predefinedChecks.add(new Question("umbilicus", true, "normal"));
                predefinedChecks.add(new Question("femoral pulses", true, "normal"));
                predefinedChecks.add(new Question("hips - test for dislocation", true, "normal"));
                predefinedChecks.add(new Question("testes fully descended R/L", true, "normal"));
                predefinedChecks.add(new Question("genitalia", true, "normal"));
                predefinedChecks.add(new Question("anal region", true, "normal"));
                predefinedChecks.add(new Question("skin", true, "normal"));
                predefinedChecks.add(new Question("reflexes", true, "normal"));
                break;

            case "6-8 weeks":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("head circumference", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - fixation", true, "normal"));
                predefinedChecks.add(new Question("eyes - response to looking with one eye", true, "normal"));
                predefinedChecks.add(new Question("eyes - eye movements", true, "normal"));
                predefinedChecks.add(new Question("cardiovascular", true, "normal"));
                predefinedChecks.add(new Question("hips - test for dislocation", true, "normal"));
                predefinedChecks.add(new Question("testes fully descended R/L", true, "normal"));
                break;

            case "4 month":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("head circumference", true, "normal"));
                break;

            case "6 month":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("head circumference", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - fixation", true, "normal"));
                predefinedChecks.add(new Question("eyes - response to looking with one eye", true, "normal"));
                predefinedChecks.add(new Question("eyes - eye movements", true, "normal"));
                predefinedChecks.add(new Question("oral - visible plaque", true, "normal"));
                predefinedChecks.add(new Question("oral - white spot or carious lesions", true, "normal"));
                predefinedChecks.add(new Question("hips - test for dislocation", true, "normal"));
                predefinedChecks.add(new Question("testes fully descended R/L", true, "normal"));
                break;

            case "12 month":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("head circumference", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - fixation", true, "normal"));
                predefinedChecks.add(new Question("eyes - response to looking with one eye", true, "normal"));
                predefinedChecks.add(new Question("eyes - eye movements", true, "normal"));
                predefinedChecks.add(new Question("oral - visible plaque", true, "normal"));
                predefinedChecks.add(new Question("oral - bleeding gums", true, "normal"));
                predefinedChecks.add(new Question("oral - white spot or carious lesions", true, "normal"));
                predefinedChecks.add(new Question("evaluate gait", true, "normal"));
                predefinedChecks.add(new Question("testes fully descended R/L", true, "normal"));
                break;

            case "18 month":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("evaluate gait", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - fixation", true, "normal"));
                predefinedChecks.add(new Question("eyes - response to looking with one eye", true, "normal"));
                predefinedChecks.add(new Question("eyes - eye movements", true, "normal"));
                predefinedChecks.add(new Question("oral - visible plaque", true, "normal"));
                predefinedChecks.add(new Question("oral - bleeding gums", true, "normal"));
                predefinedChecks.add(new Question("oral - white spot or carious lesions", true, "normal"));
                break;

            case "2 year":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("BMI", true, "normal"));
                predefinedChecks.add(new Question("evaluate gait", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - fixation", true, "normal"));
                predefinedChecks.add(new Question("eyes - response to looking with one eye", true, "normal"));
                predefinedChecks.add(new Question("eyes - eye movements", true, "normal"));
                predefinedChecks.add(new Question("oral - visible plaque", true, "normal"));
                predefinedChecks.add(new Question("oral - bleeding gums", true, "normal"));
                predefinedChecks.add(new Question("oral - white spot or carious lesions", true, "normal"));
                predefinedChecks.add(new Question("oral - facial swelling", true, "normal"));
                break;

            case "3 year":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("BMI", true, "normal"));
                predefinedChecks.add(new Question("eyes - observation", true, "normal"));
                predefinedChecks.add(new Question("eyes - corneal light reflection", true, "normal"));
                predefinedChecks.add(new Question("eyes - fixation", true, "normal"));
                predefinedChecks.add(new Question("eyes - response to looking with one eye", true, "normal"));
                predefinedChecks.add(new Question("eyes - eye movements", true, "normal"));
                predefinedChecks.add(new Question("oral - visible plaque", true, "normal"));
                predefinedChecks.add(new Question("oral - bleeding gums", true, "normal"));
                predefinedChecks.add(new Question("oral - white spot or carious lesions", true, "normal"));
                predefinedChecks.add(new Question("oral - facial swelling", true, "normal"));
                break;

            case "4 year":
                predefinedChecks.add(new Question("weight", true, "normal"));
                predefinedChecks.add(new Question("length/height", true, "normal"));
                predefinedChecks.add(new Question("BMI", true, "normal"));
                predefinedChecks.add(new Question("eyes", true, "normal"));
                predefinedChecks.add(new Question("oral - visible plaque", true, "normal"));
                predefinedChecks.add(new Question("oral - bleeding gums", true, "normal"));
                predefinedChecks.add(new Question("oral - white spot or carious lesions", true, "normal"));
                predefinedChecks.add(new Question("oral - facial swelling", true, "normal"));
                predefinedChecks.add(new Question("testes fully descended R/L", true, "normal"));
                break;

            default:
                Log.e("CheckFragment", "Unsupported checkType: " + checkType);
                break;
        }

        Log.d("CheckFragment", "Loaded predefined checks for checkType: " + checkType + ", count: " + predefinedChecks.size());
        return predefinedChecks;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.disconnect();
        }
    }
}
