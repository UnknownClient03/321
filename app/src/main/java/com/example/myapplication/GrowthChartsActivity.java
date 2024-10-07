package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

public class GrowthChartsActivity extends AppCompatActivity {

    public LoginManager manager;
    private ImageView chartImageView;
    private TextView generalInfoTextView;

    // Define chart options for different groups: General Info, Girls, and Boys.
    private String[][] chartOptions = {
            {"General Info"},
            {"Weight-for-age percentiles GIRL birth to 2 years", "Length-for-age percentiles GIRL birth to 2 years",
                    "Weight-for-age percentiles GIRL 2 to 20 years", "Stature-for-age percentiles GIRL 2 to 20 years",
                    "BMI percentiles GIRL 2 to 20 years"},
            {"Weight-for-age percentiles BOY birth to 2 years", "Length-for-age percentiles BOY birth to 2 years",
                    "Weight-for-age percentiles BOY 2 to 20 years", "Stature-for-age percentiles BOY 2 to 20 years",
                    "BMI percentiles BOY 2 to 20 years"}
    };

    // Associate image resources with the corresponding chart options for girls and boys.
    private int[][] imageResources = {
            {0}, // No image for general info
            {R.drawable.girl_weight_for_age_birth_to_2_years, R.drawable.girl_length_for_age_birth_to_2_years,
                    R.drawable.girl_weight_for_age_2_to_20_years, R.drawable.girl_stature_for_age_2_to_20_years,
                    R.drawable.girl_bmi_for_age_2_to_18_years},
            {R.drawable.boy_weight_for_age_birth_to_2_years, R.drawable.boy_length_for_age_birth_to_2_years,
                    R.drawable.boy_weight_for_age_2_to_20_years, R.drawable.boy_stature_for_age_2_to_20_years,
                    R.drawable.boy_bmi_for_age_2_to_18_years}
    };

    // Associate source links with the corresponding chart options for girls and boys.
    private String[][] imageSources = {
            {"General info about growth charts."},
            {
                    "Source: <a href='http://www.cdc.gov/growthcharts/'>CDC Growth charts – United States published 30 May 2000</a><br/>" +
                            "World Health Organisation Child Growth Standards<br/>" +
                            "<a href='http://who.int/tools/child-growth-standards/standards'>who.int</a>",
                    "Source: <a href='http://www.cdc.gov/growthcharts/'>CDC Growth charts – United States published 30 May 2000</a><br/>" +
                            "World Health Organisation Child Growth Standards<br/>" +
                            "<a href='http://who.int/tools/child-growth-standards/standards'>who.int</a>",
                    "Source: Developed by the National Center for Health Statistics in collaboration<br/>" +
                            "with the National Center for Chronic Disease Prevention and Health Promotion (2000)",
                    "Source: Developed by the National Center for Health Statistics in collaboration<br/>" +
                            "with the National Center for Chronic Disease Prevention and Health Promotion (2000)",
                    "Source: <a href='http://www.cdc.gov/growthcharts/'>CDC Growth charts – United States published 30 May 2000</a><br/>" +
                            "World Health Organisation Child Growth Standards<br/>" +
                            "<a href='http://who.int/tools/child-growth-standards/standards'>who.int</a><br/>" +
                            "BMI data source: <a href='http://pro.healthykids.nsw.gov.au'>Healthy Kids NSW</a>."
            },
            {
                    "Source: <a href='http://www.cdc.gov/growthcharts/'>CDC Growth charts – United States published 30 May 2000</a><br/>" +
                            "World Health Organisation Child Growth Standards<br/>" +
                            "<a href='http://who.int/tools/child-growth-standards/standards'>who.int</a>",
                    "Source: <a href='http://www.cdc.gov/growthcharts/'>CDC Growth charts – United States published 30 May 2000</a><br/>" +
                            "World Health Organisation Child Growth Standards<br/>" +
                            "<a href='http://who.int/tools/child-growth-standards/standards'>who.int</a>",
                    "Source: Developed by the National Center for Health Statistics in collaboration<br/>" +
                            "with the National Center for Chronic Disease Prevention and Health Promotion (2000)",
                    "Source: Developed by the National Center for Health Statistics in collaboration<br/>" +
                            "with the National Center for Chronic Disease Prevention and Health Promotion (2000)",
                    "Source: <a href='http://www.cdc.gov/growthcharts/'>CDC Growth charts – United States published 30 May 2000</a><br/>" +
                            "World Health Organisation Child Growth Standards<br/>" +
                            "<a href='http://who.int/tools/child-growth-standards/standards'>who.int</a><br/>" +
                            "BMI data source: <a href='http://pro.healthykids.nsw.gov.au'>Healthy Kids NSW</a>."
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_growth_charts);

        // Initialize generalInfoTextView for displaying general information, and hide it initially.
        generalInfoTextView = new TextView(this);
        generalInfoTextView.setVisibility(View.GONE); // Initially hide it

        // Initialize buttons for selecting chart categories (General Info, Girls, Boys).
        Button buttonOption1 = findViewById(R.id.buttonOption1);
        Button buttonOption2 = findViewById(R.id.buttonOption2);
        Button buttonOption3 = findViewById(R.id.buttonOption3);

        // Set click listeners for the buttons to load relevant charts for the selected group.
        buttonOption1.setOnClickListener(v -> displayChartsForGroup(0));
        buttonOption2.setOnClickListener(v -> displayChartsForGroup(1));
        buttonOption3.setOnClickListener(v -> displayChartsForGroup(2));

        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));

        // Set up the navigation bar with relevant buttons.
        NavBarManager.setNavBarButtons(GrowthChartsActivity.this, manager);
    }

    // Function to display charts for the selected group (General Info, Girls, Boys).
    private void displayChartsForGroup(int groupIndex) {
        // Clear any existing content from the chart container.
        LinearLayout chartContainer = findViewById(R.id.chartContainer);
        chartContainer.removeAllViews();

        // Set the group title based on the selected group (General Info, Girls, or Boys).
        String groupTitleText;
        switch (groupIndex) {
            case 0:
                groupTitleText = "General Information";
                break;
            case 1:
                groupTitleText = "Girls' Charts";
                break;
            case 2:
                groupTitleText = "Boys' Charts";
                break;
            default:
                groupTitleText = "";
        }

        // Display the group title as a centered header.
        TextView groupTitle = new TextView(this);
        groupTitle.setText(groupTitleText);
        groupTitle.setTextSize(22);
        groupTitle.setPadding(16, 16, 16, 8);
        groupTitle.setGravity(Gravity.CENTER_HORIZONTAL); // Center the heading
        chartContainer.addView(groupTitle);

        // Handle the case where "General Information" is selected.
        if (groupIndex == 0) {
            String generalInfoContent = "Measuring and monitoring your child’s growth\n" +
                    "Measuring your child’s height, weight, and head circumference tells you how your child is growing. ...\n\n" +
                    "If you would like more information about how growth charts work, please go to " +
                    "<a href='http://www.who.int/childgrowth/en/'>www.who.int</a> and " +
                    "<a href='http://www.cdc.gov/growthcharts/'>www.cdc.gov/growthcharts/</a>\n\n" +
                    "You can find an online BMI calculator at <a href='http://pro.healthykids.nsw.gov.au/calculator'>Healthy Kids BMI Calculator</a>.\n\n" +
                    "If you have concerns about your child’s eating habits or their weight, see your local child and family health nurse or your doctor.";

            // Display the general information content, and make links clickable.
            generalInfoTextView.setText(Html.fromHtml(generalInfoContent));
            generalInfoTextView.setVisibility(View.VISIBLE);
            generalInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());
            chartContainer.addView(generalInfoTextView);
        } else {
            // For Girls' or Boys' charts, dynamically load the relevant charts and images.
            for (int i = 0; i < chartOptions[groupIndex].length; i++) {
                // Display chart title.
                TextView chartText = new TextView(this);
                chartText.setText(chartOptions[groupIndex][i]);
                chartText.setTextSize(18);
                chartText.setPadding(8, 16, 8, 8);
                chartContainer.addView(chartText);

                // Display the corresponding chart image.
                chartImageView = new ImageView(this);
                chartImageView.setImageResource(imageResources[groupIndex][i]);
                chartContainer.addView(chartImageView);

                // Display the source information with clickable links.
                TextView sourceText = new TextView(this);
                sourceText.setText(Html.fromHtml(imageSources[groupIndex][i]));
                sourceText.setMovementMethod(LinkMovementMethod.getInstance());
                sourceText.setTextSize(10);
                sourceText.setPadding(8, 8, 8, 8);
                chartContainer.addView(sourceText);
            }
        }
    }
}
