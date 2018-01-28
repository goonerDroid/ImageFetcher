package com.sublime.imagefetcher.app;

import android.app.Application;

import com.sublime.imagefetcher.BuildConfig;
import com.sublime.imagefetcher.utils.Timber;

/**
 * Created by goonerdroid
 * on 28/1/18.
 */

public class ImageFetcher extends Application {

    private static ImageFetcher appInstance;

    @Override public void onCreate() {
        super.onCreate();
        appInstance = this;
        //Initializes Timber logging only on debug build :-)
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }


    public static ImageFetcher getAppInstance(){
        return appInstance;
    }
}
