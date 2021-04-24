package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class StatsActivity extends AppCompatActivity {

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
    private FloatingActionButton fab;
    private Button btnReset;

    private SharedPreferences sharedPref; // Will hold the SharedPreferences object

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the theme according to preference
        if (sharedPref.getBoolean("darkTheme", false))
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab = findViewById(R.id.floatButton);

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                StatsActivity.super.onBackPressed();
            }
        });
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
        btnReset = findViewById(R.id.btnReset);

        rdbHistory.setChecked(true);
        setStatistics("history");
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "This is main activity", Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

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

                    setStatistics("history");
                }else if(rdbMath.isChecked()) {

                    Snackbar.make(findViewById(android.R.id.content), "Math Statistics", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    setStatistics("math");
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                ((QuizMasterApplication)getApplication()).resetQuizResult();

                if(rdbHistory.isChecked() == true)
                    setStatistics("history");
                else
                    setStatistics("math");

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