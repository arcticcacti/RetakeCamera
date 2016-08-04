package com.github.arcticcacti.retakecamera.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.ImageView;

import com.github.arcticcacti.retakecamera.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Lee Holmes on 13/07/2016.
 * <p/>
 * Instrumentation test for the Camera Activity, using Espresso to interact with and test the
 * activity through its UI.
 */
@LargeTest
public class CameraActivityTest {

    @Rule
    public final ActivityTestRule<CameraActivity> activityRule = new ActivityTestRule<>(CameraActivity.class);

    private ImageView previewThumbnail;


    private static Matcher<View> containsDrawableMatching(final Drawable drawable) {
        return new TypeSafeMatcher<View>() {

            String errorDescription = null;


            @Override
            protected boolean matchesSafely(View item) {
                // implicit null check
                if (!(item instanceof ImageView)) {
                    errorDescription = "this View type isn't handled (" + item.getClass().getSimpleName() + ")";
                    fail(errorDescription);
                }
                // make sure the views contain drawables
                Drawable containedDrawable = ((ImageView) item).getDrawable();
                if (checkForNull(containedDrawable, drawable,
                        "found a null drawable%nView's drawable: %s null%nPassed drawable: %s null")) {
                    fail(errorDescription);
                }

                // handle BitmapDrawables
                if (drawable instanceof BitmapDrawable && containedDrawable instanceof BitmapDrawable) {
                    Bitmap containedBitmap = ((BitmapDrawable) containedDrawable).getBitmap();
                    Bitmap comparingBitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (checkForNull(containedBitmap, comparingBitmap,
                            "a BitmapDrawable has a null bitmap%nView's bitmap: %s null%nPassed bitmap: %s null")) {
                        fail(errorDescription);
                    }
                    return comparingBitmap.sameAs(containedBitmap);
                }

                // drawable type not handled
                String message = "can't compare these drawables%nView's drawable: %s%nPassed drawable:";
                errorDescription = String.format(message,
                        containedDrawable.getClass().getSimpleName(),
                        drawable.getClass().getSimpleName());
                fail(errorDescription);
                return false;
            }


            /**
             * Utility method to check if one or both objects are null, and set the matcher's error description.
             *
             * The errorMessage should be a format string with placeholders tokens for the two objects.
             *
             * @param obj1 the first object to null check
             * @param obj2  the second object to null check
             * @param errorMessage  the formatted error string to use for the description
             * @return true if one or both objects are null
             */
            private boolean checkForNull(@Nullable Object obj1, @Nullable Object obj2, @NonNull String errorMessage) {
                if (obj1 == null || obj2 == null) {
                    errorDescription = String.format(errorMessage,
                            obj1 == null ? "" : "not",
                            obj2 == null ? "" : "not");
                    return true;
                }
                return false;
            }


            @Override
            public void describeTo(Description description) {
                description.appendText("an ImageView holding a matching Drawable");
            }


            @Override
            protected void describeMismatchSafely(View item, Description mismatchDescription) {
                super.describeMismatchSafely(item, mismatchDescription);
                mismatchDescription.appendText(errorDescription);
            }
        };
    }


    @Before
    public void setUp() throws Exception {
        previewThumbnail = (ImageView) activityRule.getActivity().findViewById(R.id.photo_thumbnail);
        assertNotNull(previewThumbnail);
    }

    // TODO: 18/07/2016 add automatic device wake-up code, so Espresso tests can run without needing to physically turn the device on

    // TODO: 18/07/2016 use test modules for DI, so e.g. a temp folder can be passed as the photo storage folder


    @Test
    public void previewThumbnailIsInitiallyEmpty() {
        // get the Drawable contents of the preview thumbnail when we first start up
        BitmapDrawable previewDrawable = (BitmapDrawable) previewThumbnail.getDrawable();

        // the thumbnail's drawable should be blank
        assertThat(previewDrawable, is(nullValue()));
    }


    @Test
    public void previewThumbnailHasContentAfterPhotoIsTaken() throws InterruptedException {
        // click the shutter and get the contents of the preview
        onView(withId(R.id.shutter_button)).perform(click());
        // wait 5 seconds for the camera to process the picture
        Thread.sleep(5000);
        Drawable previewDrawable = previewThumbnail.getDrawable();

        // the drawable should now have an image in it
        assertThat(previewDrawable, is(notNullValue()));
    }


    @Test
    public void previewThumbnailChangesForSubsequentPhotos() throws InterruptedException {
        // take the first photo (giving it time to process) and get its preview drawable
        onView(withId(R.id.shutter_button)).perform(click());
        Thread.sleep(5000);
        Drawable firstThumbnailDrawable = previewThumbnail.getDrawable();

        // take the second photo, and wait for it to complete
        onView(withId(R.id.shutter_button)).perform(click());
        Thread.sleep(5000);

        // check the preview updates with a different drawable
        onView(withId(R.id.photo_thumbnail)).check(matches(not(containsDrawableMatching(firstThumbnailDrawable))));
    }

}