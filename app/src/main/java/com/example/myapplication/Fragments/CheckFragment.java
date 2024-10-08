package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.NavBarManager;
import com.example.myapplication.Question;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;
import com.example.myapplication.SignatureCanvas;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    private EditText commentsInput, actionTakenInput, doctorNameInput, venueInput, dateInput;
    private Button submitButton;
    private LinearLayout checksContainer;  // Container for dynamically added checks

    private List<Question> questionList;
    private HashMap<String, Spinner> spinnersMap;  // To hold the spinners for each check

    private boolean isFirstTime = false;  // Variable to track if it's the first time filling the form

    // Signature-related variables
    private SignatureCanvas signatureCanvas;
    private FrameLayout signatureContainer;
    private TextView signatureError;

    // Date fields
    private List<EditText> dateFields;

    // Protective Factors CheckBoxes
    private CheckBox immunisationUpToDateCheckbox, hearingCheckbox, visionCheckbox, hipsCheckbox, oralHealthCheckbox;

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
        venueInput = view.findViewById(R.id.venue_input);
        dateInput = view.findViewById(R.id.date_input);
        submitButton = view.findViewById(R.id.submit_button);
        checksContainer = view.findViewById(R.id.checks_container);  // This is the LinearLayout to hold specific checks

        // Protective Factors CheckBoxes
        immunisationUpToDateCheckbox = view.findViewById(R.id.immunisation_up_to_date_checkbox);
        hearingCheckbox = view.findViewById(R.id.hearing_checkbox);
        visionCheckbox = view.findViewById(R.id.vision_checkbox);
        hipsCheckbox = view.findViewById(R.id.hips_checkbox);
        oralHealthCheckbox = view.findViewById(R.id.oral_health_checkbox);

        // Signature-related views
        signatureContainer = view.findViewById(R.id.signature_container);
        signatureError = view.findViewById(R.id.signature_error);

        // Initialize the signature canvas and add it to the container
        signatureCanvas = new SignatureCanvas(getContext());
        signatureContainer.addView(signatureCanvas);

        // Initialize dateFields before setting up date pickers
        dateFields = Arrays.asList(dateInput);
        setupDatePickers();

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

    private void setupDatePickers() {
        if (dateFields == null) {
            Log.e("CheckFragment", "dateFields is null. Cannot set up date pickers.");
            return;
        }
        for (EditText dateField : dateFields) {
            setupDatePicker(dateField);
        }
    }

    private void setupDatePicker(EditText dateInput) {
        dateInput.setInputType(InputType.TYPE_NULL);
        dateInput.setFocusable(false);
        dateInput.setClickable(true);
        dateInput.setCursorVisible(false);

        dateInput.setOnClickListener(v -> {
            // Prevent opening DatePickerDialog if the field is disabled
            if (!dateInput.isEnabled()) {
                return;
            }

            Calendar calendar = Calendar.getInstance();
            String currentDate = dateInput.getText().toString();
            if (!currentDate.isEmpty()) {
                String[] parts = currentDate.split("-");
                if (parts.length == 3) {
                    try {
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]) - 1;
                        int day = Integer.parseInt(parts[2]);
                        calendar.set(year, month, day);
                    } catch (NumberFormatException e) {
                        Log.e("CheckFragment", "Invalid date format: " + currentDate);
                    }
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        dateInput.setText(formattedDate);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

    private class LoadSpecificChecksTask extends AsyncTask<Void, Void, Boolean> {

        private HashMap<String, String> assessmentData;
        private HashMap<String, String> protectiveFactorsData;

        @Override
        protected Boolean doInBackground(Void... voids) {
            assessmentData = new HashMap<>();
            protectiveFactorsData = new HashMap<>();
            questionList = new ArrayList<>(); // Ensure questionList is initialized

            // Check if data exists in ChildCheckAssessment
            String checkDataQuery = "SELECT COUNT(*) AS count FROM ChildCheckAssessment WHERE childID = ? AND checkType = ?";
            String[] checkDataParams = {String.valueOf(childID), checkType};
            char[] checkDataParamTypes = {'i', 's'};
            HashMap<String, String[]> dataResult = dbHelper.select(checkDataQuery, checkDataParams, checkDataParamTypes);

            int dataCount = 0;
            if (dataResult != null && dataResult.get("count") != null && dataResult.get("count").length > 0) {
                dataCount = Integer.parseInt(dataResult.get("count")[0]);
            }

            isFirstTime = dataCount == 0; // If no data, it's the first time

            if (!isFirstTime) {
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

                // Load data from ChildCheckProtectiveFactors
                String protectiveFactorsQuery = "SELECT immunisationUpToDate, hearing, vision, hips, oralHealth FROM ChildCheckProtectiveFactors WHERE childID = ? AND checkType = ?";
                HashMap<String, String[]> protectiveFactorsResult = dbHelper.select(protectiveFactorsQuery, params, paramTypes);

                if (protectiveFactorsResult != null && protectiveFactorsResult.get("immunisationUpToDate") != null && protectiveFactorsResult.get("immunisationUpToDate").length > 0) {
                    protectiveFactorsData.put("immunisationUpToDate", protectiveFactorsResult.get("immunisationUpToDate")[0]);
                    protectiveFactorsData.put("hearing", protectiveFactorsResult.get("hearing")[0]);
                    protectiveFactorsData.put("vision", protectiveFactorsResult.get("vision")[0]);
                    protectiveFactorsData.put("hips", protectiveFactorsResult.get("hips")[0]);
                    protectiveFactorsData.put("oralHealth", protectiveFactorsResult.get("oralHealth")[0]);
                }
            } else {
                // First time, load predefined checks
                questionList = getPredefinedChecksForCheckType(checkType);
            }

            return true; // Return true to proceed to onPostExecute
        }

        @Override
        protected void onPostExecute(Boolean dataExists) {
            // Autofill the form with data if available
            if (assessmentData != null && !assessmentData.isEmpty()) {
                weightInput.setText(assessmentData.get("weight"));
                lengthInput.setText(assessmentData.get("length"));
                headCircInput.setText(assessmentData.get("headCirc"));
                commentsInput.setText(assessmentData.get("comments"));
                actionTakenInput.setText(assessmentData.get("actionTaken"));
                doctorNameInput.setText(assessmentData.get("nameOfDoctor"));
                venueInput.setText(assessmentData.get("venue"));
                dateInput.setText(assessmentData.get("date"));

                // Update outcome spinner and checkbox
                String outcome = assessmentData.get("outcome");
                if (outcome != null) {
                    setSpinnerSelection(outcomeSpinner, outcome);
                }

                // Set checkbox state, handle potential null values
                healthInfoDiscussedCheckbox.setChecked("1".equals(assessmentData.get("healthInfoDiscussed")));

                // Handle signature
                if (assessmentData.get("signature") != null) {
                    String imageEncoded = assessmentData.get("signature");
                    if (imageEncoded != null && !imageEncoded.isEmpty()) {
                        // Create a new SignatureCanvas with the Base64 image
                        signatureCanvas = new SignatureCanvas(getContext(), imageEncoded);
                        signatureContainer.removeAllViews();
                        signatureContainer.addView(signatureCanvas);
                        // Disable the signature pad to prevent editing
                        signatureCanvas.setEnabled(false);
                    }
                }
            }

            if (protectiveFactorsData != null && !protectiveFactorsData.isEmpty()) {
                // Set CheckBoxes
                immunisationUpToDateCheckbox.setChecked("1".equals(protectiveFactorsData.get("immunisationUpToDate")));
                hearingCheckbox.setChecked("1".equals(protectiveFactorsData.get("hearing")));
                visionCheckbox.setChecked("1".equals(protectiveFactorsData.get("vision")));
                String hipsValue = protectiveFactorsData.get("hips");
                if (hipsValue != null) {
                    hipsCheckbox.setChecked("1".equals(hipsValue));
                } else {
                    hipsCheckbox.setChecked(false);
                }
                oralHealthCheckbox.setChecked("1".equals(protectiveFactorsData.get("oralHealth")));
            }

            // Dynamically add the specific checks to the LinearLayout
            checksContainer.removeAllViews();  // Clear any existing views
            if (questionList != null && !questionList.isEmpty()) {
                for (Question question : questionList) {
                    addCheckWithSpinnerToLayout(question.getQuestionText(), question.getStatus());
                }
            }

            // Set fields editable/non-editable based on isFirstTime
            setFieldsEditable(isFirstTimeFillingForm());
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
                    TextUtils.isEmpty(venueInput.getText()) ||
                    TextUtils.isEmpty(dateInput.getText())) {
                Log.e("CheckFragment", "Validation failed: Required fields are empty.");
                return false;
            }

            // Extract the signature from SignatureCanvas
            String signature = signatureCanvas.convertCanvas();
            if (signature == null || signature.isEmpty()) {
                Log.e("CheckFragment", "Validation failed: Signature is empty.");
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
            String venue = venueInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();

            // Protective Factors
            boolean immunisationUpToDate = immunisationUpToDateCheckbox.isChecked();
            boolean hearing = hearingCheckbox.isChecked();
            boolean vision = visionCheckbox.isChecked();
            Boolean hips = hipsCheckbox.isChecked();
            boolean oralHealth = oralHealthCheckbox.isChecked();

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
            if (!signageSuccess) {
                return false;
            }

            // Insert or update ChildCheckProtectiveFactors
            boolean protectiveFactorsSuccess = insertOrUpdateChildCheckProtectiveFactors(immunisationUpToDate, hearing, vision, hips, oralHealth);
            if (!protectiveFactorsSuccess) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Check submitted successfully!", Toast.LENGTH_SHORT).show();

                // Set isFirstTime to false after successful submission
                isFirstTime = false;

                // Disable fields and submit button
                setFieldsEditable(isFirstTimeFillingForm());
                submitButton.setEnabled(false);
                submitButton.setVisibility(View.GONE);

                // Navigate three fragments back to the Checks Menu Fragment
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getContext(), "Failed to submit check. Enter all fields and try again.", Toast.LENGTH_SHORT).show();
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

    private boolean insertOrUpdateChildCheckProtectiveFactors(boolean immunisationUpToDate, boolean hearing, boolean vision, Boolean hips, boolean oralHealth) {
        // Check if record exists
        String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheckProtectiveFactors WHERE childID = ? AND checkType = ?";
        String[] checkParams = {String.valueOf(childID), checkType};
        char[] checkParamTypes = {'i', 's'};

        HashMap<String, String[]> result = dbHelper.select(checkQuery, checkParams, checkParamTypes);
        int recordCount = 0;

        if (result != null && result.get("count") != null && result.get("count").length > 0) {
            try {
                recordCount = Integer.parseInt(result.get("count")[0]);
            } catch (NumberFormatException e) {
                Log.e("CheckFragment", "Error parsing count from ChildCheckProtectiveFactors query.", e);
                return false;
            }
        }

        if (recordCount > 0) {
            // Update existing record
            String updateQuery = "UPDATE ChildCheckProtectiveFactors SET immunisationUpToDate = ?, hearing = ?, vision = ?, hips = ?, oralHealth = ? WHERE childID = ? AND checkType = ?";
            String[] updateParams = {
                    immunisationUpToDate ? "1" : "0",
                    hearing ? "1" : "0",
                    vision ? "1" : "0",
                    hips != null ? (hips ? "1" : "0") : null,
                    oralHealth ? "1" : "0",
                    String.valueOf(childID),
                    checkType
            };
            char[] updateParamTypes = {'s', 's', 's', 's', 's', 'i', 's'};

            if (hips != null) {
                return dbHelper.update(updateQuery, updateParams, updateParamTypes);
            } else {
                // Set hips to NULL
                String updateQueryNullHips = "UPDATE ChildCheckProtectiveFactors SET immunisationUpToDate = ?, hearing = ?, vision = ?, hips = NULL, oralHealth = ? WHERE childID = ? AND checkType = ?";
                String[] updateParamsNullHips = {
                        immunisationUpToDate ? "1" : "0",
                        hearing ? "1" : "0",
                        vision ? "1" : "0",
                        oralHealth ? "1" : "0",
                        String.valueOf(childID),
                        checkType
                };
                char[] updateParamTypesNullHips = {'s', 's', 's', 's', 'i', 's'};
                return dbHelper.update(updateQueryNullHips, updateParamsNullHips, updateParamTypesNullHips);
            }

        } else {
            // Insert new record
            String insertQuery = "INSERT INTO ChildCheckProtectiveFactors (childID, checkType, immunisationUpToDate, hearing, vision, hips, oralHealth) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String[] insertParams = {
                    String.valueOf(childID),
                    checkType,
                    immunisationUpToDate ? "1" : "0",
                    hearing ? "1" : "0",
                    vision ? "1" : "0",
                    hips != null ? (hips ? "1" : "0") : null,
                    oralHealth ? "1" : "0"
            };
            char[] insertParamTypes = {'i', 's', 's', 's', 's', 's', 's'};

            if (hips != null) {
                return dbHelper.update(insertQuery, insertParams, insertParamTypes);
            } else {
                // Insert with hips as NULL
                String insertQueryNullHips = "INSERT INTO ChildCheckProtectiveFactors (childID, checkType, immunisationUpToDate, hearing, vision, hips, oralHealth) VALUES (?, ?, ?, ?, ?, NULL, ?)";
                String[] insertParamsNullHips = {
                        String.valueOf(childID),
                        checkType,
                        immunisationUpToDate ? "1" : "0",
                        hearing ? "1" : "0",
                        vision ? "1" : "0",
                        oralHealth ? "1" : "0"
                };
                char[] insertParamTypesNullHips = {'i', 's', 's', 's', 's', 's'};
                return dbHelper.update(insertQueryNullHips, insertParamsNullHips, insertParamTypesNullHips);
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

    private void setFieldsEditable(boolean isFirstTime) {
        // Fields that are editable only on first submission
        EditText[] editableFields = new EditText[]{
                weightInput, lengthInput, headCircInput, commentsInput, actionTakenInput, doctorNameInput, venueInput, dateInput
        };

        for (EditText field : editableFields) {
            field.setEnabled(isFirstTime);
            field.setFocusable(isFirstTime);
            field.setClickable(isFirstTime);
            field.setTextColor(getResources().getColor(isFirstTime ? R.color.black : R.color.disabled_text, null));
        }

        // Outcome spinner
        outcomeSpinner.setEnabled(isFirstTime);
        outcomeSpinner.setClickable(isFirstTime);

        // Checkboxes
        healthInfoDiscussedCheckbox.setEnabled(isFirstTime);
        immunisationUpToDateCheckbox.setEnabled(isFirstTime);
        hearingCheckbox.setEnabled(isFirstTime);
        visionCheckbox.setEnabled(isFirstTime);
        hipsCheckbox.setEnabled(isFirstTime);
        oralHealthCheckbox.setEnabled(isFirstTime);

        // Spinners for specific checks
        for (Spinner spinner : spinnersMap.values()) {
            spinner.setEnabled(isFirstTime);
            spinner.setClickable(isFirstTime);
        }

        // Handle Date Fields
        for (EditText dateField : dateFields) {
            if (!isFirstTime) {
                dateField.setOnClickListener(null); // Remove OnClickListener
                dateField.setTextColor(getResources().getColor(R.color.disabled_text, null)); // Optional: Change text color
            } else {
                setupDatePicker(dateField);
                dateField.setTextColor(getResources().getColor(R.color.black, null)); // Optional: Reset text color
            }
        }

        // Handle the SignatureCanvas
        if (signatureCanvas != null) {
            signatureCanvas.setEnabled(isFirstTime);
        }
    }

    private boolean isFirstTimeFillingForm() {
        return isFirstTime;
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;  // Avoid null values causing issues
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value.trim())) {
                spinner.setSelection(i);
                break;
            }
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
