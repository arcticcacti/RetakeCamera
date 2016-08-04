package com.github.arcticcacti.retakecamera.camera;

import android.support.annotation.Nullable;

/**
 * Created by Lee Holmes on 06/07/2016.
 * <p/>
 * A provider interface for acquiring Camera instances, to allow for easier unit testing
 * and mocking in the CameraSystem classes which use it.
 * <p/>
 * The Android API has two types of Camera - the deprecated version in android.hardware,
 * and the newer Camera2 API available in Lollipop and up. Providers should perform this
 * version check where necessary.
 */
public interface CameraProvider {

    /**
     * Get an old-type (deprecated) camera instance.
     *
     * @return the camera, or null if a camera could not be opened.
     */
    @SuppressWarnings("deprecation")
    @Nullable
    android.hardware.Camera getCameraInstance();


    /**
     * Get a new Camera2 camera instance.
     *
     * @return the camera, or null if a camera could not be opened.
     * @see com.github.arcticcacti.retakecamera.RetakeApplication#CAMERA_2_MIN_API
     */
    @Nullable
    android.graphics.Camera getCamera2Instance();

}
