package com.example.schoolhub.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.MainActivity;
import com.example.schoolhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class TeacherMainActivity extends AppCompatActivity {

    private BottomNavigationView teacherBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private final int teacherId = 1;
    private final String baseUrl = MainActivity.baseUrl;

    private TextView txtName, txtEmail;

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

        // Access header view for name & email
        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.txtName);
        txtEmail = headerView.findViewById(R.id.txtEmail);

        loadHeaderData();
        checkUnreadNotifications();

        // Load default fragment
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
            } else if (itemId == R.id.teacher_nav_notification) {
                selectedFragment = new TeacherNotificationFragment();
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
                selectedFragment = new TeacherViewSubmissionsFragment();
            } else if (id == R.id.teacher_nav_view_grades) {
                selectedFragment = new TeacherViewGradeFragment();
            } else if (id == R.id.teacher_nav_publish_marks) {
                selectedFragment = new TeacherPublishMarksFragment();
            } else if (id == R.id.teacher_nav_attendance) {
                selectedFragment = new TeacherTakeAttendanceFragment();
            } else if (id == R.id.teacher_nav_leaderboard) {
                selectedFragment = new TeacherLeaderBoardFragment();
            } else if (id == R.id.teacher_nav_schedule_exam) {
                selectedFragment = new TeacherScheduleExamFragment();
            } else if (id == R.id.teacher_nav_sittings) {
                selectedFragment = new TeacherSettingsFragment();
            } else if (id == R.id.teacher_nav_notification) {
                selectedFragment = new TeacherNotificationFragment();
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
        checkUnreadNotifications();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.teacherFragmentContainer, fragment)
                .commit();
    }

    private void loadHeaderData() {
        String url = baseUrl + "get_user_nav.php?id=" + teacherId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("TeacherMainActivity", "Header data loaded: " + response.toString() + "");
                    try {
                        String name = response.getString("name");
                        String email = response.getString("email");

                        txtName.setText(name);
                        txtEmail.setText(email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("TeacherMainActivity", "Header data load error", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void checkUnreadNotifications() {
        String url = baseUrl + "get_notifications.php?user_id=1&filter=unread";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    boolean hasUnread = response.length() > 0;

                    // Update notification icon based on unread status
                    teacherBottomNav.getMenu().findItem(R.id.teacher_nav_notification).setIcon(
                            hasUnread ? R.drawable.notification_alert : R.drawable.notification
                    );
                },
                error -> {
                    error.printStackTrace();
                    // Optional: Fallback to normal icon if error occurs
                    teacherBottomNav.getMenu().findItem(R.id.teacher_nav_notification).setIcon(R.drawable.notification);
                });

        Volley.newRequestQueue(this).add(request);
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
