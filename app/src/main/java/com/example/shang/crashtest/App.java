package com.example.shang.crashtest;

import android.app.Application;

/**
 * Created by shang on 2017/8/16.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
