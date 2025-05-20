package com.example.schoolhub.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

public class StudentMainActivity extends AppCompatActivity {

    private BottomNavigationView studentBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtName, txtEmail;

    private int userId;
    private int studentId,classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        userId = getIntent().getIntExtra("user_id", -1);



        drawerLayout = findViewById(R.id.studentDrawerLayout);
        toolbar = findViewById(R.id.studentToolbar);
        navigationView = findViewById(R.id.studentNavView);
        studentBottomNav = findViewById(R.id.studentBottomNav);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txtName);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.txtEmail);

        setSupportActionBar(toolbar);

        fetchStudentId(userId); // retrieve studentId
        checkUnreadNotifications();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        studentBottomNav.setOnItemSelectedListener(item -> {
            Fragment f = getFragmentForMenu(item.getItemId());
            if (f != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("student_id", studentId);
                f.setArguments(bundle);
                return loadFragment(f);
            }
            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment f = getFragmentForMenu(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.START);
            if (f != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("student_id", studentId);
                f.setArguments(bundle);
                return loadFragment(f);
            }
            return false;
        });
    }

    private Fragment getFragmentForMenu(int id) {
        if (id == R.id.nav_home) {
            return new StudentHomeFragment();
        } else if (id == R.id.nav_notification) {
            return new StudentNotificationFragment();
        } else if (id == R.id.nav_marks) {
            return new StudentMarksFragment();
        } else if (id == R.id.nav_schedule) {
            return new StudentScheduleFragment();
        } else if (id == R.id.nav_assignments) {
            return new StudentAssignmentsFragment();
        } else if (id == R.id.nav_attendance) {
            return new StudentAttendanceFragment();
        } else if (id == R.id.nav_leaderboard) {
            return new StudentLeaderboardFragment();
        } else if (id == R.id.nav_calender) {
            return new StudentCalendarFragment();
        } else if (id == R.id.nav_event) {
            return new StudentEventFragment();
        } else if (id == R.id.nav_sittings) {
            return new StudentSettingsFragment();
        }
        else if (id == R.id.nav_logout) {
            // Clear saved user session
            getSharedPreferences("userData", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Redirect to login screen
            Intent intent = new Intent(StudentMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);

            finish(); // finish current activity
            return null;
        }


        else {
            return null;
        }
    }


    private void fetchStudentId(int userId) {
        String url = LoginActivity.baseUrl + "get_user_role_id.php?user_id=" + userId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("role_id")) {
                            studentId = response.getInt("role_id");
                            classId = response.getInt("class_id");
                            Fragment f = new StudentHomeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("student_id", studentId);
                            bundle.putInt("class_id", classId);
                            f.setArguments(bundle);
                            loadFragment(f);
                            studentBottomNav.setSelectedItemId(R.id.nav_home);
                            loadHeaderData(studentId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );
        Volley.newRequestQueue(this).add(req);
    }

    private boolean loadFragment(Fragment fragment) {
        checkUnreadNotifications();
        getSupportFragmentManager().beginTransaction().replace(R.id.studentFragmentContainer, fragment).commit();
        return true;
    }

    private void loadHeaderData(int studentId) {
        String url = LoginActivity.baseUrl + "get_user_nav.php?id=" + studentId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        txtName.setText(response.getString("name"));
                        txtEmail.setText(response.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void checkUnreadNotifications() {
        String url = LoginActivity.baseUrl + "get_notifications.php?user_id=" + userId + "&filter=unread";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    boolean hasUnread = response.length() > 0;
                    studentBottomNav.getMenu().findItem(R.id.nav_notification).setIcon(
                            hasUnread ? R.drawable.notification_alert : R.drawable.notification);
                },
                error -> studentBottomNav.getMenu().findItem(R.id.nav_notification).setIcon(R.drawable.notification)
        );
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
