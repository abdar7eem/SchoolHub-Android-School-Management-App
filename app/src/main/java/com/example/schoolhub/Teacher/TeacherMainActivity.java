package com.example.schoolhub.Teacher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import org.json.JSONObject;

public class TeacherMainActivity extends AppCompatActivity {

    private BottomNavigationView teacherBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private int userId;
    private int teacherId;

    private TextView txtName, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        userId = getIntent().getIntExtra("user_id", -1);

        drawerLayout = findViewById(R.id.teacherDrawerLayout);
        toolbar = findViewById(R.id.teacherToolbar);
        navigationView = findViewById(R.id.teacherNavView);
        teacherBottomNav = findViewById(R.id.teacherBottomNav);

        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_red));
        }

        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.txtName);
        txtEmail = headerView.findViewById(R.id.txtEmail);

        teacherBottomNav.setEnabled(false);
        navigationView.setEnabled(false);

        fetchTeacherId(userId);
        checkUnreadNotifications();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        teacherBottomNav.setOnItemSelectedListener(item -> {
            if (teacherId == 0) return false;

            Fragment f = getTeacherFragment(item.getItemId());
            if (f != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("teacher_id", teacherId);
                bundle.putInt("user_id", userId);
                f.setArguments(bundle);
                return loadFragment(f);
            }
            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            if (teacherId == 0) return false;

            Fragment f = getTeacherFragment(item.getItemId());
            if (f != null) {
                Bundle bundle = new Bundle();
                f.setArguments(bundle);
                bundle.putInt("teacher_id", teacherId);
                loadFragment(f);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void fetchTeacherId(int userId) {
        String url = LoginActivity.baseUrl + "get_teacher_id.php?user_id=" + userId;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("teacher_id")) {
                            teacherId = response.getInt("teacher_id");

                            Fragment f = new TeacherHomeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("teacher_id", teacherId);
                            bundle.putInt("user_id", userId);
                            f.setArguments(bundle);
                            loadFragment(f);

                            teacherBottomNav.setSelectedItemId(R.id.teacher_nav_home);
                            loadHeaderData(teacherId);

                            teacherBottomNav.setEnabled(true);
                            navigationView.setEnabled(true);
                        } else {
                            Log.e("fetchTeacherId", "No teacher_id in response: " + response.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("TeacherMainActivity", "Error fetching teacher_id");
                }
        );

        Volley.newRequestQueue(this).add(req);
    }

    private Fragment getTeacherFragment(int id) {
        if (id == R.id.teacher_nav_home) {
            return new TeacherHomeFragment();
        } else if (id == R.id.teacher_nav_schedual) {
            return new TeacherScheduleFragment();
        } else if (id == R.id.teacher_nav_notification) {
            return new TeacherNotificationFragment();
        } else if (id == R.id.teacher_nav_assignments) {
            return new TeacherSendAssignmentFragment();
        } else if (id == R.id.teacher_nav_view_submissions) {
            return new TeacherViewSubmissionsFragment();
        } else if (id == R.id.teacher_nav_view_grades) {
            return new TeacherViewGradeFragment();
        } else if (id == R.id.teacher_nav_publish_marks) {
            return new TeacherPublishMarksFragment();
        } else if (id == R.id.teacher_nav_attendance) {
            return new TeacherTakeAttendanceFragment();
        } else if (id == R.id.teacher_nav_leaderboard) {
            return new TeacherLeaderBoardFragment();
        } else if (id == R.id.teacher_nav_schedule_exam) {
            return new TeacherScheduleExamFragment();
        } else if (id == R.id.teacher_nav_sittings) {
            return new TeacherSettingsFragment();
        } else if (id == R.id.teacher_nav_logout) {
            getSharedPreferences("userData", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(TeacherMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return null;
        } else {
            return null;
        }
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null && !isFinishing() && !isDestroyed()) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.teacherFragmentContainer, fragment)
                        .commitAllowingStateLoss();
                return true;
            } catch (IllegalStateException e) {
                Log.e("FragmentLoad", "State loss exception: " + e.getMessage());
            }
        }
        return false;
    }

    private void loadHeaderData(int teacherId) {
        String url = LoginActivity.baseUrl + "get_user_nav.php?id=" + teacherId;
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
                    teacherBottomNav.getMenu().findItem(R.id.teacher_nav_notification).setIcon(
                            hasUnread ? R.drawable.notification_alert : R.drawable.notification
                    );
                },
                error -> teacherBottomNav.getMenu().findItem(R.id.teacher_nav_notification).setIcon(R.drawable.notification)
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
