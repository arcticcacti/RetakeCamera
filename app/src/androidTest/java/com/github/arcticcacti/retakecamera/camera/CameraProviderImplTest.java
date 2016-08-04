package com.github.arcticcacti.retakecamera.camera;

import android.hardware.Camera;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Lee Holmes on 13/07/2016.
 *
 * Tests for the concrete implementation of CameraProvider, which interacts with the Android
 * framework to provide access to the hardware cameras.
 */
public class CameraProviderImplTest {

    private CameraProvider cameraProvider;

    @Before
    public void setUp() throws Exception {
        cameraProvider = new CameraProviderImpl(InstrumentationRegistry.getTargetContext());
    }


    @SuppressWarnings("deprecation")
    @Test
    public void getCameraInstance_returnsCamera() throws Exception {
        // attempt to get an old-style camera
        Camera camera = cameraProvider.getCameraInstance();

        // this should succeed
        assertThat(camera, is(notNullValue()));
    }

    // TODO: 13/07/2016 find a way to simulate a missing/unavailable camera and check it acts correctly
}