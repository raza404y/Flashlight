package com.blinklab.flashlight;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.view.View;
import android.widget.ImageView;

public class FlashlightController {
    private Context context;
    private CameraManager cameraManager;
    private boolean isFlashlightOn;
    private View flashlightOnView;
    private ImageView TorchlightOnView;


    public FlashlightController(Context context, View flashlightOnView,ImageView TorchlightOnView) {
        this.context = context;
        this.TorchlightOnView = TorchlightOnView;
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.isFlashlightOn = false;
        this.flashlightOnView = flashlightOnView;
    }

    public void toggleFlashlight() {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (isFlashlightOn) {
                turnOffFlashlight(cameraId);
            } else {
                turnOnFlashlight(cameraId);
            }
            updateFlashlightOnViewVisibility();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOnFlashlight(String cameraId) throws CameraAccessException {
        cameraManager.setTorchMode(cameraId, true);
        isFlashlightOn = true;
    }

    private void turnOffFlashlight(String cameraId) throws CameraAccessException {
        cameraManager.setTorchMode(cameraId, false);
        isFlashlightOn = false;
    }

    private void updateFlashlightOnViewVisibility() {
        flashlightOnView.setVisibility(isFlashlightOn ? View.VISIBLE : View.GONE);
        TorchlightOnView.setVisibility(isFlashlightOn ? View.VISIBLE : View.GONE);
    }

    public boolean isFlashlightOn() {
        return isFlashlightOn;
    }
}
