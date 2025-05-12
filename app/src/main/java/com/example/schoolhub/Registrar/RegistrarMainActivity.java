package com.example.schoolhub.Registrar;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.schoolhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class RegistrarMainActivity extends AppCompatActivity {

    private BottomNavigationView studentBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_main);

        // Initialize views
        drawerLayout = findViewById(R.id.registrarDrawerLayout);
        toolbar = findViewById(R.id.registrarToolbar);
        navigationView = findViewById(R.id.registrarNavView);
        studentBottomNav = findViewById(R.id.registrarBottomNav);

        // Set toolbar as the action bar
        setSupportActionBar(toolbar);

        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new RegistrarHomeFregment());
            studentBottomNav.setSelectedItemId(R.id.registrar_nav_home);
        }

         //Bottom Navigation logic
        studentBottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.registrar_nav_home) {
                selectedFragment = new RegistrarHomeFregment();
            } else if (id == R.id.registrar_nav_Add) {
                selectedFragment = new RegistrarAddStudentFregment();
            } else if (id == R.id.registrar_nav_schedual) {
                selectedFragment = new RegistrarScheduleFregment();
            }

            return loadFragment(selectedFragment);
        });

        // Side Navigation logic (with if-else)
//        navigationView.setNavigationItemSelectedListener(item -> {
//            drawerLayout.closeDrawer(GravityCompat.START);
//
//            Fragment selectedFragment = null;
//            int id = item.getItemId();
//
//            if (id == R.id.nav_schedule) {
//                selectedFragment = new StudentScheduleFragment();
//            } else if (id == R.id.nav_assignments) {
//                selectedFragment = new StudentAssignmentsFragment();
//            } else if (id == R.id.nav_attendance) {
//                selectedFragment = new StudentAttendanceFragment();
//            } else if (id == R.id.nav_leaderboard) {
//                selectedFragment = new StudentLeaderboardFragment();
//            } else if (id == R.id.nav_calender) {
//                selectedFragment = new StudentCalendarFragment();
//            } else if (id == R.id.nav_event) {
//                selectedFragment = new StudentEventFragment();
//            } else if (id == R.id.nav_sittings) {
//                selectedFragment = new StudentSettingsFragment();
//            } else if (id == R.id.nav_logout) {
//                finish(); // close the activity (logout)
//                return true;
//            }
//
//            return loadFragment(selectedFragment);
//        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.registrarFragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
