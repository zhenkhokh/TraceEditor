package ru.zheka.android.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.gmapexample1.MapsActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.PositionUtil
import ru.android.zheka.route.BellmannFord

class PositionReciever(var mMap: GoogleMap?, var position: PositionInterceptor) : BroadcastReceiver(), Recievable {
    var config: Config
    var geoMode = false
    var mapsActivity: MapsActivity? = null

    constructor(mMap: GoogleMap?, position: PositionInterceptor, mapsActivity: MapsActivity?) : this(mMap, position) {
        geoMode = true
        this.mapsActivity = mapsActivity
    }

    override fun onReceive(arg0: Context, intent: Intent) {
        println("recieve location: " + position.location)
        position.target?.runOnUiThread(object : Runnable {
            var location: LatLng? = null
            override fun run() {
                if (location == null) {
                    location = position.location //util.getCenter();
                }
                if (location == PositionUtil.LAT_LNG) {
                    position.initLocation()
                    location = position.location
                }
                //position.start = location;
                position.centerPosition = location
                position.target?.intent = position.newIntent //order is important
                //synchronized (PositionReciever.this.position){
                //LatLng end = position.end;
                position.updatePosition() //for manual saving
                //position.end = end;//buggy end
                //}
                /*if (location!=null && position.extraPoints.size ()>0)
					position.extraPoints.set (0,(String) new UtilePointSerializer ().serialize (location));
				*/if (config.uLocation!!) position.updateUILocation()
                println("recieve location: $location")
                val zoom = position.zoom
                if (mMap != null) {
                    var bearing = 0f
                    if (geoMode) {
                        mapsActivity!!.goPosition(true)
                        if (!BellmannFord.bearing.isNaN()) bearing = BellmannFord.bearing
                    }
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                    .target(location)
                                    .bearing(bearing)
                                    .zoom(zoom)
                                    .build()
                    ))
                }
            }
        })
    }

    init {
        config = getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        //this.position.state = PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND;
    }
}