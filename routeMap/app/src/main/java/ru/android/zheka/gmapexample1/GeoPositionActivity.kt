package ru.android.zheka.gmapexample1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import ru.android.zheka.gmapexample1.databinding.ActivityGeoBinding
import ru.android.zheka.model.GeoModel
import ru.zheka.android.timer.PositionReciever
import java.io.File
import javax.inject.Inject

class GeoPositionActivity //AppCompatActivity
    : AbstractActivity<ActivityGeoBinding>(), OnMapReadyCallback, HasAndroidInjector, OnMapLongClickListener, OnMarkerClickListener, OnMarkerDragListener {
    // choose for true statement
    var mMap: GoogleMap? = null
    private var resViewId = R.layout.activity_geo

    //TimerService timerService = TimerService.getInstance();
    private var positionReceiver: PositionReciever? = null
    private var mapType: MapTypeHandler? = null

    @Inject
    lateinit var model: GeoModel

    @Inject
    lateinit var androidInjecto: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        MainActivity._androidInjector = if (::androidInjecto.isInitialized ) androidInjecto
        else MainActivity._androidInjector
        return MainActivity._androidInjector
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
//        fixGoogleMapBug()
        super.onCreate(savedInstanceState)
        setContentView(resViewId)
        val frameLayout =
                findViewById(R.id.geoFM) as FrameLayout
        val mapFragment = SupportMapFragment.newInstance()
        getSupportFragmentManager().beginTransaction().replace(frameLayout.id, mapFragment).commit()
        println("map fragment is got $mapFragment")
        mapFragment.getMapAsync(this)
        updateOfflineState(this)
        //getCenter(getIntent()); //do not try init marker<= marker is null
        //TODO remove print
        val geoIntent: Intent = getIntent()
        println("GeoPosition geoIntent deliver")
        println(geoIntent.data as Uri)
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        val coordinate = findViewById(R.id.coordinateTextGeo) as TextView
        println("get config")
        coordinate.visibility = View.GONE
        if (config != null) if (config.uLocation!!) {
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
            model.position.positioning() //getIntent();
        } catch (e: Exception) {
            Toast.makeText(this, "Нет переданного местоположения", 15).show()
            e.printStackTrace()
        }
//        model.position!!.updatePosition()
        if (positionReceiver == null) {
            positionReceiver = PositionReciever(map, model.position)
            //LocalBroadcastManager.getInstance(this).registerReceiver(positionReciever
            //		, new IntentFilter(TimerService.BROADCAST_ACTION));
            TimerService.mListners!!.add(positionReceiver)
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
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        if (positionReceiver != null) {
            if (config!!.tenMSTime != getString(R.string.timerdata1)) if (!TimerService.mListners!!.contains(positionReceiver)) TimerService.mListners!!.add(positionReceiver)
        }
        super.onStart()
    }

    override fun onPause() {
        if (positionReceiver != null) TimerService.mListners!!.remove(positionReceiver)
        super.onPause()
    }

    /*
     * locale
     * @see roboguice.activity.RoboFragmentActivity#onStart()
     */
    override fun onStop() {
        model.position!!.mGoogleApiClient.disconnect()
        if (positionReceiver != null) TimerService.mListners!!.remove(positionReceiver)
        super.onStop()
    }

    companion object {
        const val OFFLINE = "offline"
    }

    override val layoutId
        get() = R.layout.activity_geo

    override fun initComponent() {
        AndroidInjection.inject(this)
    }

    @Inject
    lateinit var geoModel: GeoModel

    override fun onInitBinding(binding: ActivityGeoBinding?) {
        binding?.model = geoModel
        switchToFragment(R.id.geoFragment, Geo())
//        switchToFragment(R.id.geoFragment, HideGeo())
        model.activity = this
        model.config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
    }

    override fun onDestroyBinding(binding: ActivityGeoBinding?) {
    }

    override fun onResumeBinding(binding: ActivityGeoBinding?) {
    }
}