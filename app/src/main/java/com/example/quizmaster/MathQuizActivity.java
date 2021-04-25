package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MathQuizActivity  extends AppCompatActivity {
    public static final String QUIZ_TYPE = "math";
    public final int REFRESH_INTERVAL = 500; // Interval to refresh elapsed time, in milliseconds
    public final int MAX_QUESTIONS = 10;  // Maximum number of questions of the quiz
    public final String HIGHEST_SCORE_KEY = "mathHighestScore";
    public final int ANIMATION_DURATION = 2000; // Duration of animation - 2 seconds

    private QuizMasterApplication quizApplication;  // The application object
    private SharedPreferences sharedPref; // Will hold the SharedPreferences object
    private boolean quizInProgress = false;
    private QuizTimer quizTimer;  // Will control the elapsed time of the quiz
    private Timer refreshTimer;   // Will be used to periodically refresh the GUI as needed
    private QuestionManager questionManager;  // Will manage the quiz questions
    private HashMap questionHashMap;  // HashMap of current question
    private int totalQuestions;  // Total number of quiz questions - typically same as MAX_QUESTIONS
    private int correctAnswers; // Number of correct answers
    private String playerAnswer;  // The player's answer to current question
    private int highestScore; // Highest score that is saved for this type of quiz

    // Views of the GUI
    private TextView textViewQuestionNumber;
    private Button btnTime;
    private TextView textViewQuestion;
    private LinearLayout linearLayoutTextVew;
    private EditText editTextAnswer;
    private LinearLayout linearLayoutRadio1, linearLayoutRadio2;
    private RadioButton rdbOption1, rdbOption2, rdbOption3, rdbOption4;
    private Button btnSkip, btnAnswer, btnEnd;
    private ImageView imageView;

    // Control flags
    private boolean creatingActivity = false; // Flag to indicate if main activity is being created
    private boolean saveState;  // Will store the setting related to saving quiz status on close
    private boolean darkTheme;  // Will store the setting related to using dark theme
    private boolean backToMainScreen;  // Will be true at the end of a quiz to automatically return to main screen

    public MathQuizActivity SELF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creatingActivity = true;
        SELF = this;

        quizApplication = (QuizMasterApplication)getApplication();
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the theme according to preference
        darkTheme = sharedPref.getBoolean("darkTheme", false);
        if (darkTheme) {
            setTheme(R.style.DarkTheme);
            // TODO: Change background to dark version
        }
        else {
            setTheme(R.style.AppTheme);
            // TODO: Change background to light version
        }

        setContentView(R.layout.activity_quiz);

        // Obtain the highest score saved for this type of quiz
        highestScore = sharedPref.getInt(HIGHEST_SCORE_KEY, 0);

        // Instantiate the views
        textViewQuestionNumber = (TextView)findViewById(R.id.textViewQuestionNumber);
        btnTime = (Button)findViewById(R.id.btnTime);
        textViewQuestion = (TextView)findViewById(R.id.textViewQuestion);
        linearLayoutTextVew = (LinearLayout)findViewById(R.id.linearLayoutTextView);
        editTextAnswer = (EditText)findViewById(R.id.editTextAnswer);
        linearLayoutRadio1 = (LinearLayout)findViewById(R.id.linearLayoutRadio1);
        linearLayoutRadio2 = (LinearLayout)findViewById(R.id.linearLayoutRadio2);
        rdbOption1 = (RadioButton)findViewById(R.id.rdbOption1);
        rdbOption2 = (RadioButton)findViewById(R.id.rdbOption2);
        rdbOption3 = (RadioButton)findViewById(R.id.rdbOption3);
        rdbOption4 = (RadioButton)findViewById(R.id.rdbOption4);
        btnSkip = (Button)findViewById(R.id.btnSkip);
        btnAnswer = (Button)findViewById(R.id.btnAnswer);
        btnEnd = (Button)findViewById(R.id.btnEnd);
        imageView = (ImageView)findViewById(R.id.imageView);

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

                        // Check if it's time to return to MainActivity
                        if (backToMainScreen) {
                            backToMainScreen = false;
                            finish();
                        }
                    }
                });
            }
        }, REFRESH_INTERVAL, REFRESH_INTERVAL);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // If there is no quiz in progress check if there was one that was interrupted before or create a new one
        if (!quizInProgress) {
            saveState = sharedPref.getBoolean("saveOnClose", false);
            String savedQuizType = sharedPref.getString("quiz_in_progress", MainActivity.NO_QUIZ_IN_PROGRESS);
            // Check if there was a quiz in progress last time the application was closed and it was of this quiz type
            if (saveState && savedQuizType.equals(QUIZ_TYPE)) {
                long elapsedTime = sharedPref.getLong("elapsed_time", 0);
                btnTime.setText(sharedPref.getString("elapsed_time_hhmmss", getResources().getString(R.string.initial_time)));
                totalQuestions = sharedPref.getInt("total_questions", MAX_QUESTIONS);
                correctAnswers = sharedPref.getInt("correct_answers", 0);
                int currentQuestionNumber = sharedPref.getInt("current_question_number", 1);
                if (correctAnswers >= currentQuestionNumber && currentQuestionNumber > 0)
                    correctAnswers = currentQuestionNumber-1;

                resumeQuiz(elapsedTime, currentQuestionNumber);
            } else {
                // TODO: Show a countdown before starting the quiz or have the user press a button
                startQuiz();
                displayElapsedTime();
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
                // Check if an answer was effectively selected or entered
                String questionType = questionHashMap.get("quest_type").toString();
                // For question of type "WORDS", get the answer from the edit box and trim it
                if (questionType.equals("WORDS"))
                    playerAnswer = editTextAnswer.getText().toString().trim();

                if (playerAnswer.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "Please, provide an answer", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
                else {
                    playerAnswer = playerAnswer.toUpperCase(); // To compare with response in uppercase
                    // Set the corresponding smiley and increase the correct answers counter if response is correct
                    if (playerAnswer.equals(questionHashMap.get("quest_option5").toString().toUpperCase())) {
                        correctAnswers++;
                        imageView.setImageResource(R.drawable.grsmile);
                    }
                    else {
                        imageView.setImageResource(R.drawable.redsad);
                    }
                    // Pause timer and perform animation
                    quizTimer.pauseTimeKeeping();
                    enableQuizControls(false); // To prevent user from pressing buttons during animation
                    imageView.setAlpha(0f);
                    imageView.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(
                            new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {  // Callback function
                                    // Get next question and show it or finish the game if no more questions
                                    questionHashMap = questionManager.getNextQuestion();
                                    if (questionHashMap == null)
                                        // No more questions - finish the game as quiz completed
                                        finishGame(true);
                                    else {
                                        displayCurrentQuestion();
                                        quizTimer.resumeTimeKeeping();  // Resume timer
                                        enableQuizControls(true);
                                    }
                                }
                            }
                    );
                }
            }
        });

        // User wants to end (abort) quiz. This method asks for confirmation
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizTimer.pauseTimeKeeping(); // Pause timer to get user's confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(MathQuizActivity.this);
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
        enableQuizControls(true);
        correctAnswers = 0;
        questionManager = new QuestionManager(quizApplication, QUIZ_TYPE, MAX_QUESTIONS);
        totalQuestions = questionManager.getTotalQuestions(); // total number of questions effectively loaded
        quizTimer = new QuizTimer();
        quizTimer.startTimeKeeping();
        quizInProgress = true;

        // Store that a quiz is in progress in order to restore quiz after close if needed
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString("quiz_in_progress", QUIZ_TYPE);
        ed.apply();
    }

    private void resumeQuiz(long elapsedTime, int currentQuestionNumber) {
        enableQuizControls(true);
        questionManager = new QuestionManager(quizApplication, QUIZ_TYPE, totalQuestions, currentQuestionNumber);
        totalQuestions = questionManager.getTotalQuestions(); // total number of questions effectively loaded
        quizTimer = new QuizTimer(elapsedTime);
        quizTimer.resumeTimeKeeping();
        quizInProgress = true;

        // Store that a quiz is in progress in order to restore quiz after close if needed
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString("quiz_in_progress", QUIZ_TYPE);
        ed.apply();
    }

    private void displayElapsedTime() {
        String strElapsedTime = quizTimer.getElapsedTimeHHMMSS();
        btnTime.setText(strElapsedTime);
    }

    // Displays the current question along with the corresponding views for the answers
    private void displayCurrentQuestion() {
        textViewQuestionNumber.setText(String.format("Question %d",questionManager.getCurrentQuestionNumber()));
        textViewQuestion.setText(questionHashMap.get("quest_text").toString());
        imageView.setImageResource(R.drawable.questmark);
        editTextAnswer.setText("");
        playerAnswer = "";

        // Show the corresponding GUI controls for the answers depending on question type
        String questionType = questionHashMap.get("quest_type").toString();
        switch(questionType) {
            case "MC":  // Multiple Choice
                linearLayoutRadio1.setVisibility(View.VISIBLE);
                linearLayoutRadio2.setVisibility(View.VISIBLE);
                linearLayoutTextVew.setVisibility(View.GONE);
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
                linearLayoutRadio1.setVisibility(View.VISIBLE);
                linearLayoutRadio2.setVisibility(View.GONE);
                linearLayoutTextVew.setVisibility(View.GONE);
                rdbOption1.setVisibility(View.VISIBLE);
                rdbOption2.setVisibility(View.VISIBLE);
                rdbOption3.setVisibility(View.INVISIBLE);
                rdbOption4.setVisibility(View.INVISIBLE);
                rdbOption1.setText(questionHashMap.get("quest_option1").toString());
                rdbOption2.setText(questionHashMap.get("quest_option2").toString());
                rdbOption1.setChecked(false);
                rdbOption2.setChecked(false);
                break;
            case "WORDS":  // User enters text
                linearLayoutRadio1.setVisibility(View.GONE);
                linearLayoutRadio2.setVisibility(View.GONE);
                linearLayoutTextVew.setVisibility(View.VISIBLE);
                break;
            default:   // This part should never be reached
                break;
        }
    }

    private void finishGame(boolean quizCompleted) {
        // TODO: Define an fragment to display results nicely. For now only a popup message displays the results
        quizTimer.pauseTimeKeeping();
        displayElapsedTime();  // Refresh the display
        enableQuizControls(false);

        quizInProgress = false;  // No longer a quiz in progress

        // Store that a quiz is no longer in progress
        final SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString("quiz_in_progress", MainActivity.NO_QUIZ_IN_PROGRESS);  // To indicate that there is no longer a quiz in progress
        ed.apply();

        if (quizCompleted) {
            long elapsedTimeInMilliseconds = quizTimer.getElapsedTime();  // Quiz elapsed time in milliseconds
            quizApplication.updateQuizResult(QUIZ_TYPE, correctAnswers, elapsedTimeInMilliseconds); // Update quiz results in database

            // Display quiz results
            final int quizScore = quizApplication.getScore(correctAnswers, elapsedTimeInMilliseconds);
            String resultsMessage = String.format("Your results are:\r\n" +
                    "Correct answers: %d out of %d\r\n" +
                    "Elapsed time: %s\r\n" +
                    "Your Quiz Score: %d", correctAnswers, MAX_QUESTIONS, quizTimer.getElapsedTimeHHMMSS(), quizScore);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Check if there is a new highest score
                    if (quizScore > highestScore) {
                        // Update the shared preferences and the class property in memory with the new highest score
                        ed.putInt(HIGHEST_SCORE_KEY, quizScore);
                        ed.apply();
                        highestScore = quizScore;
                        // Notify the player
                        String congratulationMessage = String.format("Congratulations! You achieved a new Highest Score for a %s quiz: %d", QUIZ_TYPE, quizScore);

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SELF);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                backToMainScreen = true; // Will return to main screen automatically
                            }
                        });
                        builder.setCancelable(true);
                        builder.setTitle("New High Score");
                        builder.setMessage(congratulationMessage);
                        builder.show();
                    } else {
                        backToMainScreen = true; // Will return to main screen automatically
                    }
                }
            });
            builder.setCancelable(true);
            builder.setTitle("Quiz Completed");
            builder.setMessage(resultsMessage);
            builder.show();
        }
        else {
            String abortMessage = "Your quiz is now canceled." + System.lineSeparator() + "You can start a new quiz any time!";
            //showMessage("Confirmation", "Your quiz is now canceled." + System.lineSeparator() + "You can start a new quiz any time!");
            //super.onBackPressed(); // Return to previous screen

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    backToMainScreen = true; // Will return to main screen automatically
                }
            });
            builder.setCancelable(true);
            builder.setTitle("Confirmation");
            builder.setMessage(abortMessage);
            builder.show();
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

    // Enable or disable GUI controls of the quiz according to the parameter
    // flagEnable: true to enable, false to disable
    private void enableQuizControls(boolean flagEnable) {
        rdbOption1.setEnabled(flagEnable);
        rdbOption2.setEnabled(flagEnable);
        rdbOption3.setEnabled(flagEnable);
        rdbOption4.setEnabled(flagEnable);
        btnSkip.setEnabled(flagEnable);
        btnAnswer.setEnabled(flagEnable);
        btnEnd.setEnabled(flagEnable);
    }

    // Event called when the application is paused or deactivated, or when the orientation of the device changes
    // If a quiz is in progress:
    // - Quiz timer is paused
    // - Value of key application variables are stored here so that they can be restored later
    @Override
    protected void onPause() {
        if (quizInProgress) {
            quizTimer.pauseTimeKeeping();
            long elapsedTime = quizTimer.getElapsedTime();
            String strElapsedTime = quizTimer.getElapsedTimeHHMMSS();

            // Store the necessary values
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putLong("elapsed_time", elapsedTime);
            ed.putString("elapsed_time_hhmmss", strElapsedTime);
            ed.putInt("total_questions", totalQuestions);
            ed.putInt("correct_answers", correctAnswers);
            ed.putInt("current_question_number", questionManager.getCurrentQuestionNumber());
            ed.apply();

            creatingActivity = false;
        }

        super.onPause();
    }

    // Event called when the application is reactivated or when the rotation of the device has been completed
    // Values previously saved in onPause() are restored here to ensure user experience continuity
    @Override
    protected void onResume() {
        super.onResume();

        // Restore the selected preferences from settings
        saveState = sharedPref.getBoolean("saveOnClose", false);
        darkTheme = sharedPref.getBoolean("darkTheme", false);

        // Restore the previous state if the saveState preference is on or if we are NOT creating the activity
        // (for example, when doing orientation flip, or app deactivate/activate)
        if (saveState || !creatingActivity) {
            // Restore the saved values
            btnTime.setText(sharedPref.getString("elapsed_time_hhmmss", getResources().getString(R.string.initial_time)));
            totalQuestions = sharedPref.getInt("total_questions", MAX_QUESTIONS);
            correctAnswers = sharedPref.getInt("correct_answers", 0);
            int currentQuestion = sharedPref.getInt("current_question_number", 1);
            if (correctAnswers >= currentQuestion && currentQuestion > 0)
                correctAnswers = currentQuestion-1;

            // No need to read this item here as it is read in onCreate:
            //long elapsedTime = sharedPref.getLong("elapsed_time", 0);
        }

        creatingActivity = false;
    }

    @Override
    protected void onStop() {
        if (quizInProgress)
            startService(new Intent(getApplicationContext(), NotificationService.class));

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }

    @Override
    public void onBackPressed() {
        if (quizInProgress) {
            Snackbar.make(findViewById(android.R.id.content), "Quiz is in progress. If you want to cancel it, please tap on \"End Quiz\"", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
        else
            super.onBackPressed();  // Only return if no quiz is in progress
    }
}