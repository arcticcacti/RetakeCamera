package com.example.no.retakecamera.ui;

import android.support.annotation.NonNull;

import com.example.no.retakecamera.camera.CameraSystem;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Interface for Previews which the CameraSystem will use as a preview display.
 * <p/>
 * Previews are connected to the CameraSystem, and are then responsible for directly updating it
 * where necessary (e.g. surface reconfiguration)
 */
public interface CameraPreview {

    /**
     * Set the camera system this will act as the preview for.
     * <p/>
     * The preview will update the camera system as necessary for certain events, such as
     * setting the surface holder, or pausing/restarting the preview when reconfiguring the surface.
     *
     * @param cameraSystem the camera system to connect to
     */
    void connectToCameraSystem(@NonNull CameraSystem cameraSystem);

}
