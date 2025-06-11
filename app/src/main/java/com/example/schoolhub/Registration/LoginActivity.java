package com.example.schoolhub.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registrar.RegistrarMainActivity;
import com.example.schoolhub.Student.StudentMainActivity;
import com.example.schoolhub.Teacher.TeacherMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin;
    private CheckBox checkRemember;

    private SharedPreferences sharedPrefs;

    public static final     String baseUrl = "http://almushtaraka.atwebpages.com/";




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupViews();
        checkLoginStatus();
    }

    private void setupViews() {
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        checkRemember = findViewById(R.id.checkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        sharedPrefs = getSharedPreferences("userData", MODE_PRIVATE);

        btnLogin.setOnClickListener(view -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
        });
    }

    private void loginUser(String username, String password) {
        StringRequest request = new StringRequest(Request.Method.POST, baseUrl + "login.php",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            int userId = obj.getInt("user_id");
                            String role = obj.getString("role");

                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putInt("user_id", userId);
                            editor.putString("role", role);

                            if (checkRemember.isChecked()) {
                                editor.putBoolean("isRemembered", true);
                                editor.putString("username", username);
                                editor.putString("password", password);
                            } else {
                                // Remove previously saved login info
                                editor.putBoolean("isRemembered", false);
                                editor.remove("username");
                                editor.remove("password");
                            }

                            editor.apply();

                            Intent intent;
                            switch (role) {
                                case "student":
                                    intent = new Intent(this, StudentMainActivity.class);
                                    break;
                                case "teacher":
                                    intent = new Intent(this, TeacherMainActivity.class);
                                    break;
                                case "registrar":
                                    intent = new Intent(this, RegistrarMainActivity.class);
                                    break;
                                default:
                                    Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                                    return;
                            }
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void checkLoginStatus() {
        boolean isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);
        boolean isRemembered = sharedPrefs.getBoolean("isRemembered", false);

        if (isLoggedIn) {
            int userId = sharedPrefs.getInt("user_id", -1);
            String role = sharedPrefs.getString("role", "");

            Intent intent;
            switch (role) {
                case "student":
                    intent = new Intent(this, StudentMainActivity.class);
                    break;
                case "teacher":
                    intent = new Intent(this, TeacherMainActivity.class);
                    break;
                case "registrar":
                    intent = new Intent(this, RegistrarMainActivity.class);
                    break;
                default:
                    return;
            }
            intent.putExtra("user_id", userId);
            startActivity(intent);
            finish();
        }

        if (isRemembered) {
            etLoginUsername.setText(sharedPrefs.getString("username", ""));
            etLoginPassword.setText(sharedPrefs.getString("password", ""));
            checkRemember.setChecked(true);
        }
    }
}
