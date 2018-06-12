package ru.android.zheka.gmapexample1;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import ru.android.zheka.gmapexample1.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;
import ru.android.zheka.jsbridge.JavaScriptMenuHandler;
import ru.android.zheka.jsbridge.JsCallable;

public class MainActivity extends RoboActivity implements JsCallable{
	public static final String SETTINGS = "settings";
	public static final String COORDINATE_GO = "coordinateGo";
	public static final String EDIT_TRACE = "editTrace";
	public static final String EDIT_POINT = "editPoint";
	public static final String GEO = "geo";
	public static final String TO_TRACE = "toTrace";
	public static final String POINTS = "points";
	protected String url =  "file:///android_asset/home.html";
	protected int resViewId = R.layout.activity_home;
	@InjectView(R.id.webView)
	WebView webViewHome;
	Context context = this;
	Class clGeo, clLatLng, clPtoTr;
public static class MyDialog extends CoordinateDialog{
	public MainActivity activity;
	public Class clGeo;

	@Override
	public void process() {
		try{
			Float longitude = new Float(lonField.getText().toString());
			Float latitude = new Float(latField.getText().toString());
			LatLng point  = new LatLng(latitude, longitude);
			PositionInterceptor position = new PositionInterceptor(activity);
			position.updatePosition();
			position.centerPosition = point;
			Intent intent = position.getNewIntent();
			intent.setClass(activity, clGeo);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
			activity.finish();
		}
		catch (Exception e) {
			ru.android.zheka.gmapexample1.AlertDialog alert
					= new ru.android.zheka.gmapexample1.AlertDialog("Неверный формат чиел. Углы должны быть дробными");
			alert.show(activity.getFragmentManager(), "Ошибка");
			e.printStackTrace();
		}
	}
}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
System.out.println("---------- "+System.getProperty("java.class.path"));
try {
    clGeo = Class.forName("ru.android.zheka.gmapexample1.GeoPositionActivity");
    System.out.println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.GeoPositionActivity");
}catch (ClassNotFoundException e){
    System.out.println("---------- from MainActivity "+e.getMessage());
}
try {
    clLatLng = Class.forName("ru.android.zheka.gmapexample1.LatLngActivity");
    System.out.println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.LatLngActivity");
}catch (ClassNotFoundException e){
    System.out.println("---------- from MainActivity "+e.getMessage());
}
try {
    clPtoTr = Class.forName("ru.android.zheka.gmapexample1.PointToTraceActivity");
    System.out.println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.PointToTraceActivity");
}catch (ClassNotFoundException e){
    System.out.println("---------- from MainActivity "+e.getMessage());
}
		setContentView(resViewId);
		MenuHandler m = new MenuHandler();
		m.initJsBridge(this,url);
    }


	public void nextView(String val) {
		Config config  = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME,Config.class);
		System.out.println(" config is "+config);
      Intent intent = getIntent();
      if (val.contentEquals(GEO)) {
          Toast.makeText(this, "Map view called: " + val, 15).show();
          PositionInterceptor position = new PositionInterceptor(this);
          intent = position.updatePosition();
      	  intent.setClass(this.context, clGeo);
      	  intent.setAction(Intent.ACTION_VIEW);
      	  //explicit activity
      	  startActivity(intent);
      	  System.out.println("finish");
          finish();
      }
      if (val.contentEquals(POINTS)) {
          Toast.makeText(this, "Points view called: " + val, 15).show();
          intent.setClass(this.context, clLatLng);
      	  intent.setAction(Intent.ACTION_VIEW);
          startActivity(intent);
          finish();
       }
      if (val.contentEquals(TO_TRACE)) {
          intent.setClass(this.context, clPtoTr);
      	  intent.setAction(Intent.ACTION_VIEW);
          startActivity(intent);
          finish();
      }
      EditModel model = new EditModel();
      if (val.contentEquals(EDIT_POINT)) {
    	  model.clsName = "Point";
    	  model.clsPkg = "ru.android.zheka.db";
    	  model.name1Id = R.string.points_column_name1;
    	  model.nameId = R.string.points_column_name;
    	  intent.putExtra(EditActivity.EDIT_MODEL, model);
    	  intent.setAction(Intent.ACTION_VIEW);
    	  intent.setClass(this.context, EditActivity.class);
    	  startActivity(intent);
    	  //editActivity.startActivity(intent);
          finish();
      }
      if (val.contentEquals(EDIT_TRACE)) {
    	  model.clsName = "Trace";
    	  model.clsPkg = "ru.android.zheka.db";
    	  model.name1Id = R.string.traces_column_name1;
    	  model.nameId = R.string.traces_column_name;
    	  intent.putExtra(EditActivity.EDIT_MODEL, model);
    	  intent.setAction(Intent.ACTION_VIEW);
    	  intent.setClass(this.context, EditActivity.class);
    	  startActivity(intent);
          finish();
      }
      if (val.contentEquals(COORDINATE_GO)) {
    	  MyDialog dialog = new MyDialog();
    	  dialog.activity = this;
    	  dialog.clGeo = clGeo;
    	  dialog.show(getFragmentManager(), "Переход");
      }
      if (val.contentEquals(SETTINGS)) {
    	  intent.setAction(Intent.ACTION_VIEW);
    	  intent.setClass(this.context, SettingsActivity.class);
    	  startActivity(intent);
          finish();
      }
    }

	@Override
	public WebView getVebWebView() {
		return webViewHome;
	}
}
class MenuHandler{
	
	public void initJsBridge(JsCallable menu,String url){
		System.out.println("start initJsBridge from "+menu.getClass().getName());
        //menu.getActivity().setContentView(resViewId);
		System.out.println("start setWebViewSettings");
        WebView webViewHome = menu.getVebWebView();
		System.out.println("start setJavaScriptEnabled, webHome is "+webViewHome);
        webViewHome.getSettings().setJavaScriptEnabled(true);
		System.out.println("start setWebViewSettings");
        setWebViewSettings(webViewHome,(Activity)menu);
		System.out.println("start addJavascriptInterface");
        webViewHome.addJavascriptInterface(new JavaScriptMenuHandler(menu), "menuHandler");
		System.out.println("start loadUrl");
        webViewHome.loadUrl(url);
		System.out.println("end initJsBridge");
	}
    void setWebViewSettings(final WebView webViewHome,final Context context) {
    	System.out.println("start set webClient");
        webViewHome.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(context)
                        .setTitle("javaScript dialog")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    	System.out.println("hello from positive");
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            };
        });
    	System.out.println("end set webClient");
        
    }
}
