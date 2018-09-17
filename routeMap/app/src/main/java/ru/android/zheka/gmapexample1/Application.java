package ru.android.zheka.gmapexample1;

import android.content.Context;
import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDexApplication;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.db.UtileTracePointsSerializer;
import ru.android.zheka.route.Routing;

//TODO use it for activeandroid
public class Application extends com.activeandroid.app.Application {//extends MultiDexApplication{
	private static Config config=null;
	public static String optimizationBellmanFlag = "";
	@Override
	public void onCreate(){
		super.onCreate();

        Configuration dbConfiguration = new Configuration.Builder(this)
        .setDatabaseName("Navi.db")
        .setDatabaseVersion(1)
        .addModelClasses(Config.class,Point.class,Trace.class)
        .addTypeSerializers(UtilePointSerializer.class,UtileTracePointsSerializer.class)
        .create();
        ActiveAndroid.initialize(dbConfiguration);
        initConfig();
        if (optimizationBellmanFlag.isEmpty ())
        	optimizationBellmanFlag = getString (R.string.optimizationdata3);

	}
	public static void initConfig(){
		System.out.println("initConfig");
		config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
		Config tmp = config;
		System.out.println("config is "+config);
		if (tmp ==null || isFieldNull(config) || config.rateLimit_ms.isEmpty ()){
			config = new Config();
			config.name = DbFunctions.DEFAULT_CONFIG_NAME;
			config.optimization = false;
			config.travelMode = Routing.TravelMode.WALKING.toString();
			config.uLocation = true;// prefer move camera to location rather than intent
			config.tenMSTime = "0";
			config.avoid = "tolls"; // tolls, highways, ferries, indoor or empty
			config.bellmanFord = "";
			//config.reserved2 = "";
			config.address = AddressActivity.aDelimiter+AddressActivity.aDelimiter+AddressActivity.aDelimiter;
			config.rateLimit_ms = "800";
			config.offline = DbFunctions.DEFAULT_CONFIG_OFFLINE;
			
			Routing.TravelMode w =Enum.valueOf(Routing.TravelMode.class,config.travelMode);
			System.out.println("init "+config.toString());
			System.out.println("Routing.TravelModel.WALKING is clear "+w);
			try {
				DbFunctions.add(config);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (ConcurrentModificationException e){
				DbFunctions.delete (tmp);
				initConfig ();
			}
		}else
			System.out.println("Config was succesfully get "+config.toString());
	}
	static boolean isFieldNull(Config config){
		if (config==null)
			return true;
		Field[] fields = config.getClass().getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				if (fields[i].get(config)==null)
					return true;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// extends SomeOtherApp
   @Override protected void attachBaseContext(Context base) {
        try {
            super.attachBaseContext(base);
			MultiDex.install(this);
        } catch (RuntimeException ignored) {
            // Multidex support doesn't play well with Robolectric yet
        }
    }

}
