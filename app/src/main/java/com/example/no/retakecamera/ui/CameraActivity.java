package com.example.no.retakecamera.ui;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.no.retakecamera.CameraPresenter;
import com.example.no.retakecamera.ImageProcessor;
import com.example.no.retakecamera.R;
import com.example.no.retakecamera.RetakeApplication;
import com.example.no.retakecamera.camera.CameraSystem;
import com.example.no.retakecamera.permissions.PermissionsManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.no.retakecamera.permissions.PermissionsManager.CAMERA;

/**
 * Main camera view activity, holding a preview and the camera's controls UI.
 * <p/>
 * This should be bare-bones to allow for easier testing, relying on composition and
 * dependency injection to delegate the main app logic to other components. The activity
 * is responsible for setting up the necessary views, connecting them to the management components,
 * and informing those components of lifecycle events e.g. pausing, becoming visible etc.
 * <p/>
 * This is especially important when using the Android camera system, as open cameras
 * <strong>must</strong> be released when no longer required (i.e. no longer in the foreground),
 * or a crash can occur when another app attempts to access the camera system, requiring a reboot.
 */
public class CameraActivity extends AppCompatActivity {

    // injected components, for non-instrumented unit testing
    @Inject
    PermissionsManager permissionsManager;
    @Inject
    CameraPresenter cameraPresenter;
    @Inject
    ImageProcessor imageProcessor;

    // views
    @BindView(R.id.camera_preview)
    CameraPreview cameraPreview;
    @BindView(R.id.camera_controls)
    CameraControls cameraControls;
    @BindView(R.id.photo_thumbnail)
    ImageView photoThumbnail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // get the required views and injected components, and wire them up
        ButterKnife.bind(this);
        ((RetakeApplication) getApplication()).getInjector().inject(this);
        cameraPresenter.setPhotoListener(new PhotoHandler());
        cameraPresenter.setCameraControls(cameraControls);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // start the camera if we have the permission, otherwise request it
        if (permissionsManager.hasPermission(CAMERA)) {
            startCamera();
        } else {
            permissionsManager.requestPermission(CAMERA, this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // ALWAYS stop the camera when the app goes to the background,
        // to avoid a serious hardware conflict with any other apps that try to access it
        cameraPresenter.stopCamera();
    }


    /**
     * Put the camera system into the running state.
     * <p/>
     * This should only be called once permissions have been verified!
     */
    private void startCamera() {
        boolean success = cameraPresenter.startCamera(cameraPreview);
        if (!success) {
            Toast.makeText(this, "Couldn't start the camera", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // handle the results of a permissions request
        if (requestCode == CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                new AlertDialog.Builder(this).setMessage("App cannot function without camera permissions!").show();
            }
        }
    }


    /**
     * Basic handler for image data when a photo is taken.
     * // TODO: 09/07/2016 implement saving, and move this into a separate interface/implementation
     */
    class PhotoHandler implements CameraSystem.PhotoListener {

        @Override
        public void onPhotoTaken(final byte[] data) {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    // save the image, then make a bitmap
                    saveImage(data);
                    return imageProcessor.getBitmap(data);
                }


                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    // display the bitmap in the activity
                    photoThumbnail.setImageBitmap(bitmap);
                }
            }.execute();
        }


        private void saveImage(byte[] data) {
            // TODO: 09/07/2016 implement saving the photo
        }

    }
}
