package ru.android.zheka.gmapexample1

import com.google.android.gms.maps.GoogleMap

class MapTypeHandler(code: Int) {
    enum class Type(val code: String) {
        NONE(GoogleMap.MAP_TYPE_NONE.toString()), NORMAL(GoogleMap.MAP_TYPE_NORMAL.toString()), SATELLITE(GoogleMap.MAP_TYPE_SATELLITE.toString()), TERRAIN(GoogleMap.MAP_TYPE_TERRAIN.toString()), HYBRID(GoogleMap.MAP_TYPE_HYBRID.toString());

    }

    var type: Type? = null
    val code: Int
        get() = Integer.valueOf(type!!.code)

    companion object {
        var userCode = GoogleMap.MAP_TYPE_NORMAL // do not use NONE
    }

    init {
        //type = Enum.valueOf (Type.class,String.valueOf (code));
        type = when (code) {
            GoogleMap.MAP_TYPE_NONE -> {
                Type.NONE
            }
            GoogleMap.MAP_TYPE_NORMAL -> {
                Type.NORMAL
            }
            GoogleMap.MAP_TYPE_SATELLITE -> {
                Type.SATELLITE
            }
            GoogleMap.MAP_TYPE_TERRAIN -> {
                Type.TERRAIN
            }
            GoogleMap.MAP_TYPE_HYBRID -> {
                Type.HYBRID
            }
            else -> {
                throw IllegalStateException("undefined type of map")
            }
        }
    }
}