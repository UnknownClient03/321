package com.example.myapplication;

public class Question {
    private String questionText;
    private boolean isRequired;
    private String status; // "normal", "review", "refer"

    public Question(String questionText, boolean isRequired, String status) {
        this.questionText = questionText;
        this.isRequired = isRequired;
        this.status = status;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Optionally, include validation for status values
    public boolean isValidStatus() {
        return status.equals("normal") || status.equals("review") || status.equals("refer");
    }
}
