package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.schoolhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class StudentMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        // Initialize views
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup drawer toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load default fragment (Home)
        loadFragment(new StudentHomeFragment());

        // Bottom Navigation logic
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new StudentHomeFragment();
            } else if (id == R.id.nav_notification) {
                selectedFragment = new StudentNotificationFragment();
            } else if (id == R.id.nav_marks) {
                selectedFragment = new StudentMarksFragment();
            }

            return loadFragment(selectedFragment);
        });

        // Side Navigation logic
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer

            Fragment selectedFragment = null;
            int id = item.getItemId();

//            if (id == R.id.nav_schedule) {
//            //    selectedFragment = new StudentScheduleFragment();
//            } else if (id == R.id.nav_assignments) {
//                //selectedFragment = new StudentAssignmentsFragment();
//            } else if (id == R.id.nav_grades) {
//               // selectedFragment = new StudentGradesFragment();
//            } else if (id == R.id.nav_attendance) {
//               // selectedFragment = new StudentAttendanceFragment();
//            } else if (id == R.id.nav_leaderboard) {
//                //selectedFragment = new StudentLeaderboardFragment();
//            } else if (id == R.id.nav_logout) {
//                // handle logout, e.g. go to login screen
//                finish(); // or use startActivity
//                return true;
//            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Optional: Handle back press when drawer is open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
