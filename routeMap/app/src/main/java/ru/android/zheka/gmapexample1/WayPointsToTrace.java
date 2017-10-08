package ru.android.zheka.gmapexample1;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap.KeySetView;


import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.jsbridge.JsCallable;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Toast;

import com.activeandroid.Model;

public class WayPointsToTrace extends EditActivity {

	public static final String ADD_WAYPOINTS = "addWaypoints";
	public WayPointsToTrace() {
		resViewId = R.layout.activity_points;
		url = "file:///android_asset/waypoints.html";
	}

	@Override
	public void nextView(String val){
		if (val.contains(HOME)){
			super.nextView(val);
		}
		Intent intent = getIntent();
		if (val.contains(ADD_WAYPOINTS)){
			//TODO go to GeoPosition
			PositionInterceptor position = new PositionInterceptor(this);
			try{intent = position.positioning();			
			}catch(Exception e){
	            Toast.makeText(this, "Невозможно выполнить: начало маршрута не задано" , 15).show();
				e.printStackTrace();
				return;
			}
            if(TraceActivity.isOtherMode(position.state)){
            	AlertDialog dialog = new AlertDialog("Подан другой режим, для сброса вернитесь в начало маршрута");
            	dialog.show(getFragmentManager(), "Ошибка");
	            return;
            }
            if (position.state==TRACE_PLOT_STATE.CENTER_END_COMMAND){
	            Toast.makeText(this, "Маршрут задан, перейдите к просмотру" , 15).show();
	            return;
            }
            if(position.state==TRACE_PLOT_STATE.CENTER_COMMAND){
	            Toast.makeText(this, "Невозможно выполнить: начало маршрута не задано" , 15).show();
	            return;
            }
            if(position.state==TRACE_PLOT_STATE.CENTER_START_COMMAND){
            	//KeySetView<String, Boolean> names = status.keySet(true);//since 1.8
            	Set<String> names = new HashSet<String>();
            	for (Iterator iterator = status.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if(status.get(key))
						names.add(key);
				}
            	if (names.isEmpty()){
    	            Toast.makeText(this, "Выберете одну или несколько точек" , 15).show();
    	            return;
            	}
	            Toast.makeText(this, "Путевые точки успешно добавлены. Добавьте конец маршрута" , 15).show();
            	UtilePointSerializer util = new UtilePointSerializer();
            	for (Iterator iterator = names.iterator(); iterator.hasNext();) {
					String name = (String) iterator.next();
					Point point = (Point) DbFunctions.getModelByName(name, Point.class);
					position.centerPosition = point.data;
					position.extraPoints.add((String)util.serialize(point.data));
				}
            	intent = position.getNewIntent();
	        	intent.setClass(this.context,GeoPositionActivity.class);
	        	intent.setAction(Intent.ACTION_VIEW);
	            startActivity(intent);
	            finish();
            }else
	            Toast.makeText(this, "Этот случай недостижим." , 15).show();            
		}
	}
}
