package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context context;
    private List<Question> questions;
    private String[] statusOptions;

    public QuestionAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
        this.statusOptions = context.getResources().getStringArray(R.array.outcome_options);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question current = questions.get(position);
        holder.questionText.setText(current.getQuestionText());

        // Set up Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(adapter);

        // Set the Spinner to the current status
        int spinnerPosition = adapter.getPosition(capitalize(current.getStatus()));
        holder.statusSpinner.setSelection(spinnerPosition >= 0 ? spinnerPosition : 0);

        holder.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int spinnerPosition, long id) {
                String selectedStatus = parentView.getItemAtPosition(spinnerPosition).toString().toLowerCase();
                current.setStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optional: Handle case where nothing is selected
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        Spinner statusSpinner;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            statusSpinner = itemView.findViewById(R.id.status_spinner);
        }
    }

    /**
     * Utility method to capitalize the first letter of the status.
     * E.g., "normal" -> "Normal"
     */
    private String capitalize(String status) {
        if (status == null || status.length() == 0) return status;
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }
}
