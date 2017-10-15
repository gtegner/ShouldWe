package com.example.gustaftegner.shouldwetest;


import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by gustaftegner on 15/12/15.
 */
public class Utils {


    public static String locationToString(Location loc){
        String lat = String.valueOf(loc.getLatitude());
        String lon = String.valueOf(loc.getLongitude());

        return lat+","+lon;
    }

    public static LatLng locationToLatLng(Location loc){
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

}
