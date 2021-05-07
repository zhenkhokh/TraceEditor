package ru.android.zheka.route

import com.google.android.gms.maps.model.LatLng
import java.util.*

class Route {
    /**
     * @return the name
     */
    /**
     * @param name the name to set
     */
    var name: String? = null
    val points: MutableList<LatLng>
    val segments: MutableList<Segment>
    /**
     * @return the copyright
     */
    /**
     * @param copyright the copyright to set
     */
    var copyright: String? = null
    /**
     * @return the warning
     */
    /**
     * @param warning the warning to set
     */
    var warning: String? = null
    /**
     * @return the country
     */
    /**
     * @param country the country to set
     */
    var country: String? = null
    /**
     * @return the length
     */
    /**
     * @param length the length to set
     */
    var length = 0
    /**
     * @return the polyline
     */
    /**
     * @param polyline the polyline to set
     */
    var polyline: String? = null

    /**
     * TravelSales optimization order
     */
    var order = ArrayList<Int>()
    fun addPoint(p: LatLng) {
        points.add(p)
    }

    fun addPoints(points: List<LatLng>) {
        this.points.addAll(points)
    }

    fun addSegment(s: Segment) {
        segments.add(s)
    }

    init {
        points = ArrayList()
        segments = ArrayList()
    }
}