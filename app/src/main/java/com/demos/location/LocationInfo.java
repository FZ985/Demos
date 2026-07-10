package com.demos.location;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * author : JFZ
 * date : 2023/7/13 10:25
 * description :
 */
public class LocationInfo implements Serializable {

    private double latitude = -1;//纬度
    private double longitude = -1;//经度
    private double altitude = -1;//海拔
    private float accuracy = -1;//精度
    private String country;//国家
    private String province;//省
    private String city;//市
    private String district;//区
    private String street;//街道
    private String streetNum;//街道门号
    private String poiName;//名称
    private String address;//地址


    public LocationInfo() {
    }

    public LocationInfo(double latitude, double longitude, double altitude,
                        float accuracy, String country, String province, String city,
                        String district, String street,
                        String streetNum, String poiName, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
        this.streetNum = streetNum;
        this.poiName = poiName;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getProvince() {
        return compat(province);
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return compat(city);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return compat(district);
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return compat(street);
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return compat(country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetNum() {
        return compat(streetNum);
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public String getPoiName() {
        return compat(poiName);
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getAddress() {
        return compat(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static LocationInfo defaultInfo() {
        return new LocationInfo();
    }

    //省市区
    public String getThirdAddress() {
        return getProvince() + "/" + getCity() + "/" + getDistrict();
    }

    //街道 和 街道号
    public String getStreetAndStreetNum() {
        return getStreet() + getStreetNum();
    }


    private String compat(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    public String toJson() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("latitude", getLatitude());
        map.put("longitude", getLongitude());
        map.put("altitude", getAltitude());
        map.put("accuracy", getAccuracy());
        map.put("country", getCountry());
        map.put("province", getProvince());
        map.put("city", getCity());
        map.put("district", getDistrict());
        map.put("street", getStreet());
        map.put("streetNum", getStreetNum());
        map.put("poiName", getPoiName());
        map.put("address", getAddress());
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    @Nullable
    public static LocationInfo fromJson(@NonNull String json) {
        try {
            JSONObject obj = new JSONObject(json);
            LocationInfo info = new LocationInfo();
            info.setLatitude(obj.optDouble("latitude", -1));
            info.setLongitude(obj.optDouble("longitude", -1));
            info.setAltitude(obj.optDouble("altitude", -1));
            info.setAccuracy((float) obj.optDouble("accuracy", -1));
            info.setCountry(obj.optString("country"));
            info.setProvince(obj.optString("province"));
            info.setCity(obj.optString("city"));
            info.setDistrict(obj.optString("district"));
            info.setStreet(obj.optString("street"));
            info.setStreetNum(obj.optString("streetNum"));
            info.setPoiName(obj.optString("poiName"));
            info.setAddress(obj.optString("address"));
            return info;
        } catch (JSONException e) {
            //
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return toJson();
    }
}
