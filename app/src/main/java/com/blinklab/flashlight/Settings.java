package com.blinklab.flashlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.blueroomxyz.flashlight.R;
import com.blueroomxyz.flashlight.databinding.ActivitySettingsBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;

    private SharedPreferences sharedPreferences;
    private MaterialCheckBox checkBox;
    private MaterialCheckBox turnOffExitCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        checkBox = findViewById(R.id.checkBox);


        boolean isFlashlightOnStartup = sharedPreferences.getBoolean("flashlight_startup", false);
        checkBox.setChecked(isFlashlightOnStartup);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("flashlight_startup", isChecked);
            editor.apply();
        });

        turnOffExitCheckBox = findViewById(R.id.turnOffExit);

        boolean isFlashlightTurnOffExit = sharedPreferences.getBoolean("flashlight_turn_off_exit", false);
        turnOffExitCheckBox.setChecked(isFlashlightTurnOffExit);

        turnOffExitCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("flashlight_turn_off_exit", isChecked);
            editor.apply();
        });

        setSupportActionBar(binding.toolbarSettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        binding.toolbarSettings.setNavigationOnClickListener(view -> {
            onBackPressed();
        });



        // Get the version name and version code
        String versionName = getVersionName();
        int versionCode = getVersionCode();

        binding.flashlightVersion.setText("Version: " + versionName + " (" + versionCode + ")");

        binding.inviteFriends.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Flashlight");
                intent.putExtra(Intent.EXTRA_TEXT, "Flashlight " + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share with"));
            } catch (Exception e) {
                Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.sendFeedback.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + this.getString(R.string.email_feedback)));
                intent.setPackage("com.google.android.gm");
                intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_name));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.rateUs.setOnClickListener(view -> {
            Uri uri;
            uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getVersionName() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private int getVersionCode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isFlashlightOnStartup = sharedPreferences.getBoolean("flashlight_startup", false);
        checkBox.setChecked(isFlashlightOnStartup);
    }
}