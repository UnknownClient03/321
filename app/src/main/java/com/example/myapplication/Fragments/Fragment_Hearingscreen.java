package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.content.res.Resources;
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
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;
import com.example.myapplication.SignatureCanvas;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Fragment_Hearingscreen extends Fragment {

    private SQLConnection dbHelper;
    private int childID;
    private int guardianID;

    // UI Components
    private TextView textViewHearingscreenTitle;
    private EditText editTextName, editTextDOB, editTextLocation, editTextDate, editTextCoordinatorTelephone;
    private EditText editTextScreenBy;
    private RadioGroup radioGroupRightOutcome, radioGroupLeftOutcome, radioGroupReferAudiologist;
    private LinearLayout layoutRepeatScreenSection;
    private EditText editTextRepeatLocation, editTextRepeatDate, editTextRepeatScreenBy;
    private RadioGroup radioGroupRepeatRightOutcome, radioGroupRepeatLeftOutcome, radioGroupRepeatReferAudiologist;
    private RadioGroup radioGroupHearingRiskFactor;
    private LinearLayout layoutConsultationInstruction;
    private Button buttonSaveHearingscreen;
    private Button buttonAddRepeatScreening;

    // Signature canvases and containers
    private SignatureCanvas signatureCanvas;
    private SignatureCanvas repeatSignatureCanvas;
    private FrameLayout signatureContainer;
    private FrameLayout repeatSignatureContainer;
    private TextView signatureError;
    private TextView repeatSignatureError;

    private int coordinatorTelephone;

    // For text color changes when fields are disabled
    private int disabledTextColor;
    private int enabledTextColor;

    // Flag to check if repeat screening data exists
    private boolean repeatScreeningDataExists = false;

    public Fragment_Hearingscreen() {
    }

    public static Fragment_Hearingscreen newInstance() {
        return new Fragment_Hearingscreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1);
            guardianID = args.getInt("guardianID", -1);
            if (childID == -1 || guardianID == -1) {
                Log.e("Fragment_Hearingscreen", "Invalid childID or guardianID received");
                Toast.makeText(getActivity(), "Invalid child or guardian ID", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Fragment_Hearingscreen", "Received childID: " + childID);
                Log.d("Fragment_Hearingscreen", "Received guardianID: " + guardianID);
            }
        } else {
            Log.e("Fragment_Hearingscreen", "No arguments provided to fragment");
            Toast.makeText(getActivity(), "No child or guardian ID provided", Toast.LENGTH_SHORT).show();
        }
        dbHelper = new SQLConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hearingscreen, container, false);

        initializeUIComponents(view);
        setupListeners();
        retrieveHearingscreenData(childID);

        return view;
    }

    private void initializeUIComponents(View view) {
        // Initialize all UI components
        editTextName = view.findViewById(R.id.editText_name);
        editTextDOB = view.findViewById(R.id.editText_dob);
        editTextLocation = view.findViewById(R.id.editText_location);
        editTextDate = view.findViewById(R.id.editText_date);
        editTextScreenBy = view.findViewById(R.id.editText_screenBy);
        signatureContainer = view.findViewById(R.id.signatureContainer);
        signatureError = view.findViewById(R.id.signatureError);

        radioGroupRightOutcome = view.findViewById(R.id.radioGroup_right_outcome);
        radioGroupLeftOutcome = view.findViewById(R.id.radioGroup_left_outcome);
        radioGroupReferAudiologist = view.findViewById(R.id.radioGroup_refer_audiologist);

        // Repeat Screen Section
        buttonAddRepeatScreening = view.findViewById(R.id.button_add_repeat_screening);
        layoutRepeatScreenSection = view.findViewById(R.id.layout_repeat_screen_section);
        editTextRepeatLocation = view.findViewById(R.id.editText_repeat_location);
        editTextRepeatDate = view.findViewById(R.id.editText_repeat_date);
        editTextRepeatScreenBy = view.findViewById(R.id.editText_repeat_screenBy);
        repeatSignatureContainer = view.findViewById(R.id.repeatSignatureContainer);
        repeatSignatureError = view.findViewById(R.id.repeatSignatureError);

        radioGroupRepeatRightOutcome = view.findViewById(R.id.radioGroup_repeat_right_outcome);
        radioGroupRepeatLeftOutcome = view.findViewById(R.id.radioGroup_repeat_left_outcome);
        radioGroupRepeatReferAudiologist = view.findViewById(R.id.radioGroup_repeat_refer_audiologist);

        radioGroupHearingRiskFactor = view.findViewById(R.id.radioGroup_hearing_risk_factor);
        layoutConsultationInstruction = view.findViewById(R.id.layout_consultation_instruction);

        editTextCoordinatorTelephone = view.findViewById(R.id.editText_coordinator_telephone);
        buttonSaveHearingscreen = view.findViewById(R.id.button_saveHearingscreen);

        // Initialize SignatureCanvas
        signatureCanvas = new SignatureCanvas(getContext());
        signatureContainer.addView(signatureCanvas);

        repeatSignatureCanvas = new SignatureCanvas(getContext());
        repeatSignatureContainer.addView(repeatSignatureCanvas);

        // Set up date pickers
        setupDatePicker(editTextDate);
        setupDatePicker(editTextRepeatDate);

        // Get colors for enabling/disabling fields
        Resources res = getResources();
        disabledTextColor = res.getColor(R.color.disabled_text);
        enabledTextColor = res.getColor(R.color.black);
    }

    private void setupDatePicker(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        editText.setFocusable(false);
        editText.setOnClickListener(v -> showDatePickerDialog(editText));
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();

        // Parse existing date if available
        String currentDate = editText.getText().toString();
        if (!currentDate.isEmpty()) {
            String[] parts = currentDate.split("-");
            if (parts.length == 3) {
                try {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int day = Integer.parseInt(parts[2]);
                    calendar.set(year, month, day);
                } catch (NumberFormatException e) {
                    Log.e("Fragment_Hearingscreen", "Invalid date format: " + currentDate);
                }
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(formattedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void setupListeners() {
        buttonAddRepeatScreening.setOnClickListener(v -> {
            layoutRepeatScreenSection.setVisibility(View.VISIBLE);
            setRepeatScreenFieldsEditable(true);
            buttonAddRepeatScreening.setEnabled(false); // Disable the button after clicking
        });

        radioGroupHearingRiskFactor.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getText().toString().equalsIgnoreCase("Yes")) {
                layoutConsultationInstruction.setVisibility(View.VISIBLE);
            } else {
                layoutConsultationInstruction.setVisibility(View.GONE);
            }
        });

        buttonSaveHearingscreen.setOnClickListener(v -> saveHearingscreenData());
    }

    private void retrieveHearingscreenData(int childID) {
        try {
            if (!dbHelper.isConn()) {
                Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve initial screening (screenNumber = 1)
            String initialQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 1";
            String[] initialParams = {String.valueOf(childID)};
            char[] initialParamTypes = {'i'};

            HashMap<String, String[]> initialResult = dbHelper.select(initialQuery, initialParams, initialParamTypes);
            boolean hasData = false;

            if (initialResult != null && initialResult.get("childID") != null && initialResult.get("childID").length > 0) {
                populateHearingscreenFields(initialResult, false);
                hasData = true;
            }

            // Retrieve repeat screening (screenNumber = 2)
            String repeatQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 2";
            String[] repeatParams = {String.valueOf(childID)};
            char[] repeatParamTypes = {'i'};

            HashMap<String, String[]> repeatResult = dbHelper.select(repeatQuery, repeatParams, repeatParamTypes);

            if (repeatResult != null && repeatResult.get("childID") != null && repeatResult.get("childID").length > 0) {
                populateHearingscreenFields(repeatResult, true);
                layoutRepeatScreenSection.setVisibility(View.VISIBLE);
                setRepeatScreenFieldsEditable(false);
                buttonAddRepeatScreening.setEnabled(false);
                repeatScreeningDataExists = true;
                hasData = true;
            }

            // Retrieve and prefill child details
            retrieveChildDetails(childID);

            // Retrieve NewBornHearing data
            retrieveNewBornHearingData(childID);

            if (hasData) {
                setFieldsEditable(false);
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error retrieving Hearingscreen details.", Toast.LENGTH_SHORT).show();
            Log.e("Fragment_Hearingscreen", "Error in retrieveHearingscreenData: " + e.getMessage());
        }
    }

    private void populateHearingscreenFields(HashMap<String, String[]> result, boolean isRepeat) {
        EditText locationField = isRepeat ? editTextRepeatLocation : editTextLocation;
        EditText dateField = isRepeat ? editTextRepeatDate : editTextDate;
        EditText screenByField = isRepeat ? editTextRepeatScreenBy : editTextScreenBy;
        RadioGroup outcomeRightGroup = isRepeat ? radioGroupRepeatRightOutcome : radioGroupRightOutcome;
        RadioGroup outcomeLeftGroup = isRepeat ? radioGroupRepeatLeftOutcome : radioGroupLeftOutcome;
        RadioGroup referAudiologistGroup = isRepeat ? radioGroupRepeatReferAudiologist : radioGroupReferAudiologist;

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
            String imageEncoded = result.get("signature")[0];
            if (imageEncoded != null && !imageEncoded.isEmpty()) {
                SignatureCanvas canvas = new SignatureCanvas(getContext(), imageEncoded);
                if (isRepeat) {
                    repeatSignatureCanvas = canvas;
                    repeatSignatureContainer.removeAllViews();
                    repeatSignatureContainer.addView(repeatSignatureCanvas);
                    repeatSignatureCanvas.setEnabled(false);
                } else {
                    signatureCanvas = canvas;
                    signatureContainer.removeAllViews();
                    signatureContainer.addView(signatureCanvas);
                    signatureCanvas.setEnabled(false);
                }
            }
        }

        if (result.get("rightOutcome") != null && result.get("rightOutcome").length > 0) {
            String rightOutcome = result.get("rightOutcome")[0];
            if (rightOutcome.equalsIgnoreCase("Pass")) {
                outcomeRightGroup.check(isRepeat ? R.id.radio_repeat_right_pass : R.id.radio_right_pass);
            } else {
                outcomeRightGroup.check(isRepeat ? R.id.radio_repeat_right_refer : R.id.radio_right_refer);
            }
        }

        if (result.get("leftOutcome") != null && result.get("leftOutcome").length > 0) {
            String leftOutcome = result.get("leftOutcome")[0];
            if (leftOutcome.equalsIgnoreCase("Pass")) {
                outcomeLeftGroup.check(isRepeat ? R.id.radio_repeat_left_pass : R.id.radio_left_pass);
            } else {
                outcomeLeftGroup.check(isRepeat ? R.id.radio_repeat_left_refer : R.id.radio_left_refer);
            }
        }

        if (result.get("referToAudiologist") != null && result.get("referToAudiologist").length > 0) {
            boolean refer = result.get("referToAudiologist")[0].equals("1");
            if (refer) {
                referAudiologistGroup.check(isRepeat ? R.id.radio_repeat_refer_audiologist_yes : R.id.radio_refer_audiologist_yes);
            } else {
                referAudiologistGroup.check(isRepeat ? R.id.radio_repeat_refer_audiologist_no : R.id.radio_refer_audiologist_no);
            }
        }
    }

    private void retrieveChildDetails(int childID) {
        String query = "SELECT fname, lname, DOB FROM Child WHERE ID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result == null || result.isEmpty()) {
                Toast.makeText(getActivity(), "Error: Child details not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            prefillChildDetails(result);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error retrieving child details.", Toast.LENGTH_SHORT).show();
            Log.e("Fragment_Hearingscreen", "Error in retrieveChildDetails: " + e.getMessage());
        }
    }

    private void prefillChildDetails(HashMap<String, String[]> result) {
        String[] fnameArr = result.get("fname");
        String fname = (fnameArr != null && fnameArr.length > 0) ? fnameArr[0] : "";

        String[] lnameArr = result.get("lname");
        String lname = (lnameArr != null && lnameArr.length > 0) ? lnameArr[0] : "";

        String fullName = fname + " " + lname;
        editTextName.setText(fullName.trim());

        String[] dobArr = result.get("DOB");
        if (dobArr != null && dobArr.length > 0) {
            editTextDOB.setText(dobArr[0]);
        }
    }

    private void retrieveNewBornHearingData(int childID) {
        String query = "SELECT hearingRiskFactorIdentified, coordinatorTelephone FROM NewBornHearing WHERE childID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result != null && result.get("hearingRiskFactorIdentified") != null && result.get("hearingRiskFactorIdentified").length > 0) {
                String hearingRiskFactorIdentifiedStr = result.get("hearingRiskFactorIdentified")[0];
                boolean hearingRiskFactorIdentified = hearingRiskFactorIdentifiedStr.equals("1");
                if (hearingRiskFactorIdentified) {
                    radioGroupHearingRiskFactor.check(R.id.radio_hearing_risk_factor_yes);
                } else {
                    radioGroupHearingRiskFactor.check(R.id.radio_hearing_risk_factor_no);
                }
            }

            if (result != null && result.get("coordinatorTelephone") != null && result.get("coordinatorTelephone").length > 0) {
                String coordinatorTelephoneStr = result.get("coordinatorTelephone")[0];
                if (!coordinatorTelephoneStr.isEmpty()) {
                    editTextCoordinatorTelephone.setText(coordinatorTelephoneStr);
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error retrieving NewBornHearing data.", Toast.LENGTH_SHORT).show();
            Log.e("Fragment_Hearingscreen", "Error in retrieveNewBornHearingData: " + e.getMessage());
        }
    }

    private void saveHearingscreenData() {
        try {
            if (!dbHelper.isConn()) {
                Toast.makeText(getActivity(), "Database connection error", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = true;
            View focusView = null;

            // Validate required fields
            if (TextUtils.isEmpty(editTextName.getText()) ||
                    TextUtils.isEmpty(editTextDOB.getText()) ||
                    TextUtils.isEmpty(editTextLocation.getText()) ||
                    TextUtils.isEmpty(editTextDate.getText()) ||
                    TextUtils.isEmpty(editTextScreenBy.getText()) ||
                    radioGroupRightOutcome.getCheckedRadioButtonId() == -1 ||
                    radioGroupLeftOutcome.getCheckedRadioButtonId() == -1 ||
                    radioGroupReferAudiologist.getCheckedRadioButtonId() == -1 ||
                    radioGroupHearingRiskFactor.getCheckedRadioButtonId() == -1 ||
                    TextUtils.isEmpty(editTextCoordinatorTelephone.getText())) {

                Toast.makeText(getActivity(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            String location = editTextLocation.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String screenBy = editTextScreenBy.getText().toString().trim();

            // Extract the signature from SignatureCanvas
            String signature = signatureCanvas.convertCanvas();

            // Validate Signature
            if (signature == null || signature.isEmpty()) {
                signatureError.setVisibility(View.VISIBLE);
                isValid = false;
                if (focusView == null) focusView = signatureContainer;
            } else {
                signatureError.setVisibility(View.GONE);
            }

            RadioButton selectedRightOutcome = getView().findViewById(radioGroupRightOutcome.getCheckedRadioButtonId());
            String rightOutcome = selectedRightOutcome.getText().toString().trim();

            RadioButton selectedLeftOutcome = getView().findViewById(radioGroupLeftOutcome.getCheckedRadioButtonId());
            String leftOutcome = selectedLeftOutcome.getText().toString().trim();

            RadioButton selectedReferAudiologist = getView().findViewById(radioGroupReferAudiologist.getCheckedRadioButtonId());
            boolean referToAudiologist = selectedReferAudiologist.getText().toString().equalsIgnoreCase("Yes");

            RadioButton selectedHearingRiskFactor = getView().findViewById(radioGroupHearingRiskFactor.getCheckedRadioButtonId());
            boolean hearingRiskFactorIdentified = selectedHearingRiskFactor.getText().toString().equalsIgnoreCase("Yes");

            String coordinatorTelephoneStr = editTextCoordinatorTelephone.getText().toString().trim();

            try {
                coordinatorTelephone = Integer.parseInt(coordinatorTelephoneStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Coordinator Telephone must be a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isRepeatRequired = (layoutRepeatScreenSection.getVisibility() == View.VISIBLE);

            boolean allSavedSuccess = true;

            // Prepare and execute initial screening (screenNumber = 1)
            String initialCheckQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 1";
            String[] initialCheckParams = {String.valueOf(childID)};
            char[] initialCheckParamTypes = {'i'};

            HashMap<String, String[]> initialCheckResult = dbHelper.select(initialCheckQuery, initialCheckParams, initialCheckParamTypes);
            boolean initialExists = initialCheckResult != null && initialCheckResult.get("childID") != null && initialCheckResult.get("childID").length > 0;

            String initialSql;
            if (initialExists) {
                initialSql = "UPDATE Hearingscreen SET location = ?, date = ?, screenBy = ?, signature = ?, rightOutcome = ?, leftOutcome = ?, referToAudiologist = ? WHERE childID = ? AND screenNumber = 1";
            } else {
                initialSql = "INSERT INTO Hearingscreen (childID, screenNumber, location, date, screenBy, signature, rightOutcome, leftOutcome, referToAudiologist) VALUES (?, 1, ?, ?, ?, ?, ?, ?, ?)";
            }

            String[] initialParamsArr = initialExists
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

            boolean initialSaved = dbHelper.update(initialSql, initialParamsArr, initialParamTypesArr);
            if (initialSaved) {
                Toast.makeText(getActivity(), "Initial Hearingscreen saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to save initial Hearingscreen data", Toast.LENGTH_SHORT).show();
                allSavedSuccess = false;
            }

            // Handle Repeat Screening
            if (isRepeatRequired) {
                if (TextUtils.isEmpty(editTextRepeatLocation.getText()) ||
                        TextUtils.isEmpty(editTextRepeatDate.getText()) ||
                        TextUtils.isEmpty(editTextRepeatScreenBy.getText()) ||
                        radioGroupRepeatRightOutcome.getCheckedRadioButtonId() == -1 ||
                        radioGroupRepeatLeftOutcome.getCheckedRadioButtonId() == -1 ||
                        radioGroupRepeatReferAudiologist.getCheckedRadioButtonId() == -1) {

                    Toast.makeText(getActivity(), "Please fill in all required fields in Repeat Screen section.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String repeatLocation = editTextRepeatLocation.getText().toString().trim();
                String repeatDate = editTextRepeatDate.getText().toString().trim();
                String repeatScreenBy = editTextRepeatScreenBy.getText().toString().trim();

                // Extract the repeat signature
                String repeatSignature = repeatSignatureCanvas.convertCanvas();

                // Validate Repeat Signature
                if (repeatSignature == null || repeatSignature.isEmpty()) {
                    repeatSignatureError.setVisibility(View.VISIBLE);
                    isValid = false;
                    if (focusView == null) focusView = repeatSignatureContainer;
                } else {
                    repeatSignatureError.setVisibility(View.GONE);
                }

                RadioButton selectedRepeatRightOutcome = getView().findViewById(radioGroupRepeatRightOutcome.getCheckedRadioButtonId());
                String repeatRightOutcome = selectedRepeatRightOutcome.getText().toString().trim();

                RadioButton selectedRepeatLeftOutcome = getView().findViewById(radioGroupRepeatLeftOutcome.getCheckedRadioButtonId());
                String repeatLeftOutcome = selectedRepeatLeftOutcome.getText().toString().trim();

                RadioButton selectedRepeatReferAudiologist = getView().findViewById(radioGroupRepeatReferAudiologist.getCheckedRadioButtonId());
                boolean repeatReferAudiologist = selectedRepeatReferAudiologist.getText().toString().equalsIgnoreCase("Yes");

                String repeatCheckQuery = "SELECT * FROM Hearingscreen WHERE childID = ? AND screenNumber = 2";
                String[] repeatCheckParams = {String.valueOf(childID)};
                char[] repeatCheckParamTypes = {'i'};

                HashMap<String, String[]> repeatCheckResult = dbHelper.select(repeatCheckQuery, repeatCheckParams, repeatCheckParamTypes);
                boolean repeatExists = repeatCheckResult != null && repeatCheckResult.get("childID") != null && repeatCheckResult.get("childID").length > 0;

                String repeatSql;
                String[] repeatParamsArr;
                char[] repeatParamTypesArr;

                if (repeatExists) {
                    repeatSql = "UPDATE Hearingscreen SET location = ?, date = ?, screenBy = ?, signature = ?, rightOutcome = ?, leftOutcome = ?, referToAudiologist = ? WHERE childID = ? AND screenNumber = 2";
                } else {
                    repeatSql = "INSERT INTO Hearingscreen (childID, screenNumber, location, date, screenBy, signature, rightOutcome, leftOutcome, referToAudiologist) VALUES (?, 2, ?, ?, ?, ?, ?, ?, ?)";
                }

                if (repeatExists) {
                    repeatParamsArr = new String[]{
                            repeatLocation,
                            repeatDate,
                            repeatScreenBy,
                            repeatSignature,
                            repeatRightOutcome,
                            repeatLeftOutcome,
                            repeatReferAudiologist ? "1" : "0",
                            String.valueOf(childID)
                    };
                    repeatParamTypesArr = new char[]{'s', 's', 's', 's', 's', 's', 'i', 'i'};
                } else {
                    repeatParamsArr = new String[]{
                            String.valueOf(childID),
                            repeatLocation,
                            repeatDate,
                            repeatScreenBy,
                            repeatSignature,
                            repeatRightOutcome,
                            repeatLeftOutcome,
                            repeatReferAudiologist ? "1" : "0"
                    };
                    repeatParamTypesArr = new char[]{'i', 's', 's', 's', 's', 's', 's', 'i'};
                }

                boolean repeatSaved = dbHelper.update(repeatSql, repeatParamsArr, repeatParamTypesArr);
                if (repeatSaved) {
                    Toast.makeText(getActivity(), "Repeat Hearingscreen saved successfully", Toast.LENGTH_SHORT).show();
                    repeatScreeningDataExists = true;
                    setRepeatScreenFieldsEditable(false);
                    buttonAddRepeatScreening.setEnabled(false);
                } else {
                    Toast.makeText(getActivity(), "Failed to save repeat Hearingscreen data", Toast.LENGTH_SHORT).show();
                    allSavedSuccess = false;
                }

            } else {
                String deleteRepeatSql = "DELETE FROM Hearingscreen WHERE childID = ? AND screenNumber = 2";
                String[] deleteParams = {String.valueOf(childID)};
                char[] deleteParamTypes = {'i'};

                boolean deleteSuccess = dbHelper.update(deleteRepeatSql, deleteParams, deleteParamTypes);
                if (deleteSuccess) {
                    Toast.makeText(getActivity(), "Repeat Hearingscreen data deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to delete repeat Hearingscreen data", Toast.LENGTH_SHORT).show();
                    allSavedSuccess = false;
                }
            }

            // Insert or Update into NewBornHearing table
            saveNewBornHearingData(hearingRiskFactorIdentified);

            if (allSavedSuccess && isValid) {
                setFieldsEditable(false);
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error saving Hearingscreen", Toast.LENGTH_SHORT).show();
            Log.e("Fragment_Hearingscreen", "Error in saveHearingscreenData: " + e.getMessage());
        }
    }

    private void saveNewBornHearingData(boolean hearingRiskFactorIdentified) {
        try {
            // Define preScreeningVal based on outcomes
            String preScreeningVal;
            if (radioGroupRightOutcome.getCheckedRadioButtonId() != -1 &&
                    radioGroupLeftOutcome.getCheckedRadioButtonId() != -1) {

                RadioButton rightOutcomeBtn = getView().findViewById(radioGroupRightOutcome.getCheckedRadioButtonId());
                RadioButton leftOutcomeBtn = getView().findViewById(radioGroupLeftOutcome.getCheckedRadioButtonId());

                boolean rightPass = rightOutcomeBtn.getText().toString().equalsIgnoreCase("Pass");
                boolean leftPass = leftOutcomeBtn.getText().toString().equalsIgnoreCase("Pass");

                if (rightPass && leftPass) {
                    preScreeningVal = "Normal";
                } else if (!rightPass || !leftPass) {
                    preScreeningVal = "Refer";
                } else {
                    preScreeningVal = "review";
                }
            } else {
                preScreeningVal = "review";
            }

            // Set requiresrepeatScreen
            boolean requiresrepeatScreen = !repeatScreeningDataExists;

            // Retrieve fname, lname, and dob from Child table
            String childQuery = "SELECT fname, lname, DOB FROM Child WHERE ID = ?";
            String[] childParams = {String.valueOf(childID)};
            char[] childParamTypes = {'i'};

            HashMap<String, String[]> childResult = dbHelper.select(childQuery, childParams, childParamTypes);
            if (childResult == null || childResult.get("fname") == null || childResult.get("fname").length == 0 ||
                    childResult.get("lname") == null || childResult.get("lname").length == 0 ||
                    childResult.get("DOB") == null || childResult.get("DOB").length == 0) {
                Toast.makeText(getActivity(), "Error: Child details not found for NewBornHearing.", Toast.LENGTH_SHORT).show();
                return;
            }

            String fname = childResult.get("fname")[0];
            String lname = childResult.get("lname")[0];
            String dob = childResult.get("DOB")[0];

            // Check if record exists in NewBornHearing
            String checkQuery = "SELECT COUNT(*) AS count FROM NewBornHearing WHERE childID = ?";
            String[] checkParams = {String.valueOf(childID)};
            char[] checkParamTypes = {'i'};

            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;
            if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(checkResult.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("Fragment_Hearingscreen", "Invalid count value: " + checkResult.get("count")[0]);
                }
            }

            String hearingSql;
            String[] hearingParams;
            char[] hearingParamTypes;

            if (recordCount > 0) {
                // Update existing record and include fname and lname
                hearingSql = "UPDATE NewBornHearing SET preScreeningVal = ?, fname = ?, lname = ?, DOB = ?, requiresrepeatScreen = ?, hearingRiskFactorIdentified = ?, coordinatorTelephone = ? WHERE childID = ?";
                hearingParams = new String[]{
                        preScreeningVal,
                        fname,
                        lname,
                        dob,
                        requiresrepeatScreen ? "1" : "0",
                        hearingRiskFactorIdentified ? "1" : "0",
                        String.valueOf(coordinatorTelephone),
                        String.valueOf(childID)
                };
                hearingParamTypes = new char[]{'s', 's', 's', 'd', 'i', 'i', 'i', 'i'};
            } else {
                // Insert new record
                hearingSql = "INSERT INTO NewBornHearing (childID, preScreeningVal, fname, lname, DOB, requiresrepeatScreen, hearingRiskFactorIdentified, coordinatorTelephone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                hearingParams = new String[]{
                        String.valueOf(childID),
                        preScreeningVal,
                        fname,
                        lname,
                        dob,
                        requiresrepeatScreen ? "1" : "0",
                        hearingRiskFactorIdentified ? "1" : "0",
                        String.valueOf(coordinatorTelephone)
                };
                hearingParamTypes = new char[]{'i', 's', 's', 's', 'd', 'i', 'i', 'i'};
            }

            boolean hearingSaved = dbHelper.update(hearingSql, hearingParams, hearingParamTypes);
            if (hearingSaved) {
                Toast.makeText(getActivity(), "NewBornHearing data saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to save NewBornHearing data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error saving NewBornHearing data.", Toast.LENGTH_SHORT).show();
            Log.e("Fragment_Hearingscreen", "Error in saveNewBornHearingData: " + e.getMessage());
        }
    }

    private void setFieldsEditable(boolean editable) {
        editTextLocation.setEnabled(editable);
        editTextDate.setEnabled(editable);
        editTextScreenBy.setEnabled(editable);
        signatureCanvas.setEnabled(editable);

        editTextCoordinatorTelephone.setEnabled(editable);

        setRadioGroupEditable(radioGroupRightOutcome, editable);
        setRadioGroupEditable(radioGroupLeftOutcome, editable);
        setRadioGroupEditable(radioGroupReferAudiologist, editable);
        setRadioGroupEditable(radioGroupHearingRiskFactor, editable);

        // Handle the SignatureCanvas
        if (signatureCanvas != null) {
            signatureCanvas.setEnabled(editable);
        }
    }

    private void setRepeatScreenFieldsEditable(boolean editable) {
        editTextRepeatLocation.setEnabled(editable);
        editTextRepeatDate.setEnabled(editable);
        editTextRepeatScreenBy.setEnabled(editable);
        repeatSignatureCanvas.setEnabled(editable);

        setRadioGroupEditable(radioGroupRepeatRightOutcome, editable);
        setRadioGroupEditable(radioGroupRepeatLeftOutcome, editable);
        setRadioGroupEditable(radioGroupRepeatReferAudiologist, editable);
    }

    private void setRadioGroupEditable(RadioGroup radioGroup, boolean editable) {
        radioGroup.setEnabled(editable);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                rb.setEnabled(editable);
                rb.setTextColor(editable ? enabledTextColor : disabledTextColor);
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