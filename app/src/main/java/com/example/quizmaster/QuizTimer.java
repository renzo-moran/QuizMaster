package com.example.quizmaster;

public class QuizTimer {
    private long elapsedTime;  // Elapsed quiz time in milliseconds
    private long startPeriodTime;  // Start time of the period which duration is being measured
    private boolean timeKeeping;   // Flag to indicate if time keeping is in progress

    // Constructor to be used when time will start counting from 0 (no elapsed time)
    public QuizTimer() {
        elapsedTime = 0;
        timeKeeping = false;
    }

    // Constructor to be used when a predetermined elapsedTime should be observed
    public QuizTimer(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        timeKeeping = false;
    }

    // This will reset any previous elapsed time. If this is not desired, use resumeTimeKeeping() instead
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

    // Returns the elapsed time as a string in the format "hh:mm:ss" since the start of the time keeping
    public String getElapsedTimeHHMMSS() {
        int elapsedTimeInSeconds = (int)(getElapsedTime()/1000);  // Quiz elapsed time in milliseconds

        // Express the elapsed time in hours, minutes and seconds
        int hoursElapsedTime = elapsedTimeInSeconds/3600;
        int remainderMinutes = elapsedTimeInSeconds % 3600;
        int minutesElapsedTime = remainderMinutes/60;
        int secondsElapsedTime = remainderMinutes % 60;

        // Display the time
        String timeString = String.format("%02d:%02d:%02d", hoursElapsedTime, minutesElapsedTime, secondsElapsedTime);
        return timeString;
    }

//    public void setElapsedTime(long newElapsedTime) {
//
//    }

}
