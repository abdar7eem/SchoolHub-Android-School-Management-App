package com.example.schoolhub.Registrar;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

public class RegistrarMainActivity extends AppCompatActivity {

    private BottomNavigationView registrarBottomNav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private int userId;

    private TextView txtName, txtEmail;

    private final String baseUrl = LoginActivity.baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_main);

        userId = getIntent().getIntExtra("user_id", -1);

        drawerLayout = findViewById(R.id.registrarDrawerLayout);
        toolbar = findViewById(R.id.registrarToolbar);
        navigationView = findViewById(R.id.registrarNavView);
        registrarBottomNav = findViewById(R.id.registrarBottomNav);

        setSupportActionBar(toolbar);

        txtName = navigationView.getHeaderView(0).findViewById(R.id.txtName);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.txtEmail);

        loadInitialFragment();
        loadHeaderData(userId);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        registrarBottomNav.setOnItemSelectedListener(item -> {
            Fragment f = getRegistrarFragment(item.getItemId());
            if (f != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", userId);
                f.setArguments(bundle);
                return loadFragment(f);
            }
            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment f = getRegistrarFragment(item.getItemId());
            if (f != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", userId);
                f.setArguments(bundle);
                loadFragment(f);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadInitialFragment() {
        Fragment f = new RegistrarHomeFregment();
        Bundle bundle = new Bundle();
        bundle.putInt("registrar_id", userId);
        f.setArguments(bundle);
        loadFragment(f);
        registrarBottomNav.setSelectedItemId(R.id.registrar_nav_home);
    }

    private Fragment getRegistrarFragment(int id) {
        if (id == R.id.registrar_nav_home) {
            return new RegistrarHomeFregment();
        } else if (id == R.id.registrar_nav_Add) {
            return new RegistrarAddStudentFregment();
        } else if (id == R.id.registrar_nav_schedual) {
            return new RegistrarScheduleFregment();
        } else if (id == R.id.nav_addClass) {
            return new RegistrarAddClassFregment();
        } else if (id == R.id.nav_addStudent) {
            return new RegistrarAddStudentFregment();
        } else if (id == R.id.nav_addSubject) {
            return new RegistrarAddSubjectFregment();
        } else if (id == R.id.nav_postEvent) {
            return new RegistrarAddEventFregment();
        } else if (id == R.id.nav_addTeacher) {
            return new RegistrarAddTeacherFregment();
        } else if (id == R.id.nav_assignTeacher) {
            return new RegistrarAssignTeacherFregment();
        } else if (id == R.id.nav_assignSubject) {
            return new RegistrarAssignSubjectTeacherFregment();
        } else if (id == R.id.nav_settings) {
            return new RegistrarSettingsFregment();
        } else if (id == R.id.nav_logout) {
            getSharedPreferences("userData", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(RegistrarMainActivity.this, LoginActivity.class);
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.registrarFragmentContainer, fragment)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }

    private void loadHeaderData(int userId) {
        String url = baseUrl + "get_user_nav.php?id=" + userId;
        Log.d("loadHeaderData", "URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        txtName.setText(response.getString("name"));
                        txtEmail.setText(response.getString("email"));
                    } catch (JSONException e) {
                        Log.e("loadHeaderData", "JSON error", e);
                    }
                },
                error -> Log.e("loadHeaderData", "Volley error", error)
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
