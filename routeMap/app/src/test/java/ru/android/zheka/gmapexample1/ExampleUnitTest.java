package ru.android.zheka.gmapexample1;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Configuration;
import com.activeandroid.Model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowCamera;
//import org.robolectric.shadows.gms.ShadowGooglePlayServicesUtil;
//import org.robolectric.shadows.gms.common.ShadowGoogleApiAvailability;

import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.db.UtileTracePointsSerializer;
import ru.android.zheka.gmapexample1.GeoPositionActivity;
import ru.android.zheka.gmapexample1.LatLngActivity;
import ru.android.zheka.gmapexample1.MainActivity;
import ru.android.zheka.gmapexample1.MapsActivity;
import ru.android.zheka.gmapexample1.PositionUtil;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.TraceActivity;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import static org.fest.assertions.api.ANDROID.assertThat;
//import org.robolectric.shadows.ShadowActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@Config(manifest = "app/src/main/AndroidManifest.xml"
//,shadows = {
//		ShadowActivity.class
		//ShadowSupportMapFragment.class
		//ShadowCamera.class
		//,ShadowGooglePlayServicesUtil.class
//		ShadowMapsActivity.class
//}
		)
//@RunWith(RobolectricTestRunner.class)
@RunWith(MyTestRunner.class)
public class ExampleUnitTest {
	MapsActivity mapsActivity=null;
	GeoPositionActivity geoPositionActivity = null;
	LatLngActivity latLngActivity = null;
	MainActivity mainActivity = null;
	TraceActivity traceActivity = null;
	SettingsActivity settingsActivity = null;
	android.support.v4.app.Fragment fragment;
	Point point;
	Trace trace;
	
	@Before
	public void setup(){
		point = new Point();
		trace = new Trace();
        Configuration dbConfiguration = new Configuration.Builder(Cache.getContext())
        .setDatabaseName("Navi.db")
        .addModelClasses(ru.android.zheka.db.Config.class,Point.class,Trace.class)
        .addTypeSerializers(UtilePointSerializer.class,UtileTracePointsSerializer.class)
        .create();
        ActiveAndroid.initialize(dbConfiguration);
        Application.initConfig();
		String[] names = {"1","2"};
		for (int i = 0; i < names.length; i++) {
	        String namePoint = names[i];
	        //dataTmp.add(map);
	        Point p = new Point();
	        p.name = namePoint;
	        p.data = (LatLng)new UtilePointSerializer().deserialize("55.9896291,37.2334412");
	        //p.save();
	        System.out.println("add point in latlng");
	        if (!DbFunctions.exsistPoint(namePoint)){
		         try{DbFunctions.add(p);	         
		         }catch(IllegalAccessException e){
		        	 e.printStackTrace();
				 }catch(IllegalArgumentException e){
					 e.printStackTrace();
				 }catch(InstantiationException e){
		        	 e.printStackTrace();
		         }
		         System.out.println("adding "+namePoint);
	        }
		}
		mapsActivity = Robolectric.buildActivity(MapsActivity.class)
				.create()
				.start() // no activity
				.get();
		geoPositionActivity = Robolectric.buildActivity(GeoPositionActivity.class)
				.create()
				.start()
				.get();
		latLngActivity = Robolectric.buildActivity(LatLngActivity.class)
				.create()
				.start()
				.get();
		mainActivity = Robolectric.buildActivity(MainActivity.class)
				.create()
				.start()
				.get();
		traceActivity = Robolectric.buildActivity(TraceActivity.class)
				.create()
				.start()
				.get();
		settingsActivity = Robolectric.buildActivity(SettingsActivity.class)
				.create()
				.start()
				.get();
	}
	@Test
	public void testNextView() {
		System.out.println("----- mapsActivity.nextView(vals[i]):");
		String[] vals = {MapsActivity.HOME,MapsActivity.GEO
				,MapsActivity.SAVE_TRACE, MapsActivity.GO_POSITION};
		System.out.println(mapsActivity);
		//GoogleMap map;
		//fragment = (SupportMapFragment)mapsActivity.getSupportFragmentManager().findFragmentById(R.id.map);
		//((SupportMapFragment)fragment).getMapAsync(mapsActivity);
		//org.junit.Assert.assertTrue(map!=null);
	
		for (int i = 0; i < vals.length; i++) {
			System.out.println(vals[i]);
			mapsActivity.nextView(vals[i]);
		}
		System.out.println("----- geoPositionActivity.nextView(vals[i]):");
		String[] vals1 = {GeoPositionActivity.HOME
				//,GeoPositionActivity.MAP //TODO find dialog resource
				,GeoPositionActivity.POINTS,GeoPositionActivity.SAVE_POINT
				,GeoPositionActivity.TRACE,GeoPositionActivity.ADD_WAYPOINTS};
		System.out.println(geoPositionActivity);
		for (int i = 0; i < vals1.length; i++) {
			System.out.println(vals1[i]);
			geoPositionActivity.nextView(vals1[i]);			
		}
		// no nextView in LatLngActivity
		System.out.println("----- mainActivity.nextView(vals[i]):");
		String[] vals2 = {MainActivity.GEO, MainActivity.POINTS
				,MainActivity.TO_TRACE,MainActivity.EDIT_POINT
				,MainActivity.EDIT_TRACE, MainActivity.COORDINATE_GO
				,MainActivity.SETTINGS};
		System.out.println(mainActivity);
		for (int i = 0; i < vals2.length; i++) {
			System.out.println(vals2[i]);
			mainActivity.nextView(vals2[i]);
		}
		System.out.println("----- traceActivity.nextView(vals[i]):");
		String[] vals3 = {TraceActivity.MAP, TraceActivity.CONNECT_POINT,
				TraceActivity.GEO_ADD_POINT,TraceActivity.HOME
				,TraceActivity.RESET_TRACE};
		System.out.println(traceActivity);
		for (int i = 0; i < vals3.length; i++) {
			System.out.println(vals3[i]);
			traceActivity.nextView(vals3[i]);
		}
	}
	@Test
	public void printNotFoundedMethod(){
		System.out.println("start printNotFoundedMethod");//Log.i("XXX", "start printNotFoundedMethod");
		try {
		    Class clazz = Class.forName("com.activeandroid.util.ReflectionUtils");
		    Method[] methods = clazz.getDeclaredMethods();
		    for (Method m : methods) {
		        //Log.v("XXX", "Method found: " + m);
		    	System.out.println("Method found: " + m);
		    }
		} catch (Exception e) {
		    Log.e("XXX", "Exception: " + e);
		}
			System.out.println("end of printNotFoundedMethod");//Log.i("XXX", "end of printNotFoundedMethod");
	}
	@Test
	public void testRWPoint(){
		LatLng testPoint = PositionUtil.LAT_LNG;// get from maps or geoPosition
		String name = "testPoint"; // get from dialog
		Intent intent = new Intent();
		Uri data = Uri.parse(PositionUtil.getTracePointFalsePass(testPoint, "geo"));
		intent.setData(data);		
		geoPositionActivity.setIntent(intent);
		//geoPositionActivity.nextView(GeoPositionActivity.SAVE_POINT);
		point.data = testPoint;
		point.name = name;
		point.save();// positive process
		
	     System.out.println("start getting points ...");
         List<Model> points = DbFunctions.getTablesByModel(Point.class);
         UtilePointSerializer util = new UtilePointSerializer();
	     Map<String,String> map = new HashMap();
	     boolean testPointExist = false;
         if (points!=null)
	         for (Iterator iterator = points.iterator(); iterator.hasNext();) {
				Point rPoint = (Point) iterator.next();
				map.put(rPoint.name, (String)util.serialize(rPoint.data));
				if (rPoint.name.contentEquals(name) 
						&& ((String)util.serialize(rPoint.data))
						.contains((String)util.serialize(testPoint))) 
					testPointExist = true;
	         }
         System.out.println("all points are "+map);
         org.junit.Assert.assertTrue(testPointExist);
	}
	
	@Test
	public void testRWTrace(){
		LatLng start = PositionUtil.LAT_LNG;
		LatLng end = PositionUtil.LAT_LNG;
		String name = "testTrace";
		trace.name = name;
		trace.start = start;
		trace.end = end;		
		Intent intent = new Intent();
		Uri data1 = Uri.parse(PositionUtil.getTracePointFalsePass(start, "start"));
		Uri data2 = Uri.parse(PositionUtil.getTracePointFalsePass(end, "end"));
		intent.putExtra("start", data1);
		intent.putExtra("end", data2);
		mapsActivity.setIntent(intent);
		//mapsActivity.nextView(MapsActivity.SAVE_TRACE);
		trace.save();// positive process

		System.out.println("start getting traces ...");
        List<Model> traces = DbFunctions.getTablesByModel(Trace.class);
        UtileTracePointsSerializer util = new UtileTracePointsSerializer();
	    Map<String,String> map = new HashMap();
	    boolean testTraceExist = false;
        if (traces!=null)
	         for (Iterator iterator = traces.iterator(); iterator.hasNext();) {
				Trace rTrace = (Trace) iterator.next();
				map.put(rTrace.name, rTrace.toString());
				if (rTrace.name.contentEquals(name) 
						&& rTrace.toString()
						.contains(trace.toString())) 
					testTraceExist = true;
	         }
        System.out.println("all points are "+map);
        org.junit.Assert.assertTrue(testTraceExist);
	}
	@Test
	public void testOnItemPointSelected(){
		System.out.println("---- start testOnItemPointSelected");
		System.out.println("start testOnItemPointSelected");
		ListView listView = (ListView) latLngActivity.findViewById(android.R.id.list);
		View view = latLngActivity.findViewById(android.R.id.text1);

		latLngActivity.onListItemClick(listView
				, view
				, 0, 0);
		System.out.println("---- end testOnItemPointSelected");

	}	
	@Test
	public void testPositionAndBoundInit(){
		System.out.println("---- start testPositionAndBoundInit");
		Intent intent = new Intent();
		String uriString  = "geo:1.0,2.0?z=2.0?pass=false?title=some";
		intent.setData(Uri.parse(uriString));
		intent.putExtra("start", Uri.parse(uriString));
		intent.putExtra("end", Uri.parse(uriString));
		Uri uri = intent.getData();
		System.out.println(uri);
		PositionUtil positionUtil = new PositionUtil();
		try{positionUtil.positionAndBoundInit(intent);
		}catch(Exception e){
			System.out.println(e.getMessage());
			org.junit.Assert.assertTrue(false);
		}
		org.junit.Assert.assertTrue(positionUtil.getCenter()!=null);
		org.junit.Assert.assertTrue(positionUtil.getZoom()!=0);
		org.junit.Assert.assertTrue(positionUtil.getCenterPass()==false);
		org.junit.Assert.assertTrue(positionUtil.getTitleMarker().contentEquals("some"));
		org.junit.Assert.assertTrue(positionUtil.getStart()!=null);
		org.junit.Assert.assertTrue(positionUtil.getStartPass()==false);
		org.junit.Assert.assertTrue(positionUtil.getEnd()!=null);
		//set test
		uriString = uriString.replace("false", "true");
		positionUtil.setCenterPass(true);
		intent = positionUtil.getIntent();
		String iStringGeo = ((Uri)intent.getData()).toString();
		System.out.println("uriString:"+uriString+", intent string:"+iStringGeo);
		org.junit.Assert.assertTrue(iStringGeo.contains(uriString));
		System.out.println("---- stop testPositionAndBoundInit");
	}

	@Test
	public void testSetDef(){
		System.out.println("---- start testSetDef");
		PositionUtil positionUtil = new PositionUtil();
		Intent intent = new Intent();
		positionUtil.setCommand(TRACE_PLOT_STATE.START_COMMAND);
		org.junit.Assert.assertTrue(positionUtil.getCenterPass()==false);
		org.junit.Assert.assertTrue(positionUtil.getStartPass()==true);
		org.junit.Assert.assertTrue(positionUtil.getEndPass()==false);
		System.out.println("---- end testSetDef");
	}		
	
	@Test
	public void testDef(){
		System.out.println("---- start testDef");
		PositionUtil positionUtil = new PositionUtil();
		Intent intent = new Intent();
		String uriString  = "geo:1.0,2.0?z=2.0?pass=false?title=some";
		intent.setData(Uri.parse(uriString));
		intent.putExtra("start", Uri.parse(uriString));
		intent.putExtra("end", Uri.parse(uriString));
		TRACE_PLOT_STATE state = null;
		try{positionUtil.positionAndBoundInit(intent);
			positionUtil.setStartPass(true);
			state = positionUtil.defCommand();
		}catch(Exception e){
			org.junit.Assert.assertTrue(false);
		}
		System.out.println("startPass:"+positionUtil.getStartPass()
				+" endPass:"+positionUtil.getEndPass()
				+" centerPass:"+positionUtil.getCenterPass());
		org.junit.Assert.assertTrue(state==TRACE_PLOT_STATE.START_COMMAND);//TODO fix it
		System.out.println("---- end testDef");
	}
	@Test
	public void testMapsActivity() {
		//assertNull(null);
		assertThat(mapsActivity).isNotNull();
		assertThat(latLngActivity).isNotNull();
		assertThat(mainActivity).isNotNull();		
		assertThat(traceActivity).isNotNull();
		assertThat(geoPositionActivity).isNotNull();
	}
	@Test
	public void testFragment() {
		//assertNull(null);
		//mapsActivity.setContentView(R.layout.activity_maps);
		fragment = (SupportMapFragment)mapsActivity.getSupportFragmentManager().findFragmentById(R.id.map);
		assertThat(fragment).isNotNull();
		//assertThat(activity).isNull();
	}
	@Test
	public void testMap() {
		//mapsActivity.setContentView(R.layout.activity_maps);// try comment and see Error inflating class fragment
		fragment = (SupportMapFragment)mapsActivity.getSupportFragmentManager().findFragmentById(R.id.map);
		((SupportMapFragment)fragment).getMapAsync(new OnMapReadyCallback(){

			@Override
			public void onMapReady(GoogleMap map) {
				org.junit.Assert.assertTrue(map!=null);
				CameraUpdate cameraUpdate = CameraUpdateFactory.zoomBy(15);
				org.junit.Assert.assertTrue(cameraUpdate!=null);
				map.animateCamera(cameraUpdate);
				org.junit.Assert.assertTrue(map!=null);
			}
		});
	}
	@Test
	public void testAvoidPushAndPull() {
		
		String cmd1 = DbFunctions.AVOID_FERRIES ;
		String cmd2 = DbFunctions.AVOID_TOLLS;
		String pipeline1 = cmd1+"|"+cmd2;
		String pipeline2 = cmd1;
		String pipeline3 = cmd2;
		String pipeline4 = "";
		String pipeline5 = "|";

		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline1, cmd2)
				.contentEquals(pipeline1));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline1, cmd2)
				.contentEquals(cmd1));
		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline2, cmd2)
				.contentEquals(pipeline1));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline2, cmd2)
				.contentEquals(cmd1));
		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline3, cmd2)
				.contentEquals(cmd2));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline3, cmd2)
				.contentEquals(""));
		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline4, cmd2)
				.contentEquals(cmd2));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline4, cmd2)
				.contentEquals(""));
		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline4, "")
				.contentEquals(""));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline4, "")
				.contentEquals(""));
		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline5, "")
				.contentEquals(pipeline5));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline5, "")
				.contentEquals(pipeline5));
		System.out.println("befor pushing "+ SettingsActivity.push(pipeline5, cmd2));
		org.junit.Assert.assertTrue(SettingsActivity.push(pipeline5, cmd2)
				.contentEquals(cmd2));
		org.junit.Assert.assertTrue(SettingsActivity.pull(pipeline5, cmd2)
				.contentEquals(""));
	}
}
