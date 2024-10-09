package com.example.myapplication.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;
import java.util.Locale;

public class Fragment_ChecksSummary extends Fragment {

    private static final String ARG_CHILD_ID = "childID";
    private int childID;

    // TextViews for each age group
    private TextView date_1_4_weeks, comment_1_4_weeks;
    private TextView date_6_8_weeks, comment_6_8_weeks;
    private TextView date_4_months, comment_4_months;
    private TextView date_6_months, comment_6_months;
    private TextView date_12_months, comment_12_months;
    private TextView date_18_months, comment_18_months;
    private TextView date_2_years, comment_2_years;
    private TextView date_3_years, comment_3_years;
    private TextView date_4_years, comment_4_years;

    private HashMap<String, TextView[]> ageTextViewMap;

    public Fragment_ChecksSummary() {
        // Required empty public constructor
    }

    public static Fragment_ChecksSummary newInstance(int childID) {
        Fragment_ChecksSummary fragment = new Fragment_ChecksSummary();
        Bundle args = new Bundle();
        args.putInt(ARG_CHILD_ID, childID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve childID from arguments
        if (getArguments() != null) {
            childID = getArguments().getInt(ARG_CHILD_ID, -1);
        } else {
            childID = -1; // Handle the case where no childID is passed
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checks_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize your views here
        super.onViewCreated(view, savedInstanceState);

        // Initialize TextViews
        date_1_4_weeks = view.findViewById(R.id.textView_date_1_4_weeks);
        comment_1_4_weeks = view.findViewById(R.id.textView_comment_1_4_weeks);

        date_6_8_weeks = view.findViewById(R.id.textView_date_6_8_weeks);
        comment_6_8_weeks = view.findViewById(R.id.textView_comment_6_8_weeks);

        date_4_months = view.findViewById(R.id.textView_date_4_months);
        comment_4_months = view.findViewById(R.id.textView_comment_4_months);

        date_6_months = view.findViewById(R.id.textView_date_6_months);
        comment_6_months = view.findViewById(R.id.textView_comment_6_months);

        date_12_months = view.findViewById(R.id.textView_date_12_months);
        comment_12_months = view.findViewById(R.id.textView_comment_12_months);

        date_18_months = view.findViewById(R.id.textView_date_18_months);
        comment_18_months = view.findViewById(R.id.textView_comment_18_months);

        date_2_years = view.findViewById(R.id.textView_date_2_years);
        comment_2_years = view.findViewById(R.id.textView_comment_2_years);

        date_3_years = view.findViewById(R.id.textView_date_3_years);
        comment_3_years = view.findViewById(R.id.textView_comment_3_years);

        date_4_years = view.findViewById(R.id.textView_date_4_years);
        comment_4_years = view.findViewById(R.id.textView_comment_4_years);

        // Map age strings to TextView arrays
        ageTextViewMap = new HashMap<>();
        ageTextViewMap.put("1-4 weeks", new TextView[]{date_1_4_weeks, comment_1_4_weeks});
        ageTextViewMap.put("6-8 weeks", new TextView[]{date_6_8_weeks, comment_6_8_weeks});
        ageTextViewMap.put("4 month", new TextView[]{date_4_months, comment_4_months});
        ageTextViewMap.put("6 month", new TextView[]{date_6_months, comment_6_months});
        ageTextViewMap.put("12 month", new TextView[]{date_12_months, comment_12_months});
        ageTextViewMap.put("18 month", new TextView[]{date_18_months, comment_18_months});
        ageTextViewMap.put("2 year", new TextView[]{date_2_years, comment_2_years});
        ageTextViewMap.put("3 year", new TextView[]{date_3_years, comment_3_years});
        ageTextViewMap.put("4 year", new TextView[]{date_4_years, comment_4_years});

        // Start AsyncTask to load data
        new LoadHealthChecksTask().execute();
    }

    private class HealthCheckData {
        String dateTime;
        String comment;

        public HealthCheckData(String dateTime, String comment) {
            this.dateTime = dateTime;
            this.comment = comment;
        }
    }

    private class LoadHealthChecksTask extends AsyncTask<Void, Void, HashMap<String, HealthCheckData>> {

        @Override
        protected HashMap<String, HealthCheckData> doInBackground(Void... voids) {
            HashMap<String, HealthCheckData> healthChecksData = new HashMap<>();
            SQLConnection dbHelper = new SQLConnection("user1", ""); // Replace with actual password

            try {
                // Query the HealthChecks table for this childID
                String query = "SELECT age, dateTime, comments FROM HealthChecks WHERE childID = ?";
                String[] params = {String.valueOf(childID)};
                char[] paramTypes = {'i'};

                HashMap<String, String[]> result = dbHelper.select(query, params, paramTypes);

                if (result != null && result.get("age") != null) {
                    String[] ages = result.get("age");
                    String[] dateTimes = result.get("dateTime");
                    String[] comments = result.get("comments");

                    for (int i = 0; i < ages.length; i++) {
                        String age = ages[i];
                        String dateTime = dateTimes[i];
                        String comment = comments[i];
                        healthChecksData.put(age, new HealthCheckData(dateTime, comment));
                    }
                }
            } catch (Exception e) {
                Log.e("Fragment_ChecksSummary", "Error loading health checks data", e);
            } finally {
                dbHelper.disconnect();
            }

            return healthChecksData;
        }

        @Override
        protected void onPostExecute(HashMap<String, HealthCheckData> healthChecksData) {
            for (String age : ageTextViewMap.keySet()) {
                TextView[] views = ageTextViewMap.get(age);
                TextView dateView = views[0];
                TextView commentView = views[1];

                if (healthChecksData.containsKey(age)) {
                    HealthCheckData data = healthChecksData.get(age);
                    String dateTime = data.dateTime;
                    String comment = data.comment;

                    // Format dateTime to date (assuming dateTime is in "yyyy-MM-dd HH:mm:ss" format)
                    String date = "N/A";
                    if (!TextUtils.isEmpty(dateTime)) {
                        date = dateTime.split(" ")[0]; // Extract the date part
                    }

                    dateView.setText(date);

                    if (TextUtils.isEmpty(comment)) {
                        commentView.setText("Child's health progressing normally.");
                    } else {
                        commentView.setText(comment);
                    }

                } else {
                    dateView.setText("To be set");
                    commentView.setText("Child's health progressing normally.");
                }
            }
        }
    }
}