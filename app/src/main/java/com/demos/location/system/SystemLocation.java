package com.demos.location.system;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.demos.location.LocationData;
import com.demos.location.LocationInfo;
import com.demos.location.LocationService;
import com.demos.location.LocationSp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * by JFZ
 * 2024/7/17
 * desc：由 LocationServiceManager 创建，外部不创建
 **/
public final class SystemLocation implements LocationService {

    private Context mContext;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private OnLocationListener listener;

    private LocationManager locationManager;

    private boolean isLocationSuccess;

    private final long duration = 5000;//每隔5秒获取一次定位


    private final LocationData locationData = new LocationData();

    private final LocationInfo info = new LocationInfo();


    private final LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            log("定位onStatusChanged:" + provider + "," + status);
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            log("定位onProviderEnabled:" + provider);
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(@NonNull String provider) {
            log("定位onProviderDisabled:" + provider);
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(@NonNull Location location) {
            isLocationSuccess = true;
            log("定位成功_lat:" + location.getLatitude() + ",lon:" + location.getLongitude() + ",acc:" + location.getAccuracy() + ",alt:" + location.getAltitude());

            locationData.setLocation(location);

            info.setLatitude(location.getLatitude());
            info.setLongitude(location.getLongitude());
            info.setAccuracy(location.getAccuracy());
            info.setAltitude(location.getAltitude());

            Address address = getAddress(mContext, location);
            if (address != null) {
//                log("address:" + address.toString());
//                log(address.getCountryName());
//                log(address.getAdminArea());
//                log(address.getLocality());
//                log(address.getSubLocality());
//                log(address.getFeatureName());
//                log(address.getThoroughfare());
//                log(address.getSubThoroughfare());
//                log("line:" + address.getAddressLine(0));

                info.setCountry(address.getCountryName());
                info.setProvince(address.getAdminArea());
                info.setCity(address.getLocality());
                info.setDistrict(address.getSubLocality());
                info.setStreet(address.getThoroughfare());
                info.setStreetNum(address.getSubThoroughfare());
                info.setAddress(address.getFeatureName());

                LocationSp.get().putLocationInfo(info);
            }

            locationData.setInfo(info);

            if (listener != null) {
                listener.onLocation(locationData);
            }

//            handler.removeCallbacks(locRun);
//            handler.postDelayed(locRun, duration);
        }
    };


    private Runnable tryLocationRun = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            if (!isLocationSuccess) {
                log("获取定位失败,尝试重新定位");
                startLocation(mContext, listener);
            }
        }
    };

    private Runnable locRun = () -> {
        startLocation(mContext, listener);
    };


    //根据经纬度获取 省市区信息
    private Address getAddress(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public LocationService startLocation(Context context, OnLocationListener listener) {
        if (mContext == null) {
            mContext = context;
        }
        //监视地理位置变化
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.listener = listener;
            handler.postDelayed(() -> {
                try {
                    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    String bestProvider = getBestProvider(locationManager);
                    locationManager.removeUpdates(locationListener);

                    if (TextUtils.isEmpty(bestProvider)) {
                        bestProvider = getProvider(locationManager);
                    }
                    log("locationProvider:" + bestProvider + ",isLocationSuccess:" + isLocationSuccess);
                    locationManager.requestLocationUpdates(bestProvider, duration, 0f, locationListener);
                    handler.postDelayed(tryLocationRun, 4000);

                } catch (Exception e) {
                    log("location_catch:" + e.getMessage());
                    handler.postDelayed(tryLocationRun, 3000);
                }
            }, 500);
        }
        return this;
    }


    private String getBestProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();

        //指示提供商是否必须提供高度信息。并非所有修复都保证包含此类信息。
        criteria.setAltitudeRequired(false);

        //表明提供商是否被允许承担金钱成本。
        criteria.setCostAllowed(false);

        //指示提供商是否必须提供方位信息。并非所有定位都保证包含此类信息。
        criteria.setBearingRequired(false);

        //指示提供商是否必须提供速度信息。并非所有修复都保证包含此类信息。
        criteria.setSpeedRequired(false);

        //指示所需的纬度和经度精度。精度可能是 ACCURACY_FINE 或 ACCURACY_COARSE。更精确的位置可能会消耗更多电量，并且可能需要更长时间。
        //ACCURACY_FINE : 表示更精细的定位精度要求的常数
        //ACCURACY_COARSE : 表示近似精度要求的常数
        //ACCURACY_LOW : 表示低位置精度要求的常数 - 可用于水平、高度、速度或方位精度。对于水平和垂直位置，这大致相当于大于 500 米的精度。
        //ACCURACY_MEDIUM : 表示中等精度要求的常数 - 目前仅用于水平精度。对于水平位置，这大致相当于 100 到 500 米之间的精度。
        //ACCURACY_HIGH : 表示高精度要求的常数 - 可用于水平、高度、速度或方位精度。对于水平和垂直位置，这大致相当于小于 100 米的精度。
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //指示所需的最大功率要求。功率要求参数可能是 NO_REQUIREMENT、POWER_LOW、POWER_MEDIUM 或 POWER_HIGH。
        //NO_REQUIREMENT : 一个常数，表示应用程序不选择对某个特定功能提出要求。
        //POWER_HIGH : 表示高功率需求的常数。
        //POWER_MEDIUM : 表示中等功率需求的常数。
        //POWER_LOW : 表示低功率需求的常数。
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        try {
            String bestProvider = locationManager.getBestProvider(criteria, false);
            if (bestProvider != null && !bestProvider.isEmpty())
                return bestProvider;
        } catch (Exception e) {
        }
        return "";
    }

    private String getProvider(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(false);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            return LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            return LocationManager.NETWORK_PROVIDER;
        } else {
            return LocationManager.NETWORK_PROVIDER;
        }
    }

    @Override
    public void stopLocation(Context context) {
        handler.removeCallbacks(tryLocationRun);
        handler.removeCallbacks(locRun);
        handler.removeCallbacksAndMessages(null);
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        locationManager = null;
        listener = null;
        isLocationSuccess = false;

        locRun = null;
        tryLocationRun = null;
    }


    private void log(String m) {
        Log.e("SystemLocation", m);
    }
}
