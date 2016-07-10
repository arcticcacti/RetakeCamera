package com.example.no.retakecamera.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Concrete implementation which calls through to the Android system, checking and requesting the
 * permissions available on the current device.
 */
public class PermissionsManagerImpl implements PermissionsManager {

    private final Context context;


    public PermissionsManagerImpl(@NonNull Context context) {
        this.context = context;
    }


    @Override
    public boolean hasPermission(int type) {
        if (type == CAMERA) {
            int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            return permission == PackageManager.PERMISSION_GRANTED;
        } else {
            return false;
        }
    }


    @Override
    public void requestPermission(int type, @NonNull Activity activity) {
        if (type == CAMERA) {
            requestCameraPermission(activity);
        }
    }


    /**
     * Request permission to access the device's cameras.
     *
     * @param activity the activity which will receive the result of the request
     */
    private void requestCameraPermission(@NonNull final Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                CAMERA);
    }
}
