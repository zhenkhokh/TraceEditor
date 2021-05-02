package ru.android.zheka.geo

import com.google.android.gms.maps.model.LatLng

interface GeoCoder {
    val adresses: Array<String>
    val points: Array<LatLng>
    fun getKey(address: String): Int
}