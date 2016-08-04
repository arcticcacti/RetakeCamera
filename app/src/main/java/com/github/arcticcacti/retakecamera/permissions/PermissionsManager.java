package com.github.arcticcacti.retakecamera.permissions;

import android.app.Activity;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Lee Holmes on 06/07/2016.
 * <p/>
 * Interface for a component which handles Android permissions, checking and requesting them.
 */
public interface PermissionsManager {

    int CAMERA = 1;
    int SAVE_IMAGES = 2;

    /**
     * Check if a particular permission is currently granted.
     *
     * @param type the permission to check
     * @return true if currently granted
     */
    boolean hasPermission(@Permission int type);

    /**
     * Request a given permission.
     * <p/>
     * This will attempt to request this permission from the user. The result will be returned to
     * the specified Activity via an async callback.
     *
     * @param type     the permission to request
     * @param activity the Activity which will receive the request result
     * @see android.app.Activity#onRequestPermissionsResult(int, String[], int[])
     */
    void requestPermission(@Permission int type, @NonNull Activity activity);

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CAMERA, SAVE_IMAGES})
    @interface Permission {
    }

}
