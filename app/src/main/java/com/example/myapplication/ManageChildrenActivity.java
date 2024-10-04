package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Child;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageChildrenActivity extends AppCompatActivity {

    private static final int ADD_CHILD_REQUEST = 1; // Request code for AddChildActivity

    private ListView childListView;
    private Button removeChildButton, submitRemoveButton, addChildButton;
    private ChildListAdapter adapter;
    private List<Child> childrenList = new ArrayList<>();
    private ImageView backArrow;
    private TextView noChildrenMessage;

    private boolean isRemoveMode = false; // Track the removal mode

    private int currentGuardianID; // Store the current guardian ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_children);

        // Retrieve the guardian ID from the intent
        currentGuardianID = getIntent().getIntExtra("guardianID", 0);

        childListView = findViewById(R.id.child_list_view);
        removeChildButton = findViewById(R.id.remove_child_button);
        submitRemoveButton = findViewById(R.id.submit_remove_button);
        addChildButton = findViewById(R.id.add_child_button);
        backArrow = findViewById(R.id.back_arrow);
        noChildrenMessage = findViewById(R.id.no_children_message);

        // Initialize adapter with children data
        adapter = new ChildListAdapter(this, childrenList);
        childListView.setAdapter(adapter);

        // Set up listeners
        setupListeners();

        Bundle extras = getIntent().getExtras();
        NavBarManager.setNavBarButtons(ManageChildrenActivity.this, new LoginManager(extras.getInt("guardianID"), 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload children from the database whenever the activity resumes
        loadChildrenFromDatabase();
    }

    private void setupListeners() {
        removeChildButton.setOnClickListener(v -> {
            if (isRemoveMode) {
                // Cancel removal mode
                adapter.showCheckboxes(false);
                submitRemoveButton.setVisibility(View.GONE);
                removeChildButton.setText("Remove Child");
            } else {
                // Enable removal mode
                adapter.showCheckboxes(true);
                submitRemoveButton.setVisibility(View.VISIBLE);
                removeChildButton.setText("Cancel Remove");
            }
            isRemoveMode = !isRemoveMode; // Toggle mode
        });

        submitRemoveButton.setOnClickListener(v -> {
            // Get selected children
            List<Child> selectedChildren = adapter.getSelectedChildren();
            for (Child child : selectedChildren) {
                // Remove child from the list and the database
                childrenList.remove(child);
                removeChildFromDatabase(child.getId(), currentGuardianID);
            }
            Toast.makeText(ManageChildrenActivity.this, "Selected children removed.", Toast.LENGTH_SHORT).show();

            adapter.showCheckboxes(false);
            submitRemoveButton.setVisibility(View.GONE);
            removeChildButton.setText("Remove Child");
            isRemoveMode = false;
            loadChildrenFromDatabase(); // Refresh data
        });

        addChildButton.setOnClickListener(v -> {
            // Start AddChildActivity to add a new child
            Intent intent = new Intent(ManageChildrenActivity.this, AddChildActivity.class);
            intent.putExtra("guardianID", currentGuardianID); // Pass the guardian ID
            startActivityForResult(intent, ADD_CHILD_REQUEST);
        });

        backArrow.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CHILD_REQUEST && resultCode == RESULT_OK) {
            Log.d("ManageChildrenActivity", "Child added successfully, refreshing list.");
            loadChildrenFromDatabase(); // Refresh the list from the database
        }
    }

    private void loadChildrenFromDatabase() {
        SQLConnection sqlConnection = new SQLConnection("user1", "");

        // Clear the existing children list
        childrenList.clear();

        HashMap<String, String[]> result = sqlConnection.select("SELECT ID, fname, lname, DOB, sex, guardianID FROM Child WHERE guardianID = " + currentGuardianID);

        if (result != null && result.get("ID") != null) {
            String[] ids = result.get("ID");
            String[] fnames = result.get("fname");
            String[] lnames = result.get("lname");
            String[] dobs = result.get("DOB");
            String[] sexes = result.get("sex");
            String[] guardianIDs = result.get("guardianID");

            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                String fname = fnames[i];
                String lname = lnames[i];
                String dob = dobs[i];
                String sex = sexes[i];
                int guardianID = Integer.parseInt(guardianIDs[i]);

                if (guardianID == currentGuardianID) {
                    childrenList.add(new Child(id, fname, lname, dob, sex));
                }
            }
        }

        adapter.notifyDataSetChanged(); // Notify adapter of data change

        // Show or hide the "No children added" message based on the list size
        if (childrenList.isEmpty()) {
            noChildrenMessage.setVisibility(View.VISIBLE);
            childListView.setVisibility(View.GONE); // Hide the ListView if there are no children
        } else {
            noChildrenMessage.setVisibility(View.GONE);
            childListView.setVisibility(View.VISIBLE); // Show the ListView if there are children
        }

        sqlConnection.disconnect();
    }

    // Remove child from the database
    private void removeChildFromDatabase(int childId, int guardianID) {
        SQLConnection sqlConnection = new SQLConnection("user1", "");
        sqlConnection.update("DELETE FROM Child WHERE ID = " + childId + " AND guardianID = " + guardianID);
        sqlConnection.disconnect();
    }
}
