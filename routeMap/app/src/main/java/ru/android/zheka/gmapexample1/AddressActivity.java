package ru.android.zheka.gmapexample1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.geo.GeoCoder;
import ru.android.zheka.geo.GeoParser;
import ru.android.zheka.geo.GeoParserImpl;
import ru.android.zheka.jsbridge.JsCallable;
import ru.android.zheka.db.Config;

public class AddressActivity extends RoboActivity implements JsCallable {
    public static final String HOME = "home";
    public static final String COORDINATE_GO = "coordinateGo";
    public static final String ADDRESS_GO = "addressGo";
    public static final String CLEAR = "clearAddress";
    public static final String aDelimiter = "#";
    public Class clGeo;
    Context context = this;
    String url = "file:///android_asset/address.html";
    private TextView region,city,street,house;
    public static GeoCoder geoCoder;
	@InjectView(R.id.webViewAddress)
	WebView webView;

    public static class MyDialog extends CoordinateDialog{
        public AddressActivity activity;
        public Class clGeo;

        @Override
        public void process() {
            try{
                Float longitude = new Float(lonField.getText().toString());
                Float latitude = new Float(latField.getText().toString());
                LatLng point  = new LatLng(latitude, longitude);
                PositionInterceptor position = new PositionInterceptor(activity);
                try{position.positioning ();}
                catch (Exception e){position.updatePosition();}
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
    public void nextView(String val)  {
        Intent intent = getIntent ();
        if (val.contentEquals(HOME)){
		          intent.setClass(this.context, MainActivity.class);
		          intent.setAction(Intent.ACTION_VIEW);
		          startActivity(intent);
		          finish();
			}
        if (val.contentEquals(COORDINATE_GO)) {
            MyDialog dialog = new MyDialog ();
            dialog.activity = this;
            dialog.clGeo = clGeo;
            dialog.show(getFragmentManager(), "Переход");
        }
        if (val.contentEquals(ADDRESS_GO)){
            if (isAllFieldCorrect()){
                geoCoder = new GeoParserImpl (region.getText ().toString ()
                        ,city.getText ().toString ()
                        ,street.getText ().toString ()
                        ,house.getText ().toString ()).parse ();
                Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
                StringBuilder sb = new StringBuilder ();
                sb.append (region.getText ().toString ()).append (aDelimiter)
                        .append (city.getText ().toString ()).append (aDelimiter)
                        .append (street.getText ().toString ()).append (aDelimiter)
                        .append (house.getText ().toString ());
                config.address = sb.toString ();
                try {
                    DbFunctions.add (config);
                } catch (IllegalAccessException e) {
                    e.printStackTrace ();
                } catch (InstantiationException e) {
                    e.printStackTrace ();
                }
                if (geoCoder == null){
                    AlertDialog alert = new AlertDialog ("Неверные данные, не возможно получить координаты");
                    alert.show(getFragmentManager(), "Ошибка");
                    return;
                }
                intent.setClass(this.context, AddressGoActivity.class);
		        intent.setAction(Intent.ACTION_VIEW);
		        startActivity(intent);
		        finish();
            }else{
                AlertDialog alert = new AlertDialog ("Имеются пустые поля, используйте \"-\" для них");
                alert.show(getFragmentManager(), "Ошибка");
            }
        }
        if (val.contentEquals(CLEAR)){
            Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
            config.address=aDelimiter+aDelimiter+aDelimiter;
                try {
                    DbFunctions.add (config);
                } catch (IllegalAccessException e) {
                    e.printStackTrace ();
                } catch (InstantiationException e) {
                    e.printStackTrace ();
                }
            /*region.setText ("");
            city.setText ("");
            street.setText ("");
            house.setText ("");
            */
            intent.setClass(this.context, AddressActivity.class);
		    intent.setAction(Intent.ACTION_VIEW);
		    startActivity(intent);
		    finish();
        }
    }
    private boolean isAllFieldCorrect() {
        if (region.getText ().toString ().isEmpty ())
            return false;
        if (city.getText ().toString ().isEmpty ())
            return false;
        if (street.getText ().toString ().isEmpty ())
            return false;
        if (house.getText ().toString ().isEmpty ())
            return false;
        return true;
    }

    @Override
    public WebView getVebWebView() {
        return webView;
    }
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_adress);
        try {
            clGeo = Class.forName("ru.android.zheka.gmapexample1.GeoPositionActivity");
            System.out.println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.GeoPositionActivity");
        }catch (ClassNotFoundException e){
            System.out.println("---------- from MainActivity "+e.getMessage());
        }
        MenuHandler m = new MenuHandler();
	    m.initJsBridge(this,url);
        region = (TextView)findViewById (R.id.text_region);
        city = (TextView)findViewById (R.id.text_city);
        street = (TextView)findViewById (R.id.text_street);
        house = (TextView)findViewById (R.id.text_house);
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
        String address = config.address;
        if (!address.isEmpty ()) {
            int endpos = address.indexOf (aDelimiter);
            region.setText (address.substring (0, endpos));
            address = address.substring (endpos+1);
            endpos = address.indexOf (aDelimiter);
            city.setText (address.substring (0,endpos));
            address = address.substring (endpos+1);
            endpos = address.indexOf (aDelimiter);
            street.setText (address.substring (0,endpos));
            house.setText (address.substring (endpos+1));
        }
    }
}
