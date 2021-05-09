package ru.android.zheka.db

import com.activeandroid.serializer.TypeSerializer
import com.google.android.gms.maps.model.LatLng

class UtileTracePointsSerializer : TypeSerializer() {
    var dataTrace: DataTrace? = null
    override fun getDeserializedType(): Class<*> {
        return DataTrace::class.java
        //return PolylineOptions.class;
    }

    override fun getSerializedType(): Class<*> {
        return String::class.java
    }

    override fun serialize(data: Any): Any {
        dataTrace = data as DataTrace
        val sb = StringBuilder()
        val utilePointSerializer = UtilePointSerializer()
        for (point in data.allPoints.points) {
            sb.append(utilePointSerializer.serialize(point))
                    .append(tDelimiter)
        }
        if (sb.length >= 1) //?
            sb.deleteCharAt(sb.length - 1)
        sb.append(eDelimiter)
        for (point in data.extraPoints!!) {
            sb.append(point)
                    .append(tDelimiter)
        }
        if (sb.length >= 1) //?
            sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }

    override fun deserialize(data: Any): Any {
        val `in` = data as String
        val allAndExtra = `in`.split(eDelimiter).toTypedArray()
        val points = allAndExtra[0].split(tDelimiter).toTypedArray()
        val ePoints = allAndExtra[1].split(tDelimiter).toTypedArray()
        //PolylineOptions out = new PolylineOptions();
        val out = DataTrace()
        for (i in points.indices) {
            val s: Array<String> = points[i].split(UtilePointSerializer.Companion.pDelimiter).toTypedArray()
            val latitude: Double = s[0].toDouble()
            val longitude: Double = s[1].toDouble()
            out.allPoints.add(LatLng(latitude, longitude))
        }
        for (i in ePoints.indices) {
            out.extraPoints!!.add(ePoints[i])
        }
        return out
    }

    companion object {
        const val tDelimiter = ";"
        const val eDelimiter = "_"
    }
}