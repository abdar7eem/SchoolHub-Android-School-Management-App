package com.example.schoolhub.Student;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

public class StudentMainActivity extends AppCompatActivity {

    private BottomNavigationView studentBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        // Initialize views
        drawerLayout = findViewById(R.id.studentDrawerLayout);
        toolbar = findViewById(R.id.studentToolbar);
        navigationView = findViewById(R.id.studentNavView);
        studentBottomNav = findViewById(R.id.studentBottomNav);

        // Set toolbar as the action bar
        setSupportActionBar(toolbar);

        checkUnreadNotifications();

        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new StudentHomeFragment());
            studentBottomNav.setSelectedItemId(R.id.nav_home);
        }

        // Bottom Navigation logic
        studentBottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new StudentHomeFragment();
            } else if (id == R.id.nav_notification) {
                selectedFragment = new StudentNotificationFragment();
            } else if (id == R.id.nav_marks) {
                selectedFragment = new StudentMarksFragment();
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
            return loadFragment(selectedFragment);
        });

        // Side Navigation logic (with if-else)
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_schedule) {
                 selectedFragment = new StudentScheduleFragment();
            } else if (id == R.id.nav_assignments) {
                 selectedFragment = new StudentAssignmentsFragment();
            } else if (id == R.id.nav_attendance) {
                 selectedFragment = new StudentAttendanceFragment();
            } else if (id == R.id.nav_leaderboard) {
                 selectedFragment = new StudentLeaderboardFragment();
            } else if (id == R.id.nav_calender) {
                 selectedFragment = new StudentCalendarFragment();
            } else if (id == R.id.nav_event) {
                 selectedFragment = new StudentEventFragment();
            } else if (id == R.id.nav_sittings) {
                selectedFragment = new StudentSettingsFragment();
            } else if (id == R.id.nav_logout) {
                finish(); // close the activity (logout)
                return true;
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);

                studentBottomNav.getMenu().setGroupCheckable(0, true, false);
                for (int i = 0; i < studentBottomNav.getMenu().size(); i++) {
                    studentBottomNav.getMenu().getItem(i).setChecked(false);
                }
                studentBottomNav.getMenu().setGroupCheckable(0, true, true);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        checkUnreadNotifications();
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.studentFragmentContainer, fragment)
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


    private void checkUnreadNotifications() {
        String url = "http://192.168.1.13/SchoolHub/get_notifications.php?user_id=1&filter=unread";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    boolean hasUnread = response.length() > 0;

                    // Update notification icon based on unread status
                    studentBottomNav.getMenu().findItem(R.id.nav_notification).setIcon(
                            hasUnread ? R.drawable.notification_alert : R.drawable.notification
                    );
                },
                error -> {
                    error.printStackTrace();
                    // Optional: Fallback to normal icon if error occurs
                    studentBottomNav.getMenu().findItem(R.id.nav_notification).setIcon(R.drawable.notification);
                });

        Volley.newRequestQueue(this).add(request);
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkUnreadNotifications(); // Refresh icon each time user returns
    }


}
