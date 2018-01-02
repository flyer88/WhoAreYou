package io.dove.whoareyou;

import android.app.Application;

/**
 * Created by flyer on 28/12/2017.
 */

public class Dove {

    private DoveMonitor mMonitor;

    private Application mApplication;
    private static Dove sDove;

    public static Dove getInstance(Application application){
        if (sDove == null) {
            sDove = new Dove(application);
        }
        return sDove;
    }
    private Dove(Application application){
        this.mApplication = application;
        if (this.mMonitor == null) {
            this.mMonitor = new DoveMonitor(application.getApplicationContext());
        }
    }

    public DoveMonitor getMonitor(){
        return mMonitor;
    }

    public void init(){
        sDove.mApplication.registerActivityLifecycleCallbacks(mMonitor);
    }
}
