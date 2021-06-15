package com.example.doit2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        getSupportActionBar().hide();

        final Intent i = new Intent(SplashScreen.this, MainActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(i);
            finish();
        },1000);
    }
}