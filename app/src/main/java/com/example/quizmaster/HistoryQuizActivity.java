package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class HistoryQuizActivity extends AppCompatActivity {

    public final int REFRESH_INTERVAL = 500;
    public final int TOTAL_QUESTIONS = 10;  // Number of questions of the quiz

    private SharedPreferences sharedPref; // Will hold the SharedPreferences object
    private boolean quizInProgress = false;
    private QuizTimer quizTimer;  // Will control the elapsed time of the quiz
    private Timer refreshTimer;   // Will be used to periodically refresh the GUI as needed
    private QuestionManager questionManager;  // Will manage the quiz questions

    private Button btnTime;
    private TextView textViewQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the theme according to preference
        if (sharedPref.getBoolean("darkTheme", false))
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_quiz);

        // Instantiate the views
        btnTime = (Button)findViewById(R.id.btnTime);
        textViewQuestion = (TextView)findViewById(R.id.textViewQuestion);

        // Create the refresh timer and set its callback
        refreshTimer = new Timer(true);

        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayElapsedTime();
                    }
                });
            }
        }, REFRESH_INTERVAL, REFRESH_INTERVAL);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!quizInProgress) {
            // TODO: Show a countdown before starting the quiz or have the user press a button
            startQuiz();
            displayElapsedTime();

            // Display next question and alternatives
            String question = questionManager.getNextQuestion();
            if (question == "") {
                // No more questions - TODO: finish the game
            }
            else {
                textViewQuestion.setText(question);

                // TODO: Depending on type of question, display the answer GUIs

            }
        }
    }

    // Does all the required initializations to start a quiz and proceeds to start it
    private void startQuiz() {
        quizTimer = new QuizTimer();
        quizTimer.startTimeKeeping();

        questionManager = new QuestionManager(TOTAL_QUESTIONS);

        quizInProgress = true;
    }

    private void displayElapsedTime() {
        int elapsedTimeInSeconds = (int)(quizTimer.getElapsedTime()/1000);  // Quiz elapsed time in milliseconds

        // Express the elapsed time in hours, minutes and seconds
        int hoursElapsedTime = elapsedTimeInSeconds/3600;
        int remainderMinutes = elapsedTimeInSeconds % 3600;
        int minutesElapsedTime = remainderMinutes/60;
        int secondsElapsedTime = remainderMinutes % 60;

        // Display the time
        String timeString = String.format("%02d:%02d:%02d", hoursElapsedTime, minutesElapsedTime, secondsElapsedTime);
        btnTime.setText(timeString);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }
}