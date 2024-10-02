package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Fragment_Hearingscreen extends Fragment {

    private SQLConnection dbHelper;
    private int childID;
    private int guardianID;

    // UI Components
    private TextView textViewHearingscreenTitle;
    private EditText editTextName, editTextDOB, editTextLocation, editTextDate;
    private EditText editTextScreenBy, editTextSignature;
    private RadioGroup radioGroupRightOutcome, radioGroupLeftOutcome, radioGroupReferAudiologist;
    private LinearLayout layoutReferralReason;
    private EditText editTextReferralReason, editTextCoordinatorTelephone;
    private RadioGroup radioGroupRepeatScreenRequired;
    private LinearLayout layoutRepeatScreenSection;
    private EditText editTextRepeatLocation, editTextRepeatDate, editTextRepeatScreenBy, editTextRepeatSignature;
    private RadioGroup radioGroupRepeatRightOutcome, radioGroupRepeatLeftOutcome, radioGroupRepeatReferAudiologist;
    private RadioGroup radioGroupHearingRiskFactor;
    private LinearLayout layoutConsultationInstruction;
    private Button buttonSaveHearingscreen;

    // Calendar instance for DatePicker
    private Calendar calendar = Calendar.getInstance();

    public Fragment_Hearingscreen() {
        // Required empty public constructor
    }

    public static Fragment_Hearingscreen newInstance() {
        return new Fragment_Hearingscreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_hearingscreen, container, false);

        // Initialize SQL connection
        dbHelper = new SQLConnection(); // Ensure secure initialization
        Log.d("Fragment_Hearingscreen", "SQLConnection initialized");

        // Get childID and guardianID from bundle
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1);
            guardianID = args.getInt("guardianID", -1);
            Log.d("Fragment_Hearingscreen", "Received childID: " + childID);
            Log.d("Fragment_Hearingscreen", "Received guardianID: " + guardianID);
        } else {
            Log.e("Fragment_Hearingscreen", "No arguments passed to the fragment.");
            Toast.makeText(getActivity(), "Error: No child information provided.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize UI Components
        initializeUIComponents(view);

        // Load existing Hearingscreen data if exists
        retrieveHearingscreenData(childID);

        // Set up listeners
        setupListeners();

        return view;
    }

    /**
     * Initializes all UI components by finding them via their IDs.
     */
    private void initializeUIComponents(View view) {
        // Title
        textViewHearingscreenTitle = view.findViewById(R.id.textView_hearingscreen_title);

        // Name and Date of Birth (Non-editable)
        editTextName = view.findViewById(R.id.editText_name);
        editTextDOB = view.findViewById(R.id.editText_dob);
        editTextName.setInputType(InputType.TYPE_NULL);
        editTextDOB.setInputType(InputType.TYPE_NULL);
        editTextName.setFocusable(false);
        editTextDOB.setFocusable(false);

        // Location and Date
        editTextLocation = view.findViewById(R.id.editText_location);
        editTextDate = view.findViewById(R.id.editText_date);
        setupDatePicker(editTextDate);

        // Screen By and Signature
        editTextScreenBy = view.findViewById(R.id.editText_screenBy);
        editTextSignature = view.findViewById(R.id.editText_signature);

        // Outcomes for Right and Left Ear
        radioGroupRightOutcome = view.findViewById(R.id.radioGroup_right_outcome);
        radioGroupLeftOutcome = view.findViewById(R.id.radioGroup_left_outcome);

        // Refer Audiologist
        radioGroupReferAudiologist = view.findViewById(R.id.radioGroup_refer_audiologist);
        layoutReferralReason = view.findViewById(R.id.layout_referral_reason);
        editTextReferralReason = view.findViewById(R.id.editText_referral_reason);

        // Repeat Screen Required
        radioGroupRepeatScreenRequired = view.findViewById(R.id.radioGroup_repeat_screen_required);
        layoutRepeatScreenSection = view.findViewById(R.id.layout_repeat_screen_section);
        // Setup DatePicker for Repeat Date
        editTextRepeatDate = view.findViewById(R.id.editText_repeat_date);
        setupDatePicker(editTextRepeatDate);

        // Repeat Screen Section Fields
        editTextRepeatLocation = view.findViewById(R.id.editText_repeat_location);
        editTextRepeatScreenBy = view.findViewById(R.id.editText_repeat_screenBy);
        editTextRepeatSignature = view.findViewById(R.id.editText_repeat_signature);
        radioGroupRepeatRightOutcome = view.findViewById(R.id.radioGroup_repeat_right_outcome);
        radioGroupRepeatLeftOutcome = view.findViewById(R.id.radioGroup_repeat_left_outcome);
        radioGroupRepeatReferAudiologist = view.findViewById(R.id.radioGroup_repeat_refer_audiologist);

        // Hearing Risk Factor
        radioGroupHearingRiskFactor = view.findViewById(R.id.radioGroup_hearing_risk_factor);
        layoutConsultationInstruction = view.findViewById(R.id.layout_consultation_instruction);

        // Coordinator Telephone
        editTextCoordinatorTelephone = view.findViewById(R.id.editText_coordinator_telephone);

        // Save Button
        buttonSaveHearingscreen = view.findViewById(R.id.button_saveHearingscreen);
    }

    /**
     * Sets up DatePickerDialogs for specified EditText fields.
     */
    private void setupDatePicker(EditText dateField) {
        dateField.setInputType(InputType.TYPE_NULL);
        dateField.setFocusable(false);
        dateField.setOnClickListener(v -> showDatePickerDialog(dateField));
    }

    /**
     * Displays a DatePickerDialog and sets the selected date to the EditText.
     */
    private void showDatePickerDialog(final EditText dateField) {
        final Calendar calendar = Calendar.getInstance();
        int year, month, day;

        // Pre-set the date picker to the current value if available
        String currentDateString = dateField.getText().toString();
        if (!currentDateString.isEmpty()) {
            String[] parts = currentDateString.split("-");
            if (parts.length == 3) {
                try {
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1; // Month is 0-based
                    day = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
            } else {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
        } else {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the date to 'yyyy-MM-dd'
            String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            dateField.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Sets up all necessary listeners for dynamic UI elements.
     */
    private void setupListeners() {
        // Show/Hide Referral Reason based on Audiologist Referral
        radioGroupReferAudiologist.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getText().toString().equalsIgnoreCase("Yes")) {
                layoutReferralReason.setVisibility(View.VISIBLE);
            } else {
                layoutReferralReason.setVisibility(View.GONE);
                editTextReferralReason.setText("");
            }
        });

        // Show/Hide Repeat Screen Section based on Repeat Screen Required
        radioGroupRepeatScreenRequired.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getText().toString().equalsIgnoreCase("Yes")) {
                layoutRepeatScreenSection.setVisibility(View.VISIBLE);
            } else {
                layoutRepeatScreenSection.setVisibility(View.GONE);
                clearRepeatScreenFields();
            }
        });

        // Show/Hide Consultation Instruction based on Hearing Risk Factor
        radioGroupHearingRiskFactor.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getText().toString().equalsIgnoreCase("Yes")) {
                layoutConsultationInstruction.setVisibility(View.VISIBLE);
            } else {
                layoutConsultationInstruction.setVisibility(View.GONE);
            }
        });

        // Set up Save button listener
        buttonSaveHearingscreen.setOnClickListener(v -> saveHearingscreenData());
    }

    /**
     * Clears all fields within the Repeat Screen section.
     */
    private void clearRepeatScreenFields() {
        editTextRepeatLocation.setText("");
        editTextRepeatDate.setText("");
        editTextRepeatScreenBy.setText("");
        editTextRepeatSignature.setText("");
        radioGroupRepeatRightOutcome.clearCheck();
        radioGroupRepeatLeftOutcome.clearCheck();
        radioGroupRepeatReferAudiologist.clearCheck();
    }

    /**
     * Retrieves existing Hearingscreen data from the database and populates the UI fields.
     */
    private void retrieveHearingscreenData(int childID) {
        try {
            if (!dbHelper.isConn()) {
                Log.e("Fragment_Hearingscreen", "Error: Unable to connect to the database.");
                Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve initial screening (screenNumber = 1)
            String initialQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 1";
            String[] initialParams = {String.valueOf(childID)};
            char[] initialParamTypes = {'i'};

            HashMap<String, String[]> initialResult = dbHelper.select(initialQuery, initialParams, initialParamTypes);

            if (initialResult != null && initialResult.get("childID") != null && initialResult.get("childID").length > 0) {
                populateHearingscreenFields(initialResult, false);
            } else {
                Log.d("Fragment_Hearingscreen", "No initial Hearingscreen record found for childID: " + childID);
            }

            // Retrieve repeat screening (screenNumber = 2)
            String repeatQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 2";
            String[] repeatParams = {String.valueOf(childID)};
            char[] repeatParamTypes = {'i'};

            HashMap<String, String[]> repeatResult = dbHelper.select(repeatQuery, repeatParams, repeatParamTypes);

            if (repeatResult != null && repeatResult.get("childID") != null && repeatResult.get("childID").length > 0) {
                populateHearingscreenFields(repeatResult, true);
                radioGroupRepeatScreenRequired.check(R.id.radio_repeat_yes);
                layoutRepeatScreenSection.setVisibility(View.VISIBLE);
            } else {
                Log.d("Fragment_Hearingscreen", "No repeat Hearingscreen record found for childID: " + childID);
            }

            // Retrieve and prefill child details
            retrieveChildDetails(childID);

        } catch (Exception e) {
            Log.e("Fragment_Hearingscreen", "Error retrieving Hearingscreen details: " + e.getMessage());
            Toast.makeText(getActivity(), "Error retrieving Hearingscreen details.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Populates Hearingscreen-specific fields from the database result.
     * @param result The result map from the database query.
     * @param isRepeat Indicates whether the data is for the repeat screening.
     */
    private void populateHearingscreenFields(HashMap<String, String[]> result, boolean isRepeat) {
        // Determine the correct UI components based on whether it's repeat screening
        EditText locationField = isRepeat ? editTextRepeatLocation : editTextLocation;
        EditText dateField = isRepeat ? editTextRepeatDate : editTextDate;
        EditText screenByField = isRepeat ? editTextRepeatScreenBy : editTextScreenBy;
        EditText signatureField = isRepeat ? editTextRepeatSignature : editTextSignature;
        RadioGroup outcomeRightGroup = isRepeat ? radioGroupRepeatRightOutcome : radioGroupRightOutcome;
        RadioGroup outcomeLeftGroup = isRepeat ? radioGroupRepeatLeftOutcome : radioGroupLeftOutcome;
        RadioGroup referAudiologistGroup = isRepeat ? radioGroupRepeatReferAudiologist : radioGroupReferAudiologist;
        EditText referralReasonField = isRepeat ? editTextReferralReason : null; // Assuming referral reason is same

        // Populate fields
        if (result.get("location") != null && result.get("location").length > 0) {
            locationField.setText(result.get("location")[0]);
        }
        if (result.get("date") != null && result.get("date").length > 0) {
            dateField.setText(result.get("date")[0]);
        }
        if (result.get("screenBy") != null && result.get("screenBy").length > 0) {
            screenByField.setText(result.get("screenBy")[0]);
        }
        if (result.get("signature") != null && result.get("signature").length > 0) {
            signatureField.setText(result.get("signature")[0]);
        }

        // Right Outcome
        if (result.get("rightOutcome") != null && result.get("rightOutcome").length > 0) {
            String rightOutcome = result.get("rightOutcome")[0];
            if (rightOutcome.equalsIgnoreCase("Pass")) {
                outcomeRightGroup.check(isRepeat ? R.id.radio_repeat_right_pass : R.id.radio_right_pass);
            } else {
                outcomeRightGroup.check(isRepeat ? R.id.radio_repeat_right_refer : R.id.radio_right_refer);
            }
        }

        // Left Outcome
        if (result.get("leftOutcome") != null && result.get("leftOutcome").length > 0) {
            String leftOutcome = result.get("leftOutcome")[0];
            if (leftOutcome.equalsIgnoreCase("Pass")) {
                outcomeLeftGroup.check(isRepeat ? R.id.radio_repeat_left_pass : R.id.radio_left_pass);
            } else {
                outcomeLeftGroup.check(isRepeat ? R.id.radio_repeat_left_refer : R.id.radio_left_refer);
            }
        }

        // Refer to Audiologist
        if (result.get("referToAudiologist") != null && result.get("referToAudiologist").length > 0) {
            boolean refer = result.get("referToAudiologist")[0].equals("1");
            if (refer) {
                referAudiologistGroup.check(isRepeat ? R.id.radio_repeat_refer_audiologist_yes : R.id.radio_refer_audiologist_yes);
                if (!isRepeat && referralReasonField != null) {
                    referralReasonField.setText(result.get("Reason") != null && result.get("Reason").length > 0 ? result.get("Reason")[0] : "");
                    layoutReferralReason.setVisibility(View.VISIBLE);
                }
            } else {
                referAudiologistGroup.check(isRepeat ? R.id.radio_repeat_refer_audiologist_no : R.id.radio_refer_audiologist_no);
                if (!isRepeat && referralReasonField != null) {
                    referralReasonField.setText("");
                    layoutReferralReason.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * **New Method**: Retrieves child details from the Child table and prefills the Name and DOB fields.
     */
    private void retrieveChildDetails(int childID) {
        String query = "SELECT fname, lname, DOB FROM Child WHERE ID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result == null || result.isEmpty()) {
                Log.e("Fragment_Hearingscreen", "Error: No valid result from the Child table query.");
                Toast.makeText(getActivity(), "Error: Child details not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            prefillChildDetails(result);

        } catch (Exception e) {
            Log.e("Fragment_Hearingscreen", "Error retrieving child details: " + e.getMessage());
            Toast.makeText(getActivity(), "Error retrieving child details.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * **New Method**: Prefills the Name and DOB fields using child details.
     */
    private void prefillChildDetails(HashMap<String, String[]> result) {
        // First Name
        String[] fnameArr = result.get("fname");
        String fname = (fnameArr != null && fnameArr.length > 0) ? fnameArr[0] : "";

        // Last Name
        String[] lnameArr = result.get("lname");
        String lname = (lnameArr != null && lnameArr.length > 0) ? lnameArr[0] : "";

        // Concatenate to form Full Name
        String fullName = fname + " " + lname;
        editTextName.setText(fullName.trim());

        // Date of Birth
        String[] dobArr = result.get("DOB");
        if (dobArr != null && dobArr.length > 0) {
            editTextDOB.setText(dobArr[0]);
        }
    }

    /**
     * Saves the Hearingscreen data to the database, handling both insert and update operations.
     */
    private void saveHearingscreenData() {
        try {
            if (!dbHelper.isConn()) {
                Log.e("Fragment_Hearingscreen", "Error: Unable to connect to the database.");
                Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate required fields for initial screening
            if (TextUtils.isEmpty(editTextName.getText()) ||
                    TextUtils.isEmpty(editTextDOB.getText()) ||
                    TextUtils.isEmpty(editTextLocation.getText()) ||
                    TextUtils.isEmpty(editTextDate.getText()) ||
                    TextUtils.isEmpty(editTextScreenBy.getText()) ||
                    TextUtils.isEmpty(editTextSignature.getText()) ||
                    radioGroupRightOutcome.getCheckedRadioButtonId() == -1 ||
                    radioGroupLeftOutcome.getCheckedRadioButtonId() == -1 ||
                    radioGroupReferAudiologist.getCheckedRadioButtonId() == -1 ||
                    radioGroupHearingRiskFactor.getCheckedRadioButtonId() == -1 ||
                    TextUtils.isEmpty(editTextCoordinatorTelephone.getText())) {

                Toast.makeText(getActivity(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gather initial screening data
            String location = editTextLocation.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String screenBy = editTextScreenBy.getText().toString().trim();
            String signature = editTextSignature.getText().toString().trim();

            // Right Outcome
            RadioButton selectedRightOutcome = getView().findViewById(radioGroupRightOutcome.getCheckedRadioButtonId());
            String rightOutcome = selectedRightOutcome.getText().toString().trim();

            // Left Outcome
            RadioButton selectedLeftOutcome = getView().findViewById(radioGroupLeftOutcome.getCheckedRadioButtonId());
            String leftOutcome = selectedLeftOutcome.getText().toString().trim();

            // Refer to Audiologist
            RadioButton selectedReferAudiologist = getView().findViewById(radioGroupReferAudiologist.getCheckedRadioButtonId());
            boolean referToAudiologist = selectedReferAudiologist.getText().toString().equalsIgnoreCase("Yes");
            String referralReason = referToAudiologist ? editTextReferralReason.getText().toString().trim() : null;

            // Hearing Risk Factor
            RadioButton selectedHearingRiskFactor = getView().findViewById(radioGroupHearingRiskFactor.getCheckedRadioButtonId());
            boolean hearingRiskFactor = selectedHearingRiskFactor.getText().toString().equalsIgnoreCase("Yes");

            String coordinatorTelephone = editTextCoordinatorTelephone.getText().toString().trim();

            // Handle Repeat Screen Required
            boolean isRepeatRequired = false;
            if (radioGroupRepeatScreenRequired.getCheckedRadioButtonId() != -1) {
                RadioButton selectedRepeatScreenRequired = getView().findViewById(radioGroupRepeatScreenRequired.getCheckedRadioButtonId());
                isRepeatRequired = selectedRepeatScreenRequired.getText().toString().equalsIgnoreCase("Yes");
            }

            // Prepare initial screening data for insert or update
            String initialCheckQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 1";
            String[] initialCheckParams = {String.valueOf(childID)};
            char[] initialCheckParamTypes = {'i'};

            HashMap<String, String[]> initialCheckResult = dbHelper.select(initialCheckQuery, initialCheckParams, initialCheckParamTypes);
            boolean initialExists = initialCheckResult != null && initialCheckResult.get("childID") != null && initialCheckResult.get("childID").length > 0;

            // Prepare SQL for initial screening
            String initialSql;
            if (initialExists) {
                initialSql = "UPDATE Hearingscreen SET location = ?, date = ?, screenBy = ?, signature = ?, rightOutcome = ?, leftOutcome = ?, referToAudiologist = ? WHERE childID = ? AND screenNumber = 1";
            } else {
                initialSql = "INSERT INTO Hearingscreen (childID, screenNumber, location, date, screenBy, signature, rightOutcome, leftOutcome, referToAudiologist) VALUES (?, 1, ?, ?, ?, ?, ?, ?, ?)";
            }

            String[] initialParams = initialExists
                    ? new String[]{
                    location,
                    date,
                    screenBy,
                    signature,
                    rightOutcome,
                    leftOutcome,
                    referToAudiologist ? "1" : "0",
                    String.valueOf(childID)
            }
                    : new String[]{
                    String.valueOf(childID),
                    location,
                    date,
                    screenBy,
                    signature,
                    rightOutcome,
                    leftOutcome,
                    referToAudiologist ? "1" : "0"
            };

            char[] initialParamTypesArr = initialExists
                    ? new char[]{'s', 's', 's', 's', 's', 's', 'i', 'i'}
                    : new char[]{'i', 's', 's', 's', 's', 's', 's', 'i'};

            boolean initialSaved = dbHelper.update(initialSql, initialParams, initialParamTypesArr);
            if (initialSaved) {
                Log.d("Fragment_Hearingscreen", "Initial Hearingscreen data saved successfully for childID: " + childID);
                Toast.makeText(getActivity(), "Initial Hearingscreen saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Fragment_Hearingscreen", "Failed to save initial Hearingscreen data for childID: " + childID);
                Toast.makeText(getActivity(), "Failed to save initial Hearingscreen data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Handle Repeat Screening
            if (isRepeatRequired) {
                // Validate repeat screening fields
                if (TextUtils.isEmpty(editTextRepeatLocation.getText()) ||
                        TextUtils.isEmpty(editTextRepeatDate.getText()) ||
                        TextUtils.isEmpty(editTextRepeatScreenBy.getText()) ||
                        TextUtils.isEmpty(editTextRepeatSignature.getText()) ||
                        radioGroupRepeatRightOutcome.getCheckedRadioButtonId() == -1 ||
                        radioGroupRepeatLeftOutcome.getCheckedRadioButtonId() == -1 ||
                        radioGroupRepeatReferAudiologist.getCheckedRadioButtonId() == -1) {

                    Toast.makeText(getActivity(), "Please fill in all required fields in Repeat Screen section.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gather repeat screening data
                String repeatLocation = editTextRepeatLocation.getText().toString().trim();
                String repeatDate = editTextRepeatDate.getText().toString().trim();
                String repeatScreenBy = editTextRepeatScreenBy.getText().toString().trim();
                String repeatSignature = editTextRepeatSignature.getText().toString().trim();

                // Repeat Right Outcome
                RadioButton selectedRepeatRightOutcome = getView().findViewById(radioGroupRepeatRightOutcome.getCheckedRadioButtonId());
                String repeatRightOutcome = selectedRepeatRightOutcome.getText().toString().trim();

                // Repeat Left Outcome
                RadioButton selectedRepeatLeftOutcome = getView().findViewById(radioGroupRepeatLeftOutcome.getCheckedRadioButtonId());
                String repeatLeftOutcome = selectedRepeatLeftOutcome.getText().toString().trim();

                // Repeat Refer to Audiologist
                RadioButton selectedRepeatReferAudiologist = getView().findViewById(radioGroupRepeatReferAudiologist.getCheckedRadioButtonId());
                boolean repeatReferAudiologist = selectedRepeatReferAudiologist.getText().toString().equalsIgnoreCase("Yes");
                // Assuming 'Reason' column exists for repeat screenings as well
                String repeatReferralReason = repeatReferAudiologist ? editTextReferralReason.getText().toString().trim() : null;

                // Prepare repeat screening data for insert or update
                String repeatCheckQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 2";
                String[] repeatCheckParams = {String.valueOf(childID)};
                char[] repeatCheckParamTypes = {'i'};

                HashMap<String, String[]> repeatCheckResult = dbHelper.select(repeatCheckQuery, repeatCheckParams, repeatCheckParamTypes);
                boolean repeatExists = repeatCheckResult != null && repeatCheckResult.get("childID") != null && repeatCheckResult.get("childID").length > 0;

                // Prepare SQL for repeat screening
                String repeatSql;
                if (repeatExists) {
                    repeatSql = "UPDATE Hearingscreen SET location = ?, date = ?, screenBy = ?, signature = ?, rightOutcome = ?, leftOutcome = ?, referToAudiologist = ? WHERE childID = ? AND screenNumber = 2";
                } else {
                    repeatSql = "INSERT INTO Hearingscreen (childID, screenNumber, location, date, screenBy, signature, rightOutcome, leftOutcome, referToAudiologist) VALUES (?, 2, ?, ?, ?, ?, ?, ?, ?)";
                }

                String[] repeatParams = repeatExists
                        ? new String[]{
                        repeatLocation,
                        repeatDate,
                        repeatScreenBy,
                        repeatSignature,
                        repeatRightOutcome,
                        repeatLeftOutcome,
                        repeatReferAudiologist ? "1" : "0",
                        String.valueOf(childID)
                }
                        : new String[]{
                        String.valueOf(childID),
                        repeatLocation,
                        repeatDate,
                        repeatScreenBy,
                        repeatSignature,
                        repeatRightOutcome,
                        repeatLeftOutcome,
                        repeatReferAudiologist ? "1" : "0"
                };

                char[] repeatParamTypesArr = repeatExists
                        ? new char[]{'s', 's', 's', 's', 's', 's', 'i', 'i'}
                        : new char[]{'i', 's', 's', 's', 's', 's', 's', 'i'};

                boolean repeatSaved = dbHelper.update(repeatSql, repeatParams, repeatParamTypesArr);
                if (repeatSaved) {
                    Log.d("Fragment_Hearingscreen", "Repeat Hearingscreen data saved successfully for childID: " + childID);
                    Toast.makeText(getActivity(), "Repeat Hearingscreen saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Fragment_Hearingscreen", "Failed to save repeat Hearingscreen data for childID: " + childID);
                    Toast.makeText(getActivity(), "Failed to save repeat Hearingscreen data", Toast.LENGTH_SHORT).show();
                }

            } else {
                // If repeat screening not required, remove any existing repeat screening data
                String deleteRepeatSql = "DELETE FROM Hearingscreen WHERE childID = ? AND screenNumber = 2";
                String[] deleteParams = {String.valueOf(childID)};
                char[] deleteParamTypes = {'i'};

                boolean repeatDeleted = dbHelper.update(deleteRepeatSql, deleteParams, deleteParamTypes);
                if (repeatDeleted) {
                    Log.d("Fragment_Hearingscreen", "Existing repeat Hearingscreen data deleted for childID: " + childID);
                    Toast.makeText(getActivity(), "Repeat Hearingscreen data removed", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Fragment_Hearingscreen", "Failed to delete repeat Hearingscreen data for childID: " + childID);
                    // Not critical, so no toast
                }
            }

        } catch (Exception e) {
            Log.e("Fragment_Hearingscreen", "Error saving Hearingscreen: " + e.getMessage());
            Toast.makeText(getActivity(), "Error saving Hearingscreen", Toast.LENGTH_SHORT).show();
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
