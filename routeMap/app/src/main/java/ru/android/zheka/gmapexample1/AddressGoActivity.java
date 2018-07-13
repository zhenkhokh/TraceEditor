package ru.android.zheka.gmapexample1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.geo.GeoCoder;
import ru.android.zheka.jsbridge.JsCallable;

public class AddressGoActivity extends RoboListActivity implements JsCallable{
    public static final String HOME = "home";
    Context context = this;
    String url = "file:///android_asset/address_go.html";
    private String name = "Адреса";
    private Class clPosIntr,clActivity,cls;

    @InjectView(R.id.webViewPoint)
	WebView vebViewHome;

    @Override
    public void nextView(String val) {
        Intent intent = getIntent();
		if (val.contentEquals(HOME)){
		    intent.setClass(this.context, MainActivity.class);
		    intent.setAction(Intent.ACTION_VIEW);
		    startActivity(intent);
		    finish();
		}
        if (val.contentEquals(MainActivity.GO)) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClass(this.context, AddressActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public WebView getVebWebView() {
        return vebViewHome;
    }
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate (bundle);
        setContentView(R.layout.activity_address_go);
        MenuHandler m = new MenuHandler();
	    m.initJsBridge(this,url);
        //geoCoder = AddressActivity.geoParser.parse ();
        List<Map<String,String>> data = new ArrayList <> ();
        String[] addresses = AddressActivity.geoCoder.getAdresses();
        for (int i=0;i<addresses.length;i++){
            Map<String,String> map = new HashMap ();
            map.put (name,addresses[i]);
            data.add (map);
        }
        ListAdapter adapter = new SimpleAdapter(this.context
	        		 ,data
                     ,R.layout.row
	         		 ,new String[]{name}
	         		 ,new int[]{R.id.text1}
	         		 );
        setListAdapter(adapter);
        try {
	        cls = Class.forName("ru.android.zheka.gmapexample1.GeoPositionActivity");
	    }catch (ClassNotFoundException e){
	       	    System.out.println("---------- from AddressGoActivity "+e.getMessage());
	    }
         try {
	       	 clPosIntr = getClassLoader().loadClass("ru.android.zheka.gmapexample1.PositionInterceptor");
	     }catch (ClassNotFoundException e){
		         try {
		        	 clPosIntr = getClassLoader().getSystemClassLoader().loadClass("ru.android.zheka.gmapexample1.PositionInterceptor");
		         }catch (ClassNotFoundException ee){
			         try {
			        	 clPosIntr = Class.forName("ru.android.zheka.gmapexample1.PositionInterceptor");
			         }catch (ClassNotFoundException eee){
			             System.out.println("---------- from AddressGoActivity "+eee.getMessage());
			         }
		         }
         }
         try {
             clActivity = Class.forName("android.app.Activity");
         }catch (ClassNotFoundException e){
             System.out.println("---------- from AddressGoActivity "+e.getMessage());
         }
    }
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        Map<String,String> selectedAddress = (Map)l.getAdapter().getItem(position);
        String address = selectedAddress.get (name);
        int key = AddressActivity.geoCoder.getKey (address);
        LatLng point;
        if (key!=-1)
            point= AddressActivity.geoCoder.getPoints ()[key];
        else{
            AlertDialog dialog = new AlertDialog("Неверный ключ, такого адреса не найдено");
            dialog.show(getFragmentManager(), "Ошибка");
            return;
        }

        PositionInterceptor positionInterceptor = null;
        try{positionInterceptor = (PositionInterceptor) clPosIntr.getDeclaredConstructor(clActivity)
        													.newInstance(this);
        }catch(IllegalAccessException e){		e.printStackTrace();
        }catch (InstantiationException e) {		e.printStackTrace();
        }catch (ExceptionInInitializerError e) {e.printStackTrace();
		}catch (SecurityException e){			e.printStackTrace();
		}catch (InvocationTargetException e){	e.printStackTrace();
		}catch (NoSuchMethodException e){		e.printStackTrace();
		}
        positionInterceptor.updatePosition();
        positionInterceptor.centerPosition = point;
        Intent geoIntent = positionInterceptor.getNewIntent();

        geoIntent.setClass(this.context, cls);
        geoIntent.setAction(Intent.ACTION_VIEW);
        startActivity(geoIntent);
        finish();
    }
}
