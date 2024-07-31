package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragments.Fragment_AllAboutMe;
import com.example.myapplication.Fragments.Fragment_DentalHistory;
import com.example.myapplication.Fragments.Fragment_Father;
import com.example.myapplication.Fragments.Fragment_HealthHistory;
import com.example.myapplication.Fragments.Fragment_Language;
import com.example.myapplication.Fragments.Fragment_Mother;
import com.example.myapplication.Fragments.Fragment_MyAddress;

public class MyInfoAndFamHis extends AppCompatActivity {
    Fragment currentFrag;
    public LoginManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_info_and_fam_his);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) manager = new LoginManager(extras.getInt("guardianID"), extras.getInt("childID"));

        Fragment[] Fragments = { new Fragment_AllAboutMe(),
                new Fragment_MyAddress(),
                new Fragment_Language(),
                new Fragment_Mother(),
                new Fragment_Father(),
                new Fragment_HealthHistory(),
                new Fragment_DentalHistory() };
        currentFrag = Fragments[0];

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FRAG_MIAFH, currentFrag)
                .addToBackStack(null)
                .commit();


        setButtons((Button)findViewById(R.id.button_MIAFH_1), Fragments[0]);
        setButtons((Button)findViewById(R.id.button_MIAFH_2), Fragments[1]);
        setButtons((Button)findViewById(R.id.button_MIAFH_3), Fragments[2]);
        setButtons((Button)findViewById(R.id.button_MIAFH_4), Fragments[3]);
        setButtons((Button)findViewById(R.id.button_MIAFH_5), Fragments[4]);
        setButtons((Button)findViewById(R.id.button_MIAFH_6), Fragments[5]);
        setButtons((Button)findViewById(R.id.button_MIAFH_7), Fragments[6]);
    }
    private void setButtons(Button button, Fragment newFragment) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(currentFrag.getId(), newFragment)
                        .addToBackStack(null)
                        .commit();
                currentFrag = newFragment;
            }
        });
    }
}