package com.example.schoolhub.Teacher;

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

public class TeacherMainActivity extends AppCompatActivity {

    private BottomNavigationView teacherBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Initialize views
        drawerLayout = findViewById(R.id.teacherDrawerLayout);
        toolbar = findViewById(R.id.teacherToolbar);
        navigationView = findViewById(R.id.teacherNavView);
        teacherBottomNav = findViewById(R.id.teacherBottomNav);

        // Set toolbar as action bar
        setSupportActionBar(toolbar);

        // Set up drawer toggle (hamburger icon)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_nav,
                R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set default fragment on first launch
        if (savedInstanceState == null) {
            loadFragment(new TeacherHomeFragment());
            teacherBottomNav.setSelectedItemId(R.id.teacher_nav_home);
        }

        teacherBottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.teacher_nav_home) {
                selectedFragment = new TeacherHomeFragment();
            } else if (itemId == R.id.teacher_nav_schedual) {
                selectedFragment = new TeacherScheduleFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);

                navigationView.getMenu().setGroupCheckable(0, true, false);
                for (int i = 0; i < navigationView.getMenu().size(); i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                navigationView.getMenu().setGroupCheckable(0, true, true);

                return true;
            }

            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.teacher_nav_assignments) {
                selectedFragment = new TeacherSendAssignmentFragment();
            } else if (id == R.id.teacher_nav_view_submissions) {
                selectedFragment = new TeacherViewSubmissions();
            } else if (id == R.id.teacher_nav_view_grades) {
                selectedFragment = new TeacherViewGradeFragment();
            } else if (id == R.id.teacher_nav_attendance) {
                selectedFragment = new TeacherTakeAttendance();
            } else if (id == R.id.teacher_nav_calender) {
                // selectedFragment = new TeacherCalendarFragment();
            } else if (id == R.id.teacher_nav_schedule_exam) {
                // selectedFragment = new TeacherScheduleExamFragment();
            } else if (id == R.id.teacher_nav_sittings) {
                // selectedFragment = new TeacherSettingsFragment();
            } else if (id == R.id.teacher_nav_logout) {
                // Handle logout
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);

                teacherBottomNav.getMenu().setGroupCheckable(0, true, false);
                for (int i = 0; i < teacherBottomNav.getMenu().size(); i++) {
                    teacherBottomNav.getMenu().getItem(i).setChecked(false);
                }
                teacherBottomNav.getMenu().setGroupCheckable(0, true, true);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.teacherFragmentContainer, fragment)
                .commit();
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
