package com.github.arcticcacti.retakecamera.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.github.arcticcacti.retakecamera.RetakeApplication.CAMERA_2_MIN_API;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Real implementation of a CameraProvider, giving access to the Android framework's camera systems.
 */
public class CameraProviderImpl implements CameraProvider {

    @NonNull
    private final Context context;


    public CameraProviderImpl(@NonNull Context context) {
        this.context = context;
    }


    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public android.hardware.Camera getCameraInstance() {
        try {
            return android.hardware.Camera.open();
        } catch (Exception e) {
            // attempting to open the camera can result in serious errors, so fail gracefully
            e.printStackTrace();
            return null;
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public android.graphics.Camera getCamera2Instance() {
        int currentApi = Build.VERSION.SDK_INT;
        if (currentApi < CAMERA_2_MIN_API) {
            String errorMessage = "Failed to access Camera2 api, min SDK %d, current SDK %d";
            throw new IllegalStateException(String.format(errorMessage, CAMERA_2_MIN_API, currentApi));
        }
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        // TODO: 08/07/2016 implement Camera2
        return null;
    }
}
