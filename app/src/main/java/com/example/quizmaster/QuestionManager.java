package com.example.quizmaster;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionManager {
    private QuizMasterApplication application;  // The application object
    private String quizType;  // "history" or "math"
    private ArrayList<HashMap> questionList;  // The list of selected questions for the quiz
    private int currentQuestionNumber;  // Number of the current question - initially 0
    private SharedPreferences sharedPref; // SharedPreferences object

    // This constructor is to be used when a quiz is starting from scratch
    // Parameters are the application object, quiz type, and maximum number of questions to get from database
    public QuestionManager(QuizMasterApplication application, String quizType, int maxQuestions) {
        this.application = application;
        this.quizType = quizType;

        questionList = new ArrayList<HashMap>();
        questionList = maxQuestions > 0 ? application.getQuizQuestions(quizType, maxQuestions)
                                        : null;

        currentQuestionNumber = 0;
    }

    // This constructor is to be used when a quiz is to be resumed
    // Parameters are the application object, quiz type, total number of questions to be retrieved from DB, and current question number
    public QuestionManager(QuizMasterApplication application, String quizType, int totalQuestions, int currentQuestionNumber) {
        this.application = application;
        this.quizType = quizType;

        questionList = new ArrayList<HashMap>();
        questionList = totalQuestions > 0 ? application.getQuizQuestions(quizType, totalQuestions)
                                          : null;

        this.currentQuestionNumber = currentQuestionNumber - 1 < 0? 0 : currentQuestionNumber - 1;
    }

    public HashMap getNextQuestion() {
        if (currentQuestionNumber == questionList.size()) {
            return null;  // No more questions available
        }

        return questionList.get(currentQuestionNumber++);
    }

    // Getter for the number of current question - starting with 1
    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    // Returns total number of questions
    public int getTotalQuestions() {
        return questionList.size();
    }
}
