package com.blinklab.flashlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blueroomxyz.flashlight.R;

public class SplashScreen extends AppCompatActivity {
    ImageView startButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startButton = findViewById(R.id.startButton);
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Check if the SplashScreen has been shown before
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        // If it's the first run, show the SplashScreen, otherwise start FlashLight activity
        if (isFirstRun) {
            setContentView(R.layout.activity_splash_screen);
            ImageView startButton = findViewById(R.id.startButton);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Store in SharedPreferences that SplashScreen has been shown
                    sharedPreferences.edit().putBoolean("isFirstRun", false).apply();
                    startActivity(new Intent(SplashScreen.this, FlashLight.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(SplashScreen.this, FlashLight.class));
            finish();
        }
    }

}