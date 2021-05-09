package ru.android.zheka.db

import com.activeandroid.serializer.TypeSerializer
import com.google.android.gms.maps.model.LatLng

class UtilePointSerializer : TypeSerializer() {
    override fun getDeserializedType(): Class<*> {
        return LatLng::class.java
    }

    override fun getSerializedType(): Class<*> {
        return String::class.java
    }

    override fun serialize(data: Any): Any {
        val latitude = (data as LatLng).latitude
        val longitude = data.longitude
        val out = StringBuilder()
        out.append(latitude.toString()).append(pDelimiter).append(longitude.toString())
        return out.toString()
    }

    override fun deserialize(data: Any): Any {
        val `in` = data as String
        val s = `in`.split(pDelimiter).toTypedArray()
        val latitude: Double = s[0].toDouble()
        val longitude: Double = s[1].toDouble()
        return LatLng(latitude, longitude)
    }

    companion object {
        const val pDelimiter = ","
    }
}