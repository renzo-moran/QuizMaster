package com.example.quizmaster;

public class QuizTimer {
    private long elapsedTime;  // Elapsed quiz time in milliseconds
    private long startPeriodTime;  // Start time of the period which duration is being measured
    private boolean timeKeeping;   // Flag to indicate if time keeping is in progress

    public QuizTimer() {
        elapsedTime = 0;
        timeKeeping = false;
    }

    public void startTimeKeeping() {
        elapsedTime = 0;
        startPeriodTime = System.currentTimeMillis(); // To start counting the time of the current period
        timeKeeping = true;  // Time keeping is in progress
    }

    // Method to be called when it's desired to make a pause on the time keeping for whatever reason
    // The elapsed time for the current period will be added to elapsedTime so that when it is time to resume,
    // a new period should be started
    public void pauseTimeKeeping() {
        if (timeKeeping) {
            elapsedTime += System.currentTimeMillis() - startPeriodTime;
            timeKeeping = false;  // time keeping has stopped
        }
    }

    // Method to be called when it's desired to resume the time keeping after a pause
    // For this, a new period is started
    public void resumeTimeKeeping() {
        if (!timeKeeping) {
            startPeriodTime = System.currentTimeMillis();  // To start counting the time of a new period
            timeKeeping = true;
        }
    }

    // Returns the elapsed time in milliseconds since the start of the time keeping
    public long getElapsedTime() {
        if (timeKeeping) {
            pauseTimeKeeping(); // To refresh elapsedTime
            resumeTimeKeeping();
        }
        return elapsedTime;
    }
}
