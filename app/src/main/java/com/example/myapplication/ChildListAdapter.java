package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Child;

import java.util.ArrayList;
import java.util.List;

public class ChildListAdapter extends ArrayAdapter<Child> {

    private boolean showCheckboxes = false;
    private List<Child> selectedChildren = new ArrayList<>(); // To keep track of selected children

    public ChildListAdapter(Context context, List<Child> children) {
        super(context, 0, children);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // converts XML layout files into View objects and inflates a new view from the child_list_item XML layout file
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_list_item, parent, false);
        }

        // Get the child item for this position
        Child child = getItem(position);

        TextView childName = convertView.findViewById(R.id.child_name);
        TextView childAge = convertView.findViewById(R.id.child_age);
        ImageView profilePicture = convertView.findViewById(R.id.profile_picture);
        CheckBox checkBox = convertView.findViewById(R.id.child_checkbox);

        // Update the UI with child's data
        childName.setText(child.getName());
        childAge.setText("Age: " + child.getAge());

        // Show or hide the checkbox based on the flag
        // Uses shorthand way to write an if-else statement
        checkBox.setVisibility(showCheckboxes ? View.VISIBLE : View.GONE);
        checkBox.setChecked(selectedChildren.contains(child)); // Maintain the checkbox state

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedChildren.add(child);
            } else {
                selectedChildren.remove(child);
            }
        });

        return convertView;
    }

    // Control checkbox visibility
    public void showCheckboxes(boolean show) {
        this.showCheckboxes = show;
        notifyDataSetChanged(); // Refresh the list
    }

    // Method to get selected children when checkboxes are visible
    public List<Child> getSelectedChildren() {
        return new ArrayList<>(selectedChildren); // Return a copy of the selected children list
    }
}
