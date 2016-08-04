package com.github.arcticcacti.retakecamera.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.github.arcticcacti.retakecamera.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lee Holmes on 08/07/2016.
 * <p/>
 * Implementation of UI controls for the camera.
 */
public class CameraControlsImpl extends FrameLayout implements CameraControls {

    @Nullable
    private EventListener eventListener;
    private boolean enabled = true;


    public CameraControlsImpl(Context context) {
        super(context);
        init();
    }


    public CameraControlsImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public CameraControlsImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraControlsImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.camera_controls, this, true);
        ButterKnife.bind(this);
    }


    @Override
    public void setEventListener(@Nullable EventListener listener) {
        eventListener = listener;
    }


    @Override
    public void enable() {
        enabled = true;
    }


    @Override
    public void disable() {
        enabled = false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Control events
    ///////////////////////////////////////////////////////////////////////////


    @OnClick(R.id.shutter_button)
    public void onShutterPressed() {
        if (enabled && eventListener != null) {
            eventListener.onShutterPressed();
        }
    }
}
