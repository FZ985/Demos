package com.demos.location;

import android.location.Location;

import java.io.Serializable;

/**
 * by JFZ
 * 2024/7/17
 * desc：
 **/
public class LocationData implements Serializable {


    private Location location;

    private LocationInfo info;

    public LocationData() {
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setInfo(LocationInfo info) {
        this.info = info;
    }

    public Location getLocation() {
        return location;
    }

    public LocationInfo getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "location=" + ((location == null) ? "null" : location.toString()) +
                ",\n\n info=" + ((info == null) ? "null" : info.toString()) +
                '}';
    }
}
