package com.example.no.retakecamera;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.no.retakecamera.camera.CameraSystem;
import com.example.no.retakecamera.ui.CameraControls;
import com.example.no.retakecamera.ui.CameraPreview;

import javax.inject.Inject;

/**
 * Created by Lee Holmes on 08/07/2016.
 * <p/>
 * Concrete implementation that sits between a CameraSystem and its controls UI, initialising them
 * and handling events.
 */
public class CameraPresenterImpl implements CameraPresenter {


    @NonNull
    private final CameraSystem cameraSystem;

    @Nullable
    private CameraControls controls;


    @Inject
    public CameraPresenterImpl(@NonNull CameraSystem cameraSystem) {
        this.cameraSystem = cameraSystem;
    }


    @Override
    public void setCameraControls(@NonNull CameraControls cameraControls) {
        // connect this presenter to the controls view, disconnecting from any existing controls
        if (controls != null) {
            controls.setEventListener(null);
        }
        controls = cameraControls;
        controls.setEventListener(this);
    }


    @Override
    public void setPhotoListener(@Nullable CameraSystem.PhotoListener listener) {
        cameraSystem.setPhotoListener(listener);
    }


    @Override
    public boolean startCamera(@NonNull CameraPreview cameraPreview) {
        cameraPreview.connectToCameraSystem(cameraSystem);
        return cameraSystem.start();
    }


    @Override
    public void stopCamera() {
        cameraSystem.stop();
    }


    @Override
    public void onShutterPressed() {
        cameraSystem.takePhoto();
    }


}
