package com.demos.location;


import android.content.Context;

import com.demos.location.system.SystemLocation;


/**
 * by JFZ
 * 2024/7/17
 * desc：定位管理
 **/
public class LocationServiceManager {

    private static LocationServiceManager impl;

    private static final Object lock = new Object();

    private LocationService location;

    private int currentLocationCount = 0;

    private LocationServiceManager() {

    }

    public static LocationServiceManager get() {
        if (impl == null) {
            synchronized (lock) {
                if (impl == null) {
                    impl = new LocationServiceManager();
                }
            }
        }
        return impl;
    }

    public LocationServiceManager startLocation(Context context, LocationService.OnLocationListener listener) {
        return startLocation(context, -1, listener);
    }

    /**
     * 开始定位
     *
     * @param context  上下文
     * @param count    定位次数，小于0没有上限次数
     * @param listener 定位回调
     * @return 当前管理对象
     */
    public LocationServiceManager startLocation(Context context, int count, LocationService.OnLocationListener listener) {
        initLocation();
        location.startLocation(context, data -> {
            if (listener != null) {
                if (count <= 0) {//没有次数限制
                    currentLocationCount = 0;
                    listener.onLocation(data);
                } else {
                    currentLocationCount++;
                    listener.onLocation(data);
                    if (currentLocationCount >= count) {
                        stopLocation(context);
                    }
                }
            }
        });
        return this;
    }

    /**
     * 关闭定位
     *
     * @param context 上下文
     */
    public void stopLocation(Context context) {
        if (location != null) {
            location.stopLocation(context);
        }
        location = null;
        currentLocationCount = 0;
    }

    private void initLocation() {
        if (location == null) {
            location = new SystemLocation();
        }
    }
}
