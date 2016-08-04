package com.github.arcticcacti.retakecamera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertThat;

/**
 * Created by Lee Holmes on 17/07/2016.
 * <p/>
 * Tests for the real storage component, which relies on the Android framework
 */
public class PhotoStorageImplTest {

    private static Bitmap testBitmap;

    private PhotoStorage photoStorage;
    private File storageDir;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();


    @BeforeClass
    public static void loadBitmap() {
        // get a bitmap - just use the app icon
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        testBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        assertThat(testBitmap, is(notNullValue()));
    }


    @Before
    public void setUp() throws IOException {
        storageDir = temporaryFolder.newFolder();
        Context context = InstrumentationRegistry.getTargetContext();
        photoStorage = new PhotoStorageImpl(context, storageDir);
    }


    @Test
    public void save_writesBitmap() {
        // attempt to save the bitmap without a filename
        boolean success = photoStorage.save(testBitmap);

        // the save method should have reported success, and there should be a file saved to storage
        assertThat(success, is(true));
        assertThat(storageDir.listFiles(), is(not(emptyArray())));
    }


    @Test
    public void save_withFilenameWritesToExpectedFile() {
        // choose a filename to write to
        String desiredFilename = "image_filename";

        // attempt to save the bitmap with a specified filename
        boolean success = photoStorage.save(testBitmap, desiredFilename);

        // the save should be successful, and a saved file should exist
        assertThat(success, is(true));
        File[] savedFiles = storageDir.listFiles();
        assertThat(savedFiles, is(arrayWithSize(1)));

        // get the saved file's name and strip the extension - it should match what we specified
        String strippedSavedName = stripExtension(savedFiles[0].getName());
        assertThat(strippedSavedName, is(desiredFilename));
    }


    @Test
    public void save_withFilenameCollisionUsesAlternativeFilename() {
        // choose a filename to write to - this example includes the extension separator,
        // to make sure extension stripping happens correctly
        String desiredFilename = "image.filename";

        // save a file three times using the same filename
        boolean firstSaved = photoStorage.save(testBitmap, desiredFilename);
        boolean secondSaved = photoStorage.save(testBitmap, desiredFilename);
        boolean thirdSaved = photoStorage.save(testBitmap, desiredFilename);

        // all should have succeeded, and there should be three files in the storage folder
        assertThat(firstSaved, is(true));
        assertThat(secondSaved, is(true));
        assertThat(thirdSaved, is(true));
        File[] savedFiles = storageDir.listFiles();
        assertThat(savedFiles, is(arrayWithSize(3)));

        // check each filename starts with the desired filename, assuming a suffix is added on collisions
        // (this assumes that the alternative filenames follow the existing ones alphanumerically)
        String filename1 = stripExtension(savedFiles[0].getName());
        String filename2 = stripExtension(savedFiles[1].getName());
        String filename3 = stripExtension(savedFiles[2].getName());

        assertThat(filename1, is(desiredFilename));
        assertThat(filename2.startsWith(desiredFilename), is(true));
        assertThat(filename3.startsWith(desiredFilename), is(true));
        // ensure the uniqueness is in the main filename, not the suffix
        assertThat(filename2, is(not(filename3)));
    }


    @Test
    public void save_withFolderWriteFailure() {
        // make the storage folder unwritable
        boolean writeDisabled = storageDir.setWritable(false);
        assertThat(writeDisabled, is(true));

        // attempt to save the bitmap
        boolean success = photoStorage.save(testBitmap);

        // saving should have failed, returning false
        assertThat(success, is(false));
    }

    // TODO: 18/07/2016 added to gallery check


    /**
     * Utility method to strip the extension suffix from a filename (if it exists).
     *
     * @param fullFilename the full filename, possibly with an extension
     * @return the filename up to the extension separator (or if it doesn't exist, the whole filename)
     */
    @NonNull
    private String stripExtension(@NonNull String fullFilename) {
        int extensionSeparatorIndex = fullFilename.lastIndexOf('.');
        if (extensionSeparatorIndex == -1) {
            return fullFilename;
        }
        return fullFilename.substring(0, extensionSeparatorIndex);
    }

}