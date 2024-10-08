package com.example.myapplication.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;
import com.example.myapplication.SignatureCanvas;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Fragment_NewbornExamination extends Fragment {

    private SQLConnection dbHelper;
    private Spinner sexInput;
    private EditText dobInput, ageInput, fnameInput, lnameInput;
    private CheckBox anyConcernsInput;
    private EditText anyConcernsCommentInput;
    private EditText examinerInput, designationInput, dateInput;
    private int childID;

    // Signature-related variables
    private SignatureCanvas signatureCanvas;
    private FrameLayout signatureContainer;
    private TextView signatureError;

    // TextInputLayouts for validation
    private TextInputLayout dobLayout, ageLayout, fnameLayout, lnameLayout;
    private TextInputLayout anyConcernsCommentLayout;
    private TextInputLayout examinerLayout, designationLayout, dateLayout;

    // Declarations for the 15 checks
    private CheckBox[] checkBoxes_isNormal = new CheckBox[15];
    private EditText[] editTexts_comment = new EditText[15];
    private TextInputLayout[] editTexts_commentLayout = new TextInputLayout[15];
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

    // List to manage all date fields
    private List<EditText> dateFields;

    public Fragment_NewbornExamination() {
        // Required empty public constructor
    }

    public static Fragment_NewbornExamination newInstance() {
        return new Fragment_NewbornExamination();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new SQLConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_newborn_examination, container, false);

        // Initialize form inputs and layouts
        initializeTextInputLayouts(view);
        initializeInputs(view);
        setupSpinners();

        // Initialize dateFields before setting up date pickers
        dateFields = Arrays.asList(dobInput, dateInput);
        setupDatePickers();

        // Initialize the signature canvas and add it to the container
        signatureCanvas = new SignatureCanvas(getContext());
        signatureContainer.addView(signatureCanvas);

        // Initialize checks
        initializeChecks(view);

        // Get childID from bundle
        Bundle args = getArguments();
        if (args != null) {
            childID = args.getInt("childID", -1); // Default to -1 if not found
            if (childID == -1) {
                Log.e("Fragment_NewbornExamination", "Invalid childID received");
                Toast.makeText(getActivity(), "Invalid child ID", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Fragment_NewbornExamination", "Received childID: " + childID);
            }
        } else {
            Log.e("Fragment_NewbornExamination", "No arguments passed to the fragment.");
            Toast.makeText(getActivity(), "No child ID provided.", Toast.LENGTH_LONG).show();
        }

        // Fetch existing details from the database and pre-fill the form
        retrieveNewbornExamination(childID);

        Button saveButton = view.findViewById(R.id.button_saveNewbornExamination);

        // Set up save button click listener
        saveButton.setOnClickListener(v -> saveNewbornExamination());

        return view;
    }

    private void initializeTextInputLayouts(View view) {
        dobLayout = view.findViewById(R.id.textInputLayout_dob);
        ageLayout = view.findViewById(R.id.textInputLayout_age);
        fnameLayout = view.findViewById(R.id.textInputLayout_fname);
        lnameLayout = view.findViewById(R.id.textInputLayout_lname);
        anyConcernsCommentLayout = view.findViewById(R.id.textInputLayout_anyConcernsComment);
        examinerLayout = view.findViewById(R.id.textInputLayout_examiner);
        designationLayout = view.findViewById(R.id.textInputLayout_designation);
        dateLayout = view.findViewById(R.id.textInputLayout_date);

        // Initialize TextInputLayouts for comments in checks
        for (int i = 0; i < checkNames.length; i++) {
            String commentLayoutId = "textInputLayout_comment_" + checkNames[i]
                    .replace(" ", "").replace("/", "").replace("-", "").replace("&", "and")
                    .replace(".", "").toLowerCase();
            int resId = getResources().getIdentifier(commentLayoutId, "id", getActivity().getPackageName());
            editTexts_commentLayout[i] = view.findViewById(resId);
        }
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
        dateInput = view.findViewById(R.id.editText_date);

        // Initialize signature-related views
        signatureContainer = view.findViewById(R.id.signatureContainer);
        signatureError = view.findViewById(R.id.signatureError);
    }

    private void setupSpinners() {
        // Sex Spinner
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sex_options, android.R.layout.simple_spinner_item);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexInput.setAdapter(sexAdapter);
    }

    private void setupDatePickers() {
        if (dateFields == null) {
            Log.e("Fragment_NewbornExamination", "dateFields is null. Cannot set up date pickers.");
            return;
        }
        for (EditText dateField : dateFields) {
            setupDatePicker(dateField, getLayoutForDateField(dateField));
        }
    }

    private void setupDatePicker(EditText dateInput, TextInputLayout layout) {
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
                        Log.e("Fragment_NewbornExamination", "Invalid date format: " + currentDate);
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
                        layout.setError(null);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

    private TextInputLayout getLayoutForDateField(EditText dateField) {
        if (dateField == dobInput) return dobLayout;
        if (dateField == dateInput) return dateLayout;
        return null;
    }

    private void initializeChecks(View view) {
        for (int i = 0; i < checkNames.length; i++) {
            String isNormalId = "checkBox_isNormal_" + checkNames[i]
                    .replace(" ", "").replace("/", "").replace("-", "").replace("&", "and")
                    .replace(".", "").toLowerCase();
            String commentId = "editText_comment_" + checkNames[i]
                    .replace(" ", "").replace("/", "").replace("-", "").replace("&", "and")
                    .replace(".", "").toLowerCase();

            int isNormalResId = getResources().getIdentifier(isNormalId, "id", getActivity().getPackageName());
            int commentResId = getResources().getIdentifier(commentId, "id", getActivity().getPackageName());

            checkBoxes_isNormal[i] = view.findViewById(isNormalResId);
            editTexts_comment[i] = view.findViewById(commentResId);

            // Disable comment EditText initially if "isNormal" is checked
            final int index = i;
            checkBoxes_isNormal[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (fieldsAreEditable()) {
                    editTexts_comment[index].setEnabled(!isChecked);
                    if (isChecked) {
                        editTexts_comment[index].setText("");
                        editTexts_commentLayout[index].setError(null);
                    } else {
                        editTexts_comment[index].setTextColor(getResources().getColor(R.color.enabled_text, null));
                    }
                }
            });
        }
    }

    private void retrieveNewbornExamination(int childID) {
        String query = "SELECT * FROM NewbornExamination WHERE childID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            Log.d("Fragment_NewbornExamination", "Attempting to load data from NewbornExamination for childID: " + childID);
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (hasData(result)) {
                Log.d("Fragment_NewbornExamination", "Data found in NewbornExamination. Populating fields.");
                populateFields(result);
                setAutofilledFieldsEditable(false);
                setFieldsEditable(false);
            } else {
                Log.d("Fragment_NewbornExamination", "No data found in NewbornExamination. Attempting to load from Child table.");
                retrieveChildDetails(childID);
            }

            // Retrieve checks from NBTable
            retrieveNBTable(childID);

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error retrieving newborn examination details", e);
            Toast.makeText(getActivity(), "Error loading data.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasData(HashMap<String, String[]> data) {
        if (data == null || data.isEmpty() || data.size() == 0) return false;

        for (String key : data.keySet()) {
            String[] values = data.get(key);
            if (values != null && values.length > 0 && values[0] != null && !values[0].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void populateFields(HashMap<String, String[]> data) {
        if (data.containsKey("DOB")) {
            dobInput.setText(getValue(data, "DOB"));
        }

        if (data.containsKey("age")) {
            ageInput.setText(getValue(data, "age"));
        }

        if (data.containsKey("fname")) {
            fnameInput.setText(getValue(data, "fname"));
        }

        if (data.containsKey("lname")) {
            lnameInput.setText(getValue(data, "lname"));
        }

        if (data.containsKey("sex")) {
            setSpinnerSelection(sexInput, getValue(data, "sex"));
        }

        if (data.containsKey("anyConcerns")) {
            anyConcernsInput.setChecked(getValue(data, "anyConcerns").equals("1"));
        }

        if (data.containsKey("anyConcernsComment")) {
            anyConcernsCommentInput.setText(getValue(data, "anyConcernsComment"));
        }

        if (data.containsKey("examiner")) {
            examinerInput.setText(getValue(data, "examiner"));
        }

        if (data.containsKey("designation")) {
            designationInput.setText(getValue(data, "designation"));
        }

        if (data.containsKey("signature")) {
            String imageEncoded = getValue(data, "signature");
            if (imageEncoded != null && !imageEncoded.isEmpty()) {
                // Create a new SignatureCanvas with the Base64 image
                signatureCanvas = new SignatureCanvas(getContext(), imageEncoded);
                signatureContainer.removeAllViews();
                signatureContainer.addView(signatureCanvas);
                // Disable the signature pad to prevent editing
                signatureCanvas.setEnabled(false);
            }
        }

        if (data.containsKey("date")) {
            dateInput.setText(getValue(data, "date"));
        }

        // Calculate age if DOB is present
        if (data.containsKey("DOB")) {
            int ageInDays = calculateAgeInDays(getValue(data, "DOB"));
            ageInput.setText(String.valueOf(ageInDays));
        }

        Log.d("Fragment_NewbornExamination", "Fields populated.");
    }

    private void retrieveChildDetails(int childID) {
        String query = "SELECT fname, lname, DOB, sex FROM Child WHERE ID = ?";
        String[] params = {String.valueOf(childID)};
        char[] paramTypes = {'i'};

        try {
            HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

            if (hasData(result)) {
                prefillFormFromChild(result);
                disableSpecificFields(); // Disable only specific autofilled fields
            } else {
                Log.e("Fragment_NewbornExamination", "No data found in Child table for childID: " + childID);
                Toast.makeText(getActivity(), "No data found for the given child ID.", Toast.LENGTH_SHORT).show();
                setFieldsEditable(true); // Make fields editable as there's no existing data
            }

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error retrieving child details", e);
            Toast.makeText(getActivity(), "Error loading child details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void prefillFormFromChild(HashMap<String, String[]> data) {
        // DOB
        if (data.get("DOB") != null && data.get("DOB").length > 0) {
            dobInput.setText(data.get("DOB")[0]);
            // Calculate age in days
            int ageInDays = calculateAgeInDays(data.get("DOB")[0]);
            ageInput.setText(String.valueOf(ageInDays));
        }
        // First Name
        if (data.get("fname") != null && data.get("fname").length > 0) {
            fnameInput.setText(data.get("fname")[0]);
        }
        // Last Name
        if (data.get("lname") != null && data.get("lname").length > 0) {
            lnameInput.setText(data.get("lname")[0]);
        }
        // Sex
        if (data.get("sex") != null && data.get("sex").length > 0) {
            setSpinnerSelection(sexInput, data.get("sex")[0]);
        }

        Log.d("Fragment_NewbornExamination", "Form prefixed from Child table.");
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

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null || value.isEmpty()) return;

        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value.trim())) {
                spinner.setSelection(i);
                break;
            }
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

            if (checks == null || isNormals == null || comments == null) {
                Log.e("Fragment_NewbornExamination", "Incomplete data in NBTable.");
                return;
            }

            for (int i = 0; i < checks.length; i++) {
                for (int j = 0; j < checkNames.length; j++) {
                    if (checks[i].equalsIgnoreCase(checkNames[j])) {
                        checkBoxes_isNormal[j].setChecked(isNormals[i].equals("1"));
                        editTexts_comment[j].setText(comments[i]);
                        editTexts_comment[j].setEnabled(!isNormals[i].equals("1") && fieldsAreEditable());
                        if (!isNormals[i].equals("1")) {
                            editTexts_comment[j].setTextColor(getResources().getColor(R.color.enabled_text, null));
                        } else {
                            editTexts_comment[j].setTextColor(getResources().getColor(R.color.disabled_text, null));
                        }
                        break;
                    }
                }
            }

            Log.d("Fragment_NewbornExamination", "Examination checks retrieved and populated.");

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error retrieving NBTable details: " + e.getMessage());
            Toast.makeText(getActivity(), "Error retrieving examination checks.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveNewbornExamination() {
        // Extract and trim all input values
        String dob = dobInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim(); // This field is autofilled and non-editable
        String fname = fnameInput.getText().toString().trim();
        String lname = lnameInput.getText().toString().trim();
        String sex = sexInput.getSelectedItem().toString().trim();

        boolean anyConcerns = anyConcernsInput.isChecked();
        String anyConcernsComment = anyConcernsCommentInput.getText().toString().trim();

        String examiner = examinerInput.getText().toString().trim();
        String designation = designationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        // Extract the signature from SignatureCanvas
        String signature = signatureCanvas.convertCanvas();

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

        // Validate Date of Birth
        if (dob.isEmpty()) {
            dobLayout.setError("Date of Birth is required.");
            isValid = false;
            if (focusView == null) focusView = dobInput;
        } else if (!isValidDate(dob)) {
            dobLayout.setError("Invalid Date of Birth. Use yyyy-MM-dd and ensure it's not in the future.");
            isValid = false;
            if (focusView == null) focusView = dobInput;
        } else {
            dobLayout.setError(null);
        }

        // Validate Age
        int age = 0;
        if (ageStr.isEmpty()) {
            ageLayout.setError("Age is required.");
            isValid = false;
            if (focusView == null) focusView = ageInput;
        } else {
            try {
                age = Integer.parseInt(ageStr);
                if (age < 0) {
                    ageLayout.setError("Age cannot be negative.");
                    isValid = false;
                    if (focusView == null) focusView = ageInput;
                } else {
                    ageLayout.setError(null);
                }
            } catch (NumberFormatException e) {
                ageLayout.setError("Age must be a valid integer.");
                isValid = false;
                if (focusView == null) focusView = ageInput;
            }
        }

        // Validate Examiner
        if (examiner.isEmpty()) {
            examinerLayout.setError("Examiner is required.");
            isValid = false;
            if (focusView == null) focusView = examinerInput;
        } else if (examiner.length() > 31) {
            examinerLayout.setError("Examiner must not exceed 31 characters.");
            isValid = false;
            if (focusView == null) focusView = examinerInput;
        } else {
            examinerLayout.setError(null);
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

        // Validate Signature
        if (signature == null || signature.isEmpty()) {
            signatureError.setVisibility(View.VISIBLE);
            isValid = false;
            if (focusView == null) focusView = signatureContainer;
        } else {
            signatureError.setVisibility(View.GONE);
        }

        // Validate Date
        if (date.isEmpty()) {
            dateLayout.setError("Date is required.");
            isValid = false;
            if (focusView == null) focusView = dateInput;
        } else if (!isValidDate(date)) {
            dateLayout.setError("Invalid Date. Use yyyy-MM-dd and ensure it's not in the future.");
            isValid = false;
            if (focusView == null) focusView = dateInput;
        } else {
            dateLayout.setError(null);
        }

        // Validate Any Concerns Comment if Any Concerns is checked
        if (anyConcerns) {
            if (anyConcernsComment.isEmpty()) {
                anyConcernsCommentLayout.setError("Comments are required when 'Any Concerns' is checked.");
                isValid = false;
                if (focusView == null) focusView = anyConcernsCommentInput;
            } else if (anyConcernsComment.length() > 255) {
                anyConcernsCommentLayout.setError("Comments must not exceed 255 characters.");
                isValid = false;
                if (focusView == null) focusView = anyConcernsCommentInput;
            } else {
                anyConcernsCommentLayout.setError(null);
            }
        } else {
            anyConcernsCommentLayout.setError(null);
        }

        // Validate NBTable Checks
        for (int i = 0; i < checkNames.length; i++) {
            String checkName = checkNames[i];
            String comment = editTexts_comment[i].getText().toString().trim();

            if (comment.length() > 255) {
                editTexts_commentLayout[i].setError("Comment must not exceed 255 characters.");
                isValid = false;
                if (focusView == null) focusView = editTexts_comment[i];
            } else {
                editTexts_commentLayout[i].setError(null);
            }
        }

        // If any validation failed, show a general Toast and abort the save operation
        if (!isValid) {
            Toast.makeText(getActivity(), "Please fix the errors highlighted on the form.", Toast.LENGTH_LONG).show();
            if (focusView != null) {
                focusView.requestFocus();
            }
            return;
        }

        // Proceed to save data
        saveToDatabase(dob, age, fname, lname, sex, anyConcerns, anyConcernsComment, examiner, designation, signature, date);

        // Save the checks to NBTable
        saveChecksToDatabase();
    }

    private String getValue(HashMap<String, String[]> data, String key) {
        if (data.containsKey(key)) {
            String[] values = data.get(key);
            if (values != null && values.length > 0 && values[0] != null) {
                return values[0];
            }
        }
        return "";
    }

    private void resetErrors() {
        dobLayout.setError(null);
        ageLayout.setError(null);
        fnameLayout.setError(null);
        lnameLayout.setError(null);
        anyConcernsCommentLayout.setError(null);
        examinerLayout.setError(null);
        designationLayout.setError(null);
        dateLayout.setError(null);
        signatureError.setVisibility(View.GONE);

        for (int i = 0; i < checkNames.length; i++) {
            editTexts_commentLayout[i].setError(null);
        }

        // Reset spinner background if previously set to error
        sexInput.setBackgroundResource(android.R.drawable.spinner_background);
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

    private void saveToDatabase(String dob, int age, String fname, String lname, String sex,
                                boolean anyConcerns, String anyConcernsComment,
                                String examiner, String designation, String signature, String date) {
        String checkQuery = "SELECT COUNT(*) AS count FROM NewbornExamination WHERE childID = ?";
        String[] checkParams = {String.valueOf(childID)};
        char[] checkParamTypes = {'i'};

        try {
            Log.d("Fragment_NewbornExamination", "Checking if record exists for childID: " + childID);
            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            int recordCount = 0;
            if (checkResult != null && checkResult.get("count") != null && checkResult.get("count").length > 0) {
                recordCount = Integer.parseInt(checkResult.get("count")[0]);
            }

            // Prepare parameters
            String[] paramsToUse = new String[]{
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

            char[] paramTypes = new char[]{
                    's', // dob
                    'i', // age
                    's', // fname
                    's', // lname
                    's', // sex
                    'i', // anyConcerns
                    's', // anyConcernsComment
                    's', // examiner
                    's', // designation
                    's', // signature
                    's', // date
                    'i'  // childID
            };

            if (recordCount > 0) {
                // Update existing record
                String updateQuery = "UPDATE NewbornExamination SET DOB = ?, age = ?, fname = ?, lname = ?, sex = ?, anyConcerns = ?, anyConcernsComment = ?, examiner = ?, designation = ?, signature = ?, date = ? WHERE childID = ?";
                boolean isSuccess = dbHelper.update(updateQuery, paramsToUse, paramTypes);
                handleSaveResult(isSuccess);
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO NewbornExamination (DOB, age, fname, lname, sex, anyConcerns, anyConcernsComment, examiner, designation, signature, date, childID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                boolean isSuccess = dbHelper.update(insertQuery, paramsToUse, paramTypes);
                handleSaveResult(isSuccess);
            }

        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error saving data", e);
            Toast.makeText(getActivity(), "Error saving data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveResult(boolean isSuccess) {
        if (isSuccess) {
            Toast.makeText(getActivity(), "Newborn examination saved successfully.", Toast.LENGTH_SHORT).show();
            setAutofilledFieldsEditable(false);
            setFieldsEditable(false);

            // Disable the SignatureCanvas
            if (signatureCanvas != null) {
                signatureCanvas.setEnabled(false);
            }
        } else {
            Toast.makeText(getActivity(), "Failed to save newborn examination.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChecksToDatabase() {
        boolean hasCheckErrors = false; // Flag to track if any check has errors

        for (int i = 0; i < checkNames.length; i++) {
            String checkName = checkNames[i];
            boolean isNormal = checkBoxes_isNormal[i].isChecked();
            String comment = editTexts_comment[i].getText().toString().trim();

            // Validate the check name against the allowed values
            if (!isValidCheckName(checkName)) {
                Log.e("Fragment_NewbornExamination", "Invalid check name: " + checkName);
                hasCheckErrors = true;
                continue; // Skip invalid checks
            }

            // Validate comment length
            if (comment.length() > 255) {
                editTexts_commentLayout[i].setError("Comment must not exceed 255 characters.");
                hasCheckErrors = true;
                continue; // Skip saving this comment
            } else {
                editTexts_commentLayout[i].setError(null);
            }

            // Save each check to the database
            saveCheckToDatabase(checkName, isNormal, comment);
        }

        // If there were check errors, inform the user
        if (hasCheckErrors) {
            Toast.makeText(getActivity(), "Please fix the errors in the examination checks.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidCheckName(String checkName) {
        for (String validCheck : checkNames) {
            if (validCheck.equalsIgnoreCase(checkName)) {
                return true;
            }
        }
        return false;
    }

    private void saveCheckToDatabase(String checkName, boolean isNormal, String comment) {
        String checkQuery = "SELECT COUNT(*) AS count FROM NBTable WHERE childID = ? AND _check = ?";
        String[] checkParams = {String.valueOf(childID), checkName};
        char[] paramTypes = {'i', 's'};

        try {
            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, paramTypes);

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
                char[] paramTypesUpdate = {'i', 's', 'i', 's'};

                boolean isSuccess = dbHelper.update(updateQuery, paramsToUpdate, paramTypesUpdate);
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
                char[] paramTypesInsert = {'i', 's', 'i', 's'};

                boolean isSuccess = dbHelper.update(insertQuery, paramsToInsert, paramTypesInsert);
                if (!isSuccess) {
                    Log.e("Fragment_NewbornExamination", "Failed to insert check: " + checkName);
                }
            }
        } catch (Exception e) {
            Log.e("Fragment_NewbornExamination", "Error saving check " + checkName + ": " + e.getMessage());
        }
    }

    private void setAutofilledFieldsEditable(boolean editable) {
        // Disable or enable autofilled EditText fields based on the 'editable' flag
        dobInput.setEnabled(editable);
        ageInput.setEnabled(editable);
        fnameInput.setEnabled(editable);
        lnameInput.setEnabled(editable);
        sexInput.setEnabled(editable);

        // Update UI to reflect disabled state
        if (!editable) {
            dobInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
            ageInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
            fnameInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
            lnameInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
            sexInput.setAlpha(0.5f); // Spinner appearance
        } else {
            dobInput.setTextColor(getResources().getColor(R.color.enabled_text, null));
            ageInput.setTextColor(getResources().getColor(R.color.enabled_text, null));
            fnameInput.setTextColor(getResources().getColor(R.color.enabled_text, null));
            lnameInput.setTextColor(getResources().getColor(R.color.enabled_text, null));
            sexInput.setAlpha(1.0f); // Restore spinner appearance
        }

        Log.d("Fragment_NewbornExamination", "Autofilled fields set to " + (editable ? "editable." : "non-editable."));
    }

    private void setFieldsEditable(boolean editable) {
        Log.d("Fragment_NewbornExamination", "Setting fields editable: " + editable);

        // Handle regular EditText fields excluding ageInput
        EditText[] editableFields = new EditText[]{
                anyConcernsCommentInput, examinerInput, designationInput, dateInput
        };

        for (EditText field : editableFields) {
            field.setEnabled(editable);
            field.setClickable(editable);
            field.setFocusable(editable);
            field.setFocusableInTouchMode(editable);
            field.setCursorVisible(editable);

            if (editable) {
                field.setTextColor(getResources().getColor(R.color.enabled_text, null));
            } else {
                field.setTextColor(getResources().getColor(R.color.disabled_text, null));
            }

            Log.d("Fragment_NewbornExamination", "Field " + field.getId() + " editable: " + editable);
        }

        // Handle CheckBoxes and their comments
        for (int i = 0; i < checkNames.length; i++) {
            checkBoxes_isNormal[i].setEnabled(editable);
            checkBoxes_isNormal[i].setClickable(editable);

            // Enable comment only if not "isNormal" and overall editable
            boolean shouldEnableComment = editable && !checkBoxes_isNormal[i].isChecked();
            editTexts_comment[i].setEnabled(shouldEnableComment);
            editTexts_comment[i].setClickable(shouldEnableComment);
            editTexts_comment[i].setFocusable(shouldEnableComment);
            editTexts_comment[i].setFocusableInTouchMode(shouldEnableComment);
            editTexts_comment[i].setCursorVisible(shouldEnableComment);

            if (shouldEnableComment) {
                editTexts_comment[i].setTextColor(getResources().getColor(R.color.enabled_text, null));
            } else {
                editTexts_comment[i].setTextColor(getResources().getColor(R.color.disabled_text, null));
            }

            Log.d("Fragment_NewbornExamination", "CheckBox " + checkBoxes_isNormal[i].getId() + " editable: " + editable);
            Log.d("Fragment_NewbornExamination", "EditText Comment " + editTexts_comment[i].getId() + " editable: " + editable);
        }

        // Handle Date Fields Separately
        for (EditText dateField : dateFields) {
            if (!editable) {
                dateField.setOnClickListener(null); // Remove OnClickListener
                dateField.setTextColor(getResources().getColor(R.color.disabled_text, null)); // Optional: Change text color
            } else {
                setupDatePicker(dateField, getLayoutForDateField(dateField));
                dateField.setTextColor(getResources().getColor(R.color.enabled_text, null)); // Optional: Reset text color
            }
        }

        // Handle the SignatureCanvas
        if (signatureCanvas != null) {
            signatureCanvas.setEnabled(editable);
        }
    }

    private boolean fieldsAreEditable() {
        // Implement logic if needed to determine if fields should be editable
        // For now, assume all fields are editable unless explicitly disabled
        return true;
    }

    private void disableSpecificFields() {
        // Disable specific autofilled EditText fields
        fnameInput.setEnabled(false);
        lnameInput.setEnabled(false);
        dobInput.setEnabled(false);
        sexInput.setEnabled(false);
        ageInput.setEnabled(false);

        // Update UI to reflect disabled state
        fnameInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
        lnameInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
        dobInput.setTextColor(getResources().getColor(R.color.disabled_text, null));
        sexInput.setAlpha(0.5f); // Spinner appearance
        ageInput.setTextColor(getResources().getColor(R.color.disabled_text, null));

        Log.d("Fragment_NewbornExamination", "Specific autofilled fields disabled.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.disconnect();
        }
    }
}