package ru.android.zheka.gmapexample1

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.activeandroid.Model
import com.activeandroid.query.Delete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.android.zheka.coreUI.AbstractActivity
import ru.android.zheka.db.*
import ru.android.zheka.fragment.HideMap
import ru.android.zheka.gmapexample1.databinding.ActivityMapsBinding
import ru.android.zheka.model.MapModel
import ru.android.zheka.route.*
import ru.android.zheka.route.BellmannFord.MissMatchDataException
import ru.android.zheka.route.BellmannFord.NoDirectionException
import ru.android.zheka.vm.trace.TraceEndVM
import ru.zheka.android.timer.PositionReciever
import java.util.*
import java.util.concurrent.ExecutionException
import javax.inject.Inject

class MapsActivity : AbstractActivity<ActivityMapsBinding>(), HasAndroidInjector, OnMapReadyCallback, RoutingListener, OnCameraChangeListener {
    private val resTextId: Int = R.id.coordinateText
    private var traceDebugging: DataTrace? = null
    private var traceDebuggingSer: String? = null
    var context_: Context = this
    private var mMap: GoogleMap? = null
    protected var routing: Routing? = null

    @JvmField
    var position: PositionInterceptor? = null

    protected var resViewId = R.layout.activity_maps //R.layout.activity_maps;
    var dataTrace: DataTrace? = DataTrace()
    private var onRoutingReady = false
    private var cnt = 0
    private var cntCtrl = 0
    private var rateLimit_ms = 800
    private var prevPoint: LatLng? = null
    private var point: LatLng? = null

    var positionReciever: PositionReciever? = null
    var options: MarkerOptions? = null
    var cursorMarker: Marker? = null
    var mapType = MapTypeHandler(MapTypeHandler.userCode)

    @JvmField
    var results = ResultRouteHandler(-1) // not available
    var wayPoints = ArrayList<LatLng>()

    @Inject
    lateinit var androidInjecto: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        MainActivity._androidInjector = if (::androidInjecto.isInitialized ) androidInjecto
        else MainActivity._androidInjector
        return MainActivity._androidInjector
    }

    fun fetchWayPoints(): ArrayList<LatLng> {
        wayPoints = ArrayList()
        if (model.isFakeStart) wayPoints.add(position!!.start?:TraceEndVM.start) else wayPoints.add(position!!.centerPosition)
        val iterator: Iterator<*> = position!!.extraPoints.iterator()
        while (iterator
                        .hasNext()) {
            val sPoint = iterator.next() as String
            val point = UtilePointSerializer().deserialize(sPoint) as LatLng
            val rPoint = BellmannFord.round(point)
            if (rPoint != BellmannFord.round(position!!.start?:TraceEndVM.start)//TODO button in geoPosition
                    && rPoint != BellmannFord.round(position!!.end)) wayPoints.add(point)
        }
        wayPoints.add(position!!.end)
        return wayPoints
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resViewId)
        updateOfflineState(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val frameLayout =
                findViewById(R.id.mappp) as FrameLayout
        val mapFragment = SupportMapFragment.newInstance()
        getSupportFragmentManager().beginTransaction().replace(frameLayout.id, mapFragment).commit()
        println("map fragment is got $mapFragment")
        mapFragment.getMapAsync(this)
        println("async map")
        position = PositionInterceptor(this, resTextId)
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        rateLimit_ms = config.rateLimit_ms?.toBigDecimal()?.intValueExact()!!
        val coordinate = findViewById(resTextId) as TextView
        println("get config: $config")
        coordinate.visibility = View.GONE
        //coordinate.setText("");
        //coordinate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (config.uLocation!!) {
            coordinate.visibility = View.VISIBLE
            //coordinate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        if (TimerService.interrupted) {
            println("start timer-service")
            val intent = Intent(TimerService.BROADCAST_ACTION)
            intent.setClass(this, TimerService::class.java)
            this.startService(intent)
            //System.out.println("timeService is "+timerService);
            //timerService =  getSystemService(TimerService.class);
            //System.out.println("timeService is "+timerService);
            //timerService.startService(intent);
            //timerService.startService(intent);
        }
        println("end onCreate")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        if (options == null && googleMap != null) options = MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.cursor72_77))
                .draggable(true)
        /*
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        // MapInitilizer is not require here
        try {
            position!!.positioning()
        } catch (e: Exception) {
            Toast.makeText(this, "Нет переданного местоположения", 15).show()
            e.printStackTrace()
        }
        if (positionReciever == null) {
            positionReciever = PositionReciever(googleMap, position, this)
            //LocalBroadcastManager.getInstance(this).registerReceiver(positionReciever
            //		, new IntentFilter(TimerService.BROADCAST_ACTION));
            TimerService.mListners!!.add(positionReciever)
        }
        //if (this.center==null)
        //	this.center = PositionUtil.getGeoPosition(this);
        //CameraUpdate center = CameraUpdateFactory.newLatLng(this.center);
        //CameraUpdate zoom =  CameraUpdateFactory.zoomTo(this.zoom);
        println("zoom and center are initiated")
        if (googleMap != null) {
            mMap = googleMap
            println("position.centerPosition:" + position!!.centerPosition
                    + " position.zoom:" + position!!.zoom)
            mMap!!.mapType = mapType.code
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                    .target(position!!.centerPosition)
                    .zoom(position!!.zoom)
                    .build()))
            if (mMap!!.uiSettings != null) {
                mMap!!.uiSettings.isZoomControlsEnabled = true
                mMap!!.uiSettings.isZoomGesturesEnabled = true
                mMap!!.uiSettings.setAllGesturesEnabled(true)
                mMap!!.uiSettings.isMapToolbarEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else Toast.makeText(this, "Панель не работает", 15).show()
        } else {
            showAllert("map is null")
            return
        }
        routing = Routing()
        //(Routing.TravelMode.WALKING);
        routing!!.registerListener(this)
        // to know start==end
        position!!.centerPosition = PositionUtil.getGeoPosition(this) // <---?
        //position.centerPosition = position.getLocation ();
        prevPoint = if (!model.isFakeStart) {
            position!!.centerPosition //position.start;
            //position.start = position.centerPosition;// <---?
        } else {
            position!!.start?:TraceEndVM.start
        }
        point = position!!.end
        if (prevPoint != null && point != null) {
            if (position!!.extraPoints.size <= 0) {
                results = ResultRouteHandler(1)
                traceDebuggingSer = getIntent().getStringExtra(PositionUtil.TITLE)
                if (isOffline && (traceDebuggingSer == null || traceDebuggingSer == GeoPositionActivity.OFFLINE)) {
                    offlineIncorrectData()
                    return
                } else if (isOffline) {
                    traceDebugging = UtileTracePointsSerializer().deserialize(traceDebuggingSer!!) as DataTrace
                    traceDebugging!!.initSegments()
                    if (traceDebugging!!.hasNext()) {
                        routing = OfflineRouting(*traceDebugging!!.nextSegmentArray())
                        routing!!.registerListener(this)
                    } else {
                        offlineIncorrectData()
                        return
                    }
                }
                routing!!.execute(prevPoint, point)
            } else {
                //runOnUiThread(new Runnable() {
                val r: Runnable = object : Runnable {
                    override fun run() {
                        cnt = 0
                        cntCtrl = 0
                        var bellManPoits = ArrayList<LatLng>()
                        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                                , Config::class.java) as Config
                        val isBellman = config!!.bellmanFord == Application.optimizationBellmanFlag
                        if (!isOffline) {
                            //ArrayList<LatLng> wayPoints = new ArrayList<LatLng>();
                            fetchWayPoints()
                            if (isBellman) {
                                bellManPoits = wayPoints
                                //                                for (i in 1..2) bellManPoits.remove(position!!.end)
                                bellManPoits.add(position!!.end)
                                bellManPoits = ArrayList(Arrays.asList(
                                        *BellmannFord.process(bellManPoits.toTypedArray()))
                                )
                            }
                            if (!isBellman) {
                                routing = Routing()
                                val task = routing!!.execute(*wayPoints.toTypedArray())
                                var route: Route? = null
                                try {
                                    route = task.get()
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                } catch (e: ExecutionException) {
                                    e.printStackTrace()
                                }
                                if (route == null) {
                                    showAllert("Слишком длинная задача, уменьшите число промежуточных точек")
                                  return
                                }
                                val order = route.order
                                println("order is $order")
                                if (order.size == position!!.extraPoints.size - 1) println("order.size() is fine")
                                val temp = ArrayList<String>()
                                run {
                                    val iterator: Iterator<*> = order.iterator()
                                    while (iterator
                                                    .hasNext()) {
                                        iterator.next()
                                        temp.add("")
                                    }
                                }
                                var index_ = 0
                                val iterator: Iterator<*> = order.iterator()
                                while (iterator
                                                .hasNext()) {
                                    val index = iterator.next() as Int
                                    println("index=$index, index_=$index_")
                                    temp[index] = position!!.extraPoints[index_++]
                                }
                                position!!.setExtraPointsFromCopy(temp)
                                position!!.getExtraPoints().add(UtilePointSerializer().serialize(position?.end!!).toString())
                                //}
                                //add end point
                                //position.extraPoints.add((String)new UtilePointSerializer().serialize(position.end));
                                routing = Routing()
                                routing!!.registerListener(this@MapsActivity)
                            }
                        }
                        val iterator: Iterator<*>
                        if (isBellman && !isOffline) {
                            val iStart = bellManPoits.indexOf(position!!.start?:TraceEndVM.start)
                            val rStart = BellmannFord.round(position!!.start?:TraceEndVM.start)
                            for (i in 0 until iStart) {
                                val head = bellManPoits.removeAt(0)
                                if (rStart != BellmannFord.round(head)) bellManPoits.add(head)
                            }
                            val tmp = ArrayList<String>()
                            val iterator1: Iterator<*> = bellManPoits.iterator()
                            if (iterator1.hasNext()) // miss start
                                iterator1.next()
                            while (iterator1.hasNext()) {
                                tmp.add(iterator1.next()?.let {
                                    UtilePointSerializer().serialize(
                                            it
                                    )
                                } as String)
                            }
                            position!!.setExtraPointsFromCopy(tmp)
                        }
                        val tmp = position!!.extraPoints
                        iterator = tmp.iterator() // be happy it is just read
                        results = ResultRouteHandler(tmp.size)
                        var dataTrace: DataTrace? = null
                        if (isOffline) {
                            //String traceName = getIntent ().getStringExtra(TRACE);
                            //traceDebugging = (Trace) DbFunctions.getTraceByName (traceName);
                            traceDebuggingSer = getIntent().getStringExtra(PositionUtil.TITLE)
                            if (traceDebuggingSer == null) {
                                offlineIncorrectData()
                                return
                            }
                            traceDebugging = UtileTracePointsSerializer()
                                    .deserialize(traceDebuggingSer!!) as DataTrace
                            if (traceDebugging == null) {
                                offlineIncorrectData()
                                return
                            }
                            dataTrace = traceDebugging
                            dataTrace!!.initSegments()
                            if (dataTrace.hasNext()) {
                                routing = OfflineRouting(*dataTrace.nextSegmentArray())
                                routing!!.registerListener(this@MapsActivity)
                            } else {
                                offlineIncorrectData()
                                return
                            }
                        }
                        failuresCnt = 0
                        val curCnt = cntRun + 1
                        cntRun++
                        while (iterator.hasNext()) {
                            if (failuresCnt >= maxFailures) {
                                return
                            }
                            val sPoint = iterator.next() as String
                            point = UtilePointSerializer().deserialize(sPoint) as LatLng
                            //do {
                            if (curCnt == cntRun) routing!!.execute(prevPoint, point)
                            //Cannot execute task: the task is already running
                            routing = Routing()
                            if (isOffline) {
                                if (!dataTrace!!.hasNext()) {
                                    offlineIncorrectData()
                                    return
                                }
                                val segmentPoints = dataTrace.nextSegmentArray()
                                routing = OfflineRouting(*segmentPoints)//TODO kotlin.KotlinNullPointerException
                            }
                            routing!!.registerListener(this@MapsActivity)
                            synchronized(traceDrawMonitor /*MapsActivity.this*/) {
                                println("wait for onRoutingSuccess cnt=$cnt")
                                var goFromDad = maxFailures + 2
                                while (!onRoutingReady && goFromDad-- > 0) {
                                    try {
                                        traceDrawMonitor.wait(rateLimit_ms.toLong()) /*MapsActivity.this.wait(rateLimit_ms);*/
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                }
                                onRoutingReady = false
                            }
//							if (curCnt<cntRun)//do not touch prevPoint
//								break;
                            if (curCnt == cntRun) {
                                prevPoint = point
                                cnt++
                            }
                        }
                    }
                }
                val thread = Thread(r)
                thread.start()
            }
        } else {
            //Toast.makeText(this.context, "start or end trace points are not defined", 15);
            showAllert("start or end trace points are not defined")
        }
        println("end of onMapReady")
    }

    private fun showAllert(msg: String) {
        try { val dialog = AlertDialog(msg)
            dialog.show(getFragmentManager(), "Ошибка")
        }catch (e: IllegalStateException) {e.printStackTrace()} //TODO show in new activity if need
    }

    private fun offlineIncorrectData() {
        showAllert("Неверные данные для офлайн, маршрут не построен")
    }

    override fun onRoutingFailure() {
        println("onRoutingFailure")
        if (isOffline) {
            offlineIncorrectData()
            return
        }
        if (failuresCnt++ < maxFailures) {
            cntRun++
            try {
                routing!!.execute(prevPoint, point)
            } catch (e: IllegalStateException) {
                println("miss route point cnt=$cnt")
            }
            synchronized(traceDrawMonitor) {
                println("wait for onRoutingSuccess cnt=$cnt")
                var goFromDad = 2
                while (!onRoutingReady && goFromDad-- > 0) {
                    try {
                        traceDrawMonitor.wait(rateLimit_ms.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                onRoutingReady = false
            }
            routing = Routing()
            routing!!.registerListener(this@MapsActivity)
            cntRun--
            if (failuresCnt == 1) {
                showAllert("Возможно высокая скорость построения. Получен неожиданный ответ от maps.googleapis.com:" + GoogleParser.result)
            }
        } else {
            showAllert("Маршрут не построен, проверьте интернет соединение")
        }
    }

    override fun onRoutingStart() {
        println("onRoutingStart")
    }

    override fun onRoutingSuccess(route: Route) {
        //cntRun=cnt;
        results.addRouteIgnoreNull(route)
        println("befor synchronize block in onRoutingSuccess")
        synchronized(traceDrawMonitor /*this*/) {
            println("after synchronize block in onRoutingSuccess")
            val polyoptions = DataTrace.configPolyOptions()
            /*if (getIntent().getStringExtra(TRACE)!=null){
        	Intent intent = getIntent();
        	String traceData = intent.getStringExtra(TRACE);
        	PolylineOptions p = (PolylineOptions) new UtileTracePointsSerializer().deserialize(traceData);
        	intent.removeExtra(TRACE);// do not load twice
        	polyoptions.addAll(p.getPoints());
        	Toast.makeText(this.context, "add trace from db", 15);
        }else{*/polyoptions.addAll(route.points)
            //Toast.makeText(this.context, "add routed trace", 15);
            //}
            mMap!!.addPolyline(polyoptions)
            //int sz = position.getExtraPoints ().size ();
            //if (!position.isWriteExtra) {
            //ArrayList <String> tmp = new ArrayList <> (position.getExtraPoints ().subList (0, sz));
            //if (sz==tmp.size ()) {

            //}
            //}
            // Start marker
            var options = MarkerOptions()
            var markerPosition: LatLng? = null
            markerPosition = if (prevPoint != null) prevPoint else position!!.centerPosition
            options.position(markerPosition)
            if (cnt != cntCtrl) System.err.println("cnt!=cntCtrl - error cnt=$cnt cntCtrl=$cntCtrl")
            if (cnt == 0) options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue)) else options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_red))
            if (cnt != 0) { //TODO make visible snippet
                options.title("Extra")
                options.snippet(cnt.toString() + " " + getName(prevPoint))
                options.visible(true)
            }
            mMap!!.addMarker(options)

            // End marker
            options = MarkerOptions()
            markerPosition = if (point != null) point else position!!.centerPosition
            options.position(markerPosition)
            //extra
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_red))
            //end
            if (cnt == 0) options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
            if (position!!.extraPoints != null && cnt >= position!!.extraPoints.size - 1) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                position!!.updateUILocation(resTextId)
                val tmp = ArrayList(position!!.extraPoints)
                dataTrace!!.extraPoints = tmp
                val allPoints: List<LatLng> = ArrayList()
                val it: Iterator<String> = tmp.iterator()
                for (route1 in results.routes) {
                    dataTrace!!.addPoints(route1.points)
                    if (it.hasNext()) {
                        dataTrace!!.addPoint(it.next())
                    }
                }
                Toast.makeText(context_, String.format("КУЗьМА: %.2f км", BellmannFord.length).replace(",", "."), 15).show()
            }
            //DO NOT write end
            mMap!!.addMarker(options)
            onRoutingReady = true
        }
        println("end of onRoutingSuccess, cntCtrl is $cntCtrl cnt is $cnt")
        cntCtrl++
        failuresCnt = 0
    }

    private fun getName(point: LatLng?): String {
        return DbFunctions.getNamePointByData(point) ?: return ""
    }

    fun goPosition(isBeforeAnimation: Boolean) {
        runOnUiThread(Runnable {
            if (mMap != null) {
                if (cursorMarker != null) cursorMarker!!.remove()
                cursorMarker = mMap!!.addMarker(options!!
                        .position(position!!.location)
                )
                try {
                    fetchWayPoints()
                    BellmannFord.getBearing(wayPoints.toTypedArray())
                    while (BellmannFord.bearing == Float.NaN && wayPoints.size > 2) {
                        println("try fix bearing")
                        wayPoints.removeAt(1)
                        BellmannFord.getBearing(wayPoints.toTypedArray())
                    }
                } catch (e: NoDirectionException) {
                    e.printStackTrace()
                    Toast.makeText(this@MapsActivity, "Нет направления, задайте маршрут", 15).show()
                } catch (e: MissMatchDataException) {
                    e.printStackTrace()
                    Toast.makeText(this@MapsActivity, "Все точки маршрута одинаковы, задайте маршрут", 15).show()
                }
                if (!isBeforeAnimation) positionReciever!!.onReceive(this@MapsActivity, this@MapsActivity.getIntent())
                println("from geo: move to " + position!!.location)
            }
            //mMap.moveCamera(update);
        })
    }

    override fun onCameraChange(position: CameraPosition) {
        if (position != null) {
            this.position!!.zoom = position.zoom
            this.position!!.centerPosition = position.target
            println("MapsActivity position:"
                    + this.position!!.centerPosition.latitude + " "
                    + this.position!!.centerPosition.longitude)
        }
    }

    /*
	 * locale
	 * @see roboguice.activity.RoboFragmentActivity#onStart()
	 */
    override fun onStart() {
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        if (positionReciever != null) {
            if (config.tenMSTime != getString(R.string.timerdata1)) if (!TimerService.mListners!!.contains(positionReciever)) TimerService.mListners!!.add(positionReciever)
        }
        super.onStart()
    }

    /*
	 * locale
	 * @see roboguice.activity.RoboFragmentActivity#onStart()
	 */
    override fun onStop() {
        //saveEmergencyUseOnce ();
        position!!.mGoogleApiClient.disconnect()
        if (positionReciever != null) TimerService.mListners!!.remove(positionReciever)
        super.onStop()
    }

    override fun onPause() {
        if (!isOffline) saveOrReplaceTrace(currentName) else saveOrReplaceTrace(currentNameOffline)
        if (positionReciever != null) TimerService.mListners!!.remove(positionReciever)
        super.onPause()
    }

    //private static Long idTrace = new Long (-1);
    private fun saveOrReplaceTrace(name: String?): Boolean { // or use id, load and save
        if (dataTrace != null && TraceEndVM.isStart(position?.start) && position?.end != null) {
            if (dataTrace!!.extraPoints?.size == 0) { // null entity case
                val utilPoint = UtilePointSerializer()
//                dataTrace!!.extraPoints.add(utilPoint.serialize(position!!.start?:TraceEndVM.start) as String?)
                dataTrace!!.extraPoints?.add(utilPoint.serialize(position?.end!!) as String)
            }
            val traceOld = DbFunctions.getTraceByName(name)  //back null if add lock
            //traces = new Select ().from (Trace.class).where ("name = ?",currentName).limit (1).execute ();
            //traces = SQLiteUtils.rawQuery (Trace.class, "SELECT * from Trace where name LIKE ?", new String[]{'%'+currentName+'%'});
            val trace = Trace()
            Delete().from(Trace::class.java).where("name=?", name).execute<Model>()
            //else if (trace==null)
            //trace = Model.load (Trace.class, idTrace.longValue ());
            trace.data = dataTrace
            trace.start = position!!.start?:TraceEndVM.start
            trace.end = position!!.end
            trace.name = name
            trace.save()
            val traceNew = DbFunctions.getTraceByName(name)
            if (traceNew == null && traceOld != null) {
                Delete().from(Trace::class.java).where("name=?", name).execute<Model>()
                traceOld.save()
                showAllert(("Не удалось сохранить маршрут с именем: \""
                        + name) + "\"" + ". Откатано до прежнего")
                return false
            } else if (traceNew == null) {
                Delete().from(Trace::class.java).where("name=?", name).execute<Model>()
                showAllert("Внимание база данных повреждена!!! " +
                        "Для починки маршрут с именем: \"" + name + "\"" + " был удален")
            }
        return true
        }
        return false
    }

    companion object {
        private val traceDrawMonitor = Object()
        @JvmField
        var isOffline = false
        private var cntRun = 0
        private const val maxFailures = 3
        private var failuresCnt = 0

        @JvmStatic
        fun updateOfflineState(ctx: Context) {
            val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
            if (config.offline == ctx.getString(R.string.offlineOn)) isOffline = true else if (config.offline == ctx.getString(R.string.offlineOff)) isOffline = false
            println(" config is $config")
        }

        private const val currentName = "last_trace"
        private const val currentNameOffline = "last_offline_trace"
    }

    override val layoutId
        get() = R.layout.activity_maps

    override fun initComponent() {
        AndroidInjection.inject(this)
    }

    @Inject
    lateinit var model: MapModel

    override fun onInitBinding(binding: ActivityMapsBinding?) {
//        switchToFragment(R.id.mapFragment, ru.android.zheka.fragment.Map())
        switchToFragment(R.id.mapFragment, HideMap())
        model.actvity = this
    }

    override fun onResumeBinding(binding: ActivityMapsBinding?) {
    }

    override fun onDestroyBinding(binding: ActivityMapsBinding?) {
    }
}