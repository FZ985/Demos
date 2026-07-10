package com.demos.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.demos.BaseApp;
import com.demos.utils.BaseSharedPreferences;


/**
 * author: jfz
 * date: 2023/5/18.10:31
 **/
public class LocationSp extends BaseSharedPreferences {

    private static LocationSp locationSp;

    private LocationSp() {
    }

    public static LocationSp get() {
        if (locationSp == null) {
            synchronized (LocationSp.class) {
                if (locationSp == null) {
                    locationSp = new LocationSp();
                }
            }
        }
        return locationSp;
    }

    private final String location_lat = "location_lat";
    private final String location_lng = "location_lng";

    private final String location_info = "location_info";//高德地图完整信息


    public String getLat() {
        return getString(location_lat, "");
    }

    public String getLng() {
        return getString(location_lng, "");
    }

    public boolean hasLatlng() {
        String lat = getLat();
        String lng = getLng();
        return !TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng);
    }

    public void putLocationInfo(@NonNull LocationInfo info) {
        applyString(location_info, info.toJson());
        applyString(location_lat, String.valueOf(info.getLatitude()));
        applyString(location_lng, String.valueOf(info.getLongitude()));
    }

    public LocationInfo getLocation() {
        String json = getString(location_info, "");
        try {
            LocationInfo info = LocationInfo.fromJson(json);
            if (info != null) {
                return info;
            }
        } catch (Exception e) {
            //
        }
        return LocationInfo.defaultInfo();
    }

    @Override
    public SharedPreferences getSharedPreference() {
        return BaseApp.getInstance().getSharedPreferences("location", Context.MODE_PRIVATE);
    }
}
