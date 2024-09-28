package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Fragment_NewbornExamination extends Fragment {

    private SQLConnection dbHelper;
    private EditText dobInput, ageInput, fnameInput, lnameInput;
    private Spinner sexInput;
    private CheckBox anyConcernsInput;
    private EditText anyConcernsCommentInput;
    private EditText examinerInput, designationInput, signatureInput, dateInput;
    private int childID;

    // Declarations for the 15 checks
    private CheckBox[] checkBoxes_isNormal = new CheckBox[15];
    private EditText[] editTexts_comment = new EditText[15];
    private String[] checkNames = {
            "Head and Fontanelles",
            "Eyes",
            "Ears",
            "Mouth and palate",
            "Cardiovascular",
            "Femoral Pulses",
            "Respiratory rate",
            "Abdomen and umbilicus",
            "Anus",
            "Genitalia",
            "Testes fully descended R/L",
            "Musculo-skeletal",
            "Hips",
            "Skin",
            "Reflexes"
    };

    public Fragment_NewbornExamination() {
        // Required empty public constructor
    }

    public static Fragment_NewbornExamination newInstance() {
        return new Fragment_NewbornExamination();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_newborn_examination, container, false);

        // Initialize SQL connection
        dbHelper = new SQLConnection(); // Use your secure method to initialize the database connection
        Log.d("Fragment_NewbornExamination", "SQLConnection initialized");

        // Get childID from bundle
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1); // Default to -1 if not found
            Log.d("Fragment_NewbornExamination", "Received childID: " + childID);
        } else {
            Log.e("Fragment_NewbornExamination", "No arguments passed to the fragment.");
            return view;
        }

        // Initialize form inputs from XML
        initializeInputs(view);

        // Set up date inputs to only allow yyyy-MM-dd format via DatePickerDialog
        setupDatePickers();

        Button saveButton = view.findViewById(R.id.button_saveNewbornExamination);

        // Fetch existing details from the database and pre-fill the form
        retrieveNewbornExamination(childID);

        // Set up save button click listener
        saveButton.setOnClickListener(v -> saveNewbornExamination());

        return view;
    }

    private void initializeInputs(View view) {
        dobInput = view.findViewById(R.id.editText_dob);
        ageInput = view.findViewById(R.id.editText_age);
        fnameInput = view.findViewById(R.id.editText_fname);
        lnameInput = view.findViewById(R.id.editText_lname);
        sexInput = view.findViewById(R.id.spinner_sex);

        anyConcernsInput = view.findViewById(R.id.checkBox_anyConcerns);
        anyConcernsCommentInput = view.findViewById(R.id.editText_anyConcernsComment);

        examinerInput = view.findViewById(R.id.editText_examiner);
        designationInput = view.findViewById(R.id.editText_designation);
        signatureInput = view.findViewById(R.id.editText_signature);
        dateInput = view.findViewById(R.id.editText_date);

        // Initialize spinners
        setupSpinners();

        // Initialize checks
        initializeChecks(view);
    }

    private void setupSpinners() {
        // Sex Spinner
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sex_options, android.R.layout.simple_spinner_item);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexInput.setAdapter(sexAdapter);
    }

    private void setupDatePickers() {
        // Disable manual input and set up DatePickerDialog for date fields
        setupDateField(dobInput);
        setupDateField(dateInput);
    }

    private void setupDateField(EditText dateField) {
        dateField.setInputType(InputType.TYPE_NULL);
        dateField.setFocusable(false);
        dateField.setOnClickListener(v -> showDatePickerDialog(dateField));
    }

    private void showDatePickerDialog(final EditText dateField) {
        final Calendar calendar = Calendar.getInstance();
        int year, month, day;

        // Pre-set the date picker to the current value if available
        String currentDateString = dateField.getText().toString();
        if (!currentDateString.isEmpty()) {
            String[] parts = currentDateString.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1; // Month is 0-based
            day = Integer.parseInt(parts[2]);
        } else {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the date to 'yyyy-MM-dd'
            String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            dateField.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void initializeChecks(View view) {
        for (int i = 0; i < checkNames.length; i++) {
            String isNormalId = "checkBox_isNormal_" + checkNames[i].replace(" ", "").replace("/", "").replace("-", "").replace("&", "and").replace(".", "").toLowerCase();
            String commentId = "editText_comment_" + checkNames[i].replace(" ", "").replace("/", "").replace("-", "").replace("&", "and").replace(".", "").toLowerCase();

            int isNormalResId = getResources().getIdentifier(isNormalId, "id", getActivity().getPackageName());
            int commentResId = getResources().getIdentifier(commentId, "id", getActivity().getPackageName());

            checkBoxes_isNormal[i] = view.findViewById(isNormalResId);
            editTexts_comment[i] = view.findViewById(commentResId);
        }
    }

    private void retrieveNewbornExamination(int childID) {
        String query = "SELECT * FROM NewbornExamination WHERE childID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            if (!dbHelper.isConn()) {
                Log.e("Fragment_NewbornExamination", "Error: Unable to connect to the database.");
                return;
            }

            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result == null || result.get("fname") == null || result.get("fname").length == 0) {
                Log.d("Fragment_NewbornExamination", "No records found in NewbornExamination. Falling back to Child table...");
                retrieveChildDetails(childID);
            } else {
                prefillForm(result);
            }

            // Retrieve checks from NBTable
            retrieveNBTable(childID);

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error retrieving newborn examination details: " + e.getMessage());
        }
    }

    private void retrieveChildDetails(int childID) {
        String query = "SELECT fname, lname, DOB, sex FROM Child WHERE ID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result == null || result.isEmpty()) {
                Log.e("Fragment_NewbornExamination", "Error: No valid result from the Child table query.");
                return;
            }

            prefillFormFromChild(result);

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error retrieving child details: " + e.getMessage());
        }
    }

    private void prefillForm(HashMap<String, String[]> result) {
        // DOB
        if (result.get("DOB") != null && result.get("DOB").length > 0) {
            dobInput.setText(result.get("DOB")[0]);
            // Calculate age in days if age is not provided
            if (result.get("age") == null || result.get("age").length == 0 || result.get("age")[0].isEmpty()) {
                int ageInDays = calculateAgeInDays(result.get("DOB")[0]);
                ageInput.setText(String.valueOf(ageInDays));
            } else {
                ageInput.setText(result.get("age")[0]);
            }
        }
        // First Name
        if (result.get("fname") != null && result.get("fname").length > 0) {
            fnameInput.setText(result.get("fname")[0]);
        }
        // Last Name
        if (result.get("lname") != null && result.get("lname").length > 0) {
            lnameInput.setText(result.get("lname")[0]);
        }
        // Sex
        if (result.get("sex") != null && result.get("sex").length > 0) {
            setSpinnerSelection(sexInput, result.get("sex")[0]);
        }
        // Any Concerns
        if (result.get("anyConcerns") != null && result.get("anyConcerns").length > 0) {
            anyConcernsInput.setChecked(result.get("anyConcerns")[0].equals("1"));
        }
        // Any Concerns Comment
        if (result.get("anyConcernsComment") != null && result.get("anyConcernsComment").length > 0) {
            anyConcernsCommentInput.setText(result.get("anyConcernsComment")[0]);
        }
        // Examiner
        if (result.get("examiner") != null && result.get("examiner").length > 0) {
            examinerInput.setText(result.get("examiner")[0]);
        }
        // Designation
        if (result.get("designation") != null && result.get("designation").length > 0) {
            designationInput.setText(result.get("designation")[0]);
        }
        // Signature
        if (result.get("signature") != null && result.get("signature").length > 0) {
            signatureInput.setText(result.get("signature")[0]);
        }
        // Date
        if (result.get("date") != null && result.get("date").length > 0) {
            dateInput.setText(result.get("date")[0]);
        }
    }

    private void prefillFormFromChild(HashMap<String, String[]> result) {
        // DOB
        if (result.get("DOB") != null && result.get("DOB").length > 0) {
            dobInput.setText(result.get("DOB")[0]);
            // Calculate age in days
            int ageInDays = calculateAgeInDays(result.get("DOB")[0]);
            ageInput.setText(String.valueOf(ageInDays));
        }
        // First Name
        if (result.get("fname") != null && result.get("fname").length > 0) {
            fnameInput.setText(result.get("fname")[0]);
        }
        // Last Name
        if (result.get("lname") != null && result.get("lname").length > 0) {
            lnameInput.setText(result.get("lname")[0]);
        }
        // Sex
        if (result.get("sex") != null && result.get("sex").length > 0) {
            setSpinnerSelection(sexInput, result.get("sex")[0]);
        }
    }

    private int calculateAgeInDays(String dobStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date dob = sdf.parse(dobStr);
            Date today = new Date();

            long diffInMillis = today.getTime() - dob.getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            return (int) diffInDays;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void retrieveNBTable(int childID) {
        String query = "SELECT * FROM NBTable WHERE childID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (result == null || result.isEmpty()) {
                Log.d("Fragment_NewbornExamination", "No records found in NBTable.");
                return;
            }

            String[] checks = result.get("_check");
            String[] isNormals = result.get("isNormal");
            String[] comments = result.get("comment");

            for (int i = 0; i < checks.length; i++) {
                for (int j = 0; j < checkNames.length; j++) {
                    if (checks[i].equals(checkNames[j])) {
                        checkBoxes_isNormal[j].setChecked(isNormals[i].equals("1"));
                        editTexts_comment[j].setText(comments[i]);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error retrieving NBTable details: " + e.getMessage());
        }
    }

    private void saveNewbornExamination() {
        String dob = dobInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String fname = fnameInput.getText().toString().trim();
        String lname = lnameInput.getText().toString().trim();
        String sex = sexInput.getSelectedItem().toString().trim();

        boolean anyConcerns = anyConcernsInput.isChecked();
        String anyConcernsComment = anyConcernsCommentInput.getText().toString().trim();

        String examiner = examinerInput.getText().toString().trim();
        String designation = designationInput.getText().toString().trim();
        String signature = signatureInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        // Validate required fields
        if (fname.isEmpty() || lname.isEmpty() || dob.isEmpty() || ageStr.isEmpty() || sex.isEmpty() || examiner.isEmpty() || designation.isEmpty() || signature.isEmpty() || date.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert age to integer
        int age = 0;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to the database
        saveToDatabase(dob, age, fname, lname, sex, anyConcerns, anyConcernsComment, examiner, designation, signature, date);

        // Save the checks to NBTable
        saveChecksToDatabase();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value.trim())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void saveToDatabase(String dob, int age, String fname, String lname, String sex, boolean anyConcerns, String anyConcernsComment, String examiner, String designation, String signature, String date) {
        String checkQuery = "SELECT COUNT(*) AS count FROM NewbornExamination WHERE childID = ?";
        String[] checkParams = {String.valueOf(childID)};
        char[] checkParamTypes = {'i'};

        try {
            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            int recordCount = 0;
            if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                recordCount = Integer.parseInt(checkResult.get("count")[0]);
            }

            if (recordCount > 0) {
                // Update existing record
                String updateQuery = "UPDATE NewbornExamination SET DOB = ?, age = ?, fname = ?, lname = ?, sex = ?, anyConcerns = ?, anyConcernsComment = ?, examiner = ?, designation = ?, signature = ?, date = ? WHERE childID = ?";
                String[] paramsToUpdate = {
                        dob,
                        String.valueOf(age),
                        fname,
                        lname,
                        sex,
                        anyConcerns ? "1" : "0",
                        anyConcernsComment,
                        examiner,
                        designation,
                        signature,
                        date,
                        String.valueOf(childID)
                };
                char[] paramTypes = {
                        's', 'i', 's', 's', 's', 'i', 's', 's', 's', 's', 's', 'i'
                };

                boolean isSuccess = dbHelper.update(updateQuery, paramsToUpdate, paramTypes);
                if (isSuccess) {
                    Toast.makeText(getActivity(), "Newborn examination updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update newborn examination", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO NewbornExamination (childID, DOB, age, fname, lname, sex, anyConcerns, anyConcernsComment, examiner, designation, signature, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                String[] paramsToInsert = {
                        String.valueOf(childID),
                        dob,
                        String.valueOf(age),
                        fname,
                        lname,
                        sex,
                        anyConcerns ? "1" : "0",
                        anyConcernsComment,
                        examiner,
                        designation,
                        signature,
                        date
                };
                char[] paramTypes = {
                        'i', 's', 'i', 's', 's', 's', 'i', 's', 's', 's', 's', 's'
                };

                boolean isSuccess = dbHelper.update(insertQuery, paramsToInsert, paramTypes);
                if (isSuccess) {
                    Toast.makeText(getActivity(), "Newborn examination inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to insert newborn examination", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error saving newborn examination: " + e.getMessage());
            Toast.makeText(getActivity(), "Error saving newborn examination", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChecksToDatabase() {
        for (int i = 0; i < checkNames.length; i++) {
            boolean isNormal = checkBoxes_isNormal[i].isChecked();
            String comment = editTexts_comment[i].getText().toString().trim();
            saveCheckToDatabase(checkNames[i], isNormal, comment);
        }
    }

    private void saveCheckToDatabase(String checkName, boolean isNormal, String comment) {
        String checkQuery = "SELECT COUNT(*) AS count FROM NBTable WHERE childID = ? AND _check = ?";
        String[] checkParams = {String.valueOf(childID), checkName};
        char[] checkParamTypes = {'i', 's'};

        try {
            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            int recordCount = 0;
            if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                recordCount = Integer.parseInt(checkResult.get("count")[0]);
            }

            if (recordCount > 0) {
                // Update existing record
                String updateQuery = "UPDATE NBTable SET isNormal = ?, comment = ? WHERE childID = ? AND _check = ?";
                String[] paramsToUpdate = {
                        isNormal ? "1" : "0",
                        comment,
                        String.valueOf(childID),
                        checkName
                };
                char[] paramTypes = {'i', 's', 'i', 's'};

                boolean isSuccess = dbHelper.update(updateQuery, paramsToUpdate, paramTypes);
                if (!isSuccess) {
                    Log.e("Fragment_NewbornExamination", "Failed to update check: " + checkName);
                }
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO NBTable (childID, _check, isNormal, comment) VALUES (?, ?, ?, ?)";
                String[] paramsToInsert = {
                        String.valueOf(childID),
                        checkName,
                        isNormal ? "1" : "0",
                        comment
                };
                char[] paramTypes = {'i', 's', 'i', 's'};

                boolean isSuccess = dbHelper.update(insertQuery, paramsToInsert, paramTypes);
                if (!isSuccess) {
                    Log.e("Fragment_NewbornExamination", "Failed to insert check: " + checkName);
                }
            }
        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error saving check " + checkName + ": " + e.getMessage());
            Toast.makeText(getActivity(), "Error saving check: " + checkName, Toast.LENGTH_SHORT).show();
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
