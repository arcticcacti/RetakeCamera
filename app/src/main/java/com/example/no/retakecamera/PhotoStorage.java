package com.example.no.retakecamera;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Lee Holmes on 17/07/2016.
 *
 * Interface for a storage component, to handle saving photos.
 */
public interface PhotoStorage {

    /**
     * Save a bitmap to the storage folder as a compressed JPEG, using a generated filename.
     *
     * @param bitmap    the bitmap to compress and save
     * @return          true if the image was saved successfully
     */
    boolean save(@NonNull Bitmap bitmap);


    /**
     * Save a bitmap to the storage folder as a compressed JPEG, using the given filename.
     *
     * If a file already exists with this name, an alternative will be used. The saved image
     * will be scanned by the media store, making it visible to the system.
     *
     * @param bitmap    the bitmap to compress and save
     * @param filename  the filename to use
     * @return          true if the image was saved successfully
     */
    boolean save(@NonNull Bitmap bitmap, @NonNull String filename);

}
