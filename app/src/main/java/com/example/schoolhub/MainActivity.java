package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Student.StudentMainActivity;
import com.example.schoolhub.Teacher.TeacherMainActivity;
import com.example.schoolhub.Registrar.RegistrarMainActivity;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, StudentMainActivity.class));
        finish(); // Prevent back navigation to this splash
    }

}