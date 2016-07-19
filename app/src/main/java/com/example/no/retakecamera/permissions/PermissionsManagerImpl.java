package com.example.no.retakecamera.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Concrete implementation which calls through to the Android system, checking and requesting the
 * permissions available on the current device.
 */
public class PermissionsManagerImpl implements PermissionsManager {

    private final Context context;

    /**
     * a mapping of internal permission IDs to the equivalent Android permission strings
     */
    private final SparseArray<String> permissionStrings = new SparseArray<>();

    {
        permissionStrings.append(CAMERA, Manifest.permission.CAMERA);
        permissionStrings.append(SAVE_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    public PermissionsManagerImpl(@NonNull Context context) {
        this.context = context;
    }


    @Override
    public boolean hasPermission(@Permission int type) {
        // check if we handle this permission - if so, see if it has been granted
        String permissionString = permissionStrings.get(type);
        if (permissionString == null) {
            return false;
        }
        int permission = ContextCompat.checkSelfPermission(context, permissionString);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void requestPermission(@Permission int type, @NonNull Activity activity) {
        String permissionString = permissionStrings.get(type);
        if (permissionString != null) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionString}, type);
        }
    }
}
