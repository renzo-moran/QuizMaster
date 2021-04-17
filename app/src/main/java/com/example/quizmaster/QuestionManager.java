package com.example.quizmaster;

public class QuestionManager {
    private int totalQuestions;
    private int currentQuestion;

    public QuestionManager(int numberOfQuestions) {
        // TODO: Add a parameter to define type of questions: History or Math

        totalQuestions = numberOfQuestions > 0 ? numberOfQuestions : 0;
        // TODO: Use database method to retrieve questions from database

        currentQuestion = 0;
    }

    public String getNextQuestion() {
        if (currentQuestion == totalQuestions) {
            return "";  // TODO: change this based on the object that will be returned
        }
        currentQuestion++;
        return "What is the Roman name of Greece's Zeus?"; // TODO: change this using the array of questions
    }

    public String getAnswer() {
        return "Jupiter";
    }
}
