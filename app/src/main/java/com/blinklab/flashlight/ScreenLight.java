package com.blinklab.flashlight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blueroomxyz.flashlight.R;
import com.blueroomxyz.flashlight.databinding.ActivityScreenLightBinding;


public class ScreenLight extends AppCompatActivity {

    ActivityScreenLightBinding binding;
    private boolean isBrightnessHigh = false;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreenLightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mediaPlayer = MediaPlayer.create(this, R.raw.button_clicked);
        binding.goToSettings.setOnClickListener(view -> {
            startActivity(new Intent(ScreenLight.this,Settings.class));
        });

        binding.screenlight.setImageResource(R.drawable.cell_phone_oranage);
        binding.flashlight.setImageResource(R.drawable.flashlight_black);
        binding.flashlight.setOnClickListener(view -> {
            startActivity(new Intent(this, FlashLight.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        });
        binding.buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                toggleBrightness();
            }
        });
    }
    private void toggleBrightness() {
        if (isBrightnessHigh) {
            isBrightnessHigh = false;
            binding.buttonOn.setVisibility(View.INVISIBLE);
            binding.flashlightOn.setVisibility(View.INVISIBLE);
            binding.container.setBackground(ContextCompat.getDrawable(ScreenLight.this, R.drawable.bg_2));

        } else {
            isBrightnessHigh = true;
            binding.buttonOn.setVisibility(View.VISIBLE);
            setViewsBackgroundColor(R.color.blue);
            binding.flashlightOn.setVisibility(View.VISIBLE);
        }
    }

    private void setViewsBackgroundColor(int colorResId) {
        binding.container.setBackgroundColor(getResources().getColor(colorResId));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.invite) {

            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Flashlight");
                intent.putExtra(Intent.EXTRA_TEXT, "Flashlight " + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share with"));
            } catch (Exception e) {
                Toast.makeText(ScreenLight.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.rateUs) {
            Uri uri;
            uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(ScreenLight.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.sendFeedback) {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + this.getString(R.string.email_feedback)));
                intent.setPackage("com.google.android.gm");
                intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_name));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.settings) {
            try {
                Intent intent = new Intent(ScreenLight.this, Settings.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}