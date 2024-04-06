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
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            setContentView(R.layout.activity_splash_screen);
            ImageView startButton = findViewById(R.id.startButton);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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