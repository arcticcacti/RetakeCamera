package com.github.arcticcacti.retakecamera;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.arcticcacti.retakecamera.camera.CameraSystem;
import com.github.arcticcacti.retakecamera.ui.CameraControls;
import com.github.arcticcacti.retakecamera.ui.CameraPreview;

/**
 * Created by Lee Holmes on 08/07/2016.
 * <p/>
 * Interface for the presenter which sits between the UI and camera.
 * <p/>
 * This is the main component through which the app interacts with the camera system.
 */
public interface CameraPresenter extends CameraControls.EventListener, CameraSystem.PhotoListener {


    /**
     * Set a listener to handle the results of a photo-taking operation.
     *
     * @param listener a callback listener
     */
    void setEventListener(@Nullable EventListener listener);

    /**
     * Set the controls view which will send events to the presenter.
     *
     * @param cameraControls the UI controls for the camera system.
     */
    void setCameraControls(@NonNull CameraControls cameraControls);


    /**
     * Stop the camera if it is currently started.
     * <p/>
     * This releases the open camera instance and any references to the preview.
     * <p/>
     * <strong>Important: </strong> you <strong>must</strong> call this method when the
     * camera is no longer in use, e.g. when the activity is no longer visible, to ensure the camera
     * is released for the system to use elsewhere. Failing to do this can cause serious errors,
     * requiring the user to reset their device!
     */
    void stopCamera();

    /**
     * Start the camera system, connecting the preview surface and initialising the camera instance.
     * <p/>
     * This method must be called for the camera controls to function. If there was an error
     * initialising the camera, this method will return false.
     * <p/>
     * <strong>Important: </strong> you <strong>must</strong> call {@link #stopCamera()} when the
     * camera is no longer in use, to avoid serious errors!
     *
     * @param cameraPreview the view which the camera should use to display its preview
     * @return true if the camera started successfully
     */
    boolean startCamera(@NonNull CameraPreview cameraPreview);


    /**
     * Callbacks for events that may need to be handled externally, e.g. the results a 'take photo' operation
     */
    interface EventListener {

        /**
         * Called when a photo has been taken, providing the resulting JPEG image data.
         * @param data  the image data returned by the camera
         */
        void onPhotoTaken(@Nullable byte[] data);

    }

}
