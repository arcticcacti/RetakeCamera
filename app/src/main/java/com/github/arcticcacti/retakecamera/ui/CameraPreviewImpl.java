package com.github.arcticcacti.retakecamera.ui;

/**
 * Created by Lee Holmes on 08/07/2016.
 * <p/>
 * A SurfaceView that connects with a CameraSystem instance, to act as its preview and
 * automatically handle pausing/starting the preview as part of the surface lifecycle.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.github.arcticcacti.retakecamera.camera.CameraSystem;

/**
 * A basic Camera preview class
 */
public class CameraPreviewImpl extends SurfaceView
        implements SurfaceHolder.Callback, CameraPreview {


    @Nullable
    private CameraSystem cameraSystem = null;


    public CameraPreviewImpl(Context context) {
        super(context);
        init();

    }


    public CameraPreviewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public CameraPreviewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraPreviewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        getHolder().addCallback(this);
    }


    @Override
    public void connectToCameraSystem(@NonNull CameraSystem cameraSystem) {
        this.cameraSystem = cameraSystem;
        cameraSystem.setPreview(getHolder());
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // setting surface is handled in surfaceChanged, which is called next
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        if (cameraSystem != null) {
            cameraSystem.pause();
            // make any changes here, then update the camera with the preview surface
            cameraSystem.setPreview(holder);
            cameraSystem.start();
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        if (cameraSystem != null) {
            cameraSystem.setPreview(null);
        }
    }
}

