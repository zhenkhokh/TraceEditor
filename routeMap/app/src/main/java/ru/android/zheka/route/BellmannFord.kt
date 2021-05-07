package ru.android.zheka.route

import com.google.android.gms.maps.model.LatLng
import java.util.*

object BellmannFord {
    const val toRad = Math.PI / 180
    const val R = 6371.0 //km

    // !!! use process once
    @JvmField
    var length = 0.0
    @JvmField
    var bearing = -1000f
    var longitude1 = 0.0
    var longitude = 0.0
    var latitude1 = 0.0
    var latitude = 0.0
    var dr = 0.0
    var dr_tay = 0.0
    private const val eps = 1.0E3
    @JvmStatic
    fun process(latLngs: Array<LatLng>): Array<LatLng> {
        var order = fliplr(latLngs)
        order = getOrder(order)
        order = fliplr(order)
        order = getOrder(order)
        length = getPathLength(order)
        println("BellmanFord opt length = $length")
        return order
    }

    private fun getOrder(latLngs: Array<LatLng>): Array<LatLng> {
        val n = latLngs.size
        var whileTrue = true
        var optLen = getPathLength(latLngs)
        while (whileTrue) {
            whileTrue = false
            for (i in 1 until n - 1)  // do not touch end point
                for (j in i + 1 until n - 1) {
                    var tmp = latLngs[j]
                    latLngs[j] = latLngs[i]
                    latLngs[i] = tmp
                    val curLen = getPathLength(latLngs)
                    if (curLen < optLen) {
                        optLen = curLen
                        whileTrue = true
                        println("new opt: optLen=$optLen")
                    } else {
                        tmp = latLngs[j]
                        latLngs[j] = latLngs[i]
                        latLngs[i] = tmp
                    }
                }
        }
        return latLngs
    }

    fun getPathLength(latLngs: Array<out LatLng>): Double {
        val n = latLngs.size
        var pathLen = 0.0
        if (n == 0) return pathLen
        var longitude = latLngs[0].longitude * toRad
        var latitude = latLngs[0].latitude * toRad
        for (j in 1 until n) {
            val latitude1 = latLngs[j].latitude * toRad
            val longitude1 = latLngs[j].longitude * toRad
            val dLon = -(longitude1 - longitude)
            val dLamda = latitude1 - latitude
            val cosArg = Math.cos(dLamda) + ((Math.cos(dLon) - 1.0)
                    * Math.cos(latitude) * Math.cos(latitude1))
            latitude = latitude1
            longitude = longitude1
            var arg = Math.sqrt(1 - cosArg * cosArg) / cosArg
            // atg Tailor for small arg
            arg = arg - 1.0 / 3 * arg * arg * arg
            pathLen += R * Math.abs(arg)
        }
        return pathLen
    }

    private fun fliplr(points: Array<LatLng>): Array<LatLng> {
        val n = points.size
        val inv = Array(n){i-> points[n - i - 1]}
        return inv
    }

    @Throws(NoDirectionException::class, MissMatchDataException::class)
    fun getBearing(points: Array<LatLng>): Float {
        if (points.size < 2) throw MissMatchDataException()
        val point = round(points[0])
        var point1 = round(points[1])
        if (point == point1) {
            var i = 2
            while (point == point1 && i < points.size) {
                point1 = round(points[i++])
            }
            if (point == point1) throw NoDirectionException()
        }
        longitude1 = point1.longitude * toRad
        longitude = point.longitude * toRad
        latitude1 = point1.latitude * toRad
        latitude = point.latitude * toRad
        val dLon = longitude1 - longitude
        val dLambda = latitude1 - latitude
        val dr2 = 2.0 * (1 + (1.0 - Math.cos(dLon)) * Math.cos(latitude1) * Math.cos(latitude)
                - Math.cos(dLambda))
        dr = Math.sqrt(dr2)
        dr_tay = Math.sin(dLambda) - (1 - Math.cos(dLon)) * Math.cos(latitude1) * Math.cos(latitude)
        //if (dr==0)
        //    throw  new NoDirectionException ();
        bearing = (Math.acos(dr_tay / dr) / toRad).toFloat() // 2 roots
        if (dLon < 0) bearing = 360.0.toFloat() - bearing
        println("bearing=" + bearing + " points=" + Arrays.asList(*points))
        return bearing
    }

    fun round(point: LatLng): LatLng {
        val lat = Math.round(point.latitude * eps) / eps
        val lon = Math.round(point.longitude * eps) / eps
        return LatLng(lat, lon)
    }

    class MissMatchDataException : Exception()
    class NoDirectionException : Exception()
}