package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.quizmaster.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preparation for setting the Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle (this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetNavigationDrawer();
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
                int itemId = item.getItemId();  // id of the item
                if (itemId == R.id.history_quiz) {
                    frag = new HistoryQuizFragment();
                }
                else if (itemId == R.id.math_quiz) {
                    frag = new MathQuizFragment();
                }
                else if (itemId == R.id.settings) {
                    frag = new SettingsFragment();
                }
                else if (itemId == R.id.statistics) {
                    frag = new StatisticsFragment();
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