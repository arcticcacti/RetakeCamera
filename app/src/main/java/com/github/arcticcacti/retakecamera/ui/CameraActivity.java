package com.github.arcticcacti.retakecamera.ui;

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

import com.github.arcticcacti.retakecamera.CameraPresenter;
import com.github.arcticcacti.retakecamera.ImageProcessor;
import com.github.arcticcacti.retakecamera.PhotoStorage;
import com.github.arcticcacti.retakecamera.R;
import com.github.arcticcacti.retakecamera.RetakeApplication;
import com.github.arcticcacti.retakecamera.permissions.PermissionsManager;
import com.github.arcticcacti.retakecamera.permissions.PermissionsManager.Permission;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.arcticcacti.retakecamera.permissions.PermissionsManager.CAMERA;
import static com.github.arcticcacti.retakecamera.permissions.PermissionsManager.SAVE_IMAGES;

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

    /** the permissions required for this app to function correctly */
    private static final List<Integer> REQUIRED_PERMISSIONS = Arrays.asList(CAMERA, SAVE_IMAGES);

    @Inject
    PermissionsManager permissionsManager;
    @Inject
    CameraPresenter cameraPresenter;
    @Inject
    ImageProcessor imageProcessor;
    @Inject
    PhotoStorage photoStorage;

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
        RetakeApplication.getInjector().inject(this);
        cameraPresenter.setEventListener(new PhotoHandler());
        cameraPresenter.setCameraControls(cameraControls);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkPermissionsAndStartCamera();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // ALWAYS stop the camera when the app goes to the background,
        // to avoid a serious hardware conflict with any other apps that try to access it
        cameraPresenter.stopCamera();
    }


    /**
     * Check the app's required permissions, and start the camera if they're all granted.
     * <p/>
     * If a permission has not been granted, an attempt will be made to request it from the user.
     * The result will be returned to {@link #onRequestPermissionsResult(int, String[], int[])},
     * which should call this method again if the request was successful.
     */
    private void checkPermissionsAndStartCamera() {
        for (int permission : REQUIRED_PERMISSIONS) {
            if (!permissionsManager.hasPermission(permission)) {
                // there's a permission we need - request it and abort the check (check again when we get the result)
                permissionsManager.requestPermission(permission, this);
                return;
            }
        }
        // we only reach this if every required permission has been granted
        boolean success = cameraPresenter.startCamera(cameraPreview);
        if (!success) {
            Toast.makeText(this, "Couldn't start the camera", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // handle the results of requests for permissions we require
        if (REQUIRED_PERMISSIONS.contains(requestCode)) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // when a permission is granted, try to start the camera
                checkPermissionsAndStartCamera();
            } else {
                showPermissionsError(requestCode);
            }
        }
    }


    /**
     * Display an error when a required permission isn't granted by the user.
     *
     * @param permissionType the ungranted permission
     */
    private void showPermissionsError(@Permission int permissionType) {
        if (permissionType == CAMERA) {
            new AlertDialog.Builder(this).setMessage("This app requires permission to use the camera!").show();
        } else if (permissionType == SAVE_IMAGES) {
            new AlertDialog.Builder(this).setMessage("This app requires permission to save images!").show();
        }
    }


    /**
     * Basic handler for image data when a photo is taken.
     */
    class PhotoHandler implements CameraPresenter.EventListener {

        @Override
        public void onPhotoTaken(final byte[] data) {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    // generate the bitmap
                    return imageProcessor.getBitmap(data);
                }


                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    // save and display the bitmap in the activity
                    save(bitmap);
                    photoThumbnail.setImageBitmap(bitmap);
                }
            }.execute();
        }


        private void save(final Bitmap bitmap) {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    // attempt to save the bitmap
                    return photoStorage.save(bitmap);
                }


                @Override
                protected void onPostExecute(Boolean success) {
                    // TODO: 17/07/2016 handle failure properly
                    Toast.makeText(getApplicationContext(), "Photo save " + (success ? "successful" : "failed!"), Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }

    }
}
