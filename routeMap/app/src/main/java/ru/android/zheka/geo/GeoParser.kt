package ru.android.zheka.geo

abstract class GeoParser(var region: String, var city: String, var street: String, var house: String) {
    abstract fun parse(key: String): GeoCoder

}