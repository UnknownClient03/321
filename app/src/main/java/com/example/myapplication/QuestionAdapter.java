package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputType;
import android.widget.Toast;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questions;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question currentQuestion = questions.get(position);

        holder.questionText.setText(currentQuestion.getQuestionText());

        // Set hint in text box
        holder.answerInput.setHint("Enter answer here");

        // Clear text box when clicked
        holder.answerInput.setOnClickListener(v -> {
            if (holder.answerInput.getText().toString().isEmpty()) {
                holder.answerInput.setText("");
            }
        });

        // Retain hint text when not typing
        holder.answerInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && holder.answerInput.getText().toString().isEmpty()) {
                holder.answerInput.setHint("Enter answer here");
            }
        });

        // Allow only numeric input if the question requires it
        if (currentQuestion.isNumeric()) {
            holder.answerInput.setInputType(InputType.TYPE_CLASS_NUMBER); // Restrict input to numbers
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        EditText answerInput;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            answerInput = itemView.findViewById(R.id.answerInput);
        }
    }
}
