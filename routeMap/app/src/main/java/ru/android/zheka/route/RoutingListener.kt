package ru.android.zheka.route

interface RoutingListener {
    fun onRoutingFailure()
    fun onRoutingStart()
    fun onRoutingSuccess(route: Route)
}