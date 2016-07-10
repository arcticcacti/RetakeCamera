package com.example.no.retakecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

/**
 * Created by Lee Holmes on 08/07/2016.
 * <p/>
 * Implementation which uses the Android framework to produce Bitmap objects
 */
public class ImageProcessorImpl implements ImageProcessor {

    @Nullable
    @Override
    public Bitmap getBitmap(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
}
