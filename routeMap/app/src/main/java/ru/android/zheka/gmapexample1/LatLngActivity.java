package ru.android.zheka.gmapexample1;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.jsbridge.JsCallable;

import com.activeandroid.Model;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class LatLngActivity extends RoboListActivity implements JsCallable{
		public static final String HOME = "home";
		// no menu
		@InjectView(R.id.webViewPoint)
		WebView webViewHome;
		int resViewId = R.layout.activity_points;
		String url = "file:///android_asset/points.html";
		Context context = this;
		Intent geoIntent;
        String name;
        Class cls;// = GeoPositionActivity.class;
        Class clPosIntr=null;
        Class clActivity;
        Class clMain;

	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        name = getResources().getString(R.string.points_column_name);
	        geoIntent = getIntent();
	         setContentView(R.layout.activity_points);
	         System.out.println("---------- "+System.getProperty("java.class.path"));

	         try {
	        	    cls = Class.forName("ru.android.zheka.gmapexample1.GeoPositionActivity");
	        	    System.out.println("----------  from LatLngActivity: find  ru.android.zheka.gmapexample1.GeoPositionActivity");
	        	}catch (ClassNotFoundException e){
	        	    System.out.println("---------- from LatLngActivity "+e.getMessage());
	        	}
	         try {
	        	    clActivity = Class.forName("android.app.Activity");
	        	    System.out.println("----------  from LatLngActivity: find  android.app.Activity");
	        	}catch (ClassNotFoundException e){
	        	    System.out.println("---------- from LatLngActivity "+e.getMessage());
	        }	
	         try {
	        	 clPosIntr = getClassLoader().loadClass("ru.android.zheka.gmapexample1.PositionInterceptor");
	             System.out.println("----------  from LatLngActivity: loadClass  ru.android.zheka.gmapexample1.PositionInterceptor");
	         }catch (ClassNotFoundException e){
	             System.out.println("---------- from LatLngActivity "+e.getMessage());
		         try {
		        	 clPosIntr = getClassLoader().getSystemClassLoader().loadClass("ru.android.zheka.gmapexample1.PositionInterceptor");
		             System.out.println("----------  from LatLngActivity: loadClass  ru.android.zheka.gmapexample1.PositionInterceptor");
		         }catch (ClassNotFoundException ee){
		             System.out.println("---------- from LatLngActivity "+ee.getMessage());
			         try {
			        	 clPosIntr = Class.forName("ru.android.zheka.gmapexample1.PositionInterceptor");
			             System.out.println("----------  from LatLngActivity: find  ru.android.zheka.gmapexample1.PositionInterceptor");
			         }catch (ClassNotFoundException eee){
			             System.out.println("---------- from LatLngActivity "+eee.getMessage());
			         }
		         }
	         }
	         try {
	        	    clMain = Class.forName("ru.android.zheka.gmapexample1.MainActivity");
	        	    System.out.println("----------  from LatLngActivity: find  ru.android.zheka.gmapexample1.MainActivity");
	        	}catch (ClassNotFoundException e){
	        	    System.out.println("---------- from LatLngActivity "+e.getMessage());
	        }	
	         // no menu
	         MenuHandler m = new MenuHandler();
	         m.initJsBridge(this,url);
	         //TODO use SimpleCursorAdapter
	         List<Map<String,String>> dataTmp = new ArrayList<Map<String,String>>();
	         List<Model> points = DbFunctions.getTablesByModel(Point.class);
	         UtilePointSerializer util = new UtilePointSerializer();
	         if (points!=null)
		         for (Iterator iterator = points.iterator(); iterator.hasNext();) {
					Point point = (Point) iterator.next();
					Map map = new HashMap();
					//map.put(point.name, (String)util.serialize(point.data));
					map.put(name, point.name);
					System.out.println("read point:"+point.toString());
					dataTmp.add(map);
		         }
	         ListAdapter adapter = new SimpleAdapter(this.context
	        		 ,dataTmp
	        		 //,android.R.layout.simple_list_item_1
	        		 ,R.layout.row
	        		 //,new String[]{"Точки(Points)"}
	         		 ,new String[]{name}
	         		 //,new int[]{android.R.id.text1});
	         		 ,new int[]{R.id.text1});
	         setListAdapter(adapter);
	    }
	// instead nextView
	@Override
	public void onListItemClick(ListView l, View v,int position, long id) {
		System.out.println("------ start LatLngActivity.onListItemClick");
		Map<String,String> data = (Map)l.getAdapter().getItem(position);
		System.out.println("------ from onListItem: item is "+data);
        Intent intent = getIntent();
        PositionUtil positionUtil = new PositionUtil();
        TRACE_PLOT_STATE stateIntent;
        //String centerSer = (String)data.get(data.keySet().iterator().next());
        String pointName = (String)data.get(name);
        System.out.println("start init point,pointName: "+pointName);
        Point point = DbFunctions.getPointByName(pointName);
        System.out.println("init point: "+point);
        //System.out.println("check point 1 "+DbFunctions.getPointByName("1"));
        //System.out.println("check point 2 "+DbFunctions.getPointByName("2"));


        String centerSer = (String)new UtilePointSerializer().serialize(point.data);
        //try get ExistingCenterEndStart intention for trace
        /*try{stateIntent =  positionUtil.defCommand();  
            	geoIntent = PositionUtil.createIntentForExistingCenterEndStart(stateIntent
        		,centerSer
        		,intent);
        }catch(Exception e){
        	geoIntent=null;
        	Toast.makeText(this, "Загрузка центральной точки...", 15);
        }
        String title = data.keySet().iterator().next();
        positionUtil.setTitleMarker(title);
        LatLng center = (LatLng)new UtilePointSerializer().deserialize(centerSer);
        //initial center position, not trace
        if (geoIntent==null){
	        positionUtil.setStart(center);
	        positionUtil.setEnd(center);
	        positionUtil.setZoom(PositionUtil.zoomDefault);
        }
        positionUtil.setCenter(center);        
        */
        //PositionInterceptor positionInterceptor = new PositionInterceptor(this);
        PositionInterceptor positionInterceptor = null;

        if (positionInterceptor==null){
			positionInterceptor = new PositionInterceptor (this);
		}

        if (positionInterceptor==null)
			try{positionInterceptor = (PositionInterceptor) clPosIntr.getDeclaredConstructor(clActivity)
																.newInstance(this);
			}catch(IllegalAccessException e){		e.printStackTrace();
			}catch (InstantiationException e) {		e.printStackTrace();
			}catch (ExceptionInInitializerError e) {e.printStackTrace();
			}catch (SecurityException e){			e.printStackTrace();
			}catch (InvocationTargetException e){	e.printStackTrace();
			}catch (NoSuchMethodException e){		e.printStackTrace();
			}

		if (positionInterceptor==null) {
			Toast.makeText (this, "positionInterceptor==null, если ошибка повторяется сообщите", 15).show ();
			throw new NullPointerException ("positionInterceptor==null");
		}
		try {
			positionInterceptor.positioning ();
		}catch (Exception e) {
			Toast.makeText(this, "Что то пошло не так ...", 15).show();
			positionInterceptor.updatePosition();
        	e.printStackTrace ();
        }
        positionInterceptor.centerPosition = point.data;
        geoIntent = positionInterceptor.getNewIntent();
        
        //zoom get from default
        //geoIntent = positionUtil.getIntent();
        
        System.out.println("LatLng geoIntent dispatchment");
        System.out.println((Uri)geoIntent.getData());
        //geoIntent.setData(Uri.parse(PositionUtil.getGeoPosition(center,String.valueOf(2),title)));
        System.out.println((Uri)geoIntent.getData());
        System.out.println((Uri)geoIntent.getParcelableExtra("start"));
        System.out.println((Uri)geoIntent.getParcelableExtra("end"));
        System.out.println("LatLng setIntent:"+geoIntent);
        
        geoIntent.setClass(this.context, cls);
        geoIntent.setAction(Intent.ACTION_VIEW);        
        startActivity(geoIntent);
        finish();
	}
	/*
	class MenuHandler extends MainActivity.MenuHandlerAbstract{
		@Override
		public JsCallable getHandler() {
			return LatLngActivity.this;
		}
	}
	*/
	@Override
	public void nextView(String val) {
		Intent intent = getIntent();
		if (val.contentEquals(HOME)) {
            Toast.makeText(this, "Home view called " + val, 15).show();
            intent.setClass(this.context, clMain);
	        intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
            finish();	
		}
	}
	@Override
	public WebView getVebWebView() {
		return webViewHome;
	}
}
