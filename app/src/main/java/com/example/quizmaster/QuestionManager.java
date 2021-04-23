package com.example.quizmaster;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionManager {
    private QuizMasterApplication application;  // The application object
    private String quizType;  // "history" or "math"
    private ArrayList<HashMap> questionList;  // The list of selected questions for the quiz
    private int currentQuestion;  // Number of the current question - initially 0

    public QuestionManager(QuizMasterApplication application, String quizType, int maxQuestions) {
        this.application = application;
        this.quizType = quizType;
        questionList = new ArrayList<HashMap>();

        if (maxQuestions > 0)
        questionList = maxQuestions > 0 ? application.getQuizzes("history")  // TODO: Be able to pass max number of questions to this method
                                        : null;

        currentQuestion = 0;
    }

    public HashMap getNextQuestion() {
        if (currentQuestion == questionList.size()) {
            return null;  // No more questions available
        }

        HashMap nextQuestionHashMap = (HashMap)questionList.get(currentQuestion);
        currentQuestion++;
        return nextQuestionHashMap;
    }

    // Getter for the number of current question - starting with 1
    public int getCurrentQuestion() {
        return currentQuestion-1;
    }
}
