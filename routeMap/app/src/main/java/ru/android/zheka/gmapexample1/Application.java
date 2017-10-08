package ru.android.zheka.gmapexample1;

import java.lang.reflect.Field;

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
public class Application extends com.activeandroid.app.Application {
	public static Config config=null;
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
	}
	public static void initConfig(){
		System.out.println("initConfig");
		config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
		System.out.println("config is "+config);
		if (isFieldNull(config)){
			config = new Config();
			config.name = DbFunctions.DEFAULT_CONFIG_NAME;
			config.optimization = false;
			config.travelMode = Routing.TravelMode.WALKING.toString();
			config.uLocation = true;// prefer move camera to location rather than intent
			config.tenMSTime = "0";
			config.avoid = "tolls"; // tolls, highways, ferries, indoor or empty
			config.reserved1 = "";
			config.reserved2 = "";
			config.reserved3 = "";
			
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
}
