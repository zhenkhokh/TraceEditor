package ru.android.zheka.gmapexample1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
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
import roboguice.inject.InjectView
import ru.android.zheka.coreUI.AbstractActivity
import ru.android.zheka.db.*
import ru.android.zheka.fragment.Home
import ru.android.zheka.jsbridge.JsCallable
import ru.android.zheka.route.*
import ru.android.zheka.route.BellmannFord.MissMatchDataException
import ru.android.zheka.route.BellmannFord.NoDirectionException
import ru.zheka.android.timer.PositionReciever
import java.util.*
import java.util.concurrent.ExecutionException
import javax.inject.Inject

/*
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequestCreator;
import com.google.android.gms.location.LocationServices;
*/
//import android.app.AlertDialog;
class MapsActivity //extends AppCompatActivity
    : AbstractActivity<ViewDataBinding>(),HasAndroidInjector, OnMapReadyCallback, RoutingListener, OnCameraChangeListener {
    private val resTextId: Int = R.id.coordinateTextGeo
    private var traceDebugging: DataTrace? = null
    private var traceDebuggingSer: String? = null
    var context_: Context = this
    private var mMap: GoogleMap? = null
    protected var routing: Routing? = null
    @JvmField
	var position: PositionInterceptor? = null

    protected var url = "file:///android_asset/map.html"
    protected var resViewId = R.layout.activity_geo //R.layout.activity_maps;
    var dataTrace: DataTrace? = DataTrace()
    private var onRoutingReady = false
    private var cnt = 0
    private var cntCtrl = 0
    private var rateLimit_ms = 800
    private var prevPoint: LatLng? = null
    private var point: LatLng? = null
    private var clMain: Class<*>? = null
    private var clGeo: Class<*>? = null
    private var clTrace: Class<*>? = null
    private var config: Config? = null

    //TimerService timerService=TimerService.getInstance();
    var positionReciever: PositionReciever? = null
    var options: MarkerOptions? = null
    var cursorMarker: Marker? = null
    private var mapType = MapTypeHandler(MapTypeHandler.userCode)
    @JvmField
	var results = ResultRouteHandler(-1) // not available
    var replaceDialog = ReplaceDialog()
    var wayPoints = ArrayList<LatLng>()

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector!!
    }

    fun fetchWayPoints(): ArrayList<LatLng> {
        wayPoints = ArrayList()
        if (isFakeStart) wayPoints.add(position!!.start) else wayPoints.add(position!!.centerPosition)
        val iterator: Iterator<*> = position!!.extraPoints.iterator()
        while (iterator
                        .hasNext()) {
            val sPoint = iterator.next() as String
            val point = UtilePointSerializer().deserialize(sPoint) as LatLng
            val rPoint = BellmannFord.round(point)
            if (rPoint != BellmannFord.round(position!!.start)
                    && rPoint != BellmannFord.round(position!!.end)) wayPoints.add(point)
        }
        wayPoints.add(position!!.end)
        return wayPoints
    }

    class MySaveDialog : SaveDialog() {
        var map: MapsActivity? = null
        var position: PositionInterceptor? = null
        var dataTrace: DataTrace? = null
        override fun positiveProcess() {
            if (dataTrace != null && position!!.start != null && position!!.end != null) {
                name = nameField!!.text.toString()
                val dialog = AlertDialog("")
                if (name!!.isEmpty()) {
                    //Toast.makeText(GeoPositionActivity.this, "text must not be empty", 15);
                    dialog.msg = "Отсутсвует текст, введите название"
                    dialog.show(fragmentManager, "Ошибка")
                    return
                }
                if (DbFunctions.getTraceByName(name) != null) {
                    //dialog.msg = "Маршрут с таким именем существует, повторите сохранение";
                    //dialog.show(getFragmentManager(), "Ошибка");
                    val replaceDialog = map!!.replaceDialog!!
                    replaceDialog.map = map
                    replaceDialog.show(map!!.fragmentManager, "dialog")
                    return
                }
                if (!map!!.saveOrReplaceTrace(name)) {
                    Toast.makeText(map, "Маршрут не задан, сохранение отменено", 15).show()
                }
            } else {
                Toast.makeText(map, "Маршрут не инициализирован", 15).show()
            }
        }

        override fun newInstance(): SaveDialog {
            return this
        }

        companion object {
            var name: String? = null
        }
    }

    class ReplaceDialog : SingleChoiceDialog("Заменить существующий маршрут?", R.string.cancel_plot_trace
            , R.string.ok_plot_trace) {
        var map: MapsActivity? = null
        override fun positiveProcess() {
            if (!map!!.saveOrReplaceTrace(MySaveDialog.name)) {
                Toast.makeText(map, "Маршрут не задан, сохранение отменено", 15).show()
            }
        }

        override fun negativeProcess() {}
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resViewId)
        switchToFragment(R.id.geoFragment, ru.android.zheka.fragment.Map())
        updateOfflineState(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = getSupportFragmentManager()
                .findFragmentById(R.id.map) as SupportMapFragment
        println("map fragment is got $mapFragment")
        mapFragment.getMapAsync(this)
        println("async map")
        position = PositionInterceptor(this, resTextId)
        val classes = loadClasses("ru.android.zheka.gmapexample1.MainActivity"
                , "ru.android.zheka.gmapexample1.GeoPositionActivity"
                , "ru.android.zheka.gmapexample1.TraceActivity"
                , "ru.android.zheka.db.Config")
        clMain = classes[0]
        clGeo = classes[1]
        clTrace = classes[2]
        config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        rateLimit_ms = config!!.rateLimit_ms.toBigDecimal().intValueExact()
        val coordinate = findViewById(resTextId) as TextView
        println("get config: $config")
        coordinate.visibility = View.GONE
        //coordinate.setText("");
        //coordinate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (config != null) if (config!!.uLocation) {
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

    private fun loadClasses(vararg strings: String): Array<Class<*>?> {
        val classes: Array<Class<*>?> = arrayOfNulls(strings.size)
        for (i in 0 until strings.size) {
            try {
                classes[i] = Class.forName(strings[i])
                println("----------  from " + javaClass.name + " find  " + strings[i])
            } catch (e: ClassNotFoundException) {
                println("---------- from " + javaClass.name + " " + e.message)
            }
        }
        return classes
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
            //Toast.makeText(this, "Map is null", 15).show();
            val dialog = AlertDialog("map is null")
            dialog.show(getFragmentManager(), "Ошибка")
            return
        }
        routing = Routing()
        //(Routing.TravelMode.WALKING);
        routing!!.registerListener(this)
        // to know start==end
        position!!.centerPosition = PositionUtil.getGeoPosition(this) // <---?
        //position.centerPosition = position.getLocation ();
        prevPoint = if (!isFakeStart) {
            position!!.centerPosition //position.start;
            //position.start = position.centerPosition;// <---?
        } else {
            position!!.start
        }
        point = position!!.end
        if (prevPoint != null && point != null) {
            if (position!!.extraPoints.size <= 1) {
                results = ResultRouteHandler(1)
                traceDebuggingSer = getIntent().getStringExtra(PositionUtil.TITLE)
                if (isOffline && (traceDebuggingSer == null || traceDebuggingSer == GeoPositionActivity.OFFLINE)) {
                    offlineIncorrectData()
                    return
                } else if (isOffline) {
                    traceDebugging = UtileTracePointsSerializer().deserialize(traceDebuggingSer) as DataTrace
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
                        val isBellman = config!!.bellmanFord == Application.optimizationBellmanFlag
                        if ((config!!.optimization
                                        || isBellman) && !isOffline) {
                            //ArrayList<LatLng> wayPoints = new ArrayList<LatLng>();
                            fetchWayPoints()
                            if (isBellman) {
                                bellManPoits = wayPoints
                                /*int iEnd = bellManPoits.indexOf(position.end);
								if (iEnd < bellManPoits.size ()-1 &&
										bellManPoits.subList (iEnd+1,bellManPoits.size ()).contains (position.end)
										) {
									bellManPoits.remove (position.end);
								}
								iEnd = bellManPoits.indexOf(position.end);
								// end must be once and in the end
								if (iEnd<bellManPoits.size ()-1){
									bellManPoits.remove(position.end);
									bellManPoits.add(position.end);
								}
								*/for (i in 1..2) bellManPoits.remove(position!!.end)
                                bellManPoits.add(position!!.end)
                                bellManPoits = ArrayList(Arrays.asList(
                                        *BellmannFord.process(bellManPoits.toTypedArray()))
                                )
                            } ///else
                            //	wayPoints.remove(wayPoints.size()-1);//end point
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
                                    val dialog = AlertDialog("Слишком длинная задача, уменьшите число промежуточных точек")
                                    dialog.show(getFragmentManager(), "Ошибка")
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
                                /*if (config.bellmanFord.equals ( Application.optimizationBellmanFlag)) {
								position.extraPoints = new ArrayList <String> ();
								for (int i=0; i < wayPoints.size ();i++) {
									position.extraPoints.add((String)new UtilePointSerializer().serialize(wayPoints.get(i)));
								}
							}else {*/
                                val iterator: Iterator<*> = order.iterator()
                                while (iterator
                                                .hasNext()) {
                                    val index = iterator.next() as Int
                                    println("index=$index, index_=$index_")
                                    temp[index] = position!!.extraPoints[index_++]
                                }
                                position!!.setExtraPointsFromCopy(temp)
                                //}
                                //add end point
                                //position.extraPoints.add((String)new UtilePointSerializer().serialize(position.end));
                                routing = Routing()
                                routing!!.registerListener(this@MapsActivity)
                            }
                        }
                        val iterator: Iterator<*>
                        if (isBellman && !isOffline) {
                            val iStart = bellManPoits.indexOf(position!!.start)
                            val rStart = BellmannFord.round(position!!.start)
                            for (i in 0 until iStart) {
                                val head = bellManPoits.removeAt(0)
                                if (rStart != BellmannFord.round(head)) bellManPoits.add(head)
                            }
                            /*LatLng tmp = bellManPoits.get(1);
		        			bellManPoits.set (1,bellManPoits.get (2));
		        			bellManPoits.set (2,tmp);

							bellManPoits.add (bellManPoits.get(bellManPoits.size ()-2));
							bellManPoits.add (position.end);*/
                            /*iterator = bellManPoits.iterator ();
							if (iterator.hasNext ())
								iterator.next ();//miss start
							*/
                            val tmp = ArrayList<String>()
                            val iterator1: Iterator<*> = bellManPoits.iterator()
                            if (iterator1.hasNext()) // miss start
                                iterator1.next()
                            while (iterator1.hasNext()) {
                                tmp.add(UtilePointSerializer().serialize(
                                        iterator1.next()
                                ) as String)
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
                                    .deserialize(traceDebuggingSer) as DataTrace
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
                        while ( //Iterator iterator = position.extraPoints.iterator(); iterator
                                iterator
                                        .hasNext()) {
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
                                routing = OfflineRouting(*segmentPoints)
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

//}while(cnt<cntRun);
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
            val dialog = AlertDialog(
                    "start or end trace points are not defined")
            dialog.show(getFragmentManager(), "Ошибка")
        }
        println("end of onMapReady")
    }

    private fun offlineIncorrectData() {
        val dialog = AlertDialog("Неверные данные для офлайн, маршрут не построен")
        dialog.show(getFragmentManager(), "Ошибка")
    }

    override fun onRoutingFailure() {
        println("onRoutingFailure")
        /*
		//cntRun++;
		Runnable r = new Runnable () {
			@Override
			public void run() {
				Iterator iterator = position.extraPoints.iterator ();
				int skip = 0;
				//TODO do not copy from previouse
				//cnt--;cntCtrl--;
				final int curCnt = cntRun+1;
				cntRun++;
				for (//Iterator iterator = position.extraPoints.iterator(); iterator
						; iterator
						.hasNext (); ) {
					if (skip++ >= cnt) {
						String sPoint = (String) iterator.next ();
						point = (LatLng) (new UtilePointSerializer ().deserialize (sPoint));
						try {
							if (curCnt == cntRun)
								routing.execute (prevPoint, point);
						}catch (IllegalStateException e){continue; }
							synchronized (traceDrawMonitor) {
								System.out.println ("wait for onRoutingSuccess cnt=" + cnt);
								int goFromDad = 3;
								while (!onRoutingReady && goFromDad-->0) {
									try {
										traceDrawMonitor.wait (rateLimit_ms);
									} catch (InterruptedException e) {
										e.printStackTrace ();
									}
								}
								onRoutingReady = false;
							}
						//Cannot execute task: the task is already running
						routing = new Routing ();
						routing.registerListener (MapsActivity.this);

						if (curCnt==cntRun) {
							prevPoint = point;
							cnt++;
						}
					} else
						prevPoint = (LatLng) (new UtilePointSerializer ().deserialize ((String) iterator.next ()));

				}

			}
		};
			Thread t = new Thread (r);
			t.start();
*/if (isOffline) {
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
                try {
                    val dialog = AlertDialog("Возможно высокая скорость построения. Получен неожиданный ответ от maps.googleapis.com:" + GoogleParser.result)
                    dialog.show(getFragmentManager(), "Ошибка")
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        } else {
            try {
                val dialog = AlertDialog("Маршрут не построен, проверьте интернет соединение")
                dialog.show(getFragmentManager(), "Ошибка")
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        /*			routing = new Routing ();
			routing.registerListener (MapsActivity.this);
			routing.execute (prevPoint, point);
*/
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
            if (position!!.extraPoints != null && cnt == position!!.extraPoints.size - 1) {
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

//    override fun nextView(`val`: String) {
//        var intent = position!!.updatePosition() //new Intent();
//        if (`val`.contentEquals(HOME)) {
//            intent.setClass(context_, clMain)
//            intent.action = Intent.ACTION_VIEW
//            startActivity(intent)
//            finish()
//        }
//        if (`val`.contentEquals(GEO)) {
//            val mapIntent = position!!.updatePosition() //new Intent(Intent.ACTION_VIEW, geoUri);
//            mapIntent.action = Intent.ACTION_VIEW
//            mapIntent.setClass(context_, clGeo)
//            startActivity(mapIntent)
//            finish()
//        }
//        // SQLite pojo: name, (?)trace, (String)traceLight
//        if (`val`.contentEquals(SAVE_TRACE)) {
//            val dialog = MySaveDialog().newInstance(R.string.hint_dialog_trace) as MySaveDialog
//            dialog.map = this@MapsActivity
//            dialog.dataTrace = dataTrace
//            dialog.position = position
//            //dialog.show(getSupportFragmentManager(), "dialog");
//            dialog.show(getFragmentManager(), "dialog")
//        }
//        if (`val` == TRACE) {
//            Toast.makeText(this, "Trace view called: $`val`", 15).show()
//            intent.setClass(context_, clTrace)
//            startActivity(intent)
//            finish()
//        }
//        if (`val`.contains(GO_POSITION)) {
//            goPosition(false)
//        }
//        if (`val`.contains(ADD_POINT)) {
//            Toast.makeText(this
//                    , "Для добавления путевой точки перейдите в местоположение и установите ее по центру, далее вернитесь к просмотру"
//                    , 25).show()
//        }
//        if (`val`.contains(REMOVE_POINT)) {
//            while (position!!.isWriteExtra == true) {
//            } // or remove, copy, block new writers&readers until finished
//            println("extraPoint before removing " + position!!.extraPoints)
//            var tmp = ArrayList(position!!.extraPoints)
//            val sz = tmp.size
//            if (sz < 2) {
//                Toast.makeText(this, "Нет путевых точек для удаления", 15).show()
//                return
//            }
//            val newStart = UtilePointSerializer().deserialize(tmp[0]) as LatLng
//            //String[] temp = Arrays.copyOf (position.getExtraPoints ().toArray (new String[0]),sz);//remove(0)
//            val temp = arrayOfNulls<String>(sz - 1)
//            System.arraycopy(position!!.extraPoints.toTypedArray()
//                    , 1, temp, 0, sz - 1)
//            if (position!!.isWriteExtra == true || sz != temp.size + 1) {
//                Toast.makeText(this, "Ресурс занят, повторите операцию", 15).show()
//                return
//            }
//            tmp = ArrayList(Arrays.asList(*temp))
//            position!!.setExtraPointsFromCopy(tmp)
//            println("extraPoint after removing " + position!!.extraPoints)
//            println("extraPoint tmp " + Arrays.asList(*temp) + " len=" + temp.size)
//            //LatLng end = position.end;
//            setIntent(intent.putStringArrayListExtra(PositionUtil.EXTRA_POINTS, tmp))
//            if (isOffline) {
//                //Trace trace = (Trace)DbFunctions.getModelByName (currentNameOffline,Trace.class);
//                var dataTrace = traceDebugging!!.copy(false)
//                if (dataTrace != null) {
//                    if (dataTrace.removeFirstSegment() == null) {
//                        Toast.makeText(this, "Данные сегмента не доступны", 15).show()
//                        return
//                    }
//                    dataTrace.extraPoints = tmp
//                    dataTrace = dataTrace.copy(true)
//                    position!!.title = UtileTracePointsSerializer().serialize(dataTrace) as String
//                } else {
//                    Toast.makeText(this, "Данные маршрута не доступны", 15).show()
//                    return
//                }
//            }
//            position!!.start = newStart
//            println("from remove way-point:position.end " + position!!.end + " position.start: " + position!!.start)
//            //if (position.end==null && point!=null )
//            //	position.end = point;
//            setIntent(position!!.newIntent)
//            intent = position!!.updatePosition()
//
//            //position.end = end;
//            intent.setClass(context_, MapsActivity::class.java)
//            intent.action = Intent.ACTION_VIEW
//            Toast.makeText(this, "Ближайшая путевая точка удалена", 15).show()
//            startActivity(intent)
//            finish()
//        }
//        if (`val` == FAKE_START) {
//            isFakeStart = if (isFakeStart) false else true
//            if (isFakeStart) {
//                Toast.makeText(this, "Задан псевдо старт", 15).show()
//            } else {
//                Toast.makeText(this, "Дан старт из местоположения", 15).show()
//            }
//            intent.setClass(context_, MapsActivity::class.java)
//            intent.action = Intent.ACTION_VIEW
//            startActivity(intent)
//            finish()
//        }
//        if (`val` == MAP_TYPE) {
//            //if (mMap==null) {
//            //	Toast.makeText (this, "Ошибка: карта не определена", 15).show ();
//            //	return;
//            //}
//            when (mapType.type) {
//                MapTypeHandler.Type.NORMAL -> {
//                    MapTypeHandler.userCode = GoogleMap.MAP_TYPE_SATELLITE
//                    Toast.makeText(this, "Изменена на спутниковую, поверните экран", 15).show()
//                }
//                MapTypeHandler.Type.SATELLITE -> {
//                    MapTypeHandler.userCode = GoogleMap.MAP_TYPE_TERRAIN
//                    Toast.makeText(this, "Изменена на рельефную, повторите просмотр", 15).show()
//                }
//                MapTypeHandler.Type.TERRAIN -> {
//                    MapTypeHandler.userCode = GoogleMap.MAP_TYPE_HYBRID
//                    Toast.makeText(this, "Изменена на гибридную, поверните экран", 15).show()
//                }
//                MapTypeHandler.Type.HYBRID -> {
//                    MapTypeHandler.userCode = GoogleMap.MAP_TYPE_NORMAL
//                    Toast.makeText(this, "Изменена на обычную, повторите просмотр", 15).show()
//                }
//            }
//            mapType = MapTypeHandler(MapTypeHandler.userCode)
//            //mMap.setMapType (mapType.getCode ());// setting has no effect
//            //goPosition (false);//?
//        }
//    }

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
                } /*
						mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition
							.Builder()
							.target(position.getLocation())
							.bearing (BellmannFord.bearing)//(float) (Math.random ()*360.0)
							.zoom(position.zoom).build()));
						if (config.uLocation)
							position.updateUILocation();
							*/
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
        config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        if (positionReciever != null) {
            if (config!!.tenMSTime != getString(R.string.timerdata1)) if (!TimerService.mListners!!.contains(positionReciever)) TimerService.mListners!!.add(positionReciever)
        }
        super.onStart()
    }

    override fun getActivity(): Activity {
        return  this
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

    override fun onDestroy() {
        //saveEmergency ();
        super.onDestroy()
    }

    private val traces: List<Trace>? = null

    //private static Long idTrace = new Long (-1);
    private fun saveOrReplaceTrace(name: String?): Boolean { // or use id, load and save
        if (dataTrace != null && position != null && position!!.start != null && position!!.end != null) {
            //new UtileTracePointsSerializer ().serialize (dataTrace).toString ().isEmpty ()
            if (dataTrace!!.extraPoints.size == 0) // null entity case
                return false
            val traceOld = DbFunctions.getTraceByName(name)  //back null if add lock
            //traces = new Select ().from (Trace.class).where ("name = ?",currentName).limit (1).execute ();
            //traces = SQLiteUtils.rawQuery (Trace.class, "SELECT * from Trace where name LIKE ?", new String[]{'%'+currentName+'%'});
            val trace = Trace()
            Delete().from(Trace::class.java).where("name=?", name).execute<Model>()
            //else if (trace==null)
            //trace = Model.load (Trace.class, idTrace.longValue ());
            trace.data = dataTrace
            trace.start = position!!.start
            trace.end = position!!.end
            trace.name = name
            trace.save()
            val traceNew = DbFunctions.getTraceByName(name)
            if (traceNew == null && traceOld != null) {
                Delete().from(Trace::class.java).where("name=?", name).execute<Model>()
                traceOld.save()
                val dialog = AlertDialog("Не удалось сохранить маршрут с именем: \""
                        + name + "\"" + ". Откатано до прежнего")
                dialog.show(getFragmentManager(), "Ошибка")
                return false
            } else if (traceNew == null) {
                Delete().from(Trace::class.java).where("name=?", name).execute<Model>()
                val dialog = AlertDialog("Внимание база данных повреждена!!! " +
                        "Для починки маршрут с именем: \"" + name + "\"" + " был удален")
                dialog.show(getFragmentManager(), "Ошибка")
            }

            //idTrace = trace.save ();
            /*try {
				DbFunctions.add (trace);
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace ();
			} catch (IllegalAccessException e) {
				e.printStackTrace ();
			} catch (IllegalArgumentException e) {
				e.printStackTrace ();
			}
			*/return true
        }
        return false
    }

    companion object {
        private val traceDrawMonitor = Object()
        const val GO_POSITION = "goPosition"
        const val TRACE = "trace"

        //public static final String SAVE_POINT = "savePoint";
        const val SAVE_TRACE = "saveTrace"
        const val GEO = "geo"
        const val HOME = "home"
        const val ADD_POINT = "addPoint"
        const val REMOVE_POINT = "removePoint"
        const val FAKE_START = "fakeStart"
        const val MAP_TYPE = "mapType"
        @JvmField
		var isOffline = false
        private var cntRun = 0
        private const val maxFailures = 3
        private var failuresCnt = 0
        private var isFakeStart = false
        @JvmStatic
		fun updateOfflineState(ctx: Context) {
            val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
            if (config.offline == ctx.getString(R.string.offlineOn)) isOffline = true else if (config.offline == ctx.getString(R.string.offlineOff)) isOffline = false
            println(" config is $config")
        }

        private const val currentName = "last_trace"
        private const val currentNameOffline = "last_offline_trace"
        private const val currentQuiry = "where name is $currentName"
    }

    override val layoutId
        get() = R.layout.activity_maps

    override fun initComponent() {
        AndroidInjection.inject(this)
    }

    override fun onInitBinding(binding: ViewDataBinding?) {
    }

    override fun onResumeBinding(binding: ViewDataBinding?) {
    }

    override fun onDestroyBinding(binding: ViewDataBinding?) {
    }
}