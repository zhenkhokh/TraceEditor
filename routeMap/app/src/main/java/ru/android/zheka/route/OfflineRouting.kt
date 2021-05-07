package ru.android.zheka.route

import com.google.android.gms.maps.model.LatLng
import java.util.*

class OfflineRouting(vararg aPoints: LatLng) : Routing() {
    var aPoints: Array<out LatLng>

    //ignore input
    public override fun doInBackground(vararg aPoints: LatLng): Route? {
        val route = Route()
        route.addPoints(Arrays.asList(*this.aPoints))
        val len = (BellmannFord.getPathLength(this.aPoints) * 1000.0).toInt() //TODO get from db
        route.length = len
        val segment = Segment()
        segment.length = len
        route.addSegment(segment)
        return route
    }

    init {
        this.aPoints = aPoints
    }
}