package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.quizmaster.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String NO_QUIZ_IN_PROGRESS = "none";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private boolean creatingActivity = false; // Flag to indicate if main activity is being created
    private boolean saveState;  // Will store the setting related to saving game on close
    private boolean darkTheme;  // Will store the setting related to using dark theme

    private SharedPreferences sharedPref; // Will hold the SharedPreferences object

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        creatingActivity = true;

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        saveState = sharedPref.getBoolean("saveOnClose", false);
        darkTheme = sharedPref.getBoolean("darkTheme", false);
        String savedQuizType = sharedPref.getString("quiz_in_progress", MainActivity.NO_QUIZ_IN_PROGRESS);

        // Set the theme according to preference
        if (darkTheme)
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);

        ((QuizMasterApplication)getApplication()).insertQuiz();

        //PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set the background image according to darkTheme preference
        if (darkTheme)
            mDrawerLayout.setBackgroundResource(R.drawable.mainbw);
        else
            mDrawerLayout.setBackgroundResource(R.drawable.main);

        mToggle = new ActionBarDrawerToggle (this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetNavigationDrawer();

        // If parameter to save ongoing quiz on close is set, check if there was a quiz in progress
        if (saveState) {
            // If there was a quiz in progress before, resume it
            switch (savedQuizType) {
                case HistoryQuizActivity.QUIZ_TYPE:
                    startActivity(new Intent(getApplicationContext(), HistoryQuizActivity.class));
                    break;
                case MathQuizActivity.QUIZ_TYPE:
                    startActivity(new Intent(getApplicationContext(), MathQuizActivity.class));
                    break;
                case NO_QUIZ_IN_PROGRESS:
                default:
                    // Do nothing here - continue with menu being displayed
                    break;
            }
        }
    }

    private void SetNavigationDrawer() {
        // Create the widget reference for the navigation view
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view); // id of the navigation menu in activity_main.xml
        navView.setItemIconTintList(null);  // Important to show color icons rather than their gray shadows
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) { // method called each time a menu item is selected

                // Define fragment depending on which menu option was selected
                Fragment frag = null;
                switch (item.getItemId()) {
                    case R.id.history_quiz:
                        startActivity(new Intent(getApplicationContext(), HistoryQuizActivity.class));
                        break;
                    case R.id.math_quiz:
                        startActivity(new Intent(getApplicationContext(), MathQuizActivity.class));
                        break;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    case R.id.statistics:
                        startActivity(new Intent(getApplicationContext(), StatsActivity.class));
                        break;
                }

                if (frag != null) {
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.frame, frag);
                    trans.commit();
                    mDrawerLayout.closeDrawers(); // close the menu
                    return true;
                }
                return false;
            }
        });
    }

    // Event called when the application is paused or deactivated, or when the orientation of the device changes
    // Value of key application variables are stored here so that they can be restored later
    @Override
    protected void onPause() {
        SharedPreferences.Editor ed = sharedPref.edit();

        // TODO: Store any needed values here with ed.putString, ed.putInt, etc.
        // and finish it with ed.dcommit();
//        ed.putString(...);
//        ed.putInt(...);
        ed.commit();

        creatingActivity = false;

        super.onPause();
    }

    // Event called when the application is reactivated or when the rotation of the device has been completed
    // Values previously saves in onPause() are restored here to ensure user experience continuity
    @Override
    protected void onResume() {
        super.onResume();

        // Restore the selected preferences from settings
        saveState = sharedPref.getBoolean("saveOnClose", false);
        darkTheme = sharedPref.getBoolean("darkTheme", false);

        // Restore the previous state if the saveState preference is on or if we are NOT creating the activity
        // (for example, when doing orientation flip, or app deactivate/activate)
        if (saveState || !creatingActivity) {
            // TODO: Restore previously saved values here with sharedPref.getInt, sharedPref.getString, etc.

        }

        creatingActivity = false;
    }

    @Override
    protected void onRestart() {
        // Restart the activity only if there was a change in the theme preference
        if (darkTheme != sharedPref.getBoolean("darkTheme", false)) {
            finish();
            startActivity(getIntent());
        }
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;  // if an item is selected, return true
        }

        return super.onOptionsItemSelected(item);
    }

    // Procedure to build message string
    public void showMessage(String title, String messageContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(messageContent);
        builder.show();
    }
}