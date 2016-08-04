package com.github.arcticcacti.retakecamera.ui;

import android.support.annotation.Nullable;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Interface for the camera's controls/UI component, enabling a presenter to manipulate the
 * displayed UI, and receive callbacks from various events like button presses.
 */
public interface CameraControls {

    /**
     * Enable the controls, allowing them to send events to the listener
     */
    void enable();

    /**
     * Disable the controls. This will prevent events being dispatched to the listener
     */
    void disable();

    /**
     * Set the component which should handle this view's events.
     *
     * @param listener the event handler
     */
    void setEventListener(@Nullable EventListener listener);

    interface EventListener {

        /**
         * Called when the shutter button is pressed.
         */
        void onShutterPressed();

    }
}
