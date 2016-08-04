package com.github.arcticcacti.retakecamera;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.github.arcticcacti.retakecamera.camera.Camera1;
import com.github.arcticcacti.retakecamera.camera.CameraProvider;
import com.github.arcticcacti.retakecamera.camera.CameraProviderImpl;
import com.github.arcticcacti.retakecamera.camera.CameraSystem;
import com.github.arcticcacti.retakecamera.permissions.PermissionsManager;
import com.github.arcticcacti.retakecamera.permissions.PermissionsManagerImpl;
import com.github.arcticcacti.retakecamera.util.Logger;
import com.github.arcticcacti.retakecamera.util.LoggerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * General module class for dependencies.
 */
@Module
class RetakeModule {

    private final Application application;


    RetakeModule(Application application) {
        this.application = application;
    }


    @Provides
    @Singleton
    PermissionsManager providesPermissionsManager(Context context) {
        return new PermissionsManagerImpl(context);
    }


    @Provides
    CameraPresenter providesCameraPresenter(CameraSystem cameraSystem) {
        return new CameraPresenterImpl(cameraSystem);
    }


    @Provides
    CameraSystem providesCameraSystem(CameraProvider cameraProvider) {
        if (Build.VERSION.SDK_INT < RetakeApplication.CAMERA_2_MIN_API) {
            return new Camera1(cameraProvider);
        } else {
            // TODO: 08/07/2016 implement and return a Camera2 system
            return new Camera1(cameraProvider);
        }
    }


    @Provides
    @Singleton
    CameraProvider providesCameraProvider(@NonNull Context context) {
        return new CameraProviderImpl(context);
    }


    @Provides
    @Singleton
    ImageProcessor providesImageProcessor() {
        return new ImageProcessorImpl();
    }


    @Provides
    @Singleton
    PhotoStorage providesPhotoStorage(Context context) {
        return new PhotoStorageImpl(context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
    }


    @Provides
    @Singleton
    Logger providesLogger() {
        return new LoggerImpl();
    }


    @Provides
    @Singleton
    Context providesApplicationContext() {
        return application.getApplicationContext();
    }

}
