package ru.android.zheka.gmapexample1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.android.zheka.coreUI.AbstractActivity
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.fragment.Geo
import ru.android.zheka.gmapexample1.MapsActivity.Companion.updateOfflineState
import ru.android.zheka.model.GeoModel
import ru.zheka.android.timer.PositionReciever
import java.io.File
import javax.inject.Inject

class GeoPositionActivity //AppCompatActivity
    : AbstractActivity<ViewDataBinding>(), OnMapReadyCallback,HasAndroidInjector, OnMapLongClickListener, OnMarkerClickListener, OnMarkerDragListener {
    var clTrace: Class<*>? = null
    var clMap: Class<*>? = null
    var clPoints: Class<*>? = null
    var clMain: Class<*>? = null
    var clWayPoints: Class<*>? = null

    // choose for true statement
    var mMap: GoogleMap? = null

    var config: Config? = null
    protected var resViewId = R.layout.activity_maps

    //TimerService timerService = TimerService.getInstance();
    var positionReciever: PositionReciever? = null
    private var mapType: MapTypeHandler? = null

    @Inject
    lateinit var model: GeoModel

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector!!
    }
    private fun fixGoogleMapBug() {
        val googleBug: SharedPreferences = getSharedPreferences("google_bug", Context.MODE_PRIVATE)
        if (!googleBug.contains("fixed")) {
            val corruptedZoomTables = File(filesDir, "ZoomTables.data")
            corruptedZoomTables.delete()
            googleBug.edit().putBoolean("fixed", true).apply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public override fun onCreate(savedInstanceState: Bundle?) {
        fixGoogleMapBug()
        super.onCreate(savedInstanceState)
        setContentView(resViewId)
        val mapFragment = getSupportFragmentManager()
                .findFragmentById(R.id.map) as SupportMapFragment
        println("map fragment is got $mapFragment")
        mapFragment.getMapAsync(this)
        updateOfflineState(this)
        //getCenter(getIntent()); //do not try init marker<= marker is null
        //TODO remove print
        val geoIntent: Intent = getIntent()
        println("GeoPosition geoIntent deliver")
        println(geoIntent.data as Uri)
        try {
            clTrace = Class.forName("ru.android.zheka.gmapexample1.TraceActivity")
            println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.TraceActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from GeoPositionActivity " + e.message)
        }
        try {
            clMap = Class.forName("ru.android.zheka.gmapexample1.MapsActivity")
            println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.MapsActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from GeoPositionActivity " + e.message)
        }
        try {
            clMain = Class.forName("ru.android.zheka.gmapexample1.MainActivity")
            println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.MainActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from GeoPositionActivity " + e.message)
        }
        try {
            clPoints = Class.forName("ru.android.zheka.gmapexample1.LatLngActivity")
            println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.LatLngActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from GeoPositionActivity " + e.message)
        }
        try {
            clWayPoints = Class.forName("ru.android.zheka.gmapexample1.WayPointsToTrace")
            println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.WayPointsToTrace")
        } catch (e: ClassNotFoundException) {
            println("---------- from GeoPositionActivity " + e.message)
        }
        config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        val coordinate = findViewById(R.id.coordinateText) as TextView
        println("get config")
        coordinate.visibility = View.GONE
        if (config != null) if (config!!.uLocation) {
            coordinate.visibility = View.VISIBLE
        }
        if (TimerService.interrupted) {
            println("start timer-service")
            val intent = Intent(TimerService.BROADCAST_ACTION)
            intent.setClass(this, TimerService::class.java)
            this.startService(intent)
            //timerService = getSystemService(TimerService.class);
            //System.out.println("timeService is "+timerService);
            //timerService.startService(intent);
        }
    }
    override fun onMapReady(map: GoogleMap) {
        try {
            model.position!!.positioning() //getIntent();
        } catch (e: Exception) {
            Toast.makeText(this, "Нет переданного местоположения", 15).show()
            e.printStackTrace()
        }
        if (positionReciever == null) {
            positionReciever = PositionReciever(map, model.position)
            //LocalBroadcastManager.getInstance(this).registerReceiver(positionReciever
            //		, new IntentFilter(TimerService.BROADCAST_ACTION));
            TimerService.mListners!!.add(positionReciever)
        }
        val geoIntent: Intent = getIntent()
        println("GeoPosition geoIntent deliver")
        println(geoIntent.data as Uri)
        if (map != null) {
            mMap = map
            model.position!!.markerCenter = map.addMarker(MarkerOptions().position(model.position!!.centerPosition)
                    .title(model.position!!.title)
                    .snippet(model.position!!.tracePointName)
                    .draggable(true))
            //center = CameraUpdateFactory.newLatLng(centerPosition);
            //zoom = CameraUpdateFactory.zoomBy(positionUtil.getZoom());
            println("geoPosition.onMapReadymove camera to " + model.position!!.centerPosition.latitude
                    + " " + model.position!!.centerPosition.longitude)
            //map.moveCamera(center);
            //map.animateCamera(zoom);
            mapType = MapTypeHandler(MapTypeHandler.userCode)
            mMap!!.mapType = mapType!!.code
            map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                    .target(model.position!!.centerPosition)
                    .zoom(model.position!!.zoom).build()))
            if (mMap!!.uiSettings != null) {
                mMap!!.uiSettings.isZoomControlsEnabled = true
                mMap!!.uiSettings.isMapToolbarEnabled = true
            } else Toast.makeText(this, "Панель не работает", 15).show()
            map.setOnCameraChangeListener(model.onCameraChanged)
            map.setOnMapLongClickListener(this)
            map.setOnMarkerClickListener(this)
            map.setOnMarkerDragListener(this)
        } else Toast.makeText(this, "map is not ready: null pointer exception", 15)
    }

    override fun onMapLongClick(point: LatLng) {
        if (point != null) model.position!!.markerCenter = mMap!!.addMarker(MarkerOptions().position(point)
                .draggable(true))
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker != null) {
            marker.remove()
            return true
        }
        return false
    }

    override fun onMarkerDrag(arg0: Marker) {
        // stub
    }

    override fun onMarkerDragEnd(arg0: Marker) {
        // stub
    }

    override fun onMarkerDragStart(arg0: Marker) {
        //TODO advice to remove marker
    }

    /*
     * locale
     * @see roboguice.activity.RoboFragmentActivity#onStart()
     */
    override fun onStart() {
        config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        if (positionReciever != null) {
            if (config!!.tenMSTime != getString(R.string.timerdata1)) if (!TimerService.mListners!!.contains(positionReciever)) TimerService.mListners!!.add(positionReciever)
        }
        super.onStart()
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun onPause() {
        if (positionReciever != null) TimerService.mListners!!.remove(positionReciever)
        super.onPause()
    }

    /*
     * locale
     * @see roboguice.activity.RoboFragmentActivity#onStart()
     */
    override fun onStop() {
        model.position!!.mGoogleApiClient.disconnect()
        if (positionReciever != null) TimerService.mListners!!.remove(positionReciever)
        super.onStop()
    }

    companion object {
        const val ADD_WAYPOINTS = "addWaypoints"
        const val TRACE = "trace"
        const val MAP = "map"
        const val SAVE_POINT = "savePoint"
        const val POINTS = "points"
        const val HOME = "home"
        const val OFFLINE = "offline"
        var ready = false
        var monitor = Object()
        var msg = ""
    }

    override val layoutId
        get() = R.layout.activity_geo

    override fun initComponent() {
        AndroidInjection.inject(this)
    }

    override fun onInitBinding(binding: ViewDataBinding?) {
        switchToFragment(R.id.mapFragment, Geo())
        model.activity = this
        model.config = this.config!!
    }

    override fun onDestroyBinding(binding: ViewDataBinding?) {
    }

    override fun onResumeBinding(binding: ViewDataBinding?) {
    }
}