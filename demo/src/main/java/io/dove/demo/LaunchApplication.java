package io.dove.demo;

import android.app.Application;

import io.dove.whoareyou.Dove;

/**
 * Created by flyer on 28/12/2017.
 */

public class LaunchApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Dove.getInstance(this).init();
    }
}
