package com.github.arcticcacti.retakecamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import static android.graphics.Bitmap.CompressFormat.JPEG;

/**
 * Created by Lee Holmes on 17/07/2016.
 * <p/>
 * Concrete storage implementation, for writing photos to Android's default photo storage location,
 * and updating the media database.
 */
public class PhotoStorageImpl implements PhotoStorage {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String FILENAME_DATE_FORMAT = "yyyyMMdd_HHmmss";
    private static final int JPEG_QUALITY = 90;

    private final File storageDir;
    private final Context context;


    @Inject
    public PhotoStorageImpl(Context context, @NonNull File storageDir) {
        this.context = context;
        this.storageDir = storageDir;
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean save(@NonNull Bitmap bitmap) {
        // generate a filename based on the current date
        String filename = new SimpleDateFormat(FILENAME_DATE_FORMAT).format(new Date());
        return save(bitmap, filename);
    }


    @Override
    public boolean save(@NonNull Bitmap bitmap, @NonNull String filename) {
        try {
            File imagePath;
            int renameCount = 0;
            String renameSuffix = "";
            // keep generating filename variations (original_1 etc) until we get one that doesn't exist
            // the generated rename suffix is only appended after the first loop (if at all)
            do {
                imagePath = new File(storageDir, filename + renameSuffix + ".jpg");
                renameCount++;
                renameSuffix = "_" + renameCount;
            } while (imagePath.exists());

            boolean success = writeFile(bitmap, imagePath);
            if (success) {
                addToMediaStore(imagePath);
            }
            return success;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean writeFile(@NonNull Bitmap bitmap, File file) throws FileNotFoundException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        return bitmap.compress(JPEG, JPEG_QUALITY, out);
    }


    /**
     * Run the media scanner on an image, so it's added to the system's media content provider.
     *
     * This makes the image available to the system, other apps etc., and means the image
     * shows up in the public view of whichever folder it's saved to (e.g. the Camera folder).
     *
     * @param imagePath the path to the image which should be scanned
     */
    private void addToMediaStore(@NonNull File imagePath) {
        // set up an Intent to scan the new file, and fire it off
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imagePath);
        mediaScanIntent.setDataAndNormalize(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


}
