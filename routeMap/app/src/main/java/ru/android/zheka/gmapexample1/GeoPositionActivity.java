package ru.android.zheka.gmapexample1;

import java.nio.file.attribute.PosixFilePermission;

import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.gmapexample1.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.jsbridge.JavaScriptMenuHandler;
import ru.android.zheka.jsbridge.JsCallable;
import ru.zheka.android.timer.PositionReciever;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeoPositionActivity extends RoboFragmentActivity implements OnMapReadyCallback
	,JsCallable
	,OnMapLongClickListener
	,OnCameraChangeListener
	,OnMarkerClickListener
	,OnMarkerDragListener{
	public static final String ADD_WAYPOINTS = "addWaypoints";
	public static final String TRACE = "trace";
	public static final String MAP = "map";
	public static final String SAVE_POINT = "savePoint";
	public static final String POINTS = "points";
	public static final String HOME = "home";
	public static final String OFFLINE = "offline";

	Class clTrace, clMap, clPoints, clMain, clWayPoints;
	Context context = this;
	// choose for true statement
	PositionInterceptor position;
	GoogleMap mMap=null;
    @InjectView(R.id.webViewMaps)
    WebView webViewHome;
    Config config = null;
	protected String url =  "file:///android_asset/geo.html";
	protected int resViewId = R.layout.activity_maps;
	static boolean ready = false;
	//TimerService timerService = TimerService.getInstance();
	PositionReciever positionReciever = null;
	SingleChoiceDialog dialog = new MyDialog();
	MySaveDialog saveDialog = new MySaveDialog();
	static Object monitor = new Object();
	static String msg = "";
	private MapTypeHandler mapType;

	public static class MyDialog extends SingleChoiceDialog{
		public MyDialog(){
			super("Маршрут не закончен. Хотите закончить?"
					,R.string.cancel_plot_trace
					,R.string.ok_plot_trace);
		}
		@Override
		public void positiveProcess() {
			synchronized (monitor) {
				GeoPositionActivity.msg = "yes";
				ready = true;
				monitor.notify();
			}
		}
		@Override
		public void negativeProcess() {
			synchronized (monitor) {
				GeoPositionActivity.msg = "no";
				ready = true;
				monitor.notify();
			}
		}
	}
	public static class MySaveDialog extends SaveDialog{
        public PositionInterceptor position;
		@Override
		protected void positiveProcess() {
			System.out.println("start positiveProcess");
			Point point = new Point();
			point.data = position.centerPosition;
			point.name = nameField.getText().toString();
			AlertDialog dialog = new AlertDialog("");
			if (point.name.isEmpty()){
				//Toast.makeText(GeoPositionActivity.this, "text must not be empty", 15);
				dialog.msg = "Отсутсвует текст, введите название";
				dialog.show(getFragmentManager(), "Ошибка");
				return;
			}
			if (DbFunctions.getPointByName(point.name)!=null){
				dialog.msg = "Точка с таким именем существует";
				dialog.show(getFragmentManager(), "Ошибка");
				return;
			}
			System.out.println("start adding point "+point.toString());
			try{DbFunctions.add(point);
			}catch(java.lang.InstantiationException e){
				e.printStackTrace();
			}catch(IllegalAccessException e){
				e.printStackTrace();
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			System.out.println("end positiveProcess");
		}

		@Override
		protected SaveDialog newInstance() {
			return this;
		}
	}

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(resViewId);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        System.out.println("map fragment is got "+mapFragment);
        mapFragment.getMapAsync(this);
		MenuHandler m = new MenuHandler();
		m.initJsBridge(this,url);
		MapsActivity.updateOfflineState (this);
		//getCenter(getIntent()); //do not try init marker<= marker is null
        position = new PositionInterceptor(this);
        //TODO remove print
		Intent geoIntent = getIntent();
        System.out.println("GeoPosition geoIntent deliver");
        System.out.println((Uri)geoIntent.getData());
        System.out.println((Uri)geoIntent.getParcelableExtra("start"));
        System.out.println((Uri)geoIntent.getParcelableExtra("end"));
        
        try {
            clTrace = Class.forName("ru.android.zheka.gmapexample1.TraceActivity");
            System.out.println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.TraceActivity");
        }catch (ClassNotFoundException e){
            System.out.println("---------- from GeoPositionActivity "+e.getMessage());
        }
        try {
            clMap = Class.forName("ru.android.zheka.gmapexample1.MapsActivity");
            System.out.println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.MapsActivity");
        }catch (ClassNotFoundException e){
            System.out.println("---------- from GeoPositionActivity "+e.getMessage());
        }
        try {
            clMain = Class.forName("ru.android.zheka.gmapexample1.MainActivity");
            System.out.println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.MainActivity");
        }catch (ClassNotFoundException e){
            System.out.println("---------- from GeoPositionActivity "+e.getMessage());
        }
        try {
            clPoints = Class.forName("ru.android.zheka.gmapexample1.LatLngActivity");
            System.out.println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.LatLngActivity");
        }catch (ClassNotFoundException e){
            System.out.println("---------- from GeoPositionActivity "+e.getMessage());
        } 
        try {
            clWayPoints = Class.forName("ru.android.zheka.gmapexample1.WayPointsToTrace");
            System.out.println("----------  from GeoPositionActivity: find  ru.android.zheka.gmapexample1.WayPointsToTrace");
        }catch (ClassNotFoundException e){
            System.out.println("---------- from GeoPositionActivity "+e.getMessage());
        }
        
		config  = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
				, Config.class);
		TextView coordinate = (TextView) findViewById(PositionInterceptor.resViewId);
		System.out.println("get config");
		coordinate.setVisibility(View.GONE);
		if(config!=null)
			if(config.uLocation){
				coordinate.setVisibility(View.VISIBLE);
			}
		if (TimerService.interrupted){
			System.out.println("start timer-service");
			Intent intent = new Intent(TimerService.BROADCAST_ACTION);
			intent.setClass(this,TimerService.class);
			this.startService(intent);
			//timerService = getSystemService(TimerService.class);
			//System.out.println("timeService is "+timerService);
			//timerService.startService(intent);
		}
	}
	

	@Override
	public void nextView(String val) {
		Intent intent = position.updatePosition();
	    if (val.contentEquals(HOME)) {
	          intent.setClass(this.context, clMain);
	          intent.setAction(Intent.ACTION_VIEW);
	          startActivity(intent);
	          finish();
	    }
	    if (val.contentEquals(POINTS)) {
	          //back from LatLng to center
	          if (//position.state!=null&&
	        		  TraceActivity.isOtherMode(position.state))
	        	  position.state = TRACE_PLOT_STATE.CENTER_COMMAND;
	          intent = position.getNewIntent();
	          intent.setClass(this.context, clPoints);
	          intent.setAction(Intent.ACTION_VIEW);
	          startActivity(intent);
	          finish();
	    }
	    if (val.contentEquals(SAVE_POINT)) {
	          MySaveDialog dialog = (MySaveDialog)new MySaveDialog().newInstance(R.string.hint_dialog_point);
	          dialog.position = position;
			//dialog.show(getSupportFragmentManager(), "dialog");
			dialog.show(getFragmentManager(), "dialog");
	    }
	    if (val.contentEquals(MAP)) {	    	
	    	if (position.state!=TRACE_PLOT_STATE.CENTER_END_COMMAND){
				dialog.show(getFragmentManager(), "Сообщение");
				synchronized (monitor) {
	        		System.out.println("waiting dialog ...");
	        		while(!ready){
		        		try {
		            		monitor.wait();
						}catch (InterruptedException e) {
						 	e.printStackTrace();
						}
	        		}
					ready = false;
				}
				//finish trace
				if(msg.contains("yes")){
					return;
				}
				//else go to map
	    	}

				//if (//position.state!=null&&
				//		TraceActivity.isOtherMode(position.state))
				//	position.state = TRACE_PLOT_STATE.CENTER_START_COMMAND;
				  if ( (position.state != TRACE_PLOT_STATE.CENTER_END_COMMAND && position.start.equals (position.end)
						  || PositionUtil.LAT_LNG.equals (position.end) || position.end==null)
				  		 && position.getExtraPoints ().size ()>0)//TODO move to getNewIntent
				  	position.end = (LatLng) new UtilePointSerializer().deserialize (position.getExtraPoints ().get (position.getExtraPoints ().size ()-1));
				  if (position.end==null)
				  	if (position.centerPosition!=null)
				  		position.end = position.centerPosition;
				  	else
				  		position.end = position.start;
				  position.state = TRACE_PLOT_STATE.CENTER_START_COMMAND;
				  intent = position.getNewIntent();
				  //if (!position.extraPoints.isEmpty()){
					//	intent.putStringArrayListExtra(PositionUtil.EXTRA_POINTS, position.extraPoints);
		          intent.setClass(this.context, clMap);
		          intent.setAction(Intent.ACTION_VIEW);
		          if (MapsActivity.isOffline)
		          	intent.putExtra (PositionUtil.TITLE,OFFLINE);
		          startActivity(intent);
		          finish();
	    }
	    if (val.contentEquals(TRACE)) {
	        intent.setClass(this.context, clTrace);
	        startActivity(intent);
	        finish();
	    }
	    if (val.contentEquals(ADD_WAYPOINTS)) {
	        EditModel model = new EditModel();
	        model.clsName = "Point";
	        model.clsPkg = "ru.android.zheka.db";
	    	model.name1Id = R.string.points_column_name1;
	    	model.nameId = R.string.points_column_name;
	    	intent.putExtra(EditActivity.EDIT_MODEL, model);
	    	intent.setAction(Intent.ACTION_VIEW);
	    	intent.setClass(this.context, clWayPoints);
	    	startActivity(intent);
	    	finish();
	    }
	    if (val.equals (OFFLINE)){
	    	MapsActivity.isOffline = MapsActivity.isOffline?false:true;
	    	if (MapsActivity.isOffline)
	    		Toast.makeText(this, "Офлайн загрузка включена", 15).show();
	    	else
	    		Toast.makeText(this, "Офлайн загрузка отключена", 15).show();
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		Intent intent=null;
		try{intent = position.positioning();//getIntent();		
		}catch(Exception e){
		       Toast.makeText(this, "Нет переданного местоположения", 15).show();
		       e.printStackTrace();			
		}
    	if(positionReciever==null){	
			positionReciever = new PositionReciever(map, position);
			//LocalBroadcastManager.getInstance(this).registerReceiver(positionReciever
			//		, new IntentFilter(TimerService.BROADCAST_ACTION));
			TimerService.mListners.add(positionReciever);
    	}
		Intent geoIntent = getIntent();
        System.out.println("GeoPosition geoIntent deliver");
        System.out.println((Uri)geoIntent.getData());
        System.out.println((Uri)geoIntent.getParcelableExtra("start"));
        System.out.println((Uri)geoIntent.getParcelableExtra("end"));

		if (map!=null){
	        mMap = map;
			position.markerCenter = map.addMarker(new MarkerOptions().position(position.centerPosition)
							.title(position.title)
							.snippet(position.tracePointName)
							.draggable(true));
			//center = CameraUpdateFactory.newLatLng(centerPosition);
			//zoom = CameraUpdateFactory.zoomBy(positionUtil.getZoom());
			System.out.println("geoPosition.onMapReadymove camera to "+position.centerPosition.latitude
					+" "+position.centerPosition.longitude);
			//map.moveCamera(center);
			//map.animateCamera(zoom);
			mapType = new MapTypeHandler (MapTypeHandler.userCode);
			mMap.setMapType (mapType.getCode ());
			map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition
					.Builder()
					.target(position.centerPosition)
					.zoom(position.zoom).build()));
			if (mMap.getUiSettings()!=null) {
				mMap.getUiSettings ().setZoomControlsEnabled (true);
				mMap.getUiSettings ().setMapToolbarEnabled (true);
			}else
        		Toast.makeText(this, "Панель не работает", 15).show();
			map.setOnCameraChangeListener(this);
			map.setOnMapLongClickListener(this);
			map.setOnMarkerClickListener(this);
			map.setOnMarkerDragListener(this);
		}else
			Toast.makeText(this, "map is not ready: null pointer exception", 15);
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		if (position!=null){
			this.position.markerCenter.setPosition(position.target);
			this.position.zoom = position.zoom;
			this.position.centerPosition = position.target;
			if (config.uLocation)
				this.position.updateUILocation();
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
		if (point!=null)
			position.markerCenter = mMap.addMarker(new MarkerOptions().position(point)
					.draggable(true));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker!=null){
			marker.remove();
			return true;
		}
		return false;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// stub	
		
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// stub		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		//TODO advice to remove marker
	}

	@Override
	public WebView getVebWebView() {
		return webViewHome;
	}
	/*
	 * locale
	 * @see roboguice.activity.RoboFragmentActivity#onStart()
	 */
	protected void onStart() {
		config  = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
				, Config.class);
		if(positionReciever!=null){
			if(!config.tenMSTime.equals (getString (R.string.timerdata1)))
				if(!TimerService.mListners.contains (positionReciever))
					TimerService.mListners.add(positionReciever);
		}
		super.onStart();
	}

	@Override
	protected void onPause(){
		if (positionReciever!=null)
			TimerService.mListners.remove (positionReciever);
		super.onPause ();
	}
	/*
	 * locale
	 * @see roboguice.activity.RoboFragmentActivity#onStart()
	 */
	protected void onStop() {
	    position.mGoogleApiClient.disconnect();
	    if (positionReciever!=null)
	    	TimerService.mListners.remove(positionReciever);
	    super.onStop();
	}
}
