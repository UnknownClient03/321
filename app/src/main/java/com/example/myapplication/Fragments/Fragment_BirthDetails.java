package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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
    private EditText headCircInput, printNameInput, signatureInput, designationInput;
    private EditText hepBImmunisationGivenInput, hepBImmunoglobinGivenInput;
    private int childID;
    private int guardianID;

    // Declare variables as instance variables
    private String fname, lname, birthFacility, birthDate, sex;
    private String mothersFName, mothersLName, pregnancyComplications, bloodGroup;
    private String labour, labourComplications, typeofBirth;
    private String abnormalitiesAtBirth, problemsRequiringTreatment, postPartumComplications;
    private String feedingAtDischarge, difficultiesWithFeeding, dateOfDischarge;
    private String printName, signature, designation;
    private String vitaminKGiven;

    // Integer variables stored as Strings
    private String mothersMRN, estimatedGestation, apgarScore1Min, apgarScore5Min;
    private String birthWeight, birthLength, birthHeadCirc;
    private String dischargeWeight, headCirc;

    // Date variables
    private String newBornBloodspotScreenTest, vitaminKGiven1st, vitaminKGiven2nd, vitaminKGiven3rd;
    private String hepBImmunisationGiven, hepBImmunoglobinGiven;

    public Fragment_BirthDetails() {
        // Required empty public constructor
    }

    public static Fragment_BirthDetails newInstance() {
        return new Fragment_BirthDetails();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve childID and guardianID from fragment arguments
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1);
            guardianID = args.getInt("guardianID", -1);

            // Check if IDs are valid
            if (childID == -1 || guardianID == -1) {
                Log.e("Fragment_BirthDetails", "Invalid childID or guardianID received");
                Toast.makeText(getActivity(), "Invalid child or guardian ID", Toast.LENGTH_SHORT).show();
                // Handle the error appropriately
            } else {
                Log.d("Fragment_BirthDetails", "Received childID: " + childID);
                Log.d("Fragment_BirthDetails", "Received guardianID: " + guardianID);
            }
        } else {
            Log.e("Fragment_BirthDetails", "No arguments provided to fragment");
            Toast.makeText(getActivity(), "No child or guardian ID provided", Toast.LENGTH_SHORT).show();
            // Handle the error appropriately
        }

        // Initialize database helper
        dbHelper = new SQLConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_birth_details, container, false);

        // Initialize all your input fields here
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
        signatureInput = view.findViewById(R.id.editText_signature);
        designationInput = view.findViewById(R.id.editText_designation);

        hepBImmunisationGivenInput = view.findViewById(R.id.editText_hepBImmunisationGiven);
        hepBImmunoglobinGivenInput = view.findViewById(R.id.editText_hepBImmunoglobinGiven);

        // Set up date pickers for date fields
        setupDatePicker(birthDateInput);
        setupDatePicker(newBornBloodspotScreenTestInput); // Added date picker for Newborn Bloodspot Screen Test
        setupDatePicker(vitaminKGiven1stInput);
        setupDatePicker(vitaminKGiven2ndInput);
        setupDatePicker(vitaminKGiven3rdInput);
        setupDatePicker(dateOfDischargeInput);
        setupDatePicker(hepBImmunisationGivenInput);
        setupDatePicker(hepBImmunoglobinGivenInput);

        // Load data from database and autofill fields
        loadDataFromDatabase();

        // Set up save button
        Button btnSave = view.findViewById(R.id.button_saveBirthDetails);
        btnSave.setOnClickListener(v -> saveBirthDetails());

        return view;
    }

    private void setupDatePicker(EditText dateInput) {
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        dateInput.setText(dateStr);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void loadDataFromDatabase() {
        // First, check if there is existing data in BirthDetails for this childID
        String checkQuery = "SELECT * FROM BirthDetails WHERE childID = ?";
        String[] checkParams = {String.valueOf(childID)};
        char[] checkParamTypes = {'i'};

        try {
            Log.d("Fragment_BirthDetails", "Attempting to load data from BirthDetails for childID: " + childID);
            HashMap<String, String[]> data = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            if (hasData(data)) {
                // Data exists in BirthDetails table, populate the fields
                populateFields(data);
            } else {
                // No data in BirthDetails, load basic info from Child table
                String childQuery = "SELECT fname, lname, DOB, sex FROM Child WHERE ID = ?";
                String[] childParams = {String.valueOf(childID)};
                char[] childParamTypes = {'i'};

                Log.d("Fragment_BirthDetails", "Attempting to load data from Child for ID: " + childID);
                HashMap<String, String[]> childData = dbHelper.select(childQuery, childParams, childParamTypes);

                if (hasData(childData)) {
                    fnameInput.setText(getValue(childData, "fname"));
                    lnameInput.setText(getValue(childData, "lname"));
                    birthDateInput.setText(getValue(childData, "DOB"));
                    setSpinnerSelection(sexInput, getValue(childData, "sex"));
                }
            }
        } catch (Exception e) {
            Log.e("Fragment_BirthDetails", "Error loading data", e);
            Toast.makeText(getActivity(), "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFields(HashMap<String, String[]> data) {
        fnameInput.setText(getValue(data, "fname"));
        lnameInput.setText(getValue(data, "lname"));
        birthFacilityInput.setText(getValue(data, "birthFacility"));
        birthDateInput.setText(getValue(data, "DOB"));
        setSpinnerSelection(sexInput, getValue(data, "sex"));
        mothersFNameInput.setText(getValue(data, "mothersFName"));
        mothersLNameInput.setText(getValue(data, "mothersLName"));
        mothersMRNInput.setText(getValue(data, "mothersMRN"));
        pregnancyComplicationsInput.setText(getValue(data, "pregnancyComplications"));
        setSpinnerSelection(bloodGroupInput, getValue(data, "bloodGroup"));
        labourInput.setText(getValue(data, "labour"));
        labourComplicationsInput.setText(getValue(data, "labourComplications"));
        typeofBirthInput.setText(getValue(data, "typeofBirth"));
        estimatedGestationInput.setText(getValue(data, "estimatedGestation"));
        apgarScore1MinInput.setText(getValue(data, "apgarScore1Min"));
        apgarScore5MinInput.setText(getValue(data, "apgarScore5Min"));
        abnormalitiesAtBirthInput.setText(getValue(data, "abnormalitiesAtBirth"));
        problemsRequiringTreatmentInput.setText(getValue(data, "problemsRequiringTreatment"));
        birthWeightInput.setText(getValue(data, "birthWeight"));
        birthLengthInput.setText(getValue(data, "birthLength"));
        birthHeadCircInput.setText(getValue(data, "birthHeadCirc"));
        newBornBloodspotScreenTestInput.setText(getValue(data, "newBornBloodspotScreenTest"));
        setSpinnerSelection(vitaminKGivenInput, getValue(data, "vitaminKGiven"));
        vitaminKGiven1stInput.setText(getValue(data, "vitaminKGiven1st"));
        vitaminKGiven2ndInput.setText(getValue(data, "vitaminKGiven2nd"));
        vitaminKGiven3rdInput.setText(getValue(data, "vitaminKGiven3rd"));
        hepBImmunisationGivenInput.setText(getValue(data, "hepBImmunisationGiven"));
        hepBImmunoglobinGivenInput.setText(getValue(data, "hepBImmunoglobinGiven"));
        postPartumComplicationsInput.setText(getValue(data, "postPartumComplications"));
        setSpinnerSelection(feedingAtDischargeInput, getValue(data, "feedingAtDischarge"));
        difficultiesWithFeedingInput.setText(getValue(data, "difficultiesWithFeeding"));
        dateOfDischargeInput.setText(getValue(data, "dateOfDischarge"));
        dischargeWeightInput.setText(getValue(data, "dischargeWeight"));
        headCircInput.setText(getValue(data, "headCirc"));
        printNameInput.setText(getValue(data, "printName"));
        signatureInput.setText(getValue(data, "signature"));
        designationInput.setText(getValue(data, "designation"));
    }

    private String getValue(HashMap<String, String[]> data, String key) {
        String[] values = data.get(key);
        if (values != null && values.length > 0) {
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
            if (values != null && values.length > 0) {
                return true;
            }
        }
        return false;
    }

    private void saveBirthDetails() {
        // Collect input values from EditText and Spinner fields
        fname = fnameInput.getText().toString().trim();
        lname = lnameInput.getText().toString().trim();
        birthFacility = birthFacilityInput.getText().toString().trim();
        birthDate = birthDateInput.getText().toString().trim();
        sex = sexInput.getSelectedItem().toString().trim();

        mothersFName = mothersFNameInput.getText().toString().trim();
        mothersLName = mothersLNameInput.getText().toString().trim();
        mothersMRN = mothersMRNInput.getText().toString().trim();
        pregnancyComplications = pregnancyComplicationsInput.getText().toString().trim();
        bloodGroup = bloodGroupInput.getSelectedItem().toString().trim();
        labour = labourInput.getText().toString().trim();
        labourComplications = labourComplicationsInput.getText().toString().trim();
        typeofBirth = typeofBirthInput.getText().toString().trim();

        estimatedGestation = estimatedGestationInput.getText().toString().trim();
        apgarScore1Min = apgarScore1MinInput.getText().toString().trim();
        apgarScore5Min = apgarScore5MinInput.getText().toString().trim();
        abnormalitiesAtBirth = abnormalitiesAtBirthInput.getText().toString().trim();
        problemsRequiringTreatment = problemsRequiringTreatmentInput.getText().toString().trim();
        birthWeight = birthWeightInput.getText().toString().trim();
        birthLength = birthLengthInput.getText().toString().trim();
        birthHeadCirc = birthHeadCircInput.getText().toString().trim();

        newBornBloodspotScreenTest = newBornBloodspotScreenTestInput.getText().toString().trim();
        vitaminKGiven = vitaminKGivenInput.getSelectedItem().toString().trim();
        vitaminKGiven1st = vitaminKGiven1stInput.getText().toString().trim();
        vitaminKGiven2nd = vitaminKGiven2ndInput.getText().toString().trim();
        vitaminKGiven3rd = vitaminKGiven3rdInput.getText().toString().trim();

        postPartumComplications = postPartumComplicationsInput.getText().toString().trim();
        feedingAtDischarge = feedingAtDischargeInput.getSelectedItem().toString().trim();
        difficultiesWithFeeding = difficultiesWithFeedingInput.getText().toString().trim();
        dateOfDischarge = dateOfDischargeInput.getText().toString().trim();
        dischargeWeight = dischargeWeightInput.getText().toString().trim();
        headCirc = headCircInput.getText().toString().trim();

        printName = printNameInput.getText().toString().trim();
        signature = signatureInput.getText().toString().trim();
        designation = designationInput.getText().toString().trim();

        hepBImmunisationGiven = hepBImmunisationGivenInput.getText().toString().trim();
        hepBImmunoglobinGiven = hepBImmunoglobinGivenInput.getText().toString().trim();

        // Validate required fields
        if (fname.isEmpty() || lname.isEmpty() || birthDate.isEmpty() || sex.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date formats
        if (!isValidDate(birthDate)) {
            Toast.makeText(getActivity(), "Birth date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newBornBloodspotScreenTest.isEmpty() && !isValidDate(newBornBloodspotScreenTest)) {
            Toast.makeText(getActivity(), "Newborn Bloodspot Screen Test date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!vitaminKGiven1st.isEmpty() && !isValidDate(vitaminKGiven1st)) {
            Toast.makeText(getActivity(), "Vitamin K 1st dose date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!vitaminKGiven2nd.isEmpty() && !isValidDate(vitaminKGiven2nd)) {
            Toast.makeText(getActivity(), "Vitamin K 2nd dose date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!vitaminKGiven3rd.isEmpty() && !isValidDate(vitaminKGiven3rd)) {
            Toast.makeText(getActivity(), "Vitamin K 3rd dose date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hepBImmunisationGiven.isEmpty() && !isValidDate(hepBImmunisationGiven)) {
            Toast.makeText(getActivity(), "Hep B Immunisation Given date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hepBImmunoglobinGiven.isEmpty() && !isValidDate(hepBImmunoglobinGiven)) {
            Toast.makeText(getActivity(), "Hep B Immunoglobin Given date must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dateOfDischarge.isEmpty() && !isValidDate(dateOfDischarge)) {
            Toast.makeText(getActivity(), "Date of Discharge must be in yyyy-MM-dd format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate CHECK constraints
        if (!vitaminKGiven.equals("injection") && !vitaminKGiven.equals("oral")) {
            Toast.makeText(getActivity(), "Vitamin K Given must be 'injection' or 'oral'", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!feedingAtDischarge.equals("Breastfeeding") && !feedingAtDischarge.equals("Formula Feeding") && !feedingAtDischarge.equals("Mixed Feeding")) {
            Toast.makeText(getActivity(), "Feeding at Discharge must be 'Breastfeeding', 'Formula Feeding', or 'Mixed Feeding'", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int apgar1 = Integer.parseInt(apgarScore1Min);
            if (apgar1 < 0 || apgar1 > 10) {
                Toast.makeText(getActivity(), "Apgar Score at 1 Min must be between 0 and 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            apgarScore1MinInput.setError("Invalid number");
            return;
        }

        try {
            int apgar5 = Integer.parseInt(apgarScore5Min);
            if (apgar5 < 0 || apgar5 > 10) {
                Toast.makeText(getActivity(), "Apgar Score at 5 Min must be between 0 and 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            apgarScore5MinInput.setError("Invalid number");
            return;
        }

        // Save to the database
        saveToDatabase();
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
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

            // Prepare parameters in the exact order as columns in SQL query
            String[] paramsToUse = new String[]{
                    String.valueOf(childID),           // childID
                    fname,                             // fname
                    lname,                             // lname
                    birthFacility,                     // birthFacility
                    birthDate,                         // DOB
                    sex,                               // sex
                    mothersFName,                      // mothersFName
                    mothersLName,                      // mothersLName
                    mothersMRN,                        // mothersMRN
                    pregnancyComplications,            // pregnancyComplications
                    bloodGroup,                        // bloodGroup
                    labour,                            // labour
                    labourComplications,               // labourComplications
                    typeofBirth,                       // typeofBirth
                    estimatedGestation,                // estimatedGestation
                    apgarScore1Min,                    // apgarScore1Min
                    apgarScore5Min,                    // apgarScore5Min
                    abnormalitiesAtBirth,              // abnormalitiesAtBirth
                    problemsRequiringTreatment,        // problemsRequiringTreatment
                    birthWeight,                       // birthWeight
                    birthLength,                       // birthLength
                    birthHeadCirc,                     // birthHeadCirc
                    newBornBloodspotScreenTest.isEmpty() ? null : newBornBloodspotScreenTest, // newBornBloodspotScreenTest
                    vitaminKGiven,                     // vitaminKGiven
                    vitaminKGiven1st.isEmpty() ? null : vitaminKGiven1st, // vitaminKGiven1st
                    vitaminKGiven2nd.isEmpty() ? null : vitaminKGiven2nd, // vitaminKGiven2nd
                    vitaminKGiven3rd.isEmpty() ? null : vitaminKGiven3rd, // vitaminKGiven3rd
                    hepBImmunisationGiven.isEmpty() ? null : hepBImmunisationGiven, // hepBImmunisationGiven
                    hepBImmunoglobinGiven.isEmpty() ? null : hepBImmunoglobinGiven, // hepBImmunoglobinGiven
                    postPartumComplications,           // postPartumComplications
                    feedingAtDischarge,                // feedingAtDischarge
                    difficultiesWithFeeding,           // difficultiesWithFeeding
                    dateOfDischarge.isEmpty() ? null : dateOfDischarge, // dateOfDischarge
                    dischargeWeight,                   // dischargeWeight
                    headCirc,                          // headCirc
                    printName,                         // printName
                    signature,                         // signature
                    designation                        // designation
            };

            char[] paramTypes = new char[]{
                    'i', // childID
                    's', // fname
                    's', // lname
                    's', // birthFacility
                    'd', // DOB (birthDate)
                    's', // sex
                    's', // mothersFName
                    's', // mothersLName
                    'i', // mothersMRN
                    's', // pregnancyComplications
                    's', // bloodGroup
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

            boolean isSuccess;
            if (recordCount > 0) {
                // For UPDATE, exclude childID from parameters and add it at the end
                String[] paramsForUpdate = new String[paramsToUse.length - 1 + 1]; // Exclude childID at index 0, add at end
                System.arraycopy(paramsToUse, 1, paramsForUpdate, 0, paramsToUse.length - 1);
                paramsForUpdate[paramsForUpdate.length - 1] = String.valueOf(childID); // WHERE childID = ?

                char[] paramTypesForUpdate = new char[paramTypes.length - 1 + 1];
                System.arraycopy(paramTypes, 1, paramTypesForUpdate, 0, paramTypes.length - 1);
                paramTypesForUpdate[paramTypesForUpdate.length - 1] = 'i';

                String updateQuery = "UPDATE BirthDetails SET " +
                        "fname = ?, lname = ?, birthFacility = ?, DOB = ?, sex = ?, " +
                        "mothersFName = ?, mothersLName = ?, mothersMRN = ?, pregnancyComplications = ?, bloodGroup = ?, " +
                        "labour = ?, labourComplications = ?, typeofBirth = ?, estimatedGestation = ?, apgarScore1Min = ?, " +
                        "apgarScore5Min = ?, abnormalitiesAtBirth = ?, problemsRequiringTreatment = ?, birthWeight = ?, " +
                        "birthLength = ?, birthHeadCirc = ?, newBornBloodspotScreenTest = ?, vitaminKGiven = ?, " +
                        "vitaminKGiven1st = ?, vitaminKGiven2nd = ?, vitaminKGiven3rd = ?, hepBImmunisationGiven = ?, " +
                        "hepBImmunoglobinGiven = ?, postPartumComplications = ?, feedingAtDischarge = ?, " +
                        "difficultiesWithFeeding = ?, dateOfDischarge = ?, dischargeWeight = ?, headCirc = ?, " +
                        "printName = ?, signature = ?, designation = ? WHERE childID = ?";

                Log.d("Fragment_BirthDetails", "Executing UPDATE query for childID: " + childID);
                isSuccess = dbHelper.update(updateQuery, paramsForUpdate, paramTypesForUpdate);
            } else {
                String insertQuery = "INSERT INTO BirthDetails (" +
                        "childID, fname, lname, birthFacility, DOB, sex, mothersFName, mothersLName, mothersMRN, " +
                        "pregnancyComplications, bloodGroup, labour, labourComplications, typeofBirth, estimatedGestation, " +
                        "apgarScore1Min, apgarScore5Min, abnormalitiesAtBirth, problemsRequiringTreatment, birthWeight, " +
                        "birthLength, birthHeadCirc, newBornBloodspotScreenTest, vitaminKGiven, vitaminKGiven1st, " +
                        "vitaminKGiven2nd, vitaminKGiven3rd, hepBImmunisationGiven, hepBImmunoglobinGiven, " +
                        "postPartumComplications, feedingAtDischarge, difficultiesWithFeeding, dateOfDischarge, " +
                        "dischargeWeight, headCirc, printName, signature, designation) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                Log.d("Fragment_BirthDetails", "Executing INSERT query for childID: " + childID);
                isSuccess = dbHelper.update(insertQuery, paramsToUse, paramTypes);
            }

            if (isSuccess) {
                Toast.makeText(getActivity(), "Birth details saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to save birth details", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Fragment_BirthDetails", "Error saving birth details", e);
            Toast.makeText(getActivity(), "Error saving birth details", Toast.LENGTH_SHORT).show();
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
