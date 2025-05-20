package com.example.schoolhub;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolhub.Registrar.RegistrarMainActivity;

public class MainActivity extends AppCompatActivity {

    public static final     String baseUrl = "http://192.168.3.246/SchoolHub/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, RegistrarMainActivity.class));
        finish(); // Prevent back navigation to this splash
    }

}