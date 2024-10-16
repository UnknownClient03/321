package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginManager;
import com.example.myapplication.MyInfoAndFamHis;
import com.example.myapplication.R;
import com.example.myapplication.SQLConnection;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Language#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Language extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Language() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_AllAboutMe.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Language newInstance(String param1, String param2) {
        Fragment_Language fragment = new Fragment_Language();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //this flag denotes wether the
    private boolean updateFlag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SQLConnection c = new SQLConnection("user1", "");
        LoginManager manager = ((MyInfoAndFamHis)getActivity()).manager;
        View layout = inflater.inflate(R.layout.fragment_language, container, false);
        if(!c.isConn()) return layout;
        Button submit = (Button)layout.findViewById(R.id.button_Language);
        Button update = (Button)layout.findViewById(R.id.update_Language);
        LinearLayout lanContainer = (LinearLayout)layout.findViewById(R.id.languageContainer);
        Button addLanguage = (Button)layout.findViewById(R.id.button_add_langauge);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button button = (Button)getActivity().findViewById(R.id.button_MIAFH_4);
                button.performClick();

                SQLConnection c = new SQLConnection("user1", "");
                int ID = c.getMaxID("GuardianLanguage");
                if(updateFlag)
                {
                    String query = "DELETE FROM GuardianLanguage WHERE guardianID = ?";
                    c.update(query, new String[]{ String.valueOf(manager.guardianID) }, new char[]{ 'i' });
                }

                for(int i = 0; i < lanContainer.getChildCount(); i++)
                {
                    EditText language = (EditText)lanContainer.getChildAt(i);
                    if(language.getText().length() == 0) continue;
                    String query = "INSERT INTO GuardianLanguage VALUES (?, ?, ?)";
                    String[] params = { String.valueOf(manager.guardianID), String.valueOf(ID+i), language.getText().toString() };
                    char[] paramTypes = { 'i', 'i', 's' };
                    c.update(query, params, paramTypes);
                }
                c.disconnect();
            }});
        addLanguage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lanContainer.getChildCount() > 7) return;
                else addlanguageInput(lanContainer, new EditText(layout.getContext()));
            }});

        String query = "SELECT guardianID, ID, language FROM GuardianLanguage WHERE guardianID = ?;";
        HashMap<String, String[]> result = c.select(query, new String[]{String.valueOf(manager.guardianID)}, new char[]{'i'});
        if(result.get("ID").length == 0) update.setVisibility(View.INVISIBLE);
        else
        {
            submit.setVisibility(View.INVISIBLE);
            addLanguage.setVisibility(View.INVISIBLE);

            int num = result.get("guardianID").length;
            EditText first = (EditText)lanContainer.getChildAt(0);
            first.setText(result.get("language")[0]);
            first.setEnabled(false);

            lanContainer.post(new Runnable() {
                @Override
                public void run() {
                    for(int i = 1; i < num; i++)
                    {
                        EditText x = addlanguageInput(lanContainer, new EditText(layout.getContext()));
                        x.setText(result.get("language")[i]);
                        x.setEnabled(false);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lanContainer.getWidth(), (int)(lanContainer.getHeight()*num));
                    lanContainer.setLayoutParams(params);
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    for(int i = 0; i < num; i++)
                        lanContainer.getChildAt(i).setEnabled(true);

                    submit.setVisibility(View.VISIBLE);
                    addLanguage.setVisibility(View.VISIBLE);
                    updateFlag = true;
                }
            });
        }
        c.disconnect();
        return layout;
    }

    EditText addlanguageInput(LinearLayout lanContainer, EditText txtField)
    {
        EditText Default = (EditText)lanContainer.getChildAt(0);
        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(Default.getWidth(), Default.getHeight());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) Default.getLayoutParams();
        Params.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        txtField.setLayoutParams(Params);
        txtField.setTextSize(16);

        float fl = (lanContainer.getChildCount()+1) / ((float)lanContainer.getChildCount());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lanContainer.getWidth(), (int)(lanContainer.getHeight()*fl));

        lanContainer.setLayoutParams(params);
        lanContainer.addView(txtField);

        return txtField;
    }
}