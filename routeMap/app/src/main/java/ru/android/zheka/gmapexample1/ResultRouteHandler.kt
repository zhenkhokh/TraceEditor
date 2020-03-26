package ru.android.zheka.gmapexample1

import ru.android.zheka.route.Route
import java.util.*

class ResultRouteHandler(routeSize: Int) {
    var routes: MutableList<Route> = ArrayList() // no need synch it is done by minitor
    var routeSize = -1 //extraPointsNum + 1
    fun addRouteIgnoreNull(route: Route?) {
        if (route != null) routes.add(route)
    }

    fun addAllRoutesIgnoreNull(routes: List<Route>?) {
        if (routes != null) this.routes.addAll(routes)
    }

    val isAvailable: Boolean
        get() = if (routeSize == routes.size) true else false

    val length: Int
        get() {
            var length = 0
            for (route in routes) {
                for (segment in route.segments) {
                    length += segment.length
                }
            }
            return length
        }

    init {
        this.routeSize = routeSize
    }
}