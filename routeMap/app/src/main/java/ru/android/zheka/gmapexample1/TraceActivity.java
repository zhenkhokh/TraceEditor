package ru.android.zheka.gmapexample1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Trace;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.db.UtileTracePointsSerializer;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.jsbridge.JsCallable;

import com.activeandroid.Model;

import android.app.FragmentManager;
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

public class TraceActivity extends RoboListActivity implements JsCallable{
	public static final String CONNECT_POINT = "connectPoint";
	public static final String HOME = "home";
	public static final String GEO_ADD_POINT = "geoAddPoint";
	public static final String RESET_TRACE = "resetTrace";
	public static final String MAP = "map";
	Context context = this;
	protected String url =  "file:///android_asset/trace.html";
	protected int resViewId = R.layout.activity_points;
	String name;
	static String msg = "";
	static boolean ready=false;
	static Object monitor = new Object();
			//SingleChoiceDialog("Выберете текущую точку маршрута как путевую или как конечную") {
	static public class MyDialogFragment extends SingleChoiceDialog{
public MyDialogFragment(){
	super("");
}
				@Override
				public void positiveProcess() {
					synchronized (monitor) {
						TraceActivity.msg = "yes";
						ready = true;
						monitor.notify();
					}
				}
				@Override
				public void negativeProcess() {
					synchronized (monitor) {
						TraceActivity.msg = "no";
						ready = true;
						monitor.notify();
					}
				}
			};
	SingleChoiceDialog dialog = new MyDialogFragment();
	{
		dialog.msg="Выберете текущую точку маршрута как путевую или как конечную";
	}

	FragmentManager fm;

	@InjectView(R.id.webViewPoint)
	WebView webViewHome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
        setContentView(resViewId);
        name = getResources().getString(R.string.traces_column_name);
		MenuHandler m = new MenuHandler();
		m.initJsBridge(this,url);
        List<Map<String,String>> dataTmp = new ArrayList<Map<String,String>>();
        //Map map = new HashMap<String, Trace>(); 
        //map.put("1", "18.015365,-77.499382,18.012590,-77.500659");
        //map.put("2", "18.015365,-77.499380,18.012590,-77.500657");
        List<Model> traces = DbFunctions.getTablesByModel(Trace.class);
        UtileTracePointsSerializer util = new UtileTracePointsSerializer();
        if (traces!=null)
	        for (Iterator iterator = traces.iterator(); iterator.hasNext();) {
				Trace trace = (Trace) iterator.next();
				Map<String,String> map = new HashMap();
				map.put(name, trace.name);
				dataTmp.add(map);
			}
        //TODO SimpleCursorAdapter
        ListAdapter adapter = new SimpleAdapter(this.context,
       		 dataTmp
       		 ,android.R.layout.simple_list_item_1
       		 ,new String[]{getResources().getString(R.string.traces_column_name)}
        		 ,new int[]{android.R.id.text1});
        setListAdapter(adapter);
	}
	@Override
	public void onListItemClick(ListView l, View v,int position, long id) {
		Map<String,String> data = (Map)l.getAdapter().getItem(position);
		System.out.println("------ from onListItem: item is "+data);
		//String[] s = ((String)data).split(",");
		//String startPoint = s[0]+","+s[1];
		//String endPoint = s[2]+","+s[3];

		String nameTrace = (String)data.get(name);
		Trace trace = (Trace) DbFunctions.getModelByName(nameTrace, Trace.class);
		
        UtileTracePointsSerializer utilTrace = new UtileTracePointsSerializer();
        UtilePointSerializer utilPoint = new UtilePointSerializer();          
        PositionInterceptor position1 = new PositionInterceptor(this);
        try{position1.positioning();
        }catch(Exception e){System.out.println("get zoom: "+position1.zoom);}
        position1.start = trace.start;
        position1.end = trace.end;
        position1.centerPosition = trace.end;
        position1.setExtraPointsFromCopy (trace.data.extraPoints);
        position1.state = TRACE_PLOT_STATE.CENTER_END_COMMAND;
        setIntent(getIntent().putStringArrayListExtra(PositionUtil.EXTRA_POINTS, trace.data.extraPoints));
        MapsActivity.updateOfflineState (this);
		if (MapsActivity.isOffline)
			position1.title = (String) utilTrace.serialize (trace.data);
			//setIntent(getIntent().putExtra (PositionUtil.TITLE,(String) utilTrace.serialize (trace.data)));
        PositionUtil.isCenterAddedToTrace  = false;
        Intent intent = position1.getNewIntent();
       /*try{intent = positionUtil.createIntentForExistingCenterEndStart(TRACE_PLOT_STATE.CENTER_END_COMMAND
        		,(String)utilPoint.serialize(trace.end)
        		,intent);
        intent = positionUtil.createIntentForExistingCenterEndStart(TRACE_PLOT_STATE.CENTER_START_COMMAND
        		,(String)utilPoint.serialize(trace.start)
        		,intent);
        }catch(Exception e){
        	e.printStackTrace();
        }*/
        intent.setClass(this.context, MapsActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        //intent.putExtra("trace", (String)utilTrace.serialize(trace.data));
        //intent.putStringArrayListExtra(PositionUtil.EXTRA_POINTS, trace.data.extraPoints);
        startActivity(intent);
        finish();
	}
	@Override
	public void nextView(String val) {
        /*
        LatLng center = LatLng center = PositionUtil.getGeoPosition(this.context);
        */
    	Intent intent = getIntent();
        //TODO remove
    	System.out.println("TraceActivity nextView "+val);
        Intent geoIntent = getIntent();
        System.out.println((Uri)geoIntent.getData());
        System.out.println((Uri)geoIntent.getParcelableExtra("start"));
        System.out.println((Uri)geoIntent.getParcelableExtra("end"));
        
    	PositionUtil positionUtil = new PositionUtil();
    	LatLng center;
        try{positionUtil.positionAndBoundInit(intent);
        	center = positionUtil.getCenter();
        }catch(Exception e){
        	try{positionUtil.setCenterPosition(intent);
        	center = positionUtil.getCenter();
	       	}catch(Exception ee){
	       		//Toast.makeText(this, "center point is not specified: задайте местоположение" , 15).show();
	       		AlertDialog dialog = new AlertDialog("center point is not specified: задайте местоположение");
	       		dialog.show(getFragmentManager(), "Cooбщение");
	       		return;
	       	}
        }
	        if (val.contentEquals(MAP)){
	        	try{intent = positionUtil.createIntentForExistingCenterEndStart(TRACE_PLOT_STATE.CENTER_START_COMMAND
	            		,PositionUtil.latLngToString(positionUtil.getStart())
	            		,intent);
	        	}catch(Exception e){
	        		Toast.makeText(this, "some trace point(s) is not specified: задайте начало и конец маршрута" , 15).show();
	        		return;
	        	}
	        	intent.setClass(this.context,MapsActivity.class);
	        	intent.setAction(Intent.ACTION_VIEW);
	            startActivity(intent);
	            finish();
	        }
	        //if (val.contentEquals(GEO_START_POINT)){
	        if (val.contentEquals(GEO_ADD_POINT)){
	            boolean isStartCmd = false;
	            boolean isEndCmd = false;
	            TRACE_PLOT_STATE state=null;
	            try{positionUtil.positionAndBoundInit(intent);
	            }catch(Exception e){
	            	isStartCmd=true;
	            }
	            state = positionUtil.defCommand();
	            if(isStartCmd==false && isOtherMode(state)){
		            //Toast.makeText(this, "Подан другой режим, для сброса вернитесь в начало маршрута " + val, 30).show();
	            	AlertDialog dialog = new AlertDialog("Подан другой режим, для сброса вернитесь в начало маршрута");
	            	dialog.show(getFragmentManager(), "Ошибка");
		            return;
	            }
	            if (state==TRACE_PLOT_STATE.CENTER_END_COMMAND){
		            Toast.makeText(this, "Маршрут задан, перейдите к просмотру" , 15).show();
		            return;
	            }
	            if(state==TRACE_PLOT_STATE.CENTER_COMMAND)//<-- reset command
		            isStartCmd = true;
	            if(state==TRACE_PLOT_STATE.CENTER_START_COMMAND)
	            	isEndCmd = true;
	            if(isStartCmd){
		            Toast.makeText(this, "Начало маршрута задано, перейдите к концу" , 15).show();
	            	positionUtil.setTitleMarker("Start");
	            	positionUtil.setStart(center);
	            	positionUtil.setEnd(center);
	            	positionUtil.setCommand(TRACE_PLOT_STATE.CENTER_START_COMMAND);
	            }
	            if(isEndCmd){
	            	//final Object monitor = new Object();

					fm = getFragmentManager();
					//fm.beginTransaction().add(new DialogFragment(),"test").commit();
					dialog.show(fm, "Сообщение");
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
					if(TraceActivity.msg.contains("yes")){
						nextView(CONNECT_POINT);
						return;
					}
		            Toast.makeText(this, "Конец маршрута задан, перейдите к просмотру", 15).show();
	            	positionUtil.setTitleMarker("End");
	            	positionUtil.setEnd(center);
	            	positionUtil.setCommand(TRACE_PLOT_STATE.CENTER_END_COMMAND);
	            	positionUtil.extraPoints.add((String)new UtilePointSerializer().serialize(center));
	            }
	            if (isStartCmd||isEndCmd){
		            intent = positionUtil.getIntent();
		        	intent.setClass(this.context,GeoPositionActivity.class);
		        	//intent.setClass(this.context,TraceActivity.class);
		        	intent.setAction(Intent.ACTION_VIEW);
		            startActivity(intent);
		            finish();
	            }else //TODO alert
		            Toast.makeText(this, "Ошибка в намерении: этот случай недостижим" , 15).show();
	        }
	        if (val.contentEquals(CONNECT_POINT)){
	            Toast.makeText(this, "Путевая точка задана" , 15).show();
            	positionUtil.setTitleMarker("Extra");
            	positionUtil.setEnd(center);
            	positionUtil.setCommand(TRACE_PLOT_STATE.CENTER_START_COMMAND);
            	ArrayList<String> extraPoints=null;
            	if (intent.getStringArrayListExtra(PositionUtil.EXTRA_POINTS)!=null)
            		extraPoints = intent.getStringArrayListExtra(PositionUtil.EXTRA_POINTS);
            	else
            		extraPoints = new ArrayList<String>();
            	extraPoints.add((String)(new UtilePointSerializer().serialize(center)));
            	positionUtil.setExtraPoints(extraPoints);
	            intent = positionUtil.getIntent();
	        	intent.setClass(this.context,GeoPositionActivity.class);
	        	intent.setAction(Intent.ACTION_VIEW);
	            startActivity(intent);
	            finish();
	        }
	        //if (val.contentEquals(GEO_CONNECT_POINT)){
	        if (val.contentEquals(RESET_TRACE)){
            	Toast.makeText(this, "Текущий маршрут удален, добавьте точку в начало нового маршрута" , 30).show();
            	PositionUtil.isCenterAddedToTrace = false;
            	positionUtil.setTitleMarker("Start");
            	positionUtil.setStart(center);
            	positionUtil.setEnd(center);            	
            	positionUtil.setCommand(TRACE_PLOT_STATE.CENTER_COMMAND);
            	positionUtil.setExtraPoints(new ArrayList<String>());
            	setIntent(positionUtil.getIntent());
                //TODO remove prints
            	System.out.println("TraceActivity reset to ");
                geoIntent = getIntent();
                System.out.println((Uri)geoIntent.getData());
                System.out.println((Uri)geoIntent.getParcelableExtra("start"));
                System.out.println((Uri)geoIntent.getParcelableExtra("end"));
	        }
	        if (val.contentEquals(HOME)){
	            intent.setClass(this.context, MainActivity.class);
		        intent.setAction(Intent.ACTION_VIEW);
	            startActivity(intent);
	            finish();
	        }
	        if (val.contentEquals(MainActivity.TO_TRACE)) {
	            intent.setClass(this.context, PointToTraceActivity.class);
	            startActivity(intent);
	            finish();
	        }
	}
	@Override
	public WebView getVebWebView() {
		return webViewHome;
	}
	public static boolean isOtherMode(TRACE_PLOT_STATE state){
		if(state!=TRACE_PLOT_STATE.CENTER_START_COMMAND
        		&& state!=TRACE_PLOT_STATE.CENTER_END_COMMAND
        		&& state!=TRACE_PLOT_STATE.CENTER_COMMAND)
			return true;
		return false;
	}

}
