package com.example.no.retakecamera;

import com.example.no.retakecamera.ui.CameraActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Dagger injector class for constructed objects, e.g. Activities
 */
@Singleton
@Component(modules = RetakeModule.class)
public interface Injector {

    void inject(CameraActivity cameraActivity);

}
