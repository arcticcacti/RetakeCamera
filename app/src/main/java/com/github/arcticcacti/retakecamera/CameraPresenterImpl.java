package com.github.arcticcacti.retakecamera;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.arcticcacti.retakecamera.camera.CameraSystem;
import com.github.arcticcacti.retakecamera.ui.CameraControls;
import com.github.arcticcacti.retakecamera.ui.CameraPreview;

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

    @Nullable
    private EventListener eventListener;


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


    // TODO: 12/07/2016 should there always be a photo listener? Does it need to be possible to switch it out?
    @Override
    public void setEventListener(@Nullable EventListener listener) {
        eventListener = listener;
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
        cameraSystem.takePhoto(this);
    }


    @Override
    public void onPhotoTaken(byte[] data) {
        if (eventListener != null) {
            eventListener.onPhotoTaken(data);
        }
    }
}
