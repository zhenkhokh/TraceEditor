package ru.android.zheka.route

import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.gmapexample1.Application
import ru.android.zheka.gmapexample1.MainActivity
import java.util.*

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay
 *
 */
open class Routing : AsyncTask<LatLng, Void, Route>() {
    protected var _aListeners: ArrayList<RoutingListener>
    protected var _mTravelMode: TravelMode

    enum class TravelMode(var value: String) {
        BIKING("biking"), DRIVING("driving"), WALKING("walking"), TRANSIT("transit");

    }

    fun registerListener(mListener: RoutingListener) {
        _aListeners.add(mListener)
    }

    protected fun dispatchOnStart() {
        for (mListener in _aListeners) {
            mListener.onRoutingStart()
        }
    }

    protected fun dispatchOnFailure() {
        for (mListener in _aListeners) {
            mListener.onRoutingFailure()
        }
    }

    protected fun dispatchOnSuccess(route: Route) {
        for (mListener in _aListeners) {
            mListener.onRoutingSuccess(route)
        }
    }

    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @param aPoints
     * @return
     */
    protected override fun doInBackground(vararg aPoints: LatLng): Route? {
        for (mPoint in aPoints) {
            if (mPoint == null) return null
        }
        return GoogleParser(constructURL(*aPoints)).parse()
    }

    protected fun constructURL(vararg points: LatLng): String {
        val start = points[0]
        val dest = points[1]
        val sJsonURL = "https://maps.googleapis.com/maps/api/directions/json?"
        val mBuf = StringBuffer(sJsonURL)
        mBuf.append("origin=")
        mBuf.append(start.latitude)
        mBuf.append(',')
        mBuf.append(start.longitude)
        mBuf.append("&destination=")
        mBuf.append(dest.latitude)
        mBuf.append(',')
        mBuf.append(dest.longitude)
        mBuf.append("&sensor=true&mode=")
        mBuf.append(_mTravelMode.value)
        val config = getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        if (points.size > 2) {
            println("optimizationBellmanFlag= " + Application.optimizationBellmanFlag
                    + " points.length=" + points.size
                    + " config.bellmanFord=" + config.bellmanFord)
            mBuf.append("&waypoints=optimize:")
                    .append(config.optimization)
            val isBellman = config.bellmanFord == Application.optimizationBellmanFlag
            /*            if (isBellman) {
                points = BellmannFord.process(points);
            }
*/
//TODO make post request, not limit by 8 points in get-request
            for (i in 2 until points.size) {
                if (isBellman && i <= 4 || !isBellman) mBuf.append("|" + points[i].latitude + "," + points[i].longitude)
            }
        }
        val avoid = config.avoid?:""
        if (!avoid.isEmpty()) mBuf.append("&avoid=$avoid")
        mBuf.append("&key=").append(MainActivity.googleKey) //
        return mBuf.toString()
    }

    override fun onPreExecute() {
        dispatchOnStart()
    }

    override fun onPostExecute(result: Route?) {
        if (result == null) {
            dispatchOnFailure()
        } else {

            /*PolylineOptions mOptions = new PolylineOptions();

            for (LatLng point : result.getPoints()) {
                mOptions.add(point);
            }
*/
            dispatchOnSuccess(result)
        }
    } //end onPostExecute method

    init {
        _aListeners = ArrayList() //(Class<Routing.TravelMode>)clTravel
        val config = getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        val mTravelMode: TravelMode = TravelMode.valueOf(config.travelMode!!)
        _mTravelMode = mTravelMode
    }
}