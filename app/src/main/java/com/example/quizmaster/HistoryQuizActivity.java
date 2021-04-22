package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Button;


public class HistoryQuizActivity extends AppCompatActivity {

    public final int REFRESH_INTERVAL = 500;
    public final int MAX_QUESTIONS = 10;  // Maximum number of questions of the quiz

    private SharedPreferences sharedPref; // Will hold the SharedPreferences object
    private boolean quizInProgress = false;
    private QuizTimer quizTimer;  // Will control the elapsed time of the quiz
    private Timer refreshTimer;   // Will be used to periodically refresh the GUI as needed
    private QuestionManager questionManager;  // Will manage the quiz questions
    private HashMap questionHashMap;  // HashMap of current question
    private int score; // Number of correct questions
    private String playerAnswer;  // The player's answer to current question

    // Views of the GUI
    private TextView textViewQuestionNumber;
    private Button btnTime;
    private TextView textViewQuestion;
    private RadioButton rdbOption1, rdbOption2, rdbOption3, rdbOption4;
    private Button btnSkip, btnAnswer, btnEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the theme according to preference
        if (sharedPref.getBoolean("darkTheme", false)) {
            setTheme(R.style.DarkTheme);
            // TODO: Change background to dark version
        }
        else {
            setTheme(R.style.AppTheme);
            // TODO: Change background to light version
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_quiz);

        // Instantiate the views
        textViewQuestionNumber = (TextView)findViewById(R.id.textViewQuestionNumber);
        btnTime = (Button)findViewById(R.id.btnTime);
        textViewQuestion = (TextView)findViewById(R.id.textViewQuestion);
        rdbOption1 = (RadioButton)findViewById(R.id.rdbOption1);
        rdbOption2 = (RadioButton)findViewById(R.id.rdbOption2);
        rdbOption3 = (RadioButton)findViewById(R.id.rdbOption3);
        rdbOption4 = (RadioButton)findViewById(R.id.rdbOption4);
        btnSkip = (Button)findViewById(R.id.btnSkip);
        btnAnswer = (Button)findViewById(R.id.btnAnswer);
        btnEnd = (Button)findViewById(R.id.btnEnd);

        // Set the listener for the radio buttons
        rdbOption1.setOnCheckedChangeListener(new Radio_check());
        rdbOption2.setOnCheckedChangeListener(new Radio_check());
        rdbOption3.setOnCheckedChangeListener(new Radio_check());
        rdbOption4.setOnCheckedChangeListener(new Radio_check());

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

            // Get next question and show it or finish the game if no more questions
            questionHashMap = questionManager.getNextQuestion();
            if (questionHashMap == null) {
                // No more questions - finish the game as quiz completed
                finishGame(true);
            }
            else {
                displayCurrentQuestion();
            }
        }

        // User skipped the question therefore get the next one
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get next question and show it or finish the game if no more questions
                questionHashMap = questionManager.getNextQuestion();
                if (questionHashMap == null) {
                    // No more questions - finish the game as quiz completed
                    finishGame(true);
                }
                else {
                    displayCurrentQuestion();
                }
            }
        });

        // User submitted answer to current question
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if an answer was effectively selected
                if (playerAnswer.isEmpty()) {
                    //Toast.makeText(getApplicationContext(),"Answer was not provided", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), "Answer was not provided", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
                else {
                    // Add the point to the user if response is correct
                    if (playerAnswer.equals(questionHashMap.get("quest_option5").toString())) {
                        score++;
                    }
                    // Get next question and show it or finish the game if no more questions
                    questionHashMap = questionManager.getNextQuestion();
                    if (questionHashMap == null) {
                        // No more questions - finish the game as quiz completed
                        finishGame(true);
                    }
                    else {
                        displayCurrentQuestion();
                    }
                }
            }
        });

        // User wants to end (abort) quiz. This method asks for confirmation
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizTimer.pauseTimeKeeping(); // Pause timer to get user's confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryQuizActivity.this);
                builder.setTitle("Please confirm")
                        .setMessage("Are you sure you want to cancel the quiz?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed to abort quiz. Finish the game as quiz NOT completed
                                finishGame(false);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User changed his mind
                                quizTimer.resumeTimeKeeping();
                            }
                        }).show();


//                // Check if an answer was effectively selected
//                if (playerAnswer.isEmpty())
//                    Toast.makeText(getApplicationContext(),"Answer was not provided", Toast.LENGTH_SHORT).show();
//                else {
//                    // Add the point to the user if response is correct
//                    if (playerAnswer.equals(questionHashMap.get("quest_option5").toString())) {
//                        score++;
//                    }
//                    // Get next question and show it or finish the game if no more questions
//                    questionHashMap = questionManager.getNextQuestion();
//                    if (questionHashMap == null) {
//                        // No more questions - finish the game
//                        finishGame();
//                    }
//                    else {
//                        displayCurrentQuestion();
//                    }
//                }
            }
        });
    }

    // Manually controls the selection of radio buttons by deselecting the not-clicked ones
    // and assigns the answer based on selected radio button
    class Radio_check implements  CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView.getId() == R.id.rdbOption1) {
                    rdbOption2.setChecked(false);
                    rdbOption3.setChecked(false);
                    rdbOption4.setChecked(false);
                    playerAnswer = questionHashMap.get("quest_option1").toString();
                }
                if (buttonView.getId() == R.id.rdbOption2) {
                    rdbOption1.setChecked(false);
                    rdbOption3.setChecked(false);
                    rdbOption4.setChecked(false);
                    playerAnswer = questionHashMap.get("quest_option2").toString();
                }
                if (buttonView.getId() == R.id.rdbOption3) {
                    rdbOption1.setChecked(false);
                    rdbOption2.setChecked(false);
                    rdbOption4.setChecked(false);
                    playerAnswer = questionHashMap.get("quest_option3").toString();
                }
                if (buttonView.getId() == R.id.rdbOption4) {
                    rdbOption1.setChecked(false);
                    rdbOption2.setChecked(false);
                    rdbOption3.setChecked(false);
                    playerAnswer = questionHashMap.get("quest_option4").toString();
                }
            }
        }
    }

    // Does all the required initializations to start a quiz and proceeds to start it
    private void startQuiz() {
        quizTimer = new QuizTimer();
        quizTimer.startTimeKeeping();
        score = 0;

        questionManager = new QuestionManager((QuizMasterApplication)getApplication(), "history", MAX_QUESTIONS);

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

    // Displays the current question along with the corresponding views for the answers
    private void displayCurrentQuestion() {
        textViewQuestionNumber.setText(String.format("Question %d",questionManager.getCurrentQuestion() + 1));
        textViewQuestion.setText(questionHashMap.get("quest_text").toString());
        playerAnswer = "";

        // Show the corresponding GUI controls for the answers depending of question type
        String questionType = questionHashMap.get("quest_type").toString();
        switch(questionType) {
            case "MC":  // Multiple Choice
                rdbOption1.setVisibility(View.VISIBLE);
                rdbOption2.setVisibility(View.VISIBLE);
                rdbOption3.setVisibility(View.VISIBLE);
                rdbOption4.setVisibility(View.VISIBLE);
                rdbOption1.setText(questionHashMap.get("quest_option1").toString());
                rdbOption2.setText(questionHashMap.get("quest_option2").toString());
                rdbOption3.setText(questionHashMap.get("quest_option3").toString());
                rdbOption4.setText(questionHashMap.get("quest_option4").toString());
                rdbOption1.setChecked(false);
                rdbOption2.setChecked(false);
                rdbOption3.setChecked(false);
                rdbOption4.setChecked(false);
                break;
            case "TF":  // True or False
                rdbOption1.setVisibility(View.VISIBLE);
                rdbOption2.setVisibility(View.VISIBLE);
                rdbOption3.setVisibility(View.INVISIBLE);
                rdbOption4.setVisibility(View.INVISIBLE);
                rdbOption1.setText(questionHashMap.get("quest_option1").toString());
                rdbOption2.setText(questionHashMap.get("quest_option2").toString());
                rdbOption1.setChecked(false);
                rdbOption2.setChecked(false);
                break;
            default:   // This part should never be reached
                break;
        }
    }

    private void finishGame(boolean quizCompleted) {
        // TODO: Define an fragment to display results nicely. For now only a popup message displays the results
        if (quizCompleted) {
            int elapsedTimeInSeconds = (int)(quizTimer.getElapsedTime()/1000);  // Quiz elapsed time in milliseconds
            quizTimer.pauseTimeKeeping();
            showMessage("Quiz Completed", "Your results are:");
        }
        else {
            showMessage("Aborted", "Quiz canceled" + System.lineSeparator() + "You can start a new quiz any time!");
            //super.onBackPressed(); // Return to previous screen
        }
    }

    // Method to display a message
    private void showMessage(String title, String messageContent) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(messageContent);
        builder.show();
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