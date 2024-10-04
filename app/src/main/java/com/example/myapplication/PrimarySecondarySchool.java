package com.example.myapplication;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

public class PrimarySecondarySchool extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_secondary_school);

        // Apply system window insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the text for the TextView with clickable links
        TextView healthyEatingText = findViewById(R.id.healthyEatingText);
        healthyEatingText.setText(Html.fromHtml(getString(R.string.healthy_eating_text), Html.FROM_HTML_MODE_LEGACY));
        healthyEatingText.setMovementMethod(LinkMovementMethod.getInstance());

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(PrimarySecondarySchool.this, new LoginManager(extras.getInt("guardianID"), extras.getInt("childID")));
    }
}
