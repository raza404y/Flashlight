package com.blinklab.flashlight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blueroomxyz.flashlight.R;
import com.blueroomxyz.flashlight.databinding.ActivityFlashlightBinding;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;


public class FlashLight extends AppCompatActivity {

    ActivityFlashlightBinding binding;
    MediaPlayer mediaPlayer;
    private FlashlightController flashlightController;

    private SharedPreferences sharedPreferences;
    private boolean isFlashlightOnStartup;
    AppUpdateManager appUpdateManager;
    static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashlightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkUpdate();

        binding.goToSettings.setOnClickListener(view -> {
            startActivity(new Intent(FlashLight.this,Settings.class));
        });

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isFlashlightOnStartup = sharedPreferences.getBoolean("flashlight_startup", false);

        if (isFlashlightOnStartup) {
            turnOnFlashlight();
            binding.buttonOff.setOnClickListener(view -> {
                playSound();
                flashlightController.toggleFlashlight();
            });
            Toast.makeText(this, "Flashlight turned on at startup", Toast.LENGTH_SHORT).show();
        }else {
            turnOffFlashlight();
        }

        boolean isFlashlightTurnOffExit = sharedPreferences.getBoolean("flashlight_turn_off_exit", false);
        if (isFlashlightTurnOffExit) {
            getApplication().registerActivityLifecycleCallbacks(new FlashlightLifecycleCallbacks());
        }



        mediaPlayer = MediaPlayer.create(this, R.raw.button_clicked);

        View flashlightOnView = findViewById(R.id.button_on);
        ImageView torchlightOnView = findViewById(R.id.flashlight_on);
        flashlightController = new FlashlightController(this, flashlightOnView,torchlightOnView);

        binding.buttonOff.setOnClickListener(view -> {
            playSound();
            flashlightController.toggleFlashlight();
        });

        binding.screenlight.setOnClickListener(view -> {
            binding.screenlight.setImageResource(R.drawable.cell_phone_oranage);
            binding.flashlight.setImageResource(R.drawable.flashlight);
            startActivity(new Intent(this, ScreenLight.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        });



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void turnOnFlashlight() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void turnOffFlashlight() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false); // Turn off the flashlight
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private class FlashlightLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private boolean isAppInBackground = false;
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            if (isAppInBackground) {
                boolean isFlashlightTurnOffExit = sharedPreferences.getBoolean("flashlight_turn_off_exit", false);
                if (isFlashlightTurnOffExit) {
                    turnOnFlashlight();
                }
                isAppInBackground = false;
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            isAppInBackground = true;
            turnOffFlashlight();
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    // ## in app update method
    private void checkUpdate() {

        try {
            Task<AppUpdateInfo> appUpdateInfoTask = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            }

            assert appUpdateInfoTask != null;
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startUpdateFlow(appUpdateInfo);
                } else if  (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    startUpdateFlow(appUpdateInfo);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Kindly update now to use latest features.", Toast.LENGTH_LONG).show();
                //  finish();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }
}