package com.example.schoolhub.Teacher;

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
import com.example.schoolhub.Teacher.TeacherHomeFragment;
import com.example.schoolhub.Teacher.TeacherSendAssignmentFragment;
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
            teacherBottomNav.setSelectedItemId(R.id.nav_home); // highlight default item
        }

        // Handle Bottom Navigation clicks
        teacherBottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new TeacherHomeFragment();
                getSupportActionBar().setTitle("Dashboard");
            } else if (itemId == R.id.schedual) {
                loadFragment(new TeacherScheduleFragment());
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });

        // Handle Drawer Navigation item clicks (optional)
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // Handle drawer items here if needed
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
