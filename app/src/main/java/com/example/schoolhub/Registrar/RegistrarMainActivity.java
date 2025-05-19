package com.example.schoolhub.Registrar;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

public class RegistrarMainActivity extends AppCompatActivity {

    private BottomNavigationView registrarBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    int RegistrarId=3;

    private TextView txtName, txtEmail;

    private final String baseUrl = "http://192.168.56.1/schoolhub/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_main);

        // Initialize views
        drawerLayout = findViewById(R.id.registrarDrawerLayout);
        toolbar = findViewById(R.id.registrarToolbar);
        navigationView = findViewById(R.id.registrarNavView);
        registrarBottomNav = findViewById(R.id.registrarBottomNav);

        // Set toolbar as the action bar
        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.txtName);
        txtEmail = headerView.findViewById(R.id.txtEmail);
        loadHeaderData();

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
            registrarBottomNav.setSelectedItemId(R.id.registrar_nav_home);
        }

         //Bottom Navigation logic
        registrarBottomNav.setOnItemSelectedListener(item -> {
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

        //Side Navigation logic (with if-else)
       navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_addClass) {
                selectedFragment = new RegistrarAddClassFregment();
            } else if (id == R.id.nav_addStudent) {
                selectedFragment = new RegistrarAddStudentFregment();
            } else if (id == R.id.nav_addSubject) {
                selectedFragment = new RegistrarAddSubjectFregment();
            } else if (id == R.id.nav_postEvent) {
                selectedFragment = new RegistrarAddEventFregment();
            } else if (id == R.id.nav_addTeacher) {
                selectedFragment = new RegistrarAddTeacherFregment();
            }else if(id== R.id.nav_settings){
                selectedFragment = new RegistrarSettingsFregment();

            } else if (id == R.id.nav_logout) {
                finish(); // close the activity (logout)
                return true;
            }

            return loadFragment(selectedFragment);
        });
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

    private void loadHeaderData() {
        String url = baseUrl + "get_user_nav.php?id=" + RegistrarId;

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
}
