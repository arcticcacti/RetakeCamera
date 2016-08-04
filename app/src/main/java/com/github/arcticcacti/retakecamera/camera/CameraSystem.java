package com.github.arcticcacti.retakecamera.camera;

import android.support.annotation.Nullable;
import android.view.SurfaceHolder;

/**
 * Created by Lee Holmes on 06/07/2016.
 * <p/>
 * Interface for an abstracted Camera manager object.
 * <p/>
 * This is responsible for opening and closing a hardware camera, holding and releasing resources,
 * interfacing with a CameraPreview component, and handling camera configuration and events, like
 * taking a photo.
 * <p/>
 * Android has two types of Camera - the old, deprecated system (required for pre-Lollipop devices)
 * and the newer, more advanced Camera2 version. This interface allows for either to be used, with
 * a common interface so the app components can work with them agnostically.
 */
public interface CameraSystem {

    /**
     * Start the camera, including any attached preview surface.
     *
     * @return false if there was an error starting the camera
     */
    boolean start();

    /**
     * Stop the camera, freeing it up for other applications.
     */
    void stop();

    /**
     * Pause the camera, without freeing up resources.
     * <p/>
     * This implies the camera is being temporarily stopped (e.g. to reconfigure the preview surface)
     * and will be immediately restarted with a call to {@link #start()}. If this is not the case,
     * use {@link #stop()}!
     */
    void pause();

    /**
     * Set the actual preview surface the camera will use.
     * <p/>
     * This will automatically stop and start the preview as appropriate.
     *
     * @param holder the surface holder to use
     */
    void setPreview(@Nullable SurfaceHolder holder);


    /**
     * Take a photo, passing the result to the provided listener.
     *
     * The call will be handled asynchronously, and the listener called when it returns.
     *
     * @param photoListener the handler for the resulting image
     */
    void takePhoto(PhotoListener photoListener);

    /**
     * Interface for components which handle the results of a 'take photo' call
     */
    interface PhotoListener {

        /**
         * Called when a photo has been taken.
         *
         * @param data the resulting image data
         */
        void onPhotoTaken(byte[] data);

    }

}
