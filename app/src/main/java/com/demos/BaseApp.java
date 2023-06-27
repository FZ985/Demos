package com.demos;

import android.app.Application;

/**
 * author : JFZ
 * date : 2023/6/27 08:57
 * description :
 */
public class BaseApp extends Application {

    private static BaseApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static BaseApp getInstance() {
        return app;
    }
}
