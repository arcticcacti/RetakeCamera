package com.example.no.retakecamera;

import com.example.no.retakecamera.camera.CameraSystem;
import com.example.no.retakecamera.ui.CameraControls;
import com.example.no.retakecamera.ui.CameraPreview;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Lee Holmes on 11/07/2016.
 * <p/>
 * Unit tests for the concrete implementation of the CameraPresenter.
 */
@RunWith(MockitoJUnitRunner.class)
public class CameraPresenterImplTest {

    private final byte[] imageData = new byte[1000];
    @Mock
    CameraSystem cameraSystem;
    @Mock
    CameraPreview cameraPreview;
    @Mock
    CameraControls cameraControls;
    @Mock
    CameraPresenter.EventListener eventListener;
    @Captor
    ArgumentCaptor<byte[]> imageDataCaptor;
    private CameraPresenter cameraPresenter;


    @Before
    public void setUp() {
        cameraPresenter = new CameraPresenterImpl(cameraSystem);
    }


    @Test
    public void startCamera_connectsPreviewAndStartsCamera() {
        // start the camera, passing a preview to it
        cameraPresenter.startCamera(cameraPreview);

        // ensure the preview was connected to the camera, and the camera was started
        verify(cameraPreview).connectToCameraSystem(cameraSystem);
        verify(cameraSystem).start();
    }


    @Test
    public void startCamera_passesBackResultFromCameraSystem() {
        // make the camera system fail to start, then succeed
        when(cameraSystem.start()).thenReturn(false, true);

        // start the camera twice
        boolean firstResult = cameraPresenter.startCamera(cameraPreview);
        boolean secondResult = cameraPresenter.startCamera(cameraPreview);

        // the results (failed, succeeded) should have been passed back to the caller
        assertThat(firstResult, is(false));
        assertThat(secondResult, is(true));
    }


    @Test
    public void stopCamera_stopsTheCamera() {
        // set the camera running
        cameraPresenter.startCamera(cameraPreview);

        // stop the camera
        cameraPresenter.stopCamera();

        // ensure the camera system received the call
        verify(cameraSystem).stop();
    }


    @Test
    public void setCameraControls_setsThePresenterAsItsEventListener() {
        // set the controls on the presenter
        cameraPresenter.setCameraControls(cameraControls);

        // the controls should get the listener
        verify(cameraControls).setEventListener(cameraPresenter);
    }


    @Test
    public void setCameraControls_withExistingControlsDisconnectsOldOnes() {
        // create a second set of controls to use
        CameraControls secondControls = mock(CameraControls.class);

        // set some controls on the presenter, then set the new ones
        cameraPresenter.setCameraControls(cameraControls);
        cameraPresenter.setCameraControls(secondControls);

        // the original set should have their listener unset, and the new ones set to the presenter
        verify(cameraControls).setEventListener(null);
        verify(secondControls).setEventListener(cameraPresenter);
    }


    @Test
    public void onShutterPressed_takesPhoto() {
        // trigger the shutter press
        cameraPresenter.onShutterPressed();

        // this should pass the call to the camera (even though it isn't started)
        verify(cameraSystem).takePhoto(any(CameraSystem.PhotoListener.class));
    }


    @Test
    public void onShutterPressed_returnsImageDataIfListenerIsConnected() {
        // set the camera system to return image data to the provided listener, when a photo is taken
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CameraSystem.PhotoListener listener = (CameraSystem.PhotoListener) invocation.getArguments()[0];
                listener.onPhotoTaken(imageData);
                return null;
            }
        }).when(cameraSystem).takePhoto(any(CameraSystem.PhotoListener.class));
        // connect an event listener to get the resulting image data
        cameraPresenter.setEventListener(eventListener);

        // take the photo
        cameraPresenter.onShutterPressed();

        // the listener should receive the image data the camera returns
        verify(eventListener).onPhotoTaken(imageDataCaptor.capture());
        assertThat(imageDataCaptor.getValue(), is(imageData));
    }


}