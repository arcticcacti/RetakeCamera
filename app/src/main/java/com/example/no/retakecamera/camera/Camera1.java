package com.example.no.retakecamera.camera;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * CameraSystem implementation which uses the old deprecated camera API.
 */
@SuppressWarnings("deprecation")
public class Camera1 implements CameraSystem {

    @NonNull
    private final CameraProvider cameraProvider;
    @Nullable
    private Camera camera;
    private boolean previewRunning = false;
    @Nullable
    private SurfaceHolder previewSurfaceHolder = null;

    private boolean takingPhoto = false;


    @Inject
    public Camera1(@NonNull CameraProvider cameraProvider) {
        this.cameraProvider = cameraProvider;
    }


    @Override
    public boolean start() {
        // TODO: 10/07/2016 look into calling this with the preview surface, so we can check if it's the current one and avoid stopPreview + replace
        // don't open a new camera if we already have one (e.g. if #stop wasn't called first)
        if (camera == null) {
            // camera preview definitely hasn't been started
            previewRunning = false;
            camera = cameraProvider.getCameraInstance();
        }

        // fail if we couldn't open the camera, otherwise connect it up
        if (camera == null) {
            return false;
        }
        connectComponents();
        return true;
    }


    @Override
    public void stop() {
        // release the hardware camera, and any references to that and any views
        if (camera != null) {
            camera.release();
            camera = null;
        }
        previewSurfaceHolder = null;
    }


    @Override
    public void pause() {
        stopPreview();
    }


    private void connectComponents() {
        if (camera == null) {
            return;
        }
        if (previewSurfaceHolder != null) {
            try {
                stopPreview();
                camera.setPreviewDisplay(previewSurfaceHolder);
                startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void stopPreview() {
        // stopping the preview is a long operation, so we have a running flag to avoid it where possible
        if (camera != null && previewRunning) {
            camera.stopPreview();
            previewRunning = false;
        }
    }


    private void startPreview() {
        if (camera != null) {
            camera.startPreview();
            previewRunning = true;
        }
    }


    @Override
    public void setPreview(@Nullable SurfaceHolder holder) {
        previewSurfaceHolder = holder;
        connectComponents();
    }


    @Override
    public void takePhoto(@NonNull final PhotoListener photoListener) {
        if (camera != null && previewRunning && !takingPhoto) {
            takingPhoto = true;
            camera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    startPreview();
                    takingPhoto = false;
                    photoListener.onPhotoTaken(data);
                }
            });
        }
    }
}
