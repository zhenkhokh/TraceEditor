package ru.android.zheka.gmapexample1;

import java.lang.annotation.Target;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.DataTrace;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.db.UtileTracePointsSerializer;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.jsbridge.JavaScriptMenuHandler;
import ru.android.zheka.jsbridge.JsCallable;
import ru.android.zheka.route.Route;
import ru.android.zheka.route.Routing;
import ru.android.zheka.route.RoutingListener;
import ru.zheka.android.timer.PositionReciever;

import java.lang.InstantiationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequestCreator;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
//import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends RoboFragmentActivity implements OnMapReadyCallback
, RoutingListener
, JsCallable
, OnCameraChangeListener {
	public static final String GO_POSITION = "goPosition";
	public static final String TRACE = "trace";
	//public static final String SAVE_POINT = "savePoint";
	public static final String SAVE_TRACE = "saveTrace";
	public static final String GEO = "geo";
	public static final String HOME = "home";
	Context context = this;
    private GoogleMap mMap=null;
    protected Routing routing;
    PositionInterceptor position;
    @InjectView(R.id.webViewMaps)
    WebView webView;
	protected String url =  "file:///android_asset/map.html";
	protected int resViewId = R.layout.activity_maps;//R.layout.activity_maps;
	protected DataTrace dataTrace = new DataTrace();
	private boolean onRoutingReady = false;
	private int cnt=0;
	private int cntCtrl=0;
	LatLng prevPoint=null;
	LatLng point=null;
	Class clMain, clGeo, clTrace;
	Config config=null;
	//TimerService timerService=TimerService.getInstance();
	PositionReciever positionReciever = null;

	public static class MySaveDialog extends SaveDialog{

		public MapsActivity map;
		public PositionInterceptor position;
		public DataTrace dataTrace;

		@Override
		protected void positiveProcess() {
			Trace trace = new Trace();
			if (dataTrace!=null
					&& position.start!=null
					&& position.end!=null){
				trace.data = dataTrace;
				trace.start = position.start;
				trace.end = position.end;
				trace.name = nameField.getText().toString();
				AlertDialog dialog = new AlertDialog("");
				if (trace.name.isEmpty()){
					//Toast.makeText(GeoPositionActivity.this, "text must not be empty", 15);
					dialog.msg = "Отсутсвует текст, введите название";
					dialog.show(getFragmentManager(), "Ошибка");
					return;
				}
				if (DbFunctions.getTraceByName(trace.name)!=null){
					dialog.msg = "Маршрут с таким именем существует";
					dialog.show(getFragmentManager(), "Ошибка");
					return;
				}
				try{DbFunctions.add(trace);
				}catch(java.lang.InstantiationException e){
					e.printStackTrace();
				}catch(IllegalAccessException e){
					e.printStackTrace();
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			}else{
				Toast.makeText(map, "trace is not initialized", 15);
			}
		}

		@Override
		protected SaveDialog newInstance() {
			return this;
		}

	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resViewId);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        System.out.println("map fragment is got "+mapFragment);
        mapFragment.getMapAsync(this);
        System.out.println("async map");

        MenuHandler m = new MenuHandler();
        m.initJsBridge(this, url);  
        position = new PositionInterceptor(this);        
        Class[] classes = loadClasses("ru.android.zheka.gmapexample1.MainActivity"
        		,"ru.android.zheka.gmapexample1.GeoPositionActivity"
        		,"ru.android.zheka.gmapexample1.TraceActivity"
        		,"ru.android.zheka.db.Config");
        clMain = classes[0];
        clGeo = classes[1];
        clTrace = classes[2];
		config  = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
				, Config.class);
		TextView coordinate = (TextView) findViewById(PositionInterceptor.resViewId);
		System.out.println("get config");
		coordinate.setVisibility(View.GONE);
		//coordinate.setText("");
		//coordinate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));		
		if(config!=null)
			if(config.uLocation){
				coordinate.setVisibility(View.VISIBLE);
				//coordinate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		if (TimerService.interrupted){
			System.out.println("start timer-service");
			Intent intent = new Intent(TimerService.BROADCAST_ACTION);
			intent.setClass(this,TimerService.class);
			this.startService(intent);
			//System.out.println("timeService is "+timerService);
			//timerService =  getSystemService(TimerService.class);
			//System.out.println("timeService is "+timerService);
			//timerService.startService(intent);
			//timerService.startService(intent);
		}
		
		System.out.println("end onCreate");
	}

	private Class[] loadClasses(String ...strings ) {
		Class[] classes = new Class[strings.length]; 
		for (int i=0;i<strings.length;i++){
	         try {
	        	    classes[i] = Class.forName(strings[i]);
	        	    System.out.println("----------  from "+ getClass().getName() +" find  "+strings[i]);
	        	}catch (ClassNotFoundException e){
	        	    System.out.println("---------- from "+getClass().getName()+" "+e.getMessage());
	        	}
		}
		return classes;
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
    	/*
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    	// MapInitilizer is not require here
    	try{position.positioning();    	
    	}catch(Exception e){
    		Toast.makeText(this, "Нет переданного местоположения", 15).show();
		    e.printStackTrace();
		}
    	if(positionReciever==null){
			positionReciever = new PositionReciever(googleMap, position);
			//LocalBroadcastManager.getInstance(this).registerReceiver(positionReciever
			//		, new IntentFilter(TimerService.BROADCAST_ACTION));
			TimerService.mListners.add(positionReciever);
    	}
    	//if (this.center==null)
    	//	this.center = PositionUtil.getGeoPosition(this);
    	//CameraUpdate center = CameraUpdateFactory.newLatLng(this.center);
       //CameraUpdate zoom =  CameraUpdateFactory.zoomTo(this.zoom);
        System.out.println("zoom and center are initiated");
        if (googleMap!=null){
            mMap = googleMap;
            System.out.println("position.centerPosition:"+position.centerPosition
            		+" position.zoom:"+position.zoom);
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition
					.Builder()
					.target(position.centerPosition)
					.zoom(position.zoom)
					.build()));
        	if (mMap.getUiSettings()!=null){
				mMap.getUiSettings().setZoomControlsEnabled(true);
	        	mMap.getUiSettings().setZoomGesturesEnabled(true);
	        	mMap.getUiSettings().setAllGesturesEnabled(true);
	        	mMap.getUiSettings().setMapToolbarEnabled(true);
	        	mMap.getUiSettings().setMyLocationButtonEnabled(true);
        	}else
        		Toast.makeText(this, "Панель не работает", 15).show();
        }else{
            //Toast.makeText(this, "Map is null", 15).show();
        	AlertDialog dialog = new AlertDialog("map is null");
        	dialog.show(getFragmentManager(),"Ошибка");
            return;
        }
        routing = new Routing();
        //(Routing.TravelMode.WALKING);
        routing.registerListener(this);
        // to know start==end
		prevPoint = position.start;
		point = position.end;
        if (prevPoint!=null && point!=null){
        	if (position.extraPoints.size()<=1)
        		routing.execute(prevPoint, point);
        	else{
        		//runOnUiThread(new Runnable() {
        		Runnable r = new Runnable(){
					@Override
					public void run() {
		        		cnt=0;
		        		cntCtrl=0;
		        		if (config.optimization){
			        		ArrayList<LatLng> wayPoints = new ArrayList<LatLng>();
			        		wayPoints.add(position.start);
			        		wayPoints.add(position.end);
			        		for (Iterator iterator = position.extraPoints.iterator(); iterator
			        				.hasNext();) {
								String sPoint = (String) iterator.next();
								point = (LatLng)(new UtilePointSerializer().deserialize(sPoint));
								wayPoints.add(point);
			        		}
		        			wayPoints.remove(wayPoints.size()-1);//end point
		        			routing = new Routing();
							AsyncTask<LatLng, Void, Route> task = routing.execute(wayPoints.toArray(new LatLng[0]));
							Route route = null;
							try{route = task.get();							
							}catch(InterruptedException e){
								e.printStackTrace();
							}catch (ExecutionException e) {
								e.printStackTrace();
							}
							if (route==null){
								AlertDialog dialog = new AlertDialog("Слишком длинная задача, уменьшите число промежуточных точек");
								dialog.show(getFragmentManager(), "Ошибка");
								return;
							}
							ArrayList<Integer> order = route.getOrder();
							System.out.println("order is "+order);
							if (order.size()==position.extraPoints.size()-1)
								System.out.println("order.size() is fine");
							ArrayList<String> temp = new ArrayList<String>();
							for (Iterator iterator = order.iterator(); iterator
									.hasNext();) {
								iterator.next();
								temp.add("");
							}
							int index_= 0;
							for (Iterator iterator = order.iterator(); iterator
									.hasNext();) {
								Integer index = (Integer) iterator.next();
								System.out.println("index="+index+", index_="+index_);
								temp.set(index
										,position.extraPoints.get(index_++));
							}
							position.extraPoints = temp;
							//add end point
							position.extraPoints.add((String)new UtilePointSerializer().serialize(position.end));
					        routing = new Routing();
					        routing.registerListener(MapsActivity.this);
		        		}
		        		for (Iterator iterator = position.extraPoints.iterator(); iterator
								.hasNext();) {
							String sPoint = (String) iterator.next();
							point = (LatLng)(new UtilePointSerializer().deserialize(sPoint));
							routing.execute(prevPoint, point);
							//Cannot execute task: the task is already running
					        routing = new Routing();
					        routing.registerListener(MapsActivity.this);
					        synchronized (MapsActivity.this) {
								System.out.println("wait for onRoutingSuccess cnt="+cnt);
								while(!onRoutingReady){
									try{MapsActivity.this.wait();							
									}catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
						        onRoutingReady = false;
							}
							prevPoint = point;
							cnt++;
						}
					}
				};
        		Thread thread = new Thread(r);
        		thread.start();
        	}
        }else{
        	//Toast.makeText(this.context, "start or end trace points are not defined", 15);
	    	AlertDialog dialog = 
	    			new AlertDialog(
	    					"start or end trace points are not defined");
	    	dialog.show(getFragmentManager(),"Ошибка");
    	}
        System.out.println("end of onMapReady");
    }


	@Override
	public void onRoutingFailure() {
		System.out.println("onRoutingFailure");
	}


	@Override
	public void onRoutingStart() {
		System.out.println("onRoutingStart");
	}


	@Override	
	public void onRoutingSuccess(PolylineOptions mPolyOptions) {
		System.out.println("befor synchronize block in onRoutingSuccess");
		synchronized (this) {
			System.out.println("after synchronize block in onRoutingSuccess");

		
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE);
        polyoptions.width(10);
        if (getIntent().getStringExtra(TRACE)!=null){
        	Intent intent = getIntent();
        	String traceData = intent.getStringExtra(TRACE);
        	PolylineOptions p = (PolylineOptions) new UtileTracePointsSerializer().deserialize(traceData);
        	intent.removeExtra(TRACE);// do not load twice
        	polyoptions.addAll(p.getPoints());
        	Toast.makeText(this.context, "add trace from db", 15);
        }else{
            polyoptions.addAll(mPolyOptions.getPoints());
            Toast.makeText(this.context, "add routed trace", 15);
        }        	
        mMap.addPolyline(polyoptions);
        dataTrace.allPoints = polyoptions;
        dataTrace.extraPoints = position.extraPoints;
        
        // Start marker
        MarkerOptions options = new MarkerOptions();
        LatLng markerPosition = null;
        if(prevPoint!=null)
        	markerPosition = prevPoint;
        else
        	markerPosition = position.centerPosition;
        options.position(markerPosition);
        if (cnt!=cntCtrl)
        	System.out.println("cnt!=cntCtrl - error cnt="+cnt+" cntCtrl="+cntCtrl);
        if (cnt==0)
        	options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        else
        	options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_red));
        if (cnt!=0){//TODO make visible snippet
        	options.title("Extra");
        	options.snippet(String.valueOf(cnt)+" "+getName(prevPoint));
        	options.visible(true);
        }
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        if(point!=null)
        	markerPosition = point;
        else
        	markerPosition = position.centerPosition;
        options.position(markerPosition);
        //extra
    	options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_red));
    	//end
        if (cnt==0) 
        	options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        if (position.extraPoints!=null&&cnt==position.extraPoints.size()-1)
        	options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
//DO NOT write end
        mMap.addMarker(options);
        onRoutingReady = true;
        notify();
		}
		System.out.println("end of onRoutingSuccess, cntCtrl is "+cntCtrl+" cnt is "+cnt);
        cntCtrl++;
	}
	private String getName(LatLng point){
		String out = DbFunctions.getNamePointByData(point);
		if(out==null)
			return "";
		return out;
	}
    public void nextView(String val) {

        Intent intent = position.updatePosition();//new Intent();
        if (val.contentEquals(HOME)) {
            Toast.makeText(this, "Home view called " + val, 15).show();
            intent.setClass(this.context, clMain);
	        intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
            finish();
        }
        if (val.contentEquals(GEO)) {
            Toast.makeText(this, "Geoposition view called: " + val, 15).show();
        	Intent mapIntent = position.updatePosition();//new Intent(Intent.ACTION_VIEW, geoUri);
        	mapIntent.setAction(Intent.ACTION_VIEW);
        	mapIntent.setClass(this.context, clGeo);
            startActivity(mapIntent);
            finish();
         }
        // SQLite pojo: name, (?)trace, (String)traceLight  
        if (val.contentEquals(SAVE_TRACE)) {
        	MySaveDialog dialog = (MySaveDialog)(new MySaveDialog().newInstance(R.string.hint_dialog_trace));
        	dialog.map = MapsActivity.this;
        	dialog.dataTrace = dataTrace;
        	dialog.position = position;
			//dialog.show(getSupportFragmentManager(), "dialog");
			dialog.show(getFragmentManager(), "dialog");
         }
	    if (val.contentEquals(TRACE)) {
	    	Toast.makeText(this, "Trace view called: " + val, 15).show();
	        intent.setClass(this.context, clTrace);
	        startActivity(intent);
	        finish();
	    }
	    if (val.contains(GO_POSITION)){
    		runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					if (mMap!=null){
						mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition
							.Builder()
							.target(position.getLocation())
							.zoom(position.zoom).build()));
						if (config.uLocation)
							position.updateUILocation();
						System.out.println("from geo: move to "+position.getLocation());
					}
					//mMap.moveCamera(update);
				}
			});
	    }
      }

	@Override
	public WebView getVebWebView() {
		return webView;
	}
	@Override
	public void onCameraChange(CameraPosition position) {
		if (position!=null){
			this.position.zoom = position.zoom;
			this.position.centerPosition = position.target;
			System.out.println("MapsActivity position:"
					+this.position.centerPosition.latitude+" "
					+this.position.centerPosition.longitude);
		}
	}
	/*
	 * locale
	 * @see roboguice.activity.RoboFragmentActivity#onStart()
	 */
	protected void onStart() {
	    position.mGoogleApiClient.connect();
	    super.onStart();
	}
	/*
	 * locale
	 * @see roboguice.activity.RoboFragmentActivity#onStart()
	 */
	protected void onStop() {
	    position.mGoogleApiClient.disconnect();
	    TimerService.mListners.remove(positionReciever);
	    super.onStop();
	}
}


