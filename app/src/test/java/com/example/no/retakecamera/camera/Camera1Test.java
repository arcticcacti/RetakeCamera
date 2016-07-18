package com.example.no.retakecamera.camera;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.SurfaceHolder;

import com.example.no.retakecamera.ui.CameraPreview;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Lee Holmes on 10/07/2016.
 * <p/>
 * Tests for the concrete implementation of the Camera1 CameraSystem.
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class Camera1Test {

    @Mock
    CameraProvider cameraProvider;
    @Mock
    Camera camera;
    @Mock
    CameraPreview cameraPreview;
    @Mock
    SurfaceHolder surfaceHolder;
    @Mock
    CameraSystem.PhotoListener photoListener;

    private Camera1 cameraSystem;


    @Before
    public void setUp() {
        // have the camera provider return a mock camera, and pass that in to the CameraSystem
        when(cameraProvider.getCameraInstance()).thenReturn(camera);
        cameraSystem = new Camera1(cameraProvider);

        // have the #takePicture call to camera invoke the jpeg callback, to mock a successful photo result
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // the last callback (4th) is the jpeg one, for the finished image data
                PictureCallback callback = (PictureCallback) invocation.getArguments()[3];
                callback.onPictureTaken(new byte[1000], camera);
                return null;
            }
        }).when(camera).takePicture(any(ShutterCallback.class), any(PictureCallback.class),
                any(PictureCallback.class), any(PictureCallback.class));
    }


    ///////////////////////////////////////////////////////////////////////////
    // Start camera
    ///////////////////////////////////////////////////////////////////////////


    @Test
    public void start_opensNewCamera() {
        // start the camera
        boolean success = cameraSystem.start();

        // check the new camera was requested, and the method returns success
        verify(cameraProvider).getCameraInstance();
        assertThat(success, is(true));
    }


    @Test
    public void start_handlesFailureToOpenCamera() {
        // have the camera provider fail to open and provide a camera instance
        when(cameraProvider.getCameraInstance()).thenReturn(null);

        // try to start the camera
        boolean success = cameraSystem.start();

        // the call should fail
        assertThat(success, is(false));
    }


    @Test
    public void start_startsCorrectlyAfterFailure() throws IOException {
        // have the camera provider fail the first time, then provide a camera
        when(cameraProvider.getCameraInstance()).thenReturn(null, camera);
        // add a preview to connect to when the camera starts
        cameraSystem.setPreview(surfaceHolder);

        // start the camera twice, first to allow it to fail
        cameraSystem.start();
        boolean success = cameraSystem.start();

        // the second time should succeed and start the preview as normal
        assertThat(success, is(true));
        verify(camera).setPreviewDisplay(surfaceHolder);
        verify(camera).startPreview();
    }


    @Test
    public void start_doesNothingWhenAlreadyStarted() {
        // start the camera, then reset the mocks so we can check for further interactions
        cameraSystem.start();
        reset(cameraProvider, cameraPreview, camera);

        // call start on the already started camera system
        boolean success = cameraSystem.start();

        // there should be no calls to the camera provider, the preview or the current camera
        verifyZeroInteractions(camera, cameraProvider, cameraPreview);
        // the start call should still return true (since the camera *is* started)
        assertThat(success, is(true));
    }


    ///////////////////////////////////////////////////////////////////////////
    // Stopping camera
    ///////////////////////////////////////////////////////////////////////////


    @Test
    public void stop_closesOpenCamera() {
        cameraSystem.start();

        cameraSystem.stop();

        verify(camera).release();
    }


    @Test
    public void stop_thenStartGetsNewCamera() {
        // provide a second camera for after the first one is disposed of
        Camera secondCam = mock(Camera.class);
        when(cameraProvider.getCameraInstance()).thenReturn(camera, secondCam);

        // get a camera by starting, then stop to release it
        cameraSystem.start();
        cameraSystem.stop();
        // do the same again, which should get and stop a different camera
        cameraSystem.start();
        cameraSystem.stop();

        // there should be two separate getCamera requests
        verify(cameraProvider, times(2)).getCameraInstance();
        // after the second #start call secondCam should be the current one, and receive the #stop call
        verify(secondCam).release();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pausing camera
    ///////////////////////////////////////////////////////////////////////////


    @Test
    public void pause_stopsPreviewAndDoesNotReleaseCamera() {
        // start the camera with a preview, then reset the camera interactions
        cameraSystem.setPreview(surfaceHolder);
        cameraSystem.start();
        reset(camera);

        // pause and restart the camera
        cameraSystem.pause();
        cameraSystem.start();

        // the preview should be stopped and started on the same camera instance
        InOrder inOrder = inOrder(camera);
        inOrder.verify(camera).stopPreview();
        inOrder.verify(camera).startPreview();
    }


    @Test
    public void pause_worksWithNoCameraPreview() {
        // start the camera without a preview connected
        cameraSystem.start();

        // pause the camera, there should be no errors
        cameraSystem.pause();
    }


    @Test
    public void pause_whenAlreadyPaused() {
        // start the camera with a preview
        cameraSystem.setPreview(surfaceHolder);
        cameraSystem.start();

        // pause the running camera, then do it again
        cameraSystem.pause();
        cameraSystem.pause();

        // stop preview should only be called once
        verify(camera).stopPreview();
    }

    // TODO: 10/07/2016 setPreview when paused probably starts it! Maybe move the holder to #start


    @Test
    public void pause_whenStopped() {
        // add a preview but don't start the camera
        cameraSystem.setPreview(surfaceHolder);

        // pause the camera even though it's not running
        cameraSystem.pause();

        // this should effectively do nothing
        verifyZeroInteractions(cameraProvider, cameraPreview, surfaceHolder, camera);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Preview handling
    ///////////////////////////////////////////////////////////////////////////


    @Test
    public void setPreview_withUnstartedCameraConnectsWhenStarted() throws IOException {
        // set the preview in advance
        cameraSystem.setPreview(surfaceHolder);

        // start the camera
        cameraSystem.start();

        // check that the preview is connected and then started
        InOrder inOrder = inOrder(camera);
        inOrder.verify(camera).setPreviewDisplay(surfaceHolder);
        inOrder.verify(camera).startPreview();
    }


    @Test
    public void setPreview_connectsToStartedCamera() throws IOException {
        // start the camera first
        cameraSystem.start();

        // then connect the preview to the running camera
        cameraSystem.setPreview(surfaceHolder);

        // check that the preview is connected and then started
        InOrder inOrder = inOrder(camera);
        inOrder.verify(camera).setPreviewDisplay(surfaceHolder);
        inOrder.verify(camera).startPreview();
    }


    @Test
    public void setPreview_handlesCameraExceptionSafely() throws IOException {
        // throw an exception when trying to start the preview
        doThrow(new IOException()).when(camera).setPreviewDisplay(surfaceHolder);

        // set the preview and start the camera, which should handle the exception
        cameraSystem.setPreview(surfaceHolder);
        cameraSystem.start();
    }


    ///////////////////////////////////////////////////////////////////////////
    // Take photo
    ///////////////////////////////////////////////////////////////////////////


    @Test
    public void takePhoto_takesPhoto() {
        // set up the camera and start it
        cameraSystem.setPreview(surfaceHolder);
        cameraSystem.start();

        // take a photo!
        cameraSystem.takePhoto(photoListener);

        // the listener should be called with the results
        verify(photoListener).onPhotoTaken(any(byte[].class));
    }


    @Test
    public void takePhoto_withCameraStopped() {
        // set a preview, then start and stop the camera
        cameraSystem.setPreview(surfaceHolder);
        cameraSystem.start();
        cameraSystem.stop();

        // take the photo
        cameraSystem.takePhoto(photoListener);

        // there should be no effect (camera isn't running) and no callback to the listener
        verify(camera, never()).takePicture(any(ShutterCallback.class), any(PictureCallback.class),
                any(PictureCallback.class), any(PictureCallback.class));
        verify(photoListener, never()).onPhotoTaken(any(byte[].class));
    }


    @Test
    public void takePhoto_withPreviewStopped() {
        // start the camera with a preview, then pause it
        cameraSystem.setPreview(surfaceHolder);
        cameraSystem.start();
        cameraSystem.pause();

        // try to take the photo
        cameraSystem.takePhoto(photoListener);

        // there should be no effect (preview isn't running, which is required to take photos) and no callback
        verify(camera, never()).takePicture(any(ShutterCallback.class), any(PictureCallback.class),
                any(PictureCallback.class), any(PictureCallback.class));
        verify(photoListener, never()).onPhotoTaken(any(byte[].class));
    }


}