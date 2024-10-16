package com.example.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;

public class ChecksMenuFragment extends Fragment {

    private OnCheckSelectedListener mListener;
    private int childID = -1; // Initialize with invalid ID

    // Interface to communicate with the activity
    public interface OnCheckSelectedListener {
        void onCheckSelected(String checkType);
    }

    public ChecksMenuFragment() {
        // Required empty public constructor
    }

    public static ChecksMenuFragment newInstance() {
        ChecksMenuFragment fragment = new ChecksMenuFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCheckSelectedListener) {
            mListener = (OnCheckSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheckSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve childID from the hosting activity's intent extras
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            childID = extras.getInt("childID", -1); // Default to -1 if not found
        } else {
            childID = -1; // Handle the case where no extras are present
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checks_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize buttons and set click listeners
        Button buttonsummary = view.findViewById(R.id.buttonsummary);
        Button button1_4Weeks = view.findViewById(R.id.button_1_4_weeks);
        Button button6_8Weeks = view.findViewById(R.id.button_6_8_weeks);
        Button button4Month = view.findViewById(R.id.button_4_month);
        Button button6Month = view.findViewById(R.id.button_6_month);
        Button button12Month = view.findViewById(R.id.button_12_month);
        Button button18Month = view.findViewById(R.id.button_18_month);
        Button button2Year = view.findViewById(R.id.button_2_year);
        Button button3Year = view.findViewById(R.id.button_3_year);
        Button button4Year = view.findViewById(R.id.button_4_year);

        // Set onClickListeners for each button
        buttonsummary.setOnClickListener(v -> {
            if (childID == -1) {
                Toast.makeText(getContext(), "Invalid Child ID", Toast.LENGTH_SHORT).show();
                return;
            }

            // Instantiate Fragment_ChecksSummary with childID
            Fragment_ChecksSummary summaryFragment = Fragment_ChecksSummary.newInstance(childID);

            // Perform the fragment transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_encapsulating, summaryFragment);
            transaction.addToBackStack(null); // Optional: Add to back stack
            transaction.commit();
        });

        button1_4Weeks.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("1-4 weeks");
            }
        });

        button6_8Weeks.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("6-8 weeks");
            }
        });

        button4Month.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("4 month");
            }
        });

        button6Month.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("6 month");
            }
        });

        button12Month.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("12 month");
            }
        });

        button18Month.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("18 month");
            }
        });

        button2Year.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("2 year");
            }
        });

        button3Year.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("3 year");
            }
        });

        button4Year.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCheckSelected("4 year");
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}