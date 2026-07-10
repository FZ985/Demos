package com.demos.location;

import android.content.Context;

/**
 * by JFZ
 * 2024/7/17
 * desc：定位服务，系统定位、高德定位基于此来封装
 **/
public interface LocationService {


    LocationService startLocation(Context context, OnLocationListener listener);

    void stopLocation(Context context);

    interface OnLocationListener {

        void onLocation(LocationData data);
    }

}
