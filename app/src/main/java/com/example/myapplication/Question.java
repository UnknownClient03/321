package com.example.myapplication;

public class Question {
    private String questionText;
    private String userAnswer;
    private boolean isNumeric;

    public Question(String questionText, boolean isNumeric) {
        this.questionText = questionText;
        this.isNumeric = isNumeric;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public int getId() {
        // Dummy method, replace this with actual logic to get question ID
        return 1;  // Replace with actual question ID logic
    }
}