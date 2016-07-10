package com.example.no.retakecamera;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * Created by Lee Holmes on 08/07/2016.
 * <p/>
 * Implements methods to handle the results of image capture.
 */
public interface ImageProcessor {

    /**
     * Get a Bitmap from a byteArray
     *
     * @param imageData the image data to convert
     * @return the equivalent Bitmap, or null if a Bitmap couldn't be created from this data
     */
    @Nullable
    Bitmap getBitmap(byte[] imageData);

}
