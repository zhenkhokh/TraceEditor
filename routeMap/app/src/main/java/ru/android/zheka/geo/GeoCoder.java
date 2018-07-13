package ru.android.zheka.geo;

import com.google.android.gms.maps.model.LatLng;

public interface GeoCoder {
    String [] getAdresses();
    LatLng [] getPoints();
    int getKey(String address);
}
