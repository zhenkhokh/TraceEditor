package ru.android.zheka.gmapexample1

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.db.UtilePointSerializer
import java.util.*

class PositionUtil {
    var center: LatLng? = null
    var start: LatLng? = null
    var end: LatLng? = null
    var zoom = 0f
    var startPass = false
    var endPass = false
    var centerPass = false

    //TODO add title to Uri
    var titleMarker: String? = null
    var extraPoints: ArrayList<String> = ArrayList()

    enum class TRACE_PLOT_STATE {
        END_COMMAND, START_COMMAND, CONNECT_COMMAND, CENTER_COMMAND, CENTER_END_COMMAND, CENTER_CONNECT_COMMAND, CENTER_START_COMMAND, DONOTHING_COMMAND
    }
    var intent: Intent? = null
        get() {
            val intent_ = Intent()
            var uri: Uri?
            if (center != null && zoom > 1) {
                uri = if (titleMarker != null) {
                    Uri.parse(getGeoPositionTruePass(center!!, zoom.toString(), titleMarker!!))
                } else Uri.parse(getGeoPositionTruePass(center!!, zoom.toString()))
                try {
                    uri = setUpPassParam(uri, centerPass)
                } catch (e: Exception) {
                    println("failure")
                }
                intent_.data = uri
            }
            if (start != null) {
                uri = Uri.parse(getTracePointTruePass(start!!, "start"))
                try {
                    uri = setUpPassParam(uri, startPass)
                } catch (e: Exception) {
                }
                intent_.putExtra("start", uri)
            }
            if (end != null) {
                uri = Uri.parse(getTracePointTruePass(end!!, "end"))
                try {
                    uri = setUpPassParam(uri, endPass)
                } catch (e: Exception) {
                }
                intent_.putExtra("end", uri)
            }
            if (!extraPoints.isEmpty()) {
                intent_.putStringArrayListExtra(EXTRA_POINTS, extraPoints)
            }
            if (titleMarker != null) intent_.putExtra(TITLE, titleMarker)
            return intent_
        }
        set(value) {
            field = value
        }

    fun setCommand(command: TRACE_PLOT_STATE?) {
        when (command) {
            TRACE_PLOT_STATE.START_COMMAND -> {
                startPass = true
                endPass = false
                centerPass = false
            }
            TRACE_PLOT_STATE.CONNECT_COMMAND -> {
                startPass = true
                endPass = true
                centerPass = false
            }
            TRACE_PLOT_STATE.CENTER_CONNECT_COMMAND -> {
                startPass = true
                endPass = false
                centerPass = true
            }
            TRACE_PLOT_STATE.CENTER_START_COMMAND -> {
                startPass = true
                endPass = true
                centerPass = true
            }
            TRACE_PLOT_STATE.END_COMMAND -> {
                startPass = false
                endPass = true
                centerPass = false
            }
            TRACE_PLOT_STATE.CENTER_END_COMMAND -> {
                startPass = false
                endPass = true
                centerPass = true
            }
            TRACE_PLOT_STATE.CENTER_COMMAND -> {
                startPass = false
                endPass = false
                centerPass = true
            }
            TRACE_PLOT_STATE.DONOTHING_COMMAND -> {
                startPass = false
                endPass = false
                centerPass = false
            }
        }
    }

    @Throws(Exception::class)
    fun setCenterPosition(intent: Intent) {
        val uri = intent.data
        var latitude: Double? = null
        var longitude: Double? = null
        val s = uri!!.schemeSpecificPart
                .split(",").toTypedArray()
        val queres = uri.query
        if (s.size < 2) throw ArrayIndexOutOfBoundsException()
        try {
            latitude = s[0].toDouble()
            longitude = s[1].replace(queres!!, "")
                    .replace("?", "").toDouble()
        } catch (e: NumberFormatException) {
            val msg = "geo" + " latitude is not float format"
            if (latitude == null) throw Exception(msg)
            if (longitude == null) throw Exception(msg)
        }
        try {
            val sZoom = getQueryParameter(uri, "z")
            zoom = sZoom?.toFloat()?:throw NumberFormatException()
        } catch (e: NumberFormatException) {
            zoom = zoomDefault
        }
        titleMarker = getQueryParameter(uri, TITLE)
        center = LatLng(latitude!!, longitude!!)
        if (intent.getStringArrayListExtra(EXTRA_POINTS) != null) extraPoints = intent.getStringArrayListExtra(EXTRA_POINTS)!!
    }

    @Throws(Exception::class)
    fun positionAndBoundInit(intent: Intent) {
        var uriCenter = intent.data
        if (uriCenter == null) {
            println("center is not defined")
            throw Exception("center is not defined")
        }
        var latitude: Double? = null
        var longitude: Double? = null

        var startUri: Uri? = null
        var endUri: Uri? = null
        // Uri extra data: start[end]:lat,lng?query
        val bound = arrayOf("start", "end", "geo")
        for (i in 0..2) {
            val name = bound[i]
            var uri: Uri? = null
            var ok = true
            if (i < 2) {
                if (intent.getParcelableExtra<Parcelable>(name) as Uri? != null) // TODO java.lang.ClassCastException: java.lang.String cannot be cast
                //to android.os.Parcelable
                    uri = intent.getParcelableExtra<Parcelable>(name) as Uri
                else ok = false
            } else if (intent.data != null) uri = intent.data else ok = false
            if (ok) {
                val s = uri!!.schemeSpecificPart
                        .split(",").toTypedArray()
                val queres = uri.query
                if (s.size < 2) throw ArrayIndexOutOfBoundsException()
                try {
                    latitude = s[0].toDouble()
                    longitude = s[1].replace(queres!!, "")
                            .replace("?", "").toDouble()
                } catch (e: NumberFormatException) {
                    if (latitude == null) throw Exception("$name latitude is not float format")
                    if (longitude == null) throw Exception("$name longitude is not float format")
                }
                if (i == 0) {
                    start = LatLng(latitude!!, longitude!!)
                    startUri = uri
                }
                if (i == 1) {
                    end = LatLng(latitude!!, longitude!!)
                    endUri = uri
                }
                if (i == 2) {
                    try {
                        val sZoom = getQueryParameter(uri, "z")
                        this.zoom = sZoom?.toFloat()?:throw NumberFormatException()
                    } catch (e: NumberFormatException) {
                        this.zoom = zoomDefault
                    }
                    titleMarker = try {
                        getQueryParameter(uri, TITLE)
                    } catch (e: Exception) {
                        titleDefault
                    }
                    center = LatLng(latitude!!, longitude!!)
                    uriCenter = uri
                }
            }
        }
        if (intent.getStringArrayListExtra(EXTRA_POINTS) != null) extraPoints = intent.getStringArrayListExtra(EXTRA_POINTS)!!
        centerPass = getPassParam(uriCenter)
        try {
            startPass = getPassParam(startUri)
        } catch (e: Exception) {
            startUri = uriCenter
            startPass = getPassParam(startUri)
        }
        try {
            endPass = getPassParam(endUri)
        } catch (e: Exception) { /*endUri = startUri; endPass = getPassParam(endUri);*/
            endPass = true
            end = if (extraPoints.size > 1) {
                UtilePointSerializer().deserialize(extraPoints[extraPoints.size - 1]) as LatLng
            } else {
                throw NullPointerException("end point is undefined")
            }
        }

        // construct trace with center=start case, for adding
        //TODO do not replace some points
        /*
		if (center!=null && isCenterAddedToTrace == false
				&& extraPoints.size () >0) {
			if (BellmannFord.round ((LatLng) new UtilePointSerializer ().deserialize (extraPoints.get(0))).equals (
					BellmannFord.round(center)
			)) 	extraPoints.set (0,(String)new UtilePointSerializer ().serialize (center));
			else
				extraPoints.add(0,(String)new UtilePointSerializer ().serialize (center));
			isCenterAddedToTrace = true;
		}else if (center!=null && isCenterAddedToTrace == true && extraPoints.size ()>0)
			extraPoints.set (0,(String)new UtilePointSerializer ().serialize (center));//start
		*/
    }

    fun defCommand(): TRACE_PLOT_STATE {
        if (startPass == true && endPass == false && centerPass == false) return TRACE_PLOT_STATE.START_COMMAND
        if (startPass == true && endPass == true && centerPass == false) return TRACE_PLOT_STATE.CONNECT_COMMAND
        if (startPass == true && endPass == false && centerPass == true) return TRACE_PLOT_STATE.CENTER_CONNECT_COMMAND
        if (startPass == true && endPass == true && centerPass == true) return TRACE_PLOT_STATE.CENTER_START_COMMAND
        if (startPass == false && endPass == true && centerPass == false) return TRACE_PLOT_STATE.END_COMMAND
        if (startPass == false && endPass == true && centerPass == true) return TRACE_PLOT_STATE.CENTER_END_COMMAND
        return if (startPass == false && endPass == false && centerPass == true) TRACE_PLOT_STATE.CENTER_COMMAND else TRACE_PLOT_STATE.DONOTHING_COMMAND
        // if all are false
    }

    companion object {
        const val EXTRA_POINTS = "extraPoints"
        const val TITLE = "title"
        const val zoomDefault = 10f
        const val titleDefault = ""
        val LAT_LNG = LatLng(55.9823964, 37.1690829)
        var isCenterAddedToTrace = false
        private fun getGeoPositionTruePass(point: LatLng, vararg zommAndTitle: String): String {
            val sb = StringBuilder()
            sb.append("geo:")
                    .append(point.latitude)
                    .append(",")
                    .append(point.longitude)
                    .append("?z=")
                    .append(zommAndTitle[0])
                    .append("?pass=true")
            if (zommAndTitle.size > 1) {
                sb.append("?").append(TITLE).append("=")
                        .append(zommAndTitle[1])
            }
            return sb.toString()
        }

        fun getTracePointTruePass(point: LatLng, name: String?): String {
            val sb = StringBuilder()
            return sb.append(name)
                    .append(":")
                    .append(point.latitude)
                    .append(",")
                    .append(point.longitude)
                    .append("?pass=true")
                    .toString()
        }

        private fun getQueryParameter(uri: Uri?, name: String): String? {
            val queres = uri!!.query
            if (queres!!.lastIndexOf("$name=") == -1) return null
            val start = queres.lastIndexOf("$name=") + name.length + 1
            val end = if (queres.indexOf("?", start) == -1) queres.length else queres.indexOf("?", start)
            return queres.substring(start, end)
        }

        @Throws(Exception::class)
        private fun getPassParam(uri: Uri?): Boolean {
            if (uri != null) {
                val passString = getQueryParameter(uri, "pass")
                if (passString != null) {
                    if (passString.equals("true", ignoreCase = true)) return true
                    if (passString.equals("false", ignoreCase = true)) return false
                }
            } else throw NullPointerException("its not possible")
            throw Exception("not expected uri: $uri")
        }

        @Throws(Exception::class)
        private fun setUpPassParam(uri: Uri?, pass: Boolean): Uri {
            if (uri != null) {
                var `val` = getQueryParameter(uri, "pass")
                var uriString = uri.toString()
                //add pass if not specified
                if (`val` == null) {
                    `val`="true"
                }
                val newVal = if (pass) "true" else "false"
                uriString = uriString.replaceFirst("pass=" + `val`.toRegex(), "pass=$newVal")
                return Uri.parse(uriString)
            }
            throw Exception("cant up pass parameter: uri is null")
        }

        fun getGeoPosition(activity: Activity?): LatLng? {
            val lManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = getLastBestLocation(lManager, activity)
            return if (location != null) LatLng(location.latitude
                    , location.longitude) else LAT_LNG
        }

        private var permissionCode = 1
        private fun getLastBestLocation(mLocationManager: LocationManager, context: Activity?): Location? {
            if (!isAvailablePermissions(context)) return null
            @SuppressLint("MissingPermission") val locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            @SuppressLint("MissingPermission") val locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val GPSLocationTime: Long = locationGPS?.time?:0
            val NetLocationTime: Long = locationNet?.time?:0
            return if (0 < GPSLocationTime - NetLocationTime) {
                locationGPS
            } else {
                locationNet
            }
        }

        fun isAvailablePermissions(context: Activity?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) if (context != null && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val tmp = permissionCode++
                context.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), tmp)
                val permissions = arrayOfNulls<String>(2)
                val accesses = IntArray(2)
                context.onRequestPermissionsResult(tmp, permissions, accesses)
                var granted = 0
                for (i in 0..1) if (permissions[i] != null && (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION || permissions[i] == Manifest.permission.ACCESS_COARSE_LOCATION)) granted += accesses[i] //0 or -1
                else granted--
                if (granted < 0) {
                    Toast.makeText(context, "Нет прав на определение местоположения", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            return true
        }
    }
}