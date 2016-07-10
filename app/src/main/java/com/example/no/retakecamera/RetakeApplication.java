package com.example.no.retakecamera;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * Created by Lee Holmes on 07/07/2016.
 * <p/>
 * Custom Application class to allow access to the explicit injector.
 */
public class RetakeApplication extends Application {

    public static final int CAMERA_2_MIN_API = android.os.Build.VERSION_CODES.LOLLIPOP;

    private Injector injector;


    /**
     * Get the Injector instance, e.g. for Activity injection.
     *
     * @return The app's DI injector
     */
    @NonNull
    public Injector getInjector() {
        return injector;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        injector = DaggerInjector.builder()
                .retakeModule(new RetakeModule(this))
                .build();
    }


}
