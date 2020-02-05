package ru.android.zheka.gmapexample1;

import com.activeandroid.query.Delete;

import androidx.databinding.ViewDataBinding;
import ru.android.zheka.coreUI.AbstractActivity;
import ru.android.zheka.fragment.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import roboguice.inject.InjectView;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Trace;
import ru.android.zheka.gmapexample1.edit.EditModel;
import ru.android.zheka.jsbridge.JavaScriptMenuHandler;
import ru.android.zheka.jsbridge.JsCallable;

import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends
        AbstractActivity
        //RoboActivity
        {
    public static String googleKey = "";
	public static final String SETTINGS = "settings";
	public static final String EDIT_TRACE = "editTrace";
	public static final String EDIT_POINT = "editPoint";
	public static final String GEO = "geo";
	public static final String TO_TRACE = "toTrace";
	public static final String POINTS = "points";
	public static final String GO = "address";
	public static final String INFO = "info";
	protected String url =  "file:///android_asset/home.html";
	protected int resViewId = R.layout.activity_home;
	Class clGeo, clLatLng, clPtoTr;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initComponent() {

    }

    @Override
    protected void onInitBinding(ViewDataBinding binding) {

    }

    @Override
    protected void onResumeBinding(ViewDataBinding binding) {

    }

    @Override
    protected void onDestroyBinding(ViewDataBinding binding) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
        if (Application.isFieldNull (config)){
            new Delete ().from(Trace.class).where("name=?",DbFunctions.DEFAULT_CONFIG_NAME).execute();
            Application.initConfig ();
        }

        switchToFragment (R.id.settingsFragment, new Home ());

        if (googleKey.equals (""))
            googleKey = getResources ().getString (R.string.google_api_key);
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
    }

    @Override
    public Activity getActivity() {
        return this;
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
