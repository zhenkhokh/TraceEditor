package ru.android.zheka.gmapexample1

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE
import ru.android.zheka.route.BellmannFord
import ru.android.zheka.route.BellmannFord.process
import java.util.*

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
class PositionInterceptor @JvmOverloads constructor(var target: Activity?, private val resTextId: Int = -1) : ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    var markerCenter: Marker? = null
    var centerPosition: LatLng? = null

    // use if modify collection, use iterator from local copy then existed reader iterators will be fine
    @Volatile
    var isWriteExtra = false
    var start: LatLng? = null
    var end: LatLng? = null
    var title: String? = null
    var zoom: Float = PositionUtil.zoomDefault
    var tracePointName: String? = null
    lateinit var state: TRACE_PLOT_STATE
    var mGoogleApiClient: GoogleApiClient? = null
    var mLastLocation: Location? = null
    override fun toString(): String {
        return "PositionInterceptor{" +
                "resTextId=" + resTextId +
                ", markerCenter=" + markerCenter +
                ", centerPosition=" + centerPosition +
                ", isWriteExtra=" + isWriteExtra +
                ", start=" + start +
                ", end=" + end +
                ", title='" + title + '\'' +
                ", zoom=" + zoom +
                ", target=" + target +
                ", tracePointName='" + tracePointName + '\'' +
                ", state=" + state +
                ", extraPoints=" + _extraPoints +
                ", mGoogleApiClient=" + mGoogleApiClient +
                ", mLastLocation=" + mLastLocation +
                '}'
    }
    private var _extraPoints: ArrayList<String> = ArrayList()
    val extraPoints: ArrayList<String>
        get() {
            while (isWriteExtra) {
            }
            //if (_extraPoints!=null)
            //	return new ArrayList <String> (_extraPoints);
            return _extraPoints
        }

    fun setExtraPointsFromCopy(extraPoints: ArrayList<String>) {
        while (isWriteExtra) {
        }
        isWriteExtra = true
        //if (extraPoints!=null)
        //	this.extraPoints = new ArrayList <String> (extraPoints);// double copy
        this._extraPoints = extraPoints
        isWriteExtra = false
    }

    fun initLocation() {
        target!!.runOnUiThread(Runnable {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = GoogleApiClient.Builder(target)
                        .addConnectionCallbacks(this@PositionInterceptor)
                        .addOnConnectionFailedListener(this@PositionInterceptor)
                        .addApi(LocationServices.API)
                        .build()
                println("mGoogleApiClient is $mGoogleApiClient")
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient!!.connect()
                if (!PositionUtil.isAvailablePermissions(target)) return@Runnable
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient)
            }
            /*
				if (mLastLocation==null){
					LocationServices.getFusedLocationProviderClient (target).getLastLocation ().addOnCompleteListener (new OnCompleteListener <Location> () {
						@Override
						public void onComplete(@NonNull Task<Location> task) {
							if (task.isComplete ())
								mLastLocation = task.getResult ();
							else
								new IllegalStateException ("task is not finished");
						}
					});
				}
				*/if (mLastLocation == null) {
            updateUILocation(resTextId)
        }
            centerPosition = location
            println("mLastLocation is $mLastLocation")
        })
    }

    @Throws(Exception::class)
    fun positioning(): Intent {
        val intent = target!!.intent
        val positionUtil = PositionUtil()
        //CameraUpdate zoom, center;
        try {
            positionUtil.positionAndBoundInit(intent) //throws Exception
        } catch (e: Exception) {
            centerPosition = getCenter(intent)
            throw e
        }
        state = positionUtil.defCommand()
        tracePointName = null
        centerPosition = getCenter(intent) //it's not same as positionUtil.center
        start = positionUtil.start
        end = positionUtil.end
        when (state) {
            TRACE_PLOT_STATE.CENTER_COMMAND -> {
                tracePointName = ""
                if (tracePointName == null) tracePointName = "next"
                if (tracePointName == null) tracePointName = "start"
                run {
                    if (tracePointName == null) tracePointName = "end"
                    centerPosition = positionUtil.center
                }
            }
            TRACE_PLOT_STATE.CENTER_START_COMMAND -> {
                if (tracePointName == null) tracePointName = "next"
                if (tracePointName == null) tracePointName = "start"
                run {
                    if (tracePointName == null) tracePointName = "end"
                    centerPosition = positionUtil.center
                }
            }
            TRACE_PLOT_STATE.CENTER_CONNECT_COMMAND -> {
                if (tracePointName == null) tracePointName = "start"
                run {
                    if (tracePointName == null) tracePointName = "end"
                    centerPosition = positionUtil.center
                }
            }
            TRACE_PLOT_STATE.CENTER_END_COMMAND -> {
                if (tracePointName == null) tracePointName = "end"
                centerPosition = positionUtil.center
            }
            TRACE_PLOT_STATE.CONNECT_COMMAND -> {
                tracePointName = "next"
                run {
                    if (tracePointName == null) tracePointName = "end"
                    centerPosition = end
                }
            }
            TRACE_PLOT_STATE.END_COMMAND -> {
                if (tracePointName == null) tracePointName = "end"
                centerPosition = end
            }
            TRACE_PLOT_STATE.START_COMMAND -> {
                tracePointName = "start"
                centerPosition = start
            }
            TRACE_PLOT_STATE.DONOTHING_COMMAND -> {
                tracePointName = "center"
            }
        }
        _extraPoints = positionUtil.extraPoints
        return intent
    }

    fun updatePosition(): Intent {
//		LatLng lEnd = end;
        val intent = target!!.intent
        val positionUtil = PositionUtil()
        val center: LatLng?
        if (centerPosition != null) center = centerPosition else {
            val zoomCur = zoom
            val titleCur = title
            center = getCenter(intent)
            zoom = zoomCur
            title = titleCur
        }
        try {
            positionUtil.positionAndBoundInit(intent)
            start = positionUtil.start
            end = positionUtil.end
            positionUtil.centerPass = start != null
            positionUtil.endPass = end != null
            state = positionUtil.defCommand()
        } catch (e: Exception) {
            println("start or end point is not exist in intention")
            start = PositionUtil.LAT_LNG
            end = PositionUtil.LAT_LNG
            state = TRACE_PLOT_STATE.DONOTHING_COMMAND
        }
        positionUtil.center = center
        positionUtil.zoom = zoom
        positionUtil.titleMarker = title
        _extraPoints = positionUtil.extraPoints
        //		end = lEnd;
        positionUtil.start = start
        positionUtil.end = end
        return positionUtil.intent!!
    }

    private fun getCenter(intent: Intent): LatLng? {
        val positionUtil = PositionUtil()
        val config = getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        try {
            positionUtil.positionAndBoundInit(intent)
        } catch (e: Exception) {
            return try {
                positionUtil.setCenterPosition(intent)
                centerPosition = positionUtil.center
                title = positionUtil.titleMarker
                zoom = positionUtil.zoom
                if (mLastLocation != null && config.uLocation!!) centerPosition = location
                centerPosition
            } catch (ee: Exception) {
                Toast.makeText(target, "start point is not specified: задайте местоположение старта", Toast.LENGTH_SHORT).show()
                println("from centerInit: get geoPosition")
                if (mLastLocation != null) {
                    location
                } else PositionUtil.getGeoPosition(target)
            }
        }
        centerPosition = positionUtil.center
        if (mLastLocation != null && config.uLocation!!) centerPosition = location
        title = positionUtil.titleMarker
        zoom = positionUtil.zoom
        return centerPosition
    }

    val newIntent: Intent
        get() {
            val util = PositionUtil()
            var _state:TRACE_PLOT_STATE? = null
            try {
                util.positionAndBoundInit(target!!.intent)
            } catch (e: Exception) {
                _state = TRACE_PLOT_STATE.DONOTHING_COMMAND
            }
            util.endPass = end != null
            util.startPass = start != null
            state = _state?:util.defCommand()
            util.setCommand(state)
            util.center = centerPosition
            util.start = start
            util.end = end
            util.zoom = zoom
            util.titleMarker = tracePointName
            util.extraPoints = _extraPoints
            util.titleMarker = title
            return util.intent!!
        }

    /*
     * location
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnected(android.os.Bundle)
     */
    @SuppressLint("MissingPermission")
    override fun onConnected(arg0: Bundle?) {
        println("start ConnectionCallbacks.onConnected")
        if (!PositionUtil.isAvailablePermissions(target)) return
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient)
        /*LocationServices.getFusedLocationProviderClient (target).getLastLocation ().addOnCompleteListener (new OnCompleteListener <Location> () {
			@Override
			public void onComplete(@NonNull Task<Location> task) {
				if (task.isComplete ())
					mLastLocation = task.getResult ();
				else
					new IllegalStateException ("task is not finished");
			}
		});
		*/if (mLastLocation != null) {
            updateUILocation(resTextId)
        }
        println("mLastConnection is $mLastLocation")
        if (!PositionUtil.isAvailablePermissions(target)) return
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient
                , LocationRequest.create(), this)
        /*LocationServices.getFusedLocationProviderClient (target).getLastLocation ().addOnCompleteListener (new OnCompleteListener <Location> () {
			@Override
			public void onComplete(@NonNull Task<Location> task) {
				if (task.isComplete ())
					mLastLocation = task.getResult ();
				else
					new IllegalStateException ("task is not finished");
			}
		});
		*/println("end ConnectionCallbacks.onConnected")
    }

    fun updateUILocation() {
        updateUILocation(resTextId)
    }

    @SuppressLint("MissingPermission")
    fun updateUILocation(textId: Int) {
        println("call updateUILocation")
        val coordinate = target?.findViewById<View>(textId) as TextView?
        if (coordinate != null) {
            //check permission is alredy done
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient)
            /*LocationServices.getFusedLocationProviderClient (target).getLastLocation ().addOnCompleteListener (new OnCompleteListener <Location> () {
				@Override
				public void onComplete(@NonNull Task<Location> task) {
					if (task.isComplete ())
						mLastLocation = task.getResult ();
					else
						new IllegalStateException ("task is not finished");
				}
			});
			*/
            var text: String  = if (mLastLocation != null) {
                //LatLng location = new LatLng(mLastLocation.getLatitude()
                //		, mLastLocation.getLongitude());
                //text = location.toString();
                String.format("(%.4f %.4f)", mLastLocation!!.latitude, mLastLocation!!.longitude)
            } else String.format("(%.4f %.4f)", PositionUtil.LAT_LNG.latitude
                    , PositionUtil.LAT_LNG.longitude)
            //text = PositionUtil.LAT_LNG.toString();
            val config = getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                    , Config::class.java) as Config
            if (config.uLocation!!) {
                /*boolean isConnected3Points = start!=null ? extraPoints.size ()==1
						&& BellmannFord.round(start).equals (BellmannFord.round (end)):false;
				if ( (extraPoints.size ()==0 || isConnected3Points)
				&& end!=null && start!=null) {
					LatLng[] tmp = new LatLng[] {start,end};
					if (isConnected3Points)
						tmp[1] = (LatLng) new UtilePointSerializer ().deserialize (extraPoints.get (0));
					BellmannFord.process (tmp);
				}*/
                var lenAlenka = 0.0
                var isAlenkaReady = false
                if (target != null && target is MapsActivity) {
                    val m = target as MapsActivity
                    if (m.results.isAvailable) {
                        lenAlenka = m.results.length / 1000.0
                        isAlenkaReady = true
                    } else if (m.isPositionInitialized() && m.position.start != null
                            && m.position.end != null) //TODO use else
                        process(m.wayPoints.toTypedArray())
                }
                val sb = StringBuilder("ш.д.: ")
                sb.append(text.replace(",", "."))
                //.append (" длина: ");
                if (!isAlenkaReady) {
                    sb.append(" КУЗьМА: ")
                            .append(String.format("%.2f", BellmannFord.length).replace(",", "."))
                            .append(" ")
                } else {
                    sb.append(" АЛеНКа: ")
                            .append(String.format("%.2f", lenAlenka).replace(",", "."))
                            .append(" ")
                }
                sb.append("км")
                coordinate.visibility = View.VISIBLE
                coordinate.text = sb.toString()
            }
            println("updateUI coordinate succesfully finished. mLastLocation is $mLastLocation")
        }
    }

    /*
     * location
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)
     */
    override fun onConnectionSuspended(arg0: Int) {
        Toast.makeText(target, "Соединение "+arg0+"при передаче местоположения приостановлено", Toast.LENGTH_SHORT).show()
    }

    /*
     *
     */
    override fun onConnectionFailed(arg0: ConnectionResult) {
        Toast.makeText(target, "Соединение при передаче местоположения разорвано", Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(arg0: Location) {
        mLastLocation = arg0
        updateUILocation(resTextId)
        println("updateUILocatin called in MapsActivity.onLocationChanged")
    }

    /*
		//if (mLastLocation == null)
		LocationServices.getFusedLocationProviderClient (target).getLastLocation ().addOnCompleteListener (new OnCompleteListener <Location> () {
			@Override
			public void onComplete(@NonNull Task<Location> task) {
				if (task.isComplete ())
					mLastLocation = task.getResult ();
				else
					new IllegalStateException ("task is not finished");
			}
		});
		*/
    //updateUILocation ();
    val location: LatLng?
        get() {
            /*
            //if (mLastLocation == null)
            LocationServices.getFusedLocationProviderClient (target).getLastLocation ().addOnCompleteListener (new OnCompleteListener <Location> () {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isComplete ())
                        mLastLocation = task.getResult ();
                    else
                        new IllegalStateException ("task is not finished");
                }
            });
            */
            //updateUILocation ();
            if (mLastLocation != null) return LatLng(mLastLocation!!.latitude
                    , mLastLocation!!.longitude) else if (mGoogleApiClient != null) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient)
                if (mLastLocation != null) return LatLng(mLastLocation!!.latitude
                        , mLastLocation!!.longitude)
            }
            println("mLastLocation is not initialized... try PositionUtil.getGeoPosition")
            return PositionUtil.getGeoPosition(target)
        }

    companion object {
        const val resViewId = R.id.coordinateText
        fun isOtherMode(state: TRACE_PLOT_STATE): Boolean {
            return state != TRACE_PLOT_STATE.CONNECT_COMMAND && state != TRACE_PLOT_STATE.END_COMMAND && state != TRACE_PLOT_STATE.CENTER_START_COMMAND
        }
    }

    init {
        initLocation()
    }
}