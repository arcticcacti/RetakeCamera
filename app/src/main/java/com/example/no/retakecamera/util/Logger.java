package com.example.no.retakecamera.util;

/**
 * Created by Lee Holmes on 21/07/2016.
 * <p/>
 * Wrapper interface for Android's static Log calls, so an injectable component can be used instead.
 * <p/>
 * This allows the app to use the framework's logging system, while decoupling from it so unit tests
 * can be run easily.
 */
public interface Logger {

    int v(String tag, String msg);

    int v(String tag, String msg, Throwable tr);

    int d(String tag, String msg);

    int d(String tag, String msg, Throwable tr);

    int i(String tag, String msg);

    int i(String tag, String msg, Throwable tr);

    int w(String tag, String msg);

    int w(String tag, String msg, Throwable tr);

    int w(String tag, Throwable tr);

    int e(String tag, String msg);

    int e(String tag, String msg, Throwable tr);

    int wtf(String tag, String msg);

    int wtf(String tag, Throwable tr);

    int wtf(String tag, String msg, Throwable tr);

    String getStackTraceString(Throwable tr);

    int println(int priority, String tag, String msg);
}
