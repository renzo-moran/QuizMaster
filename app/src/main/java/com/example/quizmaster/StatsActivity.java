package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class StatsActivity extends AppCompatActivity {

    public final String HISTORY_HIGHEST_SCORE_KEY = "historyHighestScore";
    public final String MATH_HIGHEST_SCORE_KEY = "mathHighestScore";
    public final String CATEGORY_HISTORY = "history";
    public final String CATEGORY_MATH = "math";

    private RadioButton rdbHistory;
    private RadioButton rdbMath;
    private RadioGroup rdbGroup;
    private TextView txtLastPlayed;
    private TextView txtLastRatio;
    private TextView txtLastAvgTime;
    private TextView txtLastAvgScore;
    private TextView txtAllPlayed;
    private TextView txtAllRatio;
    private TextView txtAllAvgTime;
    private TextView txtAllAvgScore;
    private FloatingActionButton fab_reset, fab_data, fab_score;
    private TextView txtHighScore;
    private ViewGroup layout_container;
    private TextView lblFloatData;
    private TextView lblFloatScore;
    private TextView lblHighScore;
    private String selectedCategory;
    private int highScore;

    private SharedPreferences sharedPref; // Will hold the SharedPreferences object

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        selectedCategory = CATEGORY_HISTORY;
        // Set the theme according to preference
        if (sharedPref.getBoolean("darkTheme", false))
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);


        setContentView(R.layout.activity_stats);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

 /*       fab = findViewById(R.id.floatButton);

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                StatsActivity.super.onBackPressed();
            }
        });*/
        rdbGroup = (RadioGroup) findViewById(R.id.rdbGroup);
        rdbHistory = (RadioButton) findViewById(R.id.rdbHistory);
        rdbMath = (RadioButton)findViewById(R.id.rdbMath);
        txtLastPlayed = findViewById(R.id.txtLastPlayed);
        txtLastRatio = findViewById(R.id.txtLastRatio);
        txtLastAvgTime = findViewById(R.id.txtLastAvgTime);
        txtLastAvgScore = findViewById(R.id.txtLastAvgScore);
        txtAllPlayed = findViewById(R.id.txtAllPlayed);
        txtAllRatio = findViewById(R.id.txtAllRatio);
        txtAllAvgTime = findViewById(R.id.txtAllAvgTime);
        txtAllAvgScore = findViewById(R.id.txtAllAvgScore);
        txtHighScore = findViewById(R.id.txtHighScore);
        lblFloatData = findViewById(R.id.lblFloatData);
        lblFloatData = findViewById(R.id.lblFloatScore);

        fab_reset = findViewById(R.id.float_reset);
        fab_data = findViewById(R.id.float_data);
        fab_score = findViewById(R.id.float_high_score);
        layout_container = findViewById(R.id.layout_container);
        lblFloatData = findViewById(R.id.lblFloatData);
        lblFloatScore = findViewById(R.id.lblFloatScore);
        lblHighScore = findViewById(R.id.lblHighScore);

        rdbHistory.setChecked(true);
        setStatistics(selectedCategory);
        highScore = sharedPref.getInt(HISTORY_HIGHEST_SCORE_KEY, 0);
        txtHighScore.setText(String.valueOf(highScore));

        View parentLayout = findViewById(android.R.id.content);
/*        Snackbar.make(parentLayout, "This is main activity", Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();*/

        rdbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                HashMap statsMap = new HashMap();
                if(rdbHistory.isChecked()) {

                    Snackbar.make(findViewById(android.R.id.content), "History Statistics", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    selectedCategory = CATEGORY_HISTORY;
                    setStatistics(selectedCategory);
                }else if(rdbMath.isChecked()) {

                    Snackbar.make(findViewById(android.R.id.content), "Math Statistics", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    selectedCategory = CATEGORY_MATH;
                    setStatistics(selectedCategory);
                }
            }
        });

        fab_reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fab_data.getVisibility() == View.INVISIBLE) {
                    fab_data.setVisibility(View.VISIBLE);
                    fab_score.setVisibility(View.VISIBLE);
                    lblFloatData.setVisibility(View.VISIBLE);
                    lblFloatScore.setVisibility(View.VISIBLE);
                }else{
                    fab_data.setVisibility(View.INVISIBLE);
                    fab_score.setVisibility(View.INVISIBLE);
                    lblFloatData.setVisibility(View.INVISIBLE);
                    lblFloatScore.setVisibility(View.INVISIBLE);
                }
            }
        });

        layout_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fab_data.setVisibility(View.INVISIBLE);
                fab_score.setVisibility(View.INVISIBLE);
                lblFloatData.setVisibility(View.INVISIBLE);
                lblFloatScore.setVisibility(View.INVISIBLE);
            }
        });

        fab_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab_data.setVisibility(View.INVISIBLE);
                fab_score.setVisibility(View.INVISIBLE);
                lblFloatData.setVisibility(View.INVISIBLE);
                lblFloatScore.setVisibility(View.INVISIBLE);

                ((QuizMasterApplication)getApplication()).resetQuizResult(selectedCategory);
                setStatistics(selectedCategory);
                /*if(rdbHistory.isChecked() == true)
                    setStatistics("history");
                else
                    setStatistics("math");*/
            }
        });

        fab_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab_data.setVisibility(View.INVISIBLE);
                fab_score.setVisibility(View.INVISIBLE);
                lblFloatData.setVisibility(View.INVISIBLE);
                lblFloatScore.setVisibility(View.INVISIBLE);

                SharedPreferences.Editor ed = sharedPref.edit();
                if(selectedCategory.equals(CATEGORY_HISTORY))
                    ed.putInt(HISTORY_HIGHEST_SCORE_KEY, 0);
                else
                    ed.putInt(MATH_HIGHEST_SCORE_KEY, 0);
                ed.apply();

                txtHighScore.setText("0");
            }
        });

    }
    private void setStatistics(String category){
        HashMap statsMap = new HashMap();

        statsMap = ((QuizMasterApplication)getApplication()).getStats(category);


        txtLastPlayed.setText(statsMap.get("LAST_PLAYED").toString());
        txtLastRatio.setText(statsMap.get("LAST_RATIO").toString());
        txtLastAvgTime.setText(statsMap.get("LAST_AVG_TIME").toString());
        txtLastAvgScore.setText(statsMap.get("LAST_AVG_SCORE").toString());
        txtAllPlayed.setText(statsMap.get("ALL_PLAYED").toString());
        txtAllRatio.setText(statsMap.get("ALL_RATIO").toString());
        txtAllAvgTime.setText(statsMap.get("ALL_AVG_TIME").toString());
        txtAllAvgScore.setText(statsMap.get("ALL_AVG_SCORE").toString());


        if(category.equals(CATEGORY_HISTORY)) {
            highScore = sharedPref.getInt(HISTORY_HIGHEST_SCORE_KEY, 0);
            lblHighScore.setText("History Highest Score");
        }else {
            highScore = sharedPref.getInt(MATH_HIGHEST_SCORE_KEY, 0);
            lblHighScore.setText("Math Highest Score");
        }
        txtHighScore.setText(String.valueOf(highScore));

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