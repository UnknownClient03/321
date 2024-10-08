package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;
import com.google.android.material.textfield.TextInputLayout;
import com.example.myapplication.SignatureCanvas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class Fragment_BirthDetails extends Fragment {

    private SQLConnection dbHelper;
    private Spinner sexInput, bloodGroupInput, vitaminKGivenInput, feedingAtDischargeInput;
    private EditText fnameInput, lnameInput, birthFacilityInput, birthDateInput;
    private EditText mothersFNameInput, mothersLNameInput, mothersMRNInput, pregnancyComplicationsInput;
    private EditText labourInput, labourComplicationsInput, typeofBirthInput;
    private EditText estimatedGestationInput, apgarScore1MinInput, apgarScore5MinInput, abnormalitiesAtBirthInput;
    private EditText problemsRequiringTreatmentInput, birthWeightInput, birthLengthInput, birthHeadCircInput;
    private EditText newBornBloodspotScreenTestInput, vitaminKGiven1stInput, vitaminKGiven2ndInput, vitaminKGiven3rdInput;
    private EditText postPartumComplicationsInput, difficultiesWithFeedingInput, dateOfDischargeInput, dischargeWeightInput;
    private EditText headCircInput, printNameInput, designationInput;
    private EditText hepBImmunisationGivenInput, hepBImmunoglobinGivenInput;
    private int childID;
    private int guardianID;

    private TextInputLayout fnameLayout, lnameLayout, birthFacilityLayout, birthDateLayout;
    private TextInputLayout mothersFNameLayout, mothersLNameLayout, mothersMRNLayout, pregnancyComplicationsLayout;
    private TextInputLayout labourLayout, labourComplicationsLayout, typeofBirthLayout;
    private TextInputLayout estimatedGestationLayout, apgarScore1MinLayout, apgarScore5MinLayout, abnormalitiesAtBirthLayout;
    private TextInputLayout problemsRequiringTreatmentLayout, birthWeightLayout, birthLengthLayout, birthHeadCircLayout;
    private TextInputLayout newBornBloodspotScreenTestLayout, vitaminKGiven1stLayout, vitaminKGiven2ndLayout, vitaminKGiven3rdLayout;
    private TextInputLayout postPartumComplicationsLayout, difficultiesWithFeedingLayout, dateOfDischargeLayout, dischargeWeightLayout;
    private TextInputLayout headCircLayout, printNameLayout, designationLayout;
    private TextInputLayout hepBImmunisationGivenLayout, hepBImmunoglobinGivenLayout;

    private SignatureCanvas signatureCanvas;
    private FrameLayout signatureContainer;
    private TextView signatureError;

    // List to manage all date fields
    private List<EditText> dateFields;

    public Fragment_BirthDetails() {
    }

    public static Fragment_BirthDetails newInstance() {
        return new Fragment_BirthDetails();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1);
            guardianID = args.getInt("guardianID", -1);
            if (childID == -1 || guardianID == -1) {
                Log.e("Fragment_BirthDetails", "Invalid childID or guardianID received");
                Toast.makeText(getActivity(), "Invalid child or guardian ID", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Fragment_BirthDetails", "Received childID: " + childID);
                Log.d("Fragment_BirthDetails", "Received guardianID: " + guardianID);
            }
        } else {
            Log.e("Fragment_BirthDetails", "No arguments provided to fragment");
            Toast.makeText(getActivity(), "No child or guardian ID provided", Toast.LENGTH_SHORT).show();
        }
        dbHelper = new SQLConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_birth_details, container, false);

        initializeTextInputLayouts(view);
        initializeInputs(view);
        setupAllDatePickers();

        signatureCanvas = new SignatureCanvas(getContext());
        signatureContainer.addView(signatureCanvas);

        // Initialize dateFields list
        dateFields = Arrays.asList(
                birthDateInput,
                newBornBloodspotScreenTestInput,
                vitaminKGiven1stInput,
                vitaminKGiven2ndInput,
                vitaminKGiven3rdInput,
                dateOfDischargeInput,
                hepBImmunisationGivenInput,
                hepBImmunoglobinGivenInput
        );

        loadDataFromDatabase();

        Button btnSave = view.findViewById(R.id.button_saveBirthDetails);
        btnSave.setOnClickListener(v -> saveBirthDetails());

        return view;
    }

    private void initializeTextInputLayouts(View view) {
        fnameLayout = view.findViewById(R.id.textInputLayout_fname);
        lnameLayout = view.findViewById(R.id.textInputLayout_lname);
        birthFacilityLayout = view.findViewById(R.id.textInputLayout_birthFacility);
        birthDateLayout = view.findViewById(R.id.textInputLayout_birthDate);
        mothersFNameLayout = view.findViewById(R.id.textInputLayout_mothersFName);
        mothersLNameLayout = view.findViewById(R.id.textInputLayout_mothersLName);
        mothersMRNLayout = view.findViewById(R.id.textInputLayout_mothersMRN);
        pregnancyComplicationsLayout = view.findViewById(R.id.textInputLayout_pregnancyComplications);
        labourLayout = view.findViewById(R.id.textInputLayout_labour);
        labourComplicationsLayout = view.findViewById(R.id.textInputLayout_labourComplications);
        typeofBirthLayout = view.findViewById(R.id.textInputLayout_typeofBirth);
        estimatedGestationLayout = view.findViewById(R.id.textInputLayout_estimatedGestation);
        apgarScore1MinLayout = view.findViewById(R.id.textInputLayout_apgarScore1Min);
        apgarScore5MinLayout = view.findViewById(R.id.textInputLayout_apgarScore5Min);
        abnormalitiesAtBirthLayout = view.findViewById(R.id.textInputLayout_abnormalitiesAtBirth);
        problemsRequiringTreatmentLayout = view.findViewById(R.id.textInputLayout_problemsRequiringTreatment);
        birthWeightLayout = view.findViewById(R.id.textInputLayout_birthWeight);
        birthLengthLayout = view.findViewById(R.id.textInputLayout_birthLength);
        birthHeadCircLayout = view.findViewById(R.id.textInputLayout_birthHeadCirc);
        newBornBloodspotScreenTestLayout = view.findViewById(R.id.textInputLayout_newBornBloodspotScreenTest);
        vitaminKGiven1stLayout = view.findViewById(R.id.textInputLayout_vitaminKGiven1st);
        vitaminKGiven2ndLayout = view.findViewById(R.id.textInputLayout_vitaminKGiven2nd);
        vitaminKGiven3rdLayout = view.findViewById(R.id.textInputLayout_vitaminKGiven3rd);
        postPartumComplicationsLayout = view.findViewById(R.id.textInputLayout_postPartumComplications);
        difficultiesWithFeedingLayout = view.findViewById(R.id.textInputLayout_difficultiesWithFeeding);
        dateOfDischargeLayout = view.findViewById(R.id.textInputLayout_dateOfDischarge);
        dischargeWeightLayout = view.findViewById(R.id.textInputLayout_dischargeWeight);
        headCircLayout = view.findViewById(R.id.textInputLayout_headCirc);
        printNameLayout = view.findViewById(R.id.textInputLayout_printName);
        designationLayout = view.findViewById(R.id.textInputLayout_designation);
        hepBImmunisationGivenLayout = view.findViewById(R.id.textInputLayout_hepBImmunisationGiven);
        hepBImmunoglobinGivenLayout = view.findViewById(R.id.textInputLayout_hepBImmunoglobinGiven);
    }

    private void initializeInputs(View view) {
        fnameInput = view.findViewById(R.id.editText_fname);
        lnameInput = view.findViewById(R.id.editText_lname);
        birthDateInput = view.findViewById(R.id.editText_birthDate);
        birthFacilityInput = view.findViewById(R.id.editText_birthPlace);
        sexInput = view.findViewById(R.id.spinner_sex);

        mothersFNameInput = view.findViewById(R.id.editText_mothersFName);
        mothersLNameInput = view.findViewById(R.id.editText_mothersLName);
        mothersMRNInput = view.findViewById(R.id.editText_mothersMRN);
        pregnancyComplicationsInput = view.findViewById(R.id.editText_pregnancyComplications);
        bloodGroupInput = view.findViewById(R.id.spinner_bloodGroup);

        labourInput = view.findViewById(R.id.editText_labour);
        labourComplicationsInput = view.findViewById(R.id.editText_labourComplications);
        typeofBirthInput = view.findViewById(R.id.editText_typeofBirth);

        estimatedGestationInput = view.findViewById(R.id.editText_estimatedGestation);
        apgarScore1MinInput = view.findViewById(R.id.editText_apgarScore1Min);
        apgarScore5MinInput = view.findViewById(R.id.editText_apgarScore5Min);
        abnormalitiesAtBirthInput = view.findViewById(R.id.editText_abnormalitiesAtBirth);
        problemsRequiringTreatmentInput = view.findViewById(R.id.editText_problemsRequiringTreatment);

        birthWeightInput = view.findViewById(R.id.editText_birthWeight);
        birthLengthInput = view.findViewById(R.id.editText_birthLength);
        birthHeadCircInput = view.findViewById(R.id.editText_birthHeadCirc);

        newBornBloodspotScreenTestInput = view.findViewById(R.id.editText_newBornBloodspotScreenTest);
        vitaminKGivenInput = view.findViewById(R.id.spinner_vitaminKGiven);
        vitaminKGiven1stInput = view.findViewById(R.id.editText_vitaminKGiven1st);
        vitaminKGiven2ndInput = view.findViewById(R.id.editText_vitaminKGiven2nd);
        vitaminKGiven3rdInput = view.findViewById(R.id.editText_vitaminKGiven3rd);

        postPartumComplicationsInput = view.findViewById(R.id.editText_postPartumComplications);
        feedingAtDischargeInput = view.findViewById(R.id.spinner_feedingAtDischarge);
        difficultiesWithFeedingInput = view.findViewById(R.id.editText_difficultiesWithFeeding);
        dateOfDischargeInput = view.findViewById(R.id.editText_dateOfDischarge);
        dischargeWeightInput = view.findViewById(R.id.editText_dischargeWeight);
        headCircInput = view.findViewById(R.id.editText_headCirc);

        printNameInput = view.findViewById(R.id.editText_printName);
        designationInput = view.findViewById(R.id.editText_designation);

        hepBImmunisationGivenInput = view.findViewById(R.id.editText_hepBImmunisationGiven);
        hepBImmunoglobinGivenInput = view.findViewById(R.id.editText_hepBImmunoglobinGiven);

        signatureContainer = view.findViewById(R.id.signatureContainer);
        signatureError = view.findViewById(R.id.signatureError);
    }

    private void setupAllDatePickers() {
        setupDatePicker(birthDateInput, birthDateLayout);
        setupDatePicker(newBornBloodspotScreenTestInput, newBornBloodspotScreenTestLayout);
        setupDatePicker(vitaminKGiven1stInput, vitaminKGiven1stLayout);
        setupDatePicker(vitaminKGiven2ndInput, vitaminKGiven2ndLayout);
        setupDatePicker(vitaminKGiven3rdInput, vitaminKGiven3rdLayout);
        setupDatePicker(dateOfDischargeInput, dateOfDischargeLayout);
        setupDatePicker(hepBImmunisationGivenInput, hepBImmunisationGivenLayout);
        setupDatePicker(hepBImmunoglobinGivenInput, hepBImmunoglobinGivenLayout);
    }

    private void setupDatePicker(EditText dateInput, TextInputLayout layout) {
        dateInput.setInputType(InputType.TYPE_NULL);
        dateInput.setFocusable(false);
        dateInput.setClickable(true);
        dateInput.setOnClickListener(v -> {
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
                        Log.e("Fragment_BirthDetails", "Invalid date format: " + currentDate);
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
                        dateInput.setText(formattedDate);
                        layout.setError(null);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

    private void loadDataFromDatabase() {
        String checkQuery = "SELECT * FROM BirthDetails WHERE childID = ?";
        String[] checkParams = {String.valueOf(childID)};
        char[] checkParamTypes = {'i'};

        try {
            Log.d("Fragment_BirthDetails", "Attempting to load data from BirthDetails for childID: " + childID);
            HashMap<String, String[]> data = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            if (hasData(data)) {
                Log.d("Fragment_BirthDetails", "Data found in BirthDetails. Populating fields.");
                populateFields(data);

            } else {
                Log.d("Fragment_BirthDetails", "No data found in BirthDetails. Attempting to load from Child table.");
                String childQuery = "SELECT fname, lname, DOB, sex FROM Child WHERE ID = ?";
                String[] childParams = {String.valueOf(childID)};
                char[] childParamTypes = {'i'};

                HashMap<String, String[]> childData = dbHelper.select(childQuery, childParams, childParamTypes);

                if (hasData(childData)) {
                    fnameInput.setText(getValue(childData, "fname"));
                    lnameInput.setText(getValue(childData, "lname"));
                    birthDateInput.setText(getValue(childData, "DOB"));
                    setSpinnerSelection(sexInput, getValue(childData, "sex"));
                    fnameInput.setEnabled(false);
                    lnameInput.setEnabled(false);
                    birthDateInput.setEnabled(false);
                    sexInput.setEnabled(false);
                } else {
                    Log.e("Fragment_BirthDetails", "No data found in Child table for childID: " + childID);
                    Toast.makeText(getActivity(), "No data found for the given child ID.", Toast.LENGTH_SHORT).show();
                    setFieldsEditable(true); // Make fields editable as there's no existing data
                }
            }
        } catch (Exception e) {
            Log.e("Fragment_BirthDetails", "Error loading data", e);
            Toast.makeText(getActivity(), "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFields(HashMap<String, String[]> data) {
        if (data.containsKey("fname")) {
            fnameInput.setText(getValue(data, "fname"));
            fnameInput.setEnabled(false); // Make non-editable
        }

        if (data.containsKey("lname")) {
            lnameInput.setText(getValue(data, "lname"));
            lnameInput.setEnabled(false); // Make non-editable
        }

        if (data.containsKey("birthFacility")) {
            birthFacilityInput.setText(getValue(data, "birthFacility"));
            birthFacilityInput.setEnabled(false); // Make non-editable
        }

        if (data.containsKey("DOB")) {
            birthDateInput.setText(getValue(data, "DOB"));
            birthDateInput.setEnabled(false); // Make non-editable
        }

        if (data.containsKey("sex")) {
            setSpinnerSelection(sexInput, getValue(data, "sex"));
            sexInput.setEnabled(false); // Make non-editable
        }

        if (data.containsKey("mothersFName")) {
            mothersFNameInput.setText(getValue(data, "mothersFName"));
            mothersFNameInput.setEnabled(false);
        }

        if (data.containsKey("mothersLName")) {
            mothersLNameInput.setText(getValue(data, "mothersLName"));
            mothersLNameInput.setEnabled(false);
        }

        if (data.containsKey("mothersMRN")) {
            mothersMRNInput.setText(getValue(data, "mothersMRN"));
            mothersMRNInput.setEnabled(false);
        }

        if (data.containsKey("pregnancyComplications")) {
            pregnancyComplicationsInput.setText(getValue(data, "pregnancyComplications"));
            pregnancyComplicationsInput.setEnabled(false);
        }

        if (data.containsKey("bloodGroup")) {
            setSpinnerSelection(bloodGroupInput, getValue(data, "bloodGroup"));
            bloodGroupInput.setEnabled(false);
        }

        if (data.containsKey("labour")) {
            labourInput.setText(getValue(data, "labour"));
            labourInput.setEnabled(false);
        }

        if (data.containsKey("labourComplications")) {
            labourComplicationsInput.setText(getValue(data, "labourComplications"));
            labourComplicationsInput.setEnabled(false);
        }

        if (data.containsKey("typeofBirth")) {
            typeofBirthInput.setText(getValue(data, "typeofBirth"));
            typeofBirthInput.setEnabled(false);
        }

        if (data.containsKey("estimatedGestation")) {
            estimatedGestationInput.setText(getValue(data, "estimatedGestation"));
            estimatedGestationInput.setEnabled(false);
        }

        if (data.containsKey("apgarScore1Min")) {
            apgarScore1MinInput.setText(getValue(data, "apgarScore1Min"));
            apgarScore1MinInput.setEnabled(false);
        }

        if (data.containsKey("apgarScore5Min")) {
            apgarScore5MinInput.setText(getValue(data, "apgarScore5Min"));
            apgarScore5MinInput.setEnabled(false);
        }

        if (data.containsKey("abnormalitiesAtBirth")) {
            abnormalitiesAtBirthInput.setText(getValue(data, "abnormalitiesAtBirth"));
            abnormalitiesAtBirthInput.setEnabled(false);
        }

        if (data.containsKey("problemsRequiringTreatment")) {
            problemsRequiringTreatmentInput.setText(getValue(data, "problemsRequiringTreatment"));
            problemsRequiringTreatmentInput.setEnabled(false);
        }

        if (data.containsKey("birthWeight")) {
            birthWeightInput.setText(getValue(data, "birthWeight"));
            birthWeightInput.setEnabled(false);
        }

        if (data.containsKey("birthLength")) {
            birthLengthInput.setText(getValue(data, "birthLength"));
            birthLengthInput.setEnabled(false);
        }

        if (data.containsKey("birthHeadCirc")) {
            birthHeadCircInput.setText(getValue(data, "birthHeadCirc"));
            birthHeadCircInput.setEnabled(false);
        }

        if (data.containsKey("newBornBloodspotScreenTest")) {
            newBornBloodspotScreenTestInput.setText(getValue(data, "newBornBloodspotScreenTest"));
            newBornBloodspotScreenTestInput.setEnabled(false);
        }

        if (data.containsKey("vitaminKGiven")) {
            setSpinnerSelection(vitaminKGivenInput, getValue(data, "vitaminKGiven"));
            vitaminKGivenInput.setEnabled(false);
        }

        if (data.containsKey("vitaminKGiven1st")) {
            vitaminKGiven1stInput.setText(getValue(data, "vitaminKGiven1st"));
            vitaminKGiven1stInput.setEnabled(false);
        }

        if (data.containsKey("vitaminKGiven2nd")) {
            vitaminKGiven2ndInput.setText(getValue(data, "vitaminKGiven2nd"));
            vitaminKGiven2ndInput.setEnabled(false);
        }

        if (data.containsKey("vitaminKGiven3rd")) {
            vitaminKGiven3rdInput.setText(getValue(data, "vitaminKGiven3rd"));
            vitaminKGiven3rdInput.setEnabled(false);
        }

        if (data.containsKey("hepBImmunisationGiven")) {
            hepBImmunisationGivenInput.setText(getValue(data, "hepBImmunisationGiven"));
            hepBImmunisationGivenInput.setEnabled(false);
        }

        if (data.containsKey("hepBImmunoglobinGiven")) {
            hepBImmunoglobinGivenInput.setText(getValue(data, "hepBImmunoglobinGiven"));
            hepBImmunoglobinGivenInput.setEnabled(false);
        }

        if (data.containsKey("postPartumComplications")) {
            postPartumComplicationsInput.setText(getValue(data, "postPartumComplications"));
            postPartumComplicationsInput.setEnabled(false);
        }

        if (data.containsKey("feedingAtDischarge")) {
            setSpinnerSelection(feedingAtDischargeInput, getValue(data, "feedingAtDischarge"));
            feedingAtDischargeInput.setEnabled(false);
        }

        if (data.containsKey("difficultiesWithFeeding")) {
            difficultiesWithFeedingInput.setText(getValue(data, "difficultiesWithFeeding"));
            difficultiesWithFeedingInput.setEnabled(false);
        }

        if (data.containsKey("dateOfDischarge")) {
            dateOfDischargeInput.setText(getValue(data, "dateOfDischarge"));
            dateOfDischargeInput.setEnabled(false);
        }

        if (data.containsKey("dischargeWeight")) {
            dischargeWeightInput.setText(getValue(data, "dischargeWeight"));
            dischargeWeightInput.setEnabled(false);
        }

        if (data.containsKey("headCirc")) {
            headCircInput.setText(getValue(data, "headCirc"));
            headCircInput.setEnabled(false);
        }

        if (data.containsKey("printName")) {
            printNameInput.setText(getValue(data, "printName"));
            printNameInput.setEnabled(false);
        }

        if (data.containsKey("signature")) {
            String imageEncoded = getValue(data, "signature");
            if (imageEncoded != null && !imageEncoded.isEmpty()) {
                signatureCanvas = new SignatureCanvas(getContext(), imageEncoded);
                signatureContainer.removeAllViews();
                signatureContainer.addView(signatureCanvas);
                // Disable the signature pad to prevent editing
                signatureCanvas.setEnabled(false);
            }
        }

        if (data.containsKey("designation")) {
            designationInput.setText(getValue(data, "designation"));
            designationInput.setEnabled(false);
        }
    }

    private String getValue(HashMap<String, String[]> data, String key) {
        String[] values = data.get(key);
        if (values != null && values.length > 0 && values[0] != null) {
            return values[0];
        }
        return "";
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null || value.isEmpty()) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            String item = spinner.getItemAtPosition(i).toString();
            if (item.equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private boolean hasData(HashMap<String, String[]> data) {
        if (data == null || data.isEmpty()) return false;

        for (String key : data.keySet()) {
            String[] values = data.get(key);
            if (values != null && values.length > 0 && values[0] != null && !values[0].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void setFieldsEditable(boolean editable) {
        // Handle regular EditText fields
        EditText[] editableFields = new EditText[]{
                fnameInput, lnameInput, birthFacilityInput,
                mothersFNameInput, mothersLNameInput, mothersMRNInput, pregnancyComplicationsInput,
                labourInput, labourComplicationsInput, typeofBirthInput,
                estimatedGestationInput, apgarScore1MinInput, apgarScore5MinInput,
                abnormalitiesAtBirthInput, problemsRequiringTreatmentInput,
                birthWeightInput, birthLengthInput, birthHeadCircInput,
                newBornBloodspotScreenTestInput, vitaminKGiven1stInput, vitaminKGiven2ndInput, vitaminKGiven3rdInput,
                postPartumComplicationsInput, difficultiesWithFeedingInput, dateOfDischargeInput,
                dischargeWeightInput, headCircInput, printNameInput, designationInput,
                hepBImmunisationGivenInput, hepBImmunoglobinGivenInput
        };

        for (EditText field : editableFields) {
            field.setEnabled(editable);
        }

        // Handle Spinner fields
        Spinner[] spinnerFields = new Spinner[]{
                sexInput, bloodGroupInput, vitaminKGivenInput, feedingAtDischargeInput
        };

        for (Spinner spinner : spinnerFields) {
            spinner.setEnabled(editable);
        }

        if (signatureCanvas != null) {
            signatureCanvas.setEnabled(editable);
        }

        // Handle Date Fields Separately
        for (EditText dateField : dateFields) {
            if (!editable) {
                dateField.setOnClickListener(null); // Remove OnClickListener
                dateField.setTextColor(getResources().getColor(R.color.disabled_text)); // Optional: Change text color
            } else {
                setupDatePicker(dateField, getLayoutForDateField(dateField));
                dateField.setTextColor(getResources().getColor(R.color.enabled_text)); // Optional: Reset text color
            }
        }
    }

    private TextInputLayout getLayoutForDateField(EditText dateField) {
        if (dateField == birthDateInput) return birthDateLayout;
        if (dateField == newBornBloodspotScreenTestInput) return newBornBloodspotScreenTestLayout;
        if (dateField == vitaminKGiven1stInput) return vitaminKGiven1stLayout;
        if (dateField == vitaminKGiven2ndInput) return vitaminKGiven2ndLayout;
        if (dateField == vitaminKGiven3rdInput) return vitaminKGiven3rdLayout;
        if (dateField == dateOfDischargeInput) return dateOfDischargeLayout;
        if (dateField == hepBImmunisationGivenInput) return hepBImmunisationGivenLayout;
        if (dateField == hepBImmunoglobinGivenInput) return hepBImmunoglobinGivenLayout;
        return null;
    }

    private void saveBirthDetails() {
        // Extract all input values
        String fname = fnameInput.getText().toString().trim();
        String lname = lnameInput.getText().toString().trim();
        String birthFacility = birthFacilityInput.getText().toString().trim();
        String birthDate = birthDateInput.getText().toString().trim();
        String sex = sexInput.getSelectedItem().toString().trim();

        String mothersFName = mothersFNameInput.getText().toString().trim();
        String mothersLName = mothersLNameInput.getText().toString().trim();
        String mothersMRN = mothersMRNInput.getText().toString().trim();
        String pregnancyComplications = pregnancyComplicationsInput.getText().toString().trim();
        String bloodGroup = bloodGroupInput.getSelectedItem().toString().trim();
        String labour = labourInput.getText().toString().trim();
        String labourComplications = labourComplicationsInput.getText().toString().trim();
        String typeofBirth = typeofBirthInput.getText().toString().trim();

        String estimatedGestation = estimatedGestationInput.getText().toString().trim();
        String apgarScore1Min = apgarScore1MinInput.getText().toString().trim();
        String apgarScore5Min = apgarScore5MinInput.getText().toString().trim();
        String abnormalitiesAtBirth = abnormalitiesAtBirthInput.getText().toString().trim();
        String problemsRequiringTreatment = problemsRequiringTreatmentInput.getText().toString().trim();
        String birthWeight = birthWeightInput.getText().toString().trim();
        String birthLength = birthLengthInput.getText().toString().trim();
        String birthHeadCirc = birthHeadCircInput.getText().toString().trim();

        String newBornBloodspotScreenTest = newBornBloodspotScreenTestInput.getText().toString().trim();
        String vitaminKGiven = vitaminKGivenInput.getSelectedItem().toString().trim();
        String vitaminKGiven1st = vitaminKGiven1stInput.getText().toString().trim();
        String vitaminKGiven2nd = vitaminKGiven2ndInput.getText().toString().trim();
        String vitaminKGiven3rd = vitaminKGiven3rdInput.getText().toString().trim();

        String postPartumComplications = postPartumComplicationsInput.getText().toString().trim();
        String feedingAtDischarge = feedingAtDischargeInput.getSelectedItem().toString().trim();
        String difficultiesWithFeeding = difficultiesWithFeedingInput.getText().toString().trim();
        String dateOfDischarge = dateOfDischargeInput.getText().toString().trim();
        String dischargeWeight = dischargeWeightInput.getText().toString().trim();
        String headCirc = headCircInput.getText().toString().trim();

        String printName = printNameInput.getText().toString().trim();
        String signature = signatureCanvas.convertCanvas();
        String designation = designationInput.getText().toString().trim();

        String hepBImmunisationGiven = hepBImmunisationGivenInput.getText().toString().trim();
        String hepBImmunoglobinGiven = hepBImmunoglobinGivenInput.getText().toString().trim();

        boolean isValid = true;
        View focusView = null;

        resetErrors();

// Validation

// Validate First Name
        if (fname.isEmpty()) {
            fnameLayout.setError("First Name is required.");
            isValid = false;
            if (focusView == null) focusView = fnameInput;
        } else if (fname.length() > 31) {
            fnameLayout.setError("First Name must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = fnameInput;
        } else {
            fnameLayout.setError(null);
        }

// Validate Last Name
        if (lname.isEmpty()) {
            lnameLayout.setError("Last Name is required.");
            isValid = false;
            if (focusView == null) focusView = lnameInput;
        } else if (lname.length() > 31) {
            lnameLayout.setError("Last Name must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = lnameInput;
        } else {
            lnameLayout.setError(null);
        }

// Validate Birth Facility
        if (birthFacility.isEmpty()) {
            birthFacilityLayout.setError("Birth Facility is required.");
            isValid = false;
            if (focusView == null) focusView = birthFacilityInput;
        } else if (birthFacility.length() > 63) {
            birthFacilityLayout.setError("Birth Facility must not exceed 63 characters.");
            isValid = false;
            if (focusView == null) focusView = birthFacilityInput;
        } else {
            birthFacilityLayout.setError(null);
        }

// Validate Date of Birth
        if (birthDate.isEmpty()) {
            birthDateLayout.setError("Date of Birth is required.");
            isValid = false;
            if (focusView == null) focusView = birthDateInput;
        } else if (!isValidDate(birthDate)) {
            birthDateLayout.setError("Invalid Date of Birth.");
            isValid = false;
            if (focusView == null) focusView = birthDateInput;
        } else {
            birthDateLayout.setError(null);
        }

// Validate Mother's First Name
        if (mothersFName.isEmpty()) {
            mothersFNameLayout.setError("Mother's First Name is required.");
            isValid = false;
            if (focusView == null) focusView = mothersFNameInput;
        } else if (mothersFName.length() > 31) {
            mothersFNameLayout.setError("Mother's First Name must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = mothersFNameInput;
        } else {
            mothersFNameLayout.setError(null);
        }

// Validate Mother's Last Name
        if (mothersLName.isEmpty()) {
            mothersLNameLayout.setError("Mother's Last Name is required.");
            isValid = false;
            if (focusView == null) focusView = mothersLNameInput;
        } else if (mothersLName.length() > 31) {
            mothersLNameLayout.setError("Mother's Last Name must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = mothersLNameInput;
        } else {
            mothersLNameLayout.setError(null);
        }

// Validate Mother's MRN
        if (mothersMRN.isEmpty()) {
            mothersMRNLayout.setError("Mother's MRN is required.");
            isValid = false;
            if (focusView == null) focusView = mothersMRNInput;
        } else {
            try {
                int mrn = Integer.parseInt(mothersMRN);
                if (mrn <= 0) {
                    mothersMRNLayout.setError("Mother's MRN must be a positive integer.");
                    isValid = false;
                    if (focusView == null) focusView = mothersMRNInput;
                } else {
                    mothersMRNLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                mothersMRNLayout.setError("Mother's MRN must be a valid integer.");
                isValid = false;
                if (focusView == null) focusView = mothersMRNInput;
            }
        }

// Validate Labour
        if (labour.isEmpty()) {
            labourLayout.setError("Labour information is required.");
            isValid = false;
            if (focusView == null) focusView = labourInput;
        } else if (labour.length() > 15) {
            labourLayout.setError("Labour information must not exceed 15 characters.");
            isValid = false;
            if (focusView == null) focusView = labourInput;
        } else {
            labourLayout.setError(null);
        }

// Validate Type of Birth
        if (typeofBirth.isEmpty()) {
            typeofBirthLayout.setError("Type of Birth is required.");
            isValid = false;
            if (focusView == null) focusView = typeofBirthInput;
        } else if (typeofBirth.length() > 15) {
            typeofBirthLayout.setError("Type of Birth must not exceed 15 characters.");
            isValid = false;
            if (focusView == null) focusView = typeofBirthInput;
        } else {
            typeofBirthLayout.setError(null);
        }

// Validate Estimated Gestation
        if (estimatedGestation.isEmpty()) {
            estimatedGestationLayout.setError("Estimated Gestation is required.");
            isValid = false;
            if (focusView == null) focusView = estimatedGestationInput;
        } else {
            try {
                int gestation = Integer.parseInt(estimatedGestation);
                if (gestation <= 0) {
                    estimatedGestationLayout.setError("Estimated Gestation must be a positive integer.");
                    isValid = false;
                    if (focusView == null) focusView = estimatedGestationInput;
                } else {
                    estimatedGestationLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                estimatedGestationLayout.setError("Estimated Gestation must be a valid integer.");
                isValid = false;
                if (focusView == null) focusView = estimatedGestationInput;
            }
        }

// Validate APGAR Score at 1 Minute
        if (apgarScore1Min.isEmpty()) {
            apgarScore1MinLayout.setError("APGAR Score at 1 Minute is required.");
            isValid = false;
            if (focusView == null) focusView = apgarScore1MinInput;
        } else {
            try {
                int apgar1 = Integer.parseInt(apgarScore1Min);
                if (apgar1 <= 0 || apgar1 >= 10) {
                    apgarScore1MinLayout.setError("APGAR Score at 1 Minute must be between 1 and 9.");
                    isValid = false;
                    if (focusView == null) focusView = apgarScore1MinInput;
                } else {
                    apgarScore1MinLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                apgarScore1MinLayout.setError("APGAR Score at 1 Minute must be a valid integer.");
                isValid = false;
                if (focusView == null) focusView = apgarScore1MinInput;
            }
        }

// Validate APGAR Score at 5 Minutes
        if (apgarScore5Min.isEmpty()) {
            apgarScore5MinLayout.setError("APGAR Score at 5 Minutes is required.");
            isValid = false;
            if (focusView == null) focusView = apgarScore5MinInput;
        } else {
            try {
                int apgar5 = Integer.parseInt(apgarScore5Min);
                if (apgar5 <= 0 || apgar5 >= 10) {
                    apgarScore5MinLayout.setError("APGAR Score at 5 Minutes must be between 1 and 9.");
                    isValid = false;
                    if (focusView == null) focusView = apgarScore5MinInput;
                } else {
                    apgarScore5MinLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                apgarScore5MinLayout.setError("APGAR Score at 5 Minutes must be a valid integer.");
                isValid = false;
                if (focusView == null) focusView = apgarScore5MinInput;
            }
        }

// Validate Abnormalities at Birth
        if (abnormalitiesAtBirth.isEmpty()) {
            abnormalitiesAtBirthLayout.setError("Abnormalities at Birth is required.");
            isValid = false;
            if (focusView == null) focusView = abnormalitiesAtBirthInput;
        } else if (abnormalitiesAtBirth.length() > 255) {
            abnormalitiesAtBirthLayout.setError("Abnormalities at Birth must not exceed 255 characters.");
            isValid = false;
            if (focusView == null) focusView = abnormalitiesAtBirthInput;
        } else {
            abnormalitiesAtBirthLayout.setError(null);
        }

// Validate Problems Requiring Treatment
        if (problemsRequiringTreatment.isEmpty()) {
            problemsRequiringTreatmentLayout.setError("Problems Requiring Treatment is required.");
            isValid = false;
            if (focusView == null) focusView = problemsRequiringTreatmentInput;
        } else if (problemsRequiringTreatment.length() > 255) {
            problemsRequiringTreatmentLayout.setError("Problems Requiring Treatment must not exceed 255 characters.");
            isValid = false;
            if (focusView == null) focusView = problemsRequiringTreatmentInput;
        } else {
            problemsRequiringTreatmentLayout.setError(null);
        }

// Validate Birth Weight
        if (birthWeight.isEmpty()) {
            birthWeightLayout.setError("Birth Weight is required.");
            isValid = false;
            if (focusView == null) focusView = birthWeightInput;
        } else {
            try {
                double weight = Double.parseDouble(birthWeight);
                if (weight <= 0) {
                    birthWeightLayout.setError("Birth Weight must be a positive number.");
                    isValid = false;
                    if (focusView == null) focusView = birthWeightInput;
                } else {
                    birthWeightLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                birthWeightLayout.setError("Birth Weight must be a valid number.");
                isValid = false;
                if (focusView == null) focusView = birthWeightInput;
            }
        }

        // Validate Birth Length
        if (birthLength.isEmpty()) {
            birthLengthLayout.setError("Birth Length is required.");
            isValid = false;
            if (focusView == null) focusView = birthLengthInput;
        } else {
            try {
                double length = Double.parseDouble(birthLength);
                if (length <= 0) {
                    birthLengthLayout.setError("Birth Length must be a positive number.");
                    isValid = false;
                    if (focusView == null) focusView = birthLengthInput;
                } else {
                    birthLengthLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                birthLengthLayout.setError("Birth Length must be a valid number.");
                isValid = false;
                if (focusView == null) focusView = birthLengthInput;
            }
        }

// Validate Birth Head Circumference
        if (birthHeadCirc.isEmpty()) {
            birthHeadCircLayout.setError("Birth Head Circumference is required.");
            isValid = false;
            if (focusView == null) focusView = birthHeadCircInput;
        } else {
            try {
                double headCircValue = Double.parseDouble(birthHeadCirc);
                if (headCircValue <= 0) {
                    birthHeadCircLayout.setError("Birth Head Circumference must be a positive number.");
                    isValid = false;
                    if (focusView == null) focusView = birthHeadCircInput;
                } else {
                    birthHeadCircLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                birthHeadCircLayout.setError("Birth Head Circumference must be a valid number.");
                isValid = false;
                if (focusView == null) focusView = birthHeadCircInput;
            }
        }

// Validate Labour Complications
        if (labourComplications.isEmpty()) {
            labourComplicationsLayout.setError("Labour Complications is required.");
            isValid = false;
            if (focusView == null) focusView = labourComplicationsInput;
        } else if (labourComplications.length() > 255) {
            labourComplicationsLayout.setError("Labour Complications must not exceed 255 characters.");
            isValid = false;
            if (focusView == null) focusView = labourComplicationsInput;
        } else {
            labourComplicationsLayout.setError(null);
        }

// Validate Pregnancy Complications
        if (pregnancyComplications.isEmpty()) {
            pregnancyComplicationsLayout.setError("Pregnancy Complications is required.");
            isValid = false;
            if (focusView == null) focusView = pregnancyComplicationsInput;
        } else if (pregnancyComplications.length() > 255) {
            pregnancyComplicationsLayout.setError("Pregnancy Complications must not exceed 255 characters.");
            isValid = false;
            if (focusView == null) focusView = pregnancyComplicationsInput;
        } else {
            pregnancyComplicationsLayout.setError(null);
        }

// Validate New Born Bloodspot Screen Test Date
        if (newBornBloodspotScreenTest.isEmpty()) {
            newBornBloodspotScreenTestLayout.setError("New Born Bloodspot Screen Test date is required.");
            isValid = false;
            if (focusView == null) focusView = newBornBloodspotScreenTestInput;
        } else if (!isValidDate(newBornBloodspotScreenTest)) {
            newBornBloodspotScreenTestLayout.setError("Invalid New Born Bloodspot Screen Test date.");
            isValid = false;
            if (focusView == null) focusView = newBornBloodspotScreenTestInput;
        } else {
            newBornBloodspotScreenTestLayout.setError(null);
        }

// Validate Vitamin K Given 1st Date
        if (vitaminKGiven1st.isEmpty()) {
            vitaminKGiven1stLayout.setError("Vitamin K Given 1st date is required.");
            isValid = false;
            if (focusView == null) focusView = vitaminKGiven1stInput;
        } else if (!isValidDate(vitaminKGiven1st)) {
            vitaminKGiven1stLayout.setError("Invalid Vitamin K Given 1st date.");
            isValid = false;
            if (focusView == null) focusView = vitaminKGiven1stInput;
        } else {
            vitaminKGiven1stLayout.setError(null);
        }

// Validate Vitamin K Given 2nd Date
        if (vitaminKGiven2nd.isEmpty()) {
            vitaminKGiven2ndLayout.setError("Vitamin K Given 2nd date is required.");
            isValid = false;
            if (focusView == null) focusView = vitaminKGiven2ndInput;
        } else if (!isValidDate(vitaminKGiven2nd)) {
            vitaminKGiven2ndLayout.setError("Invalid Vitamin K Given 2nd date.");
            isValid = false;
            if (focusView == null) focusView = vitaminKGiven2ndInput;
        } else {
            vitaminKGiven2ndLayout.setError(null);
        }

// Validate Vitamin K Given 3rd Date
        if (vitaminKGiven3rd.isEmpty()) {
            vitaminKGiven3rdLayout.setError("Vitamin K Given 3rd date is required.");
            isValid = false;
            if (focusView == null) focusView = vitaminKGiven3rdInput;
        } else if (!isValidDate(vitaminKGiven3rd)) {
            vitaminKGiven3rdLayout.setError("Invalid Vitamin K Given 3rd date.");
            isValid = false;
            if (focusView == null) focusView = vitaminKGiven3rdInput;
        } else {
            vitaminKGiven3rdLayout.setError(null);
        }

// Validate Hep B Immunisation Given Date
        if (hepBImmunisationGiven.isEmpty()) {
            hepBImmunisationGivenLayout.setError("Hep B Immunisation Given date is required.");
            isValid = false;
            if (focusView == null) focusView = hepBImmunisationGivenInput;
        } else if (!isValidDate(hepBImmunisationGiven)) {
            hepBImmunisationGivenLayout.setError("Invalid Hep B Immunisation Given date.");
            isValid = false;
            if (focusView == null) focusView = hepBImmunisationGivenInput;
        } else {
            hepBImmunisationGivenLayout.setError(null);
        }

// Validate Hep B Immunoglobulin Given Date
        if (hepBImmunoglobinGiven.isEmpty()) {
            hepBImmunoglobinGivenLayout.setError("Hep B Immunoglobulin Given date is required.");
            isValid = false;
            if (focusView == null) focusView = hepBImmunoglobinGivenInput;
        } else if (!isValidDate(hepBImmunoglobinGiven)) {
            hepBImmunoglobinGivenLayout.setError("Invalid Hep B Immunoglobulin Given date.");
            isValid = false;
            if (focusView == null) focusView = hepBImmunoglobinGivenInput;
        } else {
            hepBImmunoglobinGivenLayout.setError(null);
        }

// Validate Postpartum Complications
        if (postPartumComplications.isEmpty()) {
            postPartumComplicationsLayout.setError("Postpartum Complications is required.");
            isValid = false;
            if (focusView == null) focusView = postPartumComplicationsInput;
        } else if (postPartumComplications.length() > 255) {
            postPartumComplicationsLayout.setError("Postpartum Complications must not exceed 255 characters.");
            isValid = false;
            if (focusView == null) focusView = postPartumComplicationsInput;
        } else {
            postPartumComplicationsLayout.setError(null);
        }

// Validate Difficulties With Feeding
        if (difficultiesWithFeeding.isEmpty()) {
            difficultiesWithFeedingLayout.setError("Difficulties With Feeding is required.");
            isValid = false;
            if (focusView == null) focusView = difficultiesWithFeedingInput;
        } else if (difficultiesWithFeeding.length() > 255) {
            difficultiesWithFeedingLayout.setError("Difficulties With Feeding must not exceed 255 characters.");
            isValid = false;
            if (focusView == null) focusView = difficultiesWithFeedingInput;
        } else {
            difficultiesWithFeedingLayout.setError(null);
        }

// Validate Date of Discharge
        if (dateOfDischarge.isEmpty()) {
            dateOfDischargeLayout.setError("Date of Discharge is required.");
            isValid = false;
            if (focusView == null) focusView = dateOfDischargeInput;
        } else if (!isValidDate(dateOfDischarge)) {
            dateOfDischargeLayout.setError("Invalid Date of Discharge.");
            isValid = false;
            if (focusView == null) focusView = dateOfDischargeInput;
        } else {
            dateOfDischargeLayout.setError(null);
        }

// Validate Discharge Weight
        if (dischargeWeight.isEmpty()) {
            dischargeWeightLayout.setError("Discharge Weight is required.");
            isValid = false;
            if (focusView == null) focusView = dischargeWeightInput;
        } else {
            try {
                double weight = Double.parseDouble(dischargeWeight);
                if (weight <= 0) {
                    dischargeWeightLayout.setError("Discharge Weight must be a positive number.");
                    isValid = false;
                    if (focusView == null) focusView = dischargeWeightInput;
                } else {
                    dischargeWeightLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                dischargeWeightLayout.setError("Discharge Weight must be a valid number.");
                isValid = false;
                if (focusView == null) focusView = dischargeWeightInput;
            }
        }

// Validate Head Circumference
        if (headCirc.isEmpty()) {
            headCircLayout.setError("Head Circumference is required.");
            isValid = false;
            if (focusView == null) focusView = headCircInput;
        } else {
            try {
                double headCircValue = Double.parseDouble(headCirc);
                if (headCircValue <= 0) {
                    headCircLayout.setError("Head Circumference must be a positive number.");
                    isValid = false;
                    if (focusView == null) focusView = headCircInput;
                } else {
                    headCircLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                headCircLayout.setError("Head Circumference must be a valid number.");
                isValid = false;
                if (focusView == null) focusView = headCircInput;
            }
        }

// Validate Print Name
        if (printName.isEmpty()) {
            printNameLayout.setError("Print Name is required.");
            isValid = false;
            if (focusView == null) focusView = printNameInput;
        } else if (printName.length() > 31) {
            printNameLayout.setError("Print Name must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = printNameInput;
        } else {
            printNameLayout.setError(null);
        }

// Validate Signature
        if (signature == null || signature.isEmpty()) {
            signatureError.setVisibility(View.VISIBLE);
            isValid = false;
            if (focusView == null) focusView = signatureContainer;
        } else {
            signatureError.setVisibility(View.GONE);
        }

// Validate Designation
        if (designation.isEmpty()) {
            designationLayout.setError("Designation is required.");
            isValid = false;
            if (focusView == null) focusView = designationInput;
        } else if (designation.length() > 31) {
            designationLayout.setError("Designation must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = designationInput;
        } else {
            designationLayout.setError(null);
        }

// Set focus to the first field with an error
        if (!isValid && focusView != null) {
            focusView.requestFocus();
        }

        if (!isValid) {
            Toast.makeText(getActivity(), "Please fix the errors highlighted on the form.", Toast.LENGTH_LONG).show();
            if (focusView != null) {
                focusView.requestFocus();
            }
            return;
        }

        // Save to Database
        saveToDatabase();
    }

    private void resetErrors() {
        fnameLayout.setError(null);
        lnameLayout.setError(null);
        birthFacilityLayout.setError(null);
        birthDateLayout.setError(null);
        mothersFNameLayout.setError(null);
        mothersLNameLayout.setError(null);
        mothersMRNLayout.setError(null);
        pregnancyComplicationsLayout.setError(null);
        labourLayout.setError(null);
        labourComplicationsLayout.setError(null);
        typeofBirthLayout.setError(null);
        estimatedGestationLayout.setError(null);
        apgarScore1MinLayout.setError(null);
        apgarScore5MinLayout.setError(null);
        abnormalitiesAtBirthLayout.setError(null);
        problemsRequiringTreatmentLayout.setError(null);
        birthWeightLayout.setError(null);
        birthLengthLayout.setError(null);
        birthHeadCircLayout.setError(null);
        newBornBloodspotScreenTestLayout.setError(null);
        vitaminKGiven1stLayout.setError(null);
        vitaminKGiven2ndLayout.setError(null);
        vitaminKGiven3rdLayout.setError(null);
        postPartumComplicationsLayout.setError(null);
        difficultiesWithFeedingLayout.setError(null);
        dateOfDischargeLayout.setError(null);
        dischargeWeightLayout.setError(null);
        headCircLayout.setError(null);
        printNameLayout.setError(null);
        signatureError.setVisibility(View.GONE);
        designationLayout.setError(null);
        hepBImmunisationGivenLayout.setError(null);
        hepBImmunoglobinGivenLayout.setError(null);

        sexInput.setBackgroundResource(android.R.drawable.spinner_background);
        bloodGroupInput.setBackgroundResource(android.R.drawable.spinner_background);
        vitaminKGivenInput.setBackgroundResource(android.R.drawable.spinner_background);
        feedingAtDischargeInput.setBackgroundResource(android.R.drawable.spinner_background);
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            if (date.after(new Date())) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidSex(String sex) {
        return sex.equalsIgnoreCase("M") || sex.equalsIgnoreCase("F");
    }

    private boolean isValidBloodGroup(String bloodGroup) {
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : validGroups) {
            if (group.equalsIgnoreCase(bloodGroup)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidVitaminKGiven(String vitaminKGiven) {
        String[] validOptions = {"Injection", "Oral"};
        for (String option : validOptions) {
            if (option.equalsIgnoreCase(vitaminKGiven)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidFeedingAtDischarge(String feedingAtDischarge) {
        String[] validOptions = {"Breastfeeding", "Formula Feeding", "Mixed Feeding"};
        for (String option : validOptions) {
            if (option.equalsIgnoreCase(feedingAtDischarge)) {
                return true;
            }
        }
        return false;
    }

    private void saveToDatabase() {
        String checkQuery = "SELECT COUNT(*) AS count FROM BirthDetails WHERE childID = ?";
        String[] checkParams = {String.valueOf(childID)};
        char[] checkParamTypes = {'i'};

        try {
            Log.d("Fragment_BirthDetails", "Checking if record exists for childID: " + childID);
            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            int recordCount = 0;
            if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                recordCount = Integer.parseInt(checkResult.get("count")[0]);
            }

            // Extract and validate inputs
            String fname = fnameInput.getText().toString().trim();
            String lname = lnameInput.getText().toString().trim();
            String birthFacility = birthFacilityInput.getText().toString().trim();
            String dob = birthDateInput.getText().toString().trim();
            String sex = sexInput.getSelectedItem().toString().trim();
            String mothersFName = mothersFNameInput.getText().toString().trim();
            String mothersLName = mothersLNameInput.getText().toString().trim();
            String mothersMRN = mothersMRNInput.getText().toString().trim();
            String pregnancyComplications = pregnancyComplicationsInput.getText().toString().trim().isEmpty() ? null : pregnancyComplicationsInput.getText().toString().trim();
            String bloodGroup = bloodGroupInput.getSelectedItem().toString().trim();
            String antiDGigen = "0"; // Default value as per your original code
            String newBornHearingScreenCompleted = "1"; // Default value
            String labour = labourInput.getText().toString().trim();
            String labourComplications = labourComplicationsInput.getText().toString().trim().isEmpty() ? null : labourComplicationsInput.getText().toString().trim();
            String typeofBirth = typeofBirthInput.getText().toString().trim();

            // Parse integers with validation
            int estimatedGestation = Integer.parseInt(estimatedGestationInput.getText().toString().trim());
            int apgarScore1Min = Integer.parseInt(apgarScore1MinInput.getText().toString().trim());
            int apgarScore5Min = Integer.parseInt(apgarScore5MinInput.getText().toString().trim());

            // Parse as double and round to nearest integer
            double birthWeightDouble = Double.parseDouble(birthWeightInput.getText().toString().trim());
            int birthWeight = (int) Math.round(birthWeightDouble);

            double birthLengthDouble = Double.parseDouble(birthLengthInput.getText().toString().trim());
            int birthLength = (int) Math.round(birthLengthDouble);

            double birthHeadCircDouble = Double.parseDouble(birthHeadCircInput.getText().toString().trim());
            int birthHeadCirc = (int) Math.round(birthHeadCircDouble);

            double dischargeWeightDouble = Double.parseDouble(dischargeWeightInput.getText().toString().trim());
            int dischargeWeight = (int) Math.round(dischargeWeightDouble);

            double headCircDouble = Double.parseDouble(headCircInput.getText().toString().trim());
            int headCirc = (int) Math.round(headCircDouble);


            String abnormalitiesAtBirth = abnormalitiesAtBirthInput.getText().toString().trim().isEmpty() ? null : abnormalitiesAtBirthInput.getText().toString().trim();
            String problemsRequiringTreatment = problemsRequiringTreatmentInput.getText().toString().trim().isEmpty() ? null : problemsRequiringTreatmentInput.getText().toString().trim();
            String newBornBloodspotScreenTest = newBornBloodspotScreenTestInput.getText().toString().trim().isEmpty() ? null : newBornBloodspotScreenTestInput.getText().toString().trim();
            String vitaminKGiven = vitaminKGivenInput.getSelectedItem().toString().trim();
            String vitaminKGiven1st = vitaminKGiven1stInput.getText().toString().trim().isEmpty() ? null : vitaminKGiven1stInput.getText().toString().trim();
            String vitaminKGiven2nd = vitaminKGiven2ndInput.getText().toString().trim().isEmpty() ? null : vitaminKGiven2ndInput.getText().toString().trim();
            String vitaminKGiven3rd = vitaminKGiven3rdInput.getText().toString().trim().isEmpty() ? null : vitaminKGiven3rdInput.getText().toString().trim();
            String hepBImmunisationGiven = hepBImmunisationGivenInput.getText().toString().trim().isEmpty() ? null : hepBImmunisationGivenInput.getText().toString().trim();
            String hepBImmunoglobinGiven = hepBImmunoglobinGivenInput.getText().toString().trim().isEmpty() ? null : hepBImmunoglobinGivenInput.getText().toString().trim();
            String postPartumComplications = postPartumComplicationsInput.getText().toString().trim().isEmpty() ? null : postPartumComplicationsInput.getText().toString().trim();
            String feedingAtDischarge = feedingAtDischargeInput.getSelectedItem().toString().trim();
            String difficultiesWithFeeding = difficultiesWithFeedingInput.getText().toString().trim().isEmpty() ? null : difficultiesWithFeedingInput.getText().toString().trim();
            String dateOfDischarge = dateOfDischargeInput.getText().toString().trim().isEmpty() ? null : dateOfDischargeInput.getText().toString().trim();
            String printName = printNameInput.getText().toString().trim();
            String designation = designationInput.getText().toString().trim();

            // Extract the signature from SignatureCanvas
            String signature = signatureCanvas.convertCanvas();

            // Prepare parameters for INSERT or UPDATE
            String[] paramsToUse = new String[]{
                    String.valueOf(childID),
                    fname,
                    lname,
                    birthFacility,
                    dob,
                    sex,
                    mothersFName,
                    mothersLName,
                    mothersMRN,
                    pregnancyComplications,
                    bloodGroup,
                    antiDGigen,
                    newBornHearingScreenCompleted,
                    labour,
                    labourComplications,
                    typeofBirth,
                    String.valueOf(estimatedGestation),
                    String.valueOf(apgarScore1Min),
                    String.valueOf(apgarScore5Min),
                    abnormalitiesAtBirth,
                    problemsRequiringTreatment,
                    String.valueOf(birthWeight),
                    String.valueOf(birthLength),
                    String.valueOf(birthHeadCirc),
                    newBornBloodspotScreenTest,
                    vitaminKGiven,
                    vitaminKGiven1st,
                    vitaminKGiven2nd,
                    vitaminKGiven3rd,
                    hepBImmunisationGiven,
                    hepBImmunoglobinGiven,
                    postPartumComplications,
                    feedingAtDischarge,
                    difficultiesWithFeeding,
                    dateOfDischarge,
                    String.valueOf(dischargeWeight),
                    String.valueOf(headCirc),
                    printName,
                    signature,
                    designation
            };

            char[] paramTypes = new char[]{
                    'i', // childID
                    's', // fname
                    's', // lname
                    's', // birthFacility
                    'd', // DOB
                    's', // sex
                    's', // mothersFName
                    's', // mothersLName
                    'i', // mothersMRN
                    's', // pregnancyComplications
                    's', // bloodGroup
                    'i', // antiDGigen
                    'i', // newBornHearingScreenCompleted
                    's', // labour
                    's', // labourComplications
                    's', // typeofBirth
                    'i', // estimatedGestation
                    'i', // apgarScore1Min
                    'i', // apgarScore5Min
                    's', // abnormalitiesAtBirth
                    's', // problemsRequiringTreatment
                    'i', // birthWeight
                    'i', // birthLength
                    'i', // birthHeadCirc
                    'd', // newBornBloodspotScreenTest
                    's', // vitaminKGiven
                    'd', // vitaminKGiven1st
                    'd', // vitaminKGiven2nd
                    'd', // vitaminKGiven3rd
                    'd', // hepBImmunisationGiven
                    'd', // hepBImmunoglobinGiven
                    's', // postPartumComplications
                    's', // feedingAtDischarge
                    's', // difficultiesWithFeeding
                    'd', // dateOfDischarge
                    'i', // dischargeWeight
                    'i', // headCirc
                    's', // printName
                    's', // signature
                    's'  // designation
            };

            if (recordCount > 0) {
                // Prepare parameters for UPDATE (excluding childID from values, as it's in WHERE clause)
                String[] paramsForUpdate = Arrays.copyOfRange(paramsToUse, 1, paramsToUse.length);
                paramsForUpdate = Arrays.copyOf(paramsForUpdate, paramsForUpdate.length + 1);
                paramsForUpdate[paramsForUpdate.length - 1] = String.valueOf(childID);

                char[] paramTypesForUpdate = Arrays.copyOfRange(paramTypes, 1, paramTypes.length);
                paramTypesForUpdate = Arrays.copyOf(paramTypesForUpdate, paramTypesForUpdate.length + 1);
                paramTypesForUpdate[paramTypesForUpdate.length - 1] = 'i'; // childID type

                String updateQuery = "UPDATE BirthDetails SET " +
                        "fname = ?, lname = ?, birthFacility = ?, DOB = ?, sex = ?, " +
                        "mothersFName = ?, mothersLName = ?, mothersMRN = ?, pregnancyComplications = ?, bloodGroup = ?, " +
                        "antiDGigen = ?, newBornHearingScreenCompleted = ?, " +
                        "labour = ?, labourComplications = ?, typeofBirth = ?, estimatedGestation = ?, apgarScore1Min = ?, " +
                        "apgarScore5Min = ?, abnormalitiesAtBirth = ?, problemsRequiringTreatment = ?, birthWeight = ?, " +
                        "birthLength = ?, birthHeadCirc = ?, newBornBloodspotScreenTest = ?, vitaminKGiven = ?, " +
                        "vitaminKGiven1st = ?, vitaminKGiven2nd = ?, vitaminKGiven3rd = ?, hepBImmunisationGiven = ?, " +
                        "hepBImmunoglobinGiven = ?, postPartumComplications = ?, feedingAtDischarge = ?, " +
                        "difficultiesWithFeeding = ?, dateOfDischarge = ?, dischargeWeight = ?, headCirc = ?, " +
                        "printName = ?, signature = ?, designation = ? WHERE childID = ?";

                boolean isSuccess = dbHelper.update(updateQuery, paramsForUpdate, paramTypesForUpdate);
                handleSaveResult(isSuccess);
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO BirthDetails (" +
                        "childID, fname, lname, birthFacility, DOB, sex, mothersFName, mothersLName, mothersMRN, " +
                        "pregnancyComplications, bloodGroup, antiDGigen, newBornHearingScreenCompleted, labour, " +
                        "labourComplications, typeofBirth, estimatedGestation, apgarScore1Min, apgarScore5Min, " +
                        "abnormalitiesAtBirth, problemsRequiringTreatment, birthWeight, birthLength, birthHeadCirc, " +
                        "newBornBloodspotScreenTest, vitaminKGiven, vitaminKGiven1st, vitaminKGiven2nd, vitaminKGiven3rd, " +
                        "hepBImmunisationGiven, hepBImmunoglobinGiven, postPartumComplications, feedingAtDischarge, " +
                        "difficultiesWithFeeding, dateOfDischarge, dischargeWeight, headCirc, printName, signature, " +
                        "designation" +
                        ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

                boolean isSuccess = dbHelper.update(insertQuery, paramsToUse, paramTypes);
                handleSaveResult(isSuccess);
            }

        } catch (Exception e) {
            Log.e("Fragment_BirthDetails", "Error saving data", e);
            Toast.makeText(getActivity(), "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveResult(boolean isSuccess) {
        if (isSuccess) {
            Toast.makeText(getActivity(), "Birth details saved successfully", Toast.LENGTH_SHORT).show();
            setFieldsEditable(false); // Make fields non-editable after saving
        }
        if (signatureCanvas != null) {
            signatureCanvas.setEnabled(false);
        }
        else {
            Toast.makeText(getActivity(), "Failed to save birth details", Toast.LENGTH_SHORT).show();
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
