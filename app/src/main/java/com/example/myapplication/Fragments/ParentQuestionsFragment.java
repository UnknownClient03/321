package com.example.myapplication.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ParentQuestionsFragment extends Fragment {

    private static final String ARG_CHILD_ID = "childID";
    private static final String ARG_CHECK_TYPE = "checkType";

    private int childID;
    private String checkType;
    private boolean isFirstTime;

    private EditText dobInput, fnameInput, lnameInput, parentsNotesInput;
    private Spinner sexSpinner;
    private Button submitButton;

    private TextInputLayout dobLayout, fnameLayout, lnameLayout, parentsNotesLayout;

    private SQLConnection dbHelper;

    // List to keep track of dynamically added CheckBoxes and their associated questions
    private List<CheckBox> questionCheckBoxes = new ArrayList<>();
    private List<String> questionTexts = new ArrayList<>();

    public static ParentQuestionsFragment newInstance(int childID, String checkType) {
        ParentQuestionsFragment fragment = new ParentQuestionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHILD_ID, childID);
        args.putString(ARG_CHECK_TYPE, checkType);
        fragment.setArguments(args);
        return fragment;
    }

    public ParentQuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the SQLConnection here to prevent NullPointerException
        dbHelper = new SQLConnection();

        // Get the arguments from the bundle if passed
        if (getArguments() != null) {
            childID = getArguments().getInt(ARG_CHILD_ID);
            checkType = getArguments().getString(ARG_CHECK_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parent_questions, container, false);

        // Initialize TextInputLayouts
        dobLayout = view.findViewById(R.id.textInputLayout_birthDate);
        fnameLayout = view.findViewById(R.id.textInputLayout_fname);
        lnameLayout = view.findViewById(R.id.textInputLayout_lname);
        parentsNotesLayout = view.findViewById(R.id.textInputLayout_parentsNotes);

        // Initialize UI components
        dobInput = view.findViewById(R.id.editText_birthDate);
        fnameInput = view.findViewById(R.id.editText_fname);
        lnameInput = view.findViewById(R.id.editText_lname);
        sexSpinner = view.findViewById(R.id.spinner_sex);
        parentsNotesInput = view.findViewById(R.id.editText_parentsNotes);
        submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> submitParentAnswers());  // Set the submit button's click listener

        // Ensure dbHelper is connected before loading data
        if (dbHelper != null) {
            // Step 1: Load the child's data from the Child table
            new LoadChildDataTask().execute(childID);

            // Step 2: Load the parent's questions and their saved answers, if available
            new LoadChildCheckDataTask().execute(childID, checkType);
        } else {
            Toast.makeText(getContext(), "Database connection failed.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void submitParentAnswers() {
        // Validate required fields
        if (TextUtils.isEmpty(dobInput.getText()) ||
                TextUtils.isEmpty(fnameInput.getText()) ||
                TextUtils.isEmpty(lnameInput.getText())) {
            Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dob = dobInput.getText().toString().trim();
        String fname = fnameInput.getText().toString().trim();
        String lname = lnameInput.getText().toString().trim();
        String sex = sexSpinner.getSelectedItem().toString().trim();
        String parentsNotes = parentsNotesInput.getText().toString().trim();

        // Insert or update ChildCheck record
        new InsertOrUpdateChildCheckTask(dob, fname, lname, sex, parentsNotes).execute();

        // Update the answers for the questions
        new UpdateChildCheckQuestionsTask().execute();
    }

    private void setSexSpinnerValue(String sex) {
        if (sex != null) {
            if (sex.equalsIgnoreCase("M")) {
                sexSpinner.setSelection(0); // 'M' is at position 0
            } else if (sex.equalsIgnoreCase("F")) {
                sexSpinner.setSelection(1); // 'F' is at position 1
            } else {
                sexSpinner.setSelection(0); // Default to 'M'
            }
        }
    }

    private class LoadChildDataTask extends AsyncTask<Integer, Void, HashMap<String, String[]>> {
        @Override
        protected HashMap<String, String[]> doInBackground(Integer... params) {
            int childIdParam = params[0];
            String query = "SELECT fname, lname, DOB, sex FROM Child WHERE ID = ?";
            String[] queryParams = {String.valueOf(childIdParam)};
            char[] queryParamTypes = {'i'};

            return dbHelper.select(query, queryParams, queryParamTypes);
        }

        @Override
        protected void onPostExecute(HashMap<String, String[]> result) {
            if (result != null && result.get("DOB") != null && result.get("DOB").length > 0) {
                fnameInput.setText(result.get("fname")[0]);
                lnameInput.setText(result.get("lname")[0]);
                dobInput.setText(result.get("DOB")[0]);
                setSexSpinnerValue(result.get("sex")[0]);

                // Disable fields and grey them out
                setFieldsEditable(isFirstTimeFillingForm());
                Log.d("ParentQuestionsFragment", "Child data autofilled.");
            } else {
                Toast.makeText(getContext(), "Failed to load child data.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LoadChildCheckDataTask extends AsyncTask<Object, Void, Boolean> {
        private HashMap<String, String[]> childCheckData;

        @Override
        protected Boolean doInBackground(Object... params) {
            int childIdParam = (int) params[0];
            String checkTypeParam = (String) params[1];

            String query = "SELECT DOB, fname, lname, sex, parentsNotes FROM ChildCheck WHERE childID = ? AND checkType = ?";
            String[] queryParams = {String.valueOf(childIdParam), checkTypeParam};
            char[] queryParamTypes = {'i', 's'};

            childCheckData = dbHelper.select(query, queryParams, queryParamTypes);

            // Determine if parent questions have been submitted
            String parentQuestionsQuery = "SELECT COUNT(*) AS count FROM ChildCheckQuestion WHERE childID = ? AND checkType = ?";
            HashMap<String, String[]> parentQuestionsResult = dbHelper.select(parentQuestionsQuery, queryParams, queryParamTypes);

            int questionsCount = 0;
            if (parentQuestionsResult != null && parentQuestionsResult.get("count") != null && parentQuestionsResult.get("count").length > 0) {
                questionsCount = Integer.parseInt(parentQuestionsResult.get("count")[0]);
            }

            // If questions exist, it's not the first time
            isFirstTime = questionsCount == 0;

            return childCheckData != null && childCheckData.get("DOB") != null && childCheckData.get("DOB").length > 0;
        }

        @Override
        protected void onPostExecute(Boolean dataExists) {
            if (dataExists) {
                // Data found in ChildCheck, load it into the UI
                String dob = childCheckData.get("DOB")[0];
                String sex = childCheckData.get("sex")[0];
                String fname = childCheckData.get("fname")[0];
                String lname = childCheckData.get("lname")[0];
                String parentsNotes = childCheckData.get("parentsNotes")[0];

                dobInput.setText(dob);
                setSexSpinnerValue(sex);
                fnameInput.setText(fname);
                lnameInput.setText(lname);

                if (parentsNotes != null && parentsNotes.length() > 0) {
                    parentsNotesInput.setText(parentsNotes);
                }

                // Set fields editable/non-editable based on isFirstTime
                setFieldsEditable(isFirstTimeFillingForm());

                Log.d("ParentQuestionsFragment", "Loaded ChildCheck data into UI.");
            } else {
                // No data found in ChildCheck, fall back to loading from Child table
                Log.d("ParentQuestionsFragment", "No ChildCheck data found, fetching from Child table.");
                new LoadChildTableDataTask().execute(childID);
            }

            // Continue loading the questions
            new LoadChildCheckQuestionsTask().execute(childID, checkType);
        }
    }


    private class LoadChildCheckQuestionsTask extends AsyncTask<Object, Void, HashMap<String, String[]>> {
        @Override
        protected HashMap<String, String[]> doInBackground(Object... params) {
            int childIdParam = (int) params[0];
            String checkTypeParam = (String) params[1];

            String query = "SELECT question, condition FROM ChildCheckQuestion WHERE childID = ? AND checkType = ?";
            String[] queryParams = {String.valueOf(childIdParam), checkTypeParam};
            char[] queryParamTypes = {'i', 's'};

            return dbHelper.select(query, queryParams, queryParamTypes);
        }

        @Override
        protected void onPostExecute(HashMap<String, String[]> result) {
            LinearLayout parentQuestionLayout = getView().findViewById(R.id.parent_question_layout);
            parentQuestionLayout.removeAllViews(); // Clear previous views

            if (result != null && result.get("question") != null && result.get("question").length > 0) {
                String[] questions = result.get("question");
                String[] conditions = result.get("condition");

                for (int i = 0; i < questions.length; i++) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(questions[i]);

                    // Set the checked state based on the saved answer (condition)
                    boolean isChecked = conditions[i].equals("1");
                    checkBox.setChecked(isChecked);

                    parentQuestionLayout.addView(checkBox);
                    questionCheckBoxes.add(checkBox);
                    questionTexts.add(questions[i]); // Store question texts for later use during submission
                }

                // Set fields editable/non-editable based on isFirstTime
                setFieldsEditable(isFirstTimeFillingForm());


            } else {
                // No data, first time
                // Initialize questions if none are found in the database
                Log.d("ParentQuestionsFragment", "No ChildCheckQuestion data found, initializing predefined questions.");
                new InitializeQuestionsTask().execute(childID, checkType);
            }
        }
    }

    private class LoadChildTableDataTask extends AsyncTask<Integer, Void, HashMap<String, String[]>> {
        @Override
        protected HashMap<String, String[]> doInBackground(Integer... params) {
            int childIdParam = params[0];

            // Query the Child table to fetch the child's data
            String query = "SELECT fname, lname, DOB, sex FROM Child WHERE ID = ?";
            String[] queryParams = {String.valueOf(childIdParam)};
            char[] queryParamTypes = {'i'};

            return dbHelper.select(query, queryParams, queryParamTypes);
        }

        @Override
        protected void onPostExecute(HashMap<String, String[]> result) {
            if (result != null && result.get("DOB") != null && result.get("DOB").length > 0) {
                // Data found in Child table, load it into the UI
                String dob = result.get("DOB")[0];
                String sex = result.get("sex")[0];
                String fname = result.get("fname")[0];
                String lname = result.get("lname")[0];

                dobInput.setText(dob);
                setSexSpinnerValue(sex);
                fnameInput.setText(fname);
                lnameInput.setText(lname);

                Log.d("ParentQuestionsFragment", "Loaded Child data from Child table into UI.");

                // Enable fields since no data is saved yet
                setFieldsEditable(isFirstTimeFillingForm());

            } else {
                // Handle case where no data is found in Child table (unlikely if child exists)
                Log.e("ParentQuestionsFragment", "No data found in Child table for childID: " + childID);
                Toast.makeText(getContext(), "No data found for this child.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class InsertOrUpdateChildCheckTask extends AsyncTask<Void, Void, Boolean> {
        private String dob, fname, lname, sex, parentsNotes;

        public InsertOrUpdateChildCheckTask(String dob, String fname, String lname, String sex, String parentsNotes) {
            this.dob = dob;
            this.fname = fname;
            this.lname = lname;
            this.sex = sex;
            this.parentsNotes = parentsNotes;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheck WHERE childID = ? AND checkType = ?";
            String[] checkParams = {String.valueOf(childID), checkType};
            char[] checkParamTypes = {'i', 's'};

            HashMap<String, String[]> checkResult = dbHelper.select(checkQuery, checkParams, checkParamTypes);

            if (checkResult != null && checkResult.get("count") != null) {
                int count = Integer.parseInt(checkResult.get("count")[0]);
                if (count > 0) {
                    // Update existing ChildCheck record
                    String updateQuery = "UPDATE ChildCheck SET DOB = ?, fname = ?, lname = ?, sex = ?, parentsNotes = ? WHERE childID = ? AND checkType = ?";
                    String[] updateParams = {dob, fname, lname, sex, parentsNotes, String.valueOf(childID), checkType};
                    char[] updateParamTypes = {'s', 's', 's', 's', 's', 'i', 's'};

                    return dbHelper.update(updateQuery, updateParams, updateParamTypes);
                } else {
                    // Insert new ChildCheck record
                    String insertQuery = "INSERT INTO ChildCheck (childID, checkType, DOB, fname, lname, sex, parentsNotes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    String[] insertParams = {String.valueOf(childID), checkType, dob, fname, lname, sex, parentsNotes};
                    char[] insertParamTypes = {'i', 's', 's', 's', 's', 's', 's'};

                    return dbHelper.update(insertQuery, insertParams, insertParamTypes);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d("ParentQuestionsFragment", "ChildCheck record saved successfully.");
                // Disable fields and grey them out
                setFieldsEditable(false);
            } else {
                Log.e("ParentQuestionsFragment", "Failed to save ChildCheck record.");
            }
        }
    }

    private class UpdateChildCheckQuestionsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean allUpdatesSuccessful = true;

            for (int i = 0; i < questionCheckBoxes.size(); i++) {
                CheckBox checkBox = questionCheckBoxes.get(i);
                String question = questionTexts.get(i);
                String condition = checkBox.isChecked() ? "1" : "0"; // Get the checked state (1 = checked, 0 = unchecked)

                String updateQuery = "UPDATE ChildCheckQuestion SET condition = ? WHERE childID = ? AND checkType = ? AND question = ?";
                String[] updateParams = {condition, String.valueOf(childID), checkType, question};
                char[] updateParamTypes = {'s', 'i', 's', 's'};

                boolean isUpdated = dbHelper.update(updateQuery, updateParams, updateParamTypes);
                if (!isUpdated) {
                    Log.e("ParentQuestionsFragment", "Failed to update condition for question: " + question);
                    allUpdatesSuccessful = false;
                }
            }

            return allUpdatesSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Parent questions updated successfully.", Toast.LENGTH_SHORT).show();

                // Set isFirstTime to false after successful submission
                isFirstTime = false;

                // Disable fields and grey them out
                setFieldsEditable(isFirstTimeFillingForm());

                moveToCheckFragment();
            } else {
                Toast.makeText(getContext(), "Failed to update some parent questions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void moveToCheckFragment() {
        CheckFragment checkFragment = CheckFragment.newInstance(childID, checkType);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_encapsulating, checkFragment)
                .addToBackStack(null)
                .commit();
    }

    private class InitializeQuestionsTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            int childIdParam = (int) params[0];
            String checkTypeParam = (String) params[1];

            // Ensure that the ChildCheck entry exists before inserting questions
            if (!ensureChildCheckExists(childIdParam, checkTypeParam)) {
                Log.e("ParentQuestionsFragment", "Failed to ensure ChildCheck exists.");
                return false;
            }

            // Access the predefined questions for this checkType
            List<String> predefinedQuestions = getPredefinedQuestions(checkTypeParam);

            for (String question : predefinedQuestions) {
                // Insert each question with a default unchecked state (condition = 0)
                String insertQuery = "INSERT INTO ChildCheckQuestion (childID, checkType, question, condition) VALUES (?, ?, ?, ?)";
                String[] insertParams = {
                        String.valueOf(childIdParam),
                        checkTypeParam,
                        question,
                        "0" // Default condition is unchecked (0)
                };
                char[] insertParamTypes = {'i', 's', 's', 'i'};

                boolean isInserted = dbHelper.update(insertQuery, insertParams, insertParamTypes);
                if (!isInserted) {
                    Log.e("ParentQuestionsFragment", "Failed to insert question: " + question);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                isFirstTime = true;
                new LoadChildCheckQuestionsTask().execute(childID, checkType);
            } else {
                Toast.makeText(getContext(), "Failed to initialize questions.", Toast.LENGTH_LONG).show();
            }
        }

        private boolean ensureChildCheckExists(int childId, String checkType) {
            // Check if an entry exists in the ChildCheck table for the current childID and checkType
            String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheck WHERE childID = ? AND checkType = ?";
            String[] checkParams = {String.valueOf(childId), checkType};
            char[] checkParamTypes = {'i', 's'};

            HashMap<String, String[]> result = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;

            if (result != null && result.get("count") != null && result.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(result.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("ParentQuestionsFragment", "Error parsing count value.", e);
                }
            }

            // If no record exists, create one
            if (recordCount == 0) {
                String insertQuery = "INSERT INTO ChildCheck (childID, checkType, DOB, fname, lname, sex, parentsNotes) " +
                        "SELECT ID, ?, DOB, fname, lname, sex, '' FROM Child WHERE ID = ?";
                String[] insertParams = {checkType, String.valueOf(childId)};
                char[] insertParamTypes = {'s', 'i'};

                return dbHelper.update(insertQuery, insertParams, insertParamTypes);
            }

            return true; // Record already exists
        }
    }

        private boolean ensureChildCheckExists(int childId, String checkType) {
            // Check if an entry exists in the ChildCheck table for the current childID and checkType
            String checkQuery = "SELECT COUNT(*) AS count FROM ChildCheck WHERE childID = ? AND checkType = ?";
            String[] checkParams = {String.valueOf(childId), checkType};
            char[] checkParamTypes = {'i', 's'};

            HashMap<String, String[]> result = dbHelper.select(checkQuery, checkParams, checkParamTypes);
            int recordCount = 0;

            if (result != null && result.get("count") != null && result.get("count").length > 0) {
                try {
                    recordCount = Integer.parseInt(result.get("count")[0]);
                } catch (NumberFormatException e) {
                    Log.e("ParentQuestionsFragment", "Error parsing count value.", e);
                }
            }

            // If no record exists, create one
            if (recordCount == 0) {
                String insertQuery = "INSERT INTO ChildCheck (childID, checkType, DOB, fname, lname, sex, parentsNotes) " +
                        "SELECT ID, ?, DOB, fname, lname, sex, '' FROM Child WHERE ID = ?";
                String[] insertParams = {checkType, String.valueOf(childId)};
                char[] insertParamTypes = {'s', 'i'};

                return dbHelper.update(insertQuery, insertParams, insertParamTypes);
            }

            return true; // Record already exists
        }

        private List<String> getPredefinedQuestions(String checkType) {
            switch (checkType) {
                case "1-4 weeks":
                    return Arrays.asList(
                            "Have you completed the health risk factor questions",
                            "I am concerned about my baby's hearing",
                            "Others have said they are concerned about my baby's hearing",
                            "I am concerned about my baby's vision",
                            "My baby is exposed to smoking and/or vaping in the home or car",
                            "I place my baby on their back for sleeping"
                    );
                case "6-8 weeks":
                    return Arrays.asList(
                            "I have had my postnatal check",
                            "My baby was also checked",
                            "I have concerns about my baby",
                            "I have completed the health risk factor questions",
                            "I am concerned about my baby's hearing",
                            "Others have said they are concerned about my baby's hearing",
                            "My baby turns towards light",
                            "My baby smiles at me",
                            "My baby looks at my face and makes eye contact with me",
                            "I have noticed that one or both of my baby's pupils are white",
                            "My baby and I enjoy being together",
                            "I read, talk to and play with my baby",
                            "My baby is exposed to smoking and/or vaping in the home or car",
                            "I place my baby on their back for sleeping"
                    );
                case "4 month":
                    return Arrays.asList(
                            "I have completed the health risk factor questions",
                            "I have completed the dental risk factor questions"
                    );
                case "6 month":
                    return Arrays.asList(
                            "I have concerns about my baby",
                            "I have completed the health risk factor questions",
                            "I have completed the dental risk factor questions",
                            "I am concerned about my baby's hearing",
                            "Others have said they are concerned about my baby's hearing",
                            "My baby turns towards light",
                            "I have noticed that one or both of my baby's pupils are white",
                            "My baby and I enjoy being together",
                            "I read, talk to and play with my baby",
                            "My baby is exposed to smoking and/or vaping in the home or car",
                            "I place my baby on their back for sleeping"
                    );
                case "12 month":
                    return Arrays.asList(
                            "I have completed the health risk factor questions",
                            "I have completed the dental risk factor questions",
                            "I am concerned about my child's hearing",
                            "Others have said they are concerned about my child's hearing",
                            "I am concerned about my child's vision",
                            "My child has a turned or lazy eye (squint or strabismus)",
                            "My child has difficulty seeing small objects",
                            "My child recognises familiar objects and people from a distance",
                            "My child is exposed to smoking and/or vaping in the home/car",
                            "My child has teeth",
                            "My child has had problems with their teeth or teething",
                            "My child uses a bottle to help them go to sleep",
                            "My child walks around with a bottle or feeder cup between meals",
                            "I brush my child's teeth twice a day"
                    );
                case "18 month":
                case "2 year":
                    return Arrays.asList(
                            "I have completed the health risk factor questions",
                            "I have completed the dental risk factor questions",
                            "I am concerned about my child's hearing",
                            "Others have said they are concerned about my child's hearing",
                            "I am concerned about my child's vision",
                            "My child has a turned or lazy eye (squint or strabismus)",
                            "My child has difficulty seeing small objects",
                            "My child recognises familiar objects and people from a distance",
                            "My child is exposed to smoking and/or vaping in the home/car",
                            "My child has sweet drinks and snacks throughout the day",
                            "My child still uses a bottle"
                    );
                case "3 year":
                    return Arrays.asList(
                            "I have completed the health risk factor questions",
                            "I have completed the dental risk factor questions",
                            "I am concerned about my child's hearing",
                            "Others have said they are concerned about my child's hearing",
                            "I am concerned about my child's vision",
                            "My child has a turned or lazy eye (squint or strabismus)",
                            "My child has difficulty seeing small objects",
                            "My child recognises familiar objects and people from a distance",
                            "My child is exposed to smoking and/or vaping in the home/car",
                            "I am concerned about my child's teeth",
                            "My child has pain in their mouth",
                            "My child has sweet drinks and snacks throughout the day"
                    );
                case "4 year":
                    return Arrays.asList(
                            "I have completed the health risk factor questions",
                            "I have completed the dental risk factor questions",
                            "I am concerned about my child's hearing",
                            "Others have said they are concerned about my child's hearing",
                            "I am concerned about my child's vision",
                            "My child has a turned or lazy eye (squint or strabismus)",
                            "My child is exposed to smoking and/or vaping in the home/car",
                            "I am concerned about my child's teeth",
                            "My child has pain in their mouth",
                            "My child has sweet drinks and snacks throughout the day"
                    );
                default:
                    return new ArrayList<>(); // Return an empty list or a default set of questions
            }
        }

    private boolean isFirstTimeFillingForm() {
        return isFirstTime;
    }

    private void setFieldsEditable(boolean isFirstTime) {
        // Fields that are always non-editable
        EditText[] nonEditableFields = new EditText[]{
                dobInput, fnameInput, lnameInput
        };

        for (EditText field : nonEditableFields) {
            field.setEnabled(false);
            field.setFocusable(false);
            field.setClickable(false);
            field.setTextColor(getResources().getColor(R.color.disabled_text));
        }

        sexSpinner.setEnabled(false);
        sexSpinner.setClickable(false);

        if (isFirstTime) {
            // parentsNotesInput is editable
            parentsNotesInput.setEnabled(true);
            parentsNotesInput.setFocusable(true);
            parentsNotesInput.setClickable(true);
            parentsNotesInput.setTextColor(getResources().getColor(R.color.black));

            // CheckBoxes are editable
            for (CheckBox checkBox : questionCheckBoxes) {
                checkBox.setEnabled(true);
                checkBox.setTextColor(getResources().getColor(R.color.black));
            }
        } else {
            // All fields are not editable
            parentsNotesInput.setEnabled(false);
            parentsNotesInput.setFocusable(false);
            parentsNotesInput.setClickable(false);
            parentsNotesInput.setTextColor(getResources().getColor(R.color.disabled_text));

            // CheckBoxes are not editable
            for (CheckBox checkBox : questionCheckBoxes) {
                checkBox.setEnabled(false);
                checkBox.setTextColor(getResources().getColor(R.color.disabled_text));
            }
        }
    }

}