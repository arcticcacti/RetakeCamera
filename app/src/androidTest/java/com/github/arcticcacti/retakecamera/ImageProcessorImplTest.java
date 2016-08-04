package com.github.arcticcacti.retakecamera;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Lee Holmes on 12/07/2016.
 * <p/>
 * Instrumentation test for the ImageProcessor which accesses the Android framework
 */
public class ImageProcessorImplTest {

    private ImageProcessor imageProcessor;

    private static byte[] imageData;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @BeforeClass
    public static void loadImageData() throws IOException {
        // get a bitmap - just use the app icon
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        assertThat(bitmap, is(notNullValue()));

        // convert it to a byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(PNG, 0, out);
        imageData = out.toByteArray();
    }


    @Before
    public void setUp() throws Exception {
        imageProcessor = new ImageProcessorImpl();
    }


    @Test
    public void getBitmap_generatesBitmapWithValidData() throws Exception {
        // get a bitmap from our source data
        Bitmap bitmap = imageProcessor.getBitmap(imageData);

        // make sure we get a bitmap back
        assertThat(bitmap, is(notNullValue()));
    }


    @Test
    public void getBitmap_withBadDataReturnsNull() {
        // generate a bitmap from an empty data array
        Bitmap bitmap = imageProcessor.getBitmap(new byte[0]);

        // the result should be a null bitmap
        assertThat(bitmap, is(nullValue()));
    }
}