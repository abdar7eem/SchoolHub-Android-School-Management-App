package com.example.schoolhub.Registration;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.schoolhub.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashGif = findViewById(R.id.splashGif);

        Glide.with(this)
                .asGif()
                .load(R.raw.splash_logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(splashGif);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, SPLASH_TIME_OUT);
    }
}
